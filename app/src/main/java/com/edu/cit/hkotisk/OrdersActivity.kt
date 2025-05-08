package com.edu.cit.hkotisk

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edu.cit.hkotisk.data.api.RetrofitClient
import com.edu.cit.hkotisk.data.model.OrderResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    companion object {
        fun createIntent(context: android.content.Context) = android.content.Intent(context, OrdersActivity::class.java)
    }

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var loadingSpinner: android.widget.ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        setupViews()
        fetchOrders()
    }

    private fun setupViews() {
        bottomNav = findViewById(R.id.nav_view)
        bottomNav.setOnNavigationItemSelectedListener(this)
        bottomNav.selectedItemId = R.id.navigation_orders

        loadingSpinner = findViewById(R.id.loadingSpinner)

        ordersRecyclerView = findViewById(R.id.orders_recycler)
        ordersAdapter = OrdersAdapter()
        ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrdersActivity)
            adapter = ordersAdapter
        }
    }

    private fun fetchOrders() {
        loadingSpinner.visibility = android.view.View.VISIBLE
        RetrofitClient.createProductService(this).getOrders().enqueue(object : Callback<List<OrderResponse>> {
            override fun onResponse(call: Call<List<OrderResponse>>, response: Response<List<OrderResponse>>) {
                loadingSpinner.visibility = android.view.View.GONE
                if (response.isSuccessful) {
                    response.body()?.let { orders ->
                        ordersAdapter.updateOrders(orders)
                    }
                } else {
                    Toast.makeText(this@OrdersActivity, "Failed to fetch orders", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {
                loadingSpinner.visibility = android.view.View.GONE
                Toast.makeText(this@OrdersActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_dashboard -> {
                startActivity(Dashboard.createIntent(this))
                finish()
                return true
            }
            R.id.navigation_cart -> {
                startActivity(CartActivity.createIntent(this))
                finish()
                return true
            }
            R.id.navigation_profile -> {
                startActivity(ProfileActivity.createIntent(this))
                finish()
                return true
            }
            R.id.navigation_orders -> {
                return true
            }
        }
        return false
    }
}
