package com.example.podomarket.product

import ChatRoomBubbleRecyclerViewAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.podomarket.R
import com.example.podomarket.chat.ExampleChat
import com.example.podomarket.common.CommonUtil
import com.example.podomarket.common.DraggableFragment
import com.example.podomarket.model.ChatModel
import com.example.podomarket.viewmodel.AuthViewModel
import com.example.podomarket.viewmodel.BoardViewModel
import com.example.podomarket.viewmodel.ChatViewModel

class ChatRoomFragment : DraggableFragment() {
    private val chatViewModel = ChatViewModel()
    private val boardViewModel = BoardViewModel()
    private val authViewModel = AuthViewModel()
    private lateinit var chatBubbleRecyclerView: RecyclerView
    private val currentUser = authViewModel.getCurrentUserUid()
    private lateinit var adapter: ChatRoomBubbleRecyclerViewAdapter
    companion object {
        private const val ARG_CHAT_UUID = "arg_chat_uuid"
        private const val ARG_BOARD_UUID = "arg_board_uuid"
        private const val ARG_RECEIVER_NAME = "arg_receiver_name"
        fun newInstance(chatUuid: String, boardUuid: String, receiverName: String): ChatRoomFragment {
            val fragment = ChatRoomFragment()
            val args = Bundle()
            args.putString(ARG_CHAT_UUID, chatUuid)
            args.putString(ARG_BOARD_UUID, boardUuid)
            args.putString(ARG_RECEIVER_NAME, receiverName)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat_room, container, false)
        // 뒤로가기 버튼
        val backButton = view.findViewById<ImageView>(R.id.back_button)
        val chatUuid = requireArguments().getString(ChatRoomFragment.ARG_CHAT_UUID)
        val boardUuid = requireArguments().getString(ChatRoomFragment.ARG_BOARD_UUID)
        val receiverName = requireArguments().getString(ChatRoomFragment.ARG_RECEIVER_NAME)

        backButton.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                0,
                R.anim.exit_to_right
            )
            transaction.remove(this@ChatRoomFragment)
            transaction.commit()
        }

        if(chatUuid == null || boardUuid == null || receiverName == null){
            Toast.makeText(requireContext(), "채팅방 정보가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }else {
            chatUuid.let { uuid ->
                chatViewModel.getAllChatsFromRoom(uuid) { chat ->
                    // 채팅방 정보 UI에 표시
                    displayChatInfo(view, chat, boardUuid, receiverName)
                }
            }
            addSendButton(view,chatUuid,boardUuid,receiverName)
        }
        return view
    }

    private fun addSendButton(view : View, chatUuid: String, boardUuid: String, receiverName: String){
        // EditText에 값이 입력되면 전송버튼 색상 변경하도록 구현
        val messageEditText = view.findViewById<EditText>(R.id.message_edittext)
        val sendButton = view.findViewById<ImageView>(R.id.send_button)
        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                val isNotEmpty = s?.isNotEmpty() == true
                sendButton.isEnabled = isNotEmpty
                sendButton.setImageResource(if (isNotEmpty) R.drawable.ic_send_active else R.drawable.ic_send_inactive)
            }
        })
        sendButton.setOnClickListener {
            chatViewModel.sendChat(chatUuid,messageEditText.text.toString()){
                isSuccess->
                if(isSuccess){
                    chatUuid.let { uuid ->
                        chatViewModel.getAllChatsFromRoom(uuid) { chat ->
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    private fun displayChatInfo(view: View, chat: List<ChatModel>, boardUuid: String, receiverName: String) {
        if(currentUser == null){
            return
        }
        val userName = view.findViewById<TextView>(R.id.user_name)
        val productStatus = view.findViewById<TextView>(R.id.chat_product_status)
        val productTitle = view.findViewById<TextView>(R.id.chat_product_title)
        val productPrice = view.findViewById<TextView>(R.id.product_price)
        val productImage = view.findViewById<ImageView>(R.id.product_image)
        boardUuid.let { uuid ->
            boardViewModel.getBoard(uuid) { board ->
                productStatus.text = if(board.sold){ "판매완료" }else{ "판매중" }
                productTitle.text = board.title
                productPrice.text = board.price.toString()
                Glide.with(view.context)
                    .load(board.pictures)
                    .into(productImage)
            }
        }
        userName.text = receiverName
        adapter = ChatRoomBubbleRecyclerViewAdapter(chat,currentUser)
        chatBubbleRecyclerView = view.findViewById(R.id.chat_room_recyclerview)
        chatBubbleRecyclerView.adapter = adapter
        chatBubbleRecyclerView.layoutManager = LinearLayoutManager(view.context)
    }

}
