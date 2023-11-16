package com.example.podomarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.podomarket.product.ProductListFragment
import com.example.podomarket.viewmodel.ChatViewModel

class TestActivity : AppCompatActivity() {
    private val chatViewModel = ChatViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market)

        chatViewModel.chatCollection.addSnapshotListener { snapshot, _ ->
            for (dc in snapshot!!.documentChanges) {
                chatViewModel.getAllMyChatRooms {
                    // 다시 업데이트 가능
                }

                chatViewModel.getAllChatsFromRoom("room_uuid") {
                    // 다시 업데이트 가능
                }
            }
        }

        val productListFragment = ProductListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, productListFragment)
            .commit()
    }
}
