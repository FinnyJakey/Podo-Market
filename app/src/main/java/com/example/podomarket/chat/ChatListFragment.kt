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
import com.example.podomarket.model.ChatRoomModel
import com.example.podomarket.viewmodel.AuthViewModel
import com.example.podomarket.viewmodel.ChatViewModel

class ChatListFragment : Fragment() {
    private val chatViewModel = ChatViewModel()
    private val authviewModel = AuthViewModel()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)

        // 뒤로가기 버튼 구현
        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        //출력용 채팅 Roooms 데이터
        chatViewModel.getAllMyChatRooms { dataList ->
            // 아이템 클릭 시
            val itemClickListener = object : ChatListRecyclerViewAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val chatRoom : ChatRoomModel = dataList[position]
                    //상대방유저
                    var sendUserId = authviewModel.getCurrentUserUid()
                    var receiverUserId = chatRoom.participants[0]
                    if(sendUserId == chatRoom.participants[0]) receiverUserId = chatRoom.participants[1]

                    authviewModel.getUserName(receiverUserId){name->
                        if(name != null){
                            // 채팅방 화면으로 이동, 정보저장
                            val chatRoomFragment = ChatRoomFragment.newInstance(chatRoom.chatRoomUuid, chatRoom.boardUuid, name)
                            val transaction = parentFragmentManager.beginTransaction()
                            transaction.setCustomAnimations(
                                R.anim.enter_from_right,
                                0,
                            )
                            transaction.add(R.id.fragment_container, chatRoomFragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
                        }else{
                            println("가져오기 실패")
                        }
                    }
                }
            }
            val recyclerView = view.findViewById<RecyclerView>(R.id.chat_recyclerview)
            val adapter = ChatListRecyclerViewAdapter(dataList, itemClickListener)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this.context)

        }
        return view
    }
}
