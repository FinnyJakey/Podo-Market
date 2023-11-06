package com.example.podomarket.product

import ChatListRecyclerViewAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.podomarket.R

class ChatListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)

        // 뒤로가기 버튼 구현
        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        // 출력용 데이터
        val dataList: MutableList<String> = mutableListOf("User 1", "User 2", "User 3", "User 4", "User 5", "User 6", "User 7")
        // 아이템 클릭 시
        val itemClickListener = object : ChatListRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // 채팅방 화면으로 이동
                val chatRoomFragment = ChatRoomFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    0,
                )
                transaction.add(R.id.fragment_container, chatRoomFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        // 리사이클러 뷰에 아이템 추가
        val recyclerView = view.findViewById<RecyclerView>(R.id.chat_recyclerview)
        val adapter = ChatListRecyclerViewAdapter(dataList,itemClickListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return view
    }
}
