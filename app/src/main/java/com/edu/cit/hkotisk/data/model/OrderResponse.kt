package com.edu.cit.hkotisk.data.model

import java.util.Date

data class OrderResponse(
    val orderId: Int,
    val email: String,
    val orderStatus: String,
    val orderDate: String,
    val totalCost: Double,
    val items: List<OrderItem>
)

data class OrderItem(
    val cartId: Int,
    val orderId: Int,
    val email: String,
    val dateAdded: String,
    val quantity: Int,
    val price: Double,
    val productId: Int,
    val productName: String,
    val productCategory: String,
    val productImage: String,
    val ordered: Boolean
)
