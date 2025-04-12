package com.edu.cit.hkotisk.data.api

import com.edu.cit.hkotisk.data.model.Product
import com.edu.cit.hkotisk.data.model.ProductResponse
import retrofit2.Call
import retrofit2.http.GET

interface ProductService {
    @GET("user/product")
     fun getProducts(): Call<ProductResponse>
}
