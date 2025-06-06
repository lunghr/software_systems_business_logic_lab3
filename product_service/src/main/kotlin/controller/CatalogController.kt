package com.example.controller

import com.example.model.Category
import com.example.model.Product
import com.example.service.CategoryService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/category")
@CrossOrigin(origins = ["*"])
class CatalogController(
    private val categoryService: CategoryService,
) {

    @PostMapping("/create/{categoryName}")
    fun createParentCategory(
        @PathVariable categoryName: String
    ): Category {
        return categoryService.createPaternalCategory(categoryName)
    }

    @PostMapping("/create/{parentCategoryName}/{childCategoryName}")
    fun createChildCategory(
        @PathVariable parentCategoryName: String,
        @PathVariable childCategoryName: String
    ): Category =
        categoryService.createChildCategory(parentCategoryName, childCategoryName)

    @GetMapping("/get")
    fun getParentCategories(): List<String> {
        return categoryService.getParentCategories().map { it.key.name }
    }

    @GetMapping("/get/{categoryName}")
    fun getChildCategories(
        @PathVariable categoryName: String
    ): List<String> {
        return categoryService.getChildrenOfCategory(categoryName).map { it.key.name }
    }

    @GetMapping("/get/categories/{categoryName}/products")
    fun getProducts(
        @PathVariable categoryName: String
    ): List<Product> {
        return categoryService.getProductsByCategory(categoryName)
    }

}