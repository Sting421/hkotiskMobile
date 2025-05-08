package com.edu.cit.hkotisk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edu.cit.hkotisk.data.model.OrderResponse
import java.text.SimpleDateFormat
import java.util.Locale

class OrdersAdapter : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {
    private var orders: List<OrderResponse> = emptyList()

    fun updateOrders(newOrders: List<OrderResponse>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderId: TextView = itemView.findViewById(R.id.order_id)
        private val orderStatus: TextView = itemView.findViewById(R.id.order_status)
        private val orderDate: TextView = itemView.findViewById(R.id.order_date)
        private val totalCost: TextView = itemView.findViewById(R.id.total_cost)
        private val orderItemsRecycler: RecyclerView = itemView.findViewById(R.id.order_items_recycler)
        private val orderItemsAdapter = OrderItemAdapter()

        init {
            orderItemsRecycler.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = orderItemsAdapter
            }
        }

        fun bind(order: OrderResponse) {
            orderId.text = order.orderId.toString()
            orderStatus.text = order.orderStatus
            orderStatus.setBackgroundResource(
                when (order.orderStatus) {
                    "COMPLETED" -> R.drawable.status_completed_background
                    "PROCESSING" -> R.drawable.status_processing_background
                    else -> R.drawable.status_cancelled_background
                }
            )
            
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                .parse(order.orderDate)
            orderDate.text = dateFormat.format(date)
            
            totalCost.text = String.format("$%.2f", order.totalCost)
            orderItemsAdapter.updateItems(order.items)
        }
    }
}
