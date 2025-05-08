package com.edu.cit.hkotisk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edu.cit.hkotisk.data.model.OrderItem

class OrderItemAdapter : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {
    private var items: List<OrderItem> = emptyList()

    fun updateItems(newItems: List<OrderItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.product_image)
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productCategory: TextView = itemView.findViewById(R.id.product_category)
        private val productQuantity: TextView = itemView.findViewById(R.id.product_quantity)
        private val productPrice: TextView = itemView.findViewById(R.id.product_price)

        fun bind(item: OrderItem) {
            Glide.with(itemView.context)
                .load(item.productImage)
                .centerCrop()
                .into(productImage)

            productName.text = item.productName
            productCategory.text = item.productCategory
            productQuantity.text = "Quantity: ${item.quantity}"
            productPrice.text = String.format("$%.2f", item.price * item.quantity)
        }
    }
}
