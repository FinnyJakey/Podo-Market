package com.example.podomarket.product
import ProductRecyclerViewAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.podomarket.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_list, container, false)
        val chatIcon = view.findViewById<ImageView>(R.id.chat_icon)
        //채팅 버튼 구현
        chatIcon.setOnClickListener {
            val chatListFragment = ChatListFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, chatListFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // 판매글 추가 플로팅 버튼
        val addButton = view.findViewById<FloatingActionButton>(R.id.add_button)
        addButton.setOnClickListener {
            val productAddFragment = ProductAddFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, productAddFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        // 출력용 데이터
        val dataList: MutableList<String> = mutableListOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7")
        // 아이템 클릭 시
        val itemClickListener = object : ProductRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // 판매글 상세 화면 프래그먼트로 이동
                val productDetailFragment = ProductDetailFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    0,
                )
                transaction.add(R.id.fragment_container, productDetailFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        // 리사이클러 뷰에 아이템 추가
        val recyclerView = view.findViewById<RecyclerView>(R.id.product_recyclerview)
        val adapter = ProductRecyclerViewAdapter(dataList,itemClickListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return view
    }
}