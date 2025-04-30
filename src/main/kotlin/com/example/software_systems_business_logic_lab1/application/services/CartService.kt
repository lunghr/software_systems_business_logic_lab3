package com.example.software_systems_business_logic_lab1.application.services

import com.example.software_systems_business_logic_lab1.application.models.*
import com.example.software_systems_business_logic_lab1.application.models.key_classes.CartProductKey
import com.example.software_systems_business_logic_lab1.application.repos.CartProductRepository
import com.example.software_systems_business_logic_lab1.application.repos.CartRepository
import org.springframework.data.cassandra.core.CassandraTemplate
import org.springframework.data.cassandra.core.InsertOptions
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val cartProductRepository: CartProductRepository,
    private val productService: ProductService,
    private val cassandraTemplate: CassandraTemplate,
) {
    fun createCart(user: User): Cart {
        return cartRepository.save(Cart(user = user))
    }

    fun addProductToCart(cartId: UUID, productId: UUID): CartProduct {
        require(cartRepository.existsById(cartId)) { throw CartNotFoundException() }
        val key = cartProductRepository.findCartProductByKeyCartIdAndKeyProductId(cartId, productId)?.key
        if (key != null) throw ProductAlreadyInCart()
        if (!productService.isExists(productId)) throw ProductNotFoundException()
        return cartProductRepository.save(toCartProduct(cartId, productId, quantity = 1))
    }

    fun incrementProductQuantity(cartId: UUID, productId: UUID): Int {
        return cartProductRepository.findCartProductByKeyCartIdAndKeyProductId(cartId, productId)?.let { cartProduct ->
            if (productService.isAvailableToOrder(productId, 1)) {
                cartProductRepository.updateQuantity(cartProduct.quantity + 1, cartId, productId)
                cartProduct.quantity + 1
            } else {
                throw OutOfStockException()
            }
        } ?: throw ProductNotFoundException()

    }

    fun decrementProductQuantity(cartId: UUID, productId: UUID): Int {
        return cartProductRepository.findCartProductByKeyCartIdAndKeyProductId(cartId, productId)?.let { cartProduct ->
            if (cartProduct.quantity > 1) {
                cartProductRepository.updateQuantity(cartProduct.quantity - 1, cartId, productId)
                return cartProduct.quantity - 1
            } else {
                cartProductRepository.delete(cartProduct)
                return 0
            }
        } ?: throw ProductNotFoundException()
    }

    fun deleteProductFromCart(cartId: UUID, productId: UUID): CartProduct {
        return cartProductRepository.findCartProductByKeyCartIdAndKeyProductId(cartId, productId)?.let { cartProduct ->
            cartProductRepository.delete(cartProduct)
            cartProduct
        } ?: throw ProductNotFoundException()
    }

    fun getCart(cartId: UUID): List<CartProduct> {
        require(cartRepository.existsById(cartId)) { throw CartNotFoundException() }
        val cart = cartProductRepository.findByKeyCartId(cartId)
        return cart.map {
            it.takeIf { productService.isAvailableToOrder(it.key.productId, it.quantity) } ?: it.copy(
                quantity = -1
            )
        }
    }

    fun getValidCartProductUUIDs(cartId: UUID): List<UUID> {
        return cartProductRepository.findByKeyCartId(cartId).map { it.key.productId }
            .filter { productService.isAvailableToOrder(it, 1) }
    }

    fun getUser(cartId: UUID): User {
        return cartRepository.findCartById(cartId)?.user
            ?: throw CartNotFoundException()
    }
}
