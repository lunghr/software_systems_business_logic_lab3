package com.example.software_systems_business_logic_lab1.application.repos

import com.example.software_systems_business_logic_lab1.application.models.CartProduct
import com.example.software_systems_business_logic_lab1.application.models.key_classes.CartProductKey
import org.springframework.data.cassandra.core.WriteResult
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import java.util.*

interface CartProductRepository: CassandraRepository<CartProduct, CartProductKey> {

    fun findCartProductByKeyCartIdAndKeyProductId(cartId: UUID, productId: UUID): CartProduct?

    fun findByKeyCartId(cartId: UUID): List<CartProduct>

    @Query("UPDATE cart_products SET quantity = ?0 WHERE cart_id = ?1 AND product_id = ?2 IF EXISTS")
    fun updateQuantity(quantity: Int, cartId: UUID, productId: UUID): Boolean
}