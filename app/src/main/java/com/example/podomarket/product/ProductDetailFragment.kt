package com.example.podomarket.product

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.podomarket.R
import com.example.podomarket.common.DraggableFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductDetailFragment : DraggableFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)
        val chatButton = view.findViewById<CardView>(R.id.chat_button)
        chatButton.setOnClickListener {
            val chatRoomFragment = ChatRoomFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, chatRoomFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        val moreButton = view.findViewById<ImageView>(R.id.more_button)
        var overlayView = view.findViewById<View>(R.id.overlay_view)
        val productMenu = view.findViewById<CardView>(R.id.product_menu)
        overlayView.setOnClickListener {
            productMenu.visibility = View.GONE
            overlayView.visibility = View.GONE
        }
        moreButton.setOnClickListener {
            productMenu.visibility = View.VISIBLE
            overlayView.visibility = View.VISIBLE
            productMenu.bringToFront()
        }
        productMenu.setOnClickListener{
            val productEditFragment = ProductEditFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, productEditFragment)
            transaction.addToBackStack(null)
            transaction.commit()
            productMenu.visibility = View.GONE
            overlayView.visibility = View.GONE
        }
        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                0,
                R.anim.exit_to_right
            )
            transaction.remove(this@ProductDetailFragment)
            transaction.commit()
        }
        return view
    }
}
