package com.example.podomarket.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.podomarket.R
import com.example.podomarket.common.DraggableFragment
import com.example.podomarket.model.BoardModel
import com.example.podomarket.viewmodel.AuthViewModel
import com.example.podomarket.viewmodel.BoardViewModel
import com.google.firebase.Timestamp

// 제품 상세 페이지
class ProductDetailFragment : DraggableFragment() {
    private val boardViewModel = BoardViewModel()
    private val authViewModel = AuthViewModel()

    companion object {
        private const val ARG_BOARD_UUID = "arg_board_uuid"

        fun newInstance(boardUuid: String): ProductDetailFragment {
            val fragment = ProductDetailFragment()
            val args = Bundle()
            args.putString(ARG_BOARD_UUID, boardUuid)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)
        val boardUuid = arguments?.getString(ARG_BOARD_UUID)
        val currentUser = authViewModel.getCurrentUserUid()

        // 채팅 버튼
        moveChatFragmentButton(view)

        // 더보기 버튼, 클릭시 나오는 메뉴 구현
       val moreButton = view.findViewById<ImageView>(R.id.more_button)
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
            // 게시물 올린 유저uuid와 현재 uuid가 동일할시 수정 가능
            boardUuid?.let { uuid ->
                boardViewModel.getBoard(uuid) { board ->
                    // ProductEditFragment로 UUID를 전달하여 생성
                    val productEditFragment = ProductEditFragment.newInstance(uuid)
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.add(R.id.fragment_container, productEditFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                    productMenu.visibility = View.GONE
                    overlayView.visibility = View.GONE
                }
            }
        }

        // 뒤로가기 버튼 구현
        moveBackFragment(view)

        // Board 정보를 가져와서 UI에 표시
        boardUuid?.let { uuid ->
            boardViewModel.getBoard(uuid) { board ->
                // 상품 정보 UI에 표시
                displayBoardInfo(view, board)
                // 현재 로그인한 유저가 쓴 글이 아닐 경우
                if(currentUser != board.userId){
                    // 더보기 버튼 사라짐
                    moreButton.visibility = View.GONE
                }
            }
        }
        return view
    }

    private fun moveChatFragmentButton(view:View){
        val chatButton = view.findViewById<CardView>(R.id.chat_button)
        // 채팅 버튼 클릭시 -> 채팅방 화면 프래그먼트 실행 및 이동
        chatButton.setOnClickListener {
            val chatRoomFragment = ChatRoomFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, chatRoomFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun moveBackFragment(view:View){
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
    }

    private fun displayBoardInfo(view: View, board: BoardModel) {
        // 대표 이미지 표시
        val representativeImage = view.findViewById<ImageView>(R.id.detail_product_image)

        val firstImageUrl = board.pictures.firstOrNull()

        // Glide를 통해 이미지 업로드
        firstImageUrl?.let { imageUrl ->
            Glide.with(view.context)
                .load(imageUrl)
                .into(representativeImage)
        }

        // 상품 제목, 판매자 이름, 가격, 몇 분전 게시물인지 표시
        val titleTextView = view.findViewById<TextView>(R.id.product_detail_title)
        val userNameTextView = view.findViewById<TextView>(R.id.seller_name_2)
        val contentTextView = view.findViewById<TextView>(R.id.product_detail_content)
        val timeAgoTextView = view.findViewById<TextView>(R.id.product_detail_time)
        val priceTextView = view.findViewById<TextView>(R.id.product_detail_price)
        val timeAgo = calculateTimeAgo(board.createdAt)

        userNameTextView.text = board.userName
        titleTextView.text = board.title
        timeAgoTextView.text = timeAgo
        contentTextView.text = board.content
        priceTextView.text = "${board.price} 원"
    }

    //게시물 올린시간 계산(1시간 미만시 분, 1일 미만시 시간, 이외에는 며칠인지 표시
    private fun calculateTimeAgo(createdAt: Timestamp): String {
        val currentTime = Timestamp.now()
        val timeDifferenceMillis = currentTime.toDate().time - createdAt.toDate().time

        val minutes = (timeDifferenceMillis / (1000 * 60)).toInt()

        return when {
            minutes < 60 -> "$minutes 분 전"
            minutes < 24 * 60 -> "${minutes / 60} 시간 전"
            else -> "${minutes / (24 * 60)} 일 전"
        }
    }
}
