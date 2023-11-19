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
import com.example.podomarket.model.ChatModel
import com.example.podomarket.model.ChatRoomModel
import com.example.podomarket.viewmodel.AuthViewModel
import com.example.podomarket.viewmodel.ChatViewModel
import com.google.firebase.Timestamp

//최종적으로 해야하는게 이제 dataList의 타입을 List<ChatRoomModel>로 해야함 근데 이제 그걸 통해서 ChatModel의 최근 메세지도 하나 출력해줘야한다
class ChatListFragment : Fragment() {
    private val authViewModel = AuthViewModel()
    private val chatViewModel = ChatViewModel()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)

        // 뒤로가기 버튼 구현
        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        //출력용 채팅Roooms 데이터
        val userId: String? = authViewModel.getCurrentUserUid()
        val dataList:List<ChatRoomModel> = getAllMyChatRoomsEx(userId)

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
        /*
        chatViewModel.getAllMyChatRooms{dataList ->
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
        }

         */
        return view
    }

    //임시 메서드, ChatModelView변경시 코드 변경해야함
    private fun getAllMyChatRoomsEx(userId: String?): List<ChatRoomModel> {
        //더미 데이터 생성
        return listOf(
            ChatRoomModel(
                "uuid1",
                listOf(
                    ChatModel(Timestamp.now(), "첫번째 방 1번째 메세지입니다!", "JRqzhuasy1MLxYNH7YVpqfzpGl43", "허준영"),
                    ChatModel(Timestamp.now(), "첫번째 방 2번째 메세지입니다!", "1tSOyWFDXcb6uuI3LEraQPycYlS2", "김지수")
                    // 다른 채팅 메시지들도 추가해주세요.
                ),
                listOf("JRqzhuasy1MLxYNH7YVpqfzpGl43", "1tSOyWFDXcb6uuI3LEraQPycYlS2"),
                Timestamp.now()
            ),
            ChatRoomModel(
                "uuid2",
                listOf(
                    ChatModel(Timestamp.now(), "두번째 방 1번째 메세지입니다!", "1tSOyWFDXcb6uuI3LEraQPycYlS2", "김지수"),
                    ChatModel(Timestamp.now(), "두번째 방 2번째 메세지입니다!", "JRqzhuasy1MLxYNH7YVpqfzpGl43", "허준영")
                    // 다른 채팅 메시지들도 추가해주세요.
                ),
                listOf("1tSOyWFDXcb6uuI3LEraQPycYlS2", "JRqzhuasy1MLxYNH7YVpqfzpGl43"),
                Timestamp.now()
            )
            // 나머지 채팅방들도 마찬가지로 데이터를 채워주세요.
        )
    }
}
