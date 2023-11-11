package com.example.podomarket.product
import ProductRecyclerViewAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.podomarket.MainActivity
import com.example.podomarket.R
import com.example.podomarket.viewmodel.AuthViewModel
import com.example.podomarket.viewmodel.BoardViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductListFragment : Fragment() {
    private val boardViewModel = BoardViewModel()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_list, container, false)
        val chatIcon = view.findViewById<ImageView>(R.id.chat_icon)
        // 채팅 버튼 구현
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

        // 로그아웃 버튼
        val logoutButton = view.findViewById<ImageView>(R.id.logout_button)
        logoutButton.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.context)
            alertDialog.setTitle("로그아웃 확인")
            alertDialog.setMessage("로그아웃하시겠습니까?")
            alertDialog.setPositiveButton("로그아웃") { dialog, which ->
                (activity as MainActivity).Logout()
            }
            alertDialog.setNegativeButton("취소") { dialog, which ->
            }
            alertDialog.show()
        }


        // 리사이클러뷰 설정
        recyclerView = view.findViewById(R.id.product_recyclerview)
        adapter = ProductRecyclerViewAdapter(emptyList(), itemClickListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 게시물 목록을 가져와서 업데이트
        boardViewModel.getAllBoards()

        // 리사이클러뷰에 데이터 갱신
        boardViewModel.getBoardsLiveData().observe(viewLifecycleOwner, Observer { boards ->
            adapter.setBoards(boards)
        })
        return view
    }

    // 아이템 클릭 이벤트 처리
    private val itemClickListener = object : ProductRecyclerViewAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {
            // 판매글 상세 화면 프래그먼트로 이동
            val boardUuid = adapter.getItem(position).uuid
            val productDetailFragment = ProductDetailFragment.newInstance(boardUuid)
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
}