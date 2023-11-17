package com.example.podomarket.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.podomarket.R
import kotlin.properties.Delegates

class ProductDisplayPageAdapter(private val totalPage: Int, private var currentPage:Int) : RecyclerView.Adapter<ProductDisplayPageAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pageCircle: ImageView = itemView.findViewById(R.id.page_circle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_image_page_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(position == currentPage){
            holder.pageCircle.setImageResource(R.drawable.ic_circle_white)
        }
        else{
            holder.pageCircle.setImageResource(R.drawable.ic_circle_gray)
        }
    }

    override fun getItemCount(): Int {
        return totalPage
    }

    fun updatePageImage(page: Int) {
        currentPage = page
        notifyDataSetChanged()
    }
}
