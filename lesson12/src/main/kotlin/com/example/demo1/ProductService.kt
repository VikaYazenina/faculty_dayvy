package com.example.demo1

import com.example.demo1.ProductServiceProperties
import com.example.demo1.Product
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Service
class ProductService (
    private val properties: ProductServiceProperties
){
    private val products=ConcurrentHashMap<Long, Product>()
        private val idgen=AtomicLong(1)
        init{
            log("Инициализировано с настройками: $properties")
        }
        //create
        fun create(name: String, category: String, price: Double): Product?{
            log("Продукт: name=$name, category=$category, price=$price")
            if(products.size>=properties.maxProducts){
                log("Error: limit")
                return null
            }
            if(properties.forbiddenNames.any{it.equals(name, ignoreCase=true)}){
                log("Error: forbidden")
                return null
            }
            if(properties.validateCategories && properties.allowedCategories.isNotEmpty() &&
                !properties.allowedCategories.contains(category.lowercase())){
                log("Error: $category is forbidden")
                return null
            }
            if(price<=0){
                log("Error: wrong price")
                return null
            }
            val product=Product(
                id=idgen.getAndIncrement(),
                name=name,
                category=category,
                price=price
            )
            products[product.id]=product
            log("Success: $product is created")
            return product
        }
        //read
        fun getAllProducts(): List<Product>{
            log("${products.size}")
            return products.values.toList()
        }
    fun getProduct(id: Long): Product?{
        log("Search $id")
        return products[id]
    }
    fun getCategoryOfProducts(category: String): List<Product>{
        log("Search $category")
        return products.values.filter{it.category.equals(category, ignoreCase=true)}
    }
        //update
        fun updateProduct(id: Long, name: String?, category: String?, price: Double?):
                Product?{
            val product=products[id]?:run{
                log("Error")
                return null
            }
            name?.let{newName-> if(properties.forbiddenNames.any{it.equals(newName, ignoreCase=true)}){
                log("Error: name forbidden")
                return null
            }
                product.name=newName
            }
            category?.let{newCategory->
                if( properties.validateCategories && properties.allowedCategories.isNotEmpty() &&
                    !properties.allowedCategories.contains(newCategory.lowercase())){
                    log("Error: category forbidden")
                    return null
                }
                product.category=newCategory
            }
            price?.let{ newPrice ->
                if(newPrice<=0){
                    log("Error: wrong price")
                    return null
                }
                product.price=newPrice
            }
            product.updated=LocalDateTime.now()
            log("Product is successfully updated")
            return product

        }
    //delete
    fun delete(id: Long): Boolean{
        return if(products.containsKey(id)){
            products.remove(id)
            log("$id is deleted")
            true
        }else{
            log("Error: $id doesnt exist")
            false
        }
    }
    fun getStats(): Map<String, Any>{
        return mapOf(
            "totalProducts" to products.size,
            "maxProducts" to properties.maxProducts,
            "availableSlots" to (properties.maxProducts - products.size),
            "forbiddenNames" to properties.forbiddenNames,
            "allowedCategories" to properties.allowedCategories,
            "validateCategories" to properties.validateCategories

        )
    }
    fun clear(){
        products.clear()
        idgen.set(1)
        log("All is deleted")
    }
    private fun log(message: String){
        if(properties.enableLogging){
            println("[ProductService] $message")
        }
    }


}
