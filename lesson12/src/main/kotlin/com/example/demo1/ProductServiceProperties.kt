package com.example.demo1

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix="product.service")
class ProductServiceProperties {
    var maxProducts: Int=100
    var forbiddenNames: MutableList<String>=mutableListOf()
    var allowedCategories: MutableList<String>=mutableListOf()
    var validateCategories: Boolean=true
    var enableLogging:Boolean=true
    override fun toString(): String{
        return "ProductServiceProperties(" + "maxProducts=$maxProducts, " + "forbiddenNames=$forbiddenNames, " +
                "allowedCategories=$allowedCategories, " + "validateCategories=$validateCategories, " +
                "enableLogging=$enableLogging)"
    }

}
