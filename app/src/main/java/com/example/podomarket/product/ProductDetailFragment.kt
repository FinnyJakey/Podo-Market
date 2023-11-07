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

// 제품 상세 페이지
class ProductDetailFragment : DraggableFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)
        // 채팅 버튼 구현
        val chatButton = view.findViewById<CardView>(R.id.chat_button)
        // 채팅 버튼 클릭시 -> 채팅방 화면 프래그먼트 실행 및 이동
        chatButton.setOnClickListener {
            val chatRoomFragment = ChatRoomFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, chatRoomFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        // 더보기 버튼 구현
        val moreButton = view.findViewById<ImageView>(R.id.more_button)
        // 더보기 버튼 클릭 시 나타나는 메뉴 구현용
        var overlayView = view.findViewById<View>(R.id.overlay_view)
        val productMenu = view.findViewById<CardView>(R.id.product_menu)
        // 더보기 버튼 클릭 -> 메뉴 버튼, 바깥 레이아웃을 출력
        moreButton.setOnClickListener {
            productMenu.visibility = View.VISIBLE
            overlayView.visibility = View.VISIBLE
            // 메뉴 버튼을 가장 앞으로 가져옴
            productMenu.bringToFront()
        }
        // 바깥 레이아웃을 클릭 시 -> 바깥 레이아웃과 메뉴 버튼 사라짐
        overlayView.setOnClickListener {
            productMenu.visibility = View.GONE
            overlayView.visibility = View.GONE
        }
        // 메뉴 버튼 클릭 시 -> 메뉴 버튼과 바깥 레이아웃 사라지고, 게시글 수정 프래그먼트로 이동
        productMenu.setOnClickListener{
            val productEditFragment = ProductEditFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, productEditFragment)
            transaction.addToBackStack(null)
            transaction.commit()
            productMenu.visibility = View.GONE
            overlayView.visibility = View.GONE
        }
        // 뒤로가기 버튼 구현
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
