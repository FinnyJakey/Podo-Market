package com.example.podomarket.product

import ChatRoomRecyclerViewAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.podomarket.R
import com.example.podomarket.chat.ExampleChat
import com.example.podomarket.common.DraggableFragment

class ChatRoomFragment : DraggableFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat_room, container, false)
        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                0,
                R.anim.exit_to_right
            )
            transaction.remove(this@ChatRoomFragment)
            transaction.commit()
        }

        val dataList: MutableList<ExampleChat> = mutableListOf()
        dataList.add(ExampleChat("안녕하세요 구매하고 싶습니다.", "me"))
        dataList.add(ExampleChat("혹시 에누리 해주실 수 있으신가요?", "me"))
        dataList.add(ExampleChat("죄송합니다 ..", "you"))
        dataList.add(ExampleChat("넵 알겠습니다.", "me"))
        val itemClickListener = object : ChatRoomRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
            }
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.chat_room_recyclerview)
        val adapter = ChatRoomRecyclerViewAdapter(dataList,itemClickListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

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

        return view
    }
}
