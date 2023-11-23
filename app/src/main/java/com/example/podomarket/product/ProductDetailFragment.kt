package com.example.podomarket.product

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.podomarket.R
import com.example.podomarket.common.CommonUtil
import com.example.podomarket.common.DraggableFragment
import com.example.podomarket.model.BoardModel
import com.example.podomarket.viewmodel.AuthViewModel
import com.example.podomarket.viewmodel.BoardViewModel
import com.example.podomarket.viewmodel.ChatViewModel
import com.google.firebase.Timestamp
import kotlin.properties.Delegates

// 제품 상세 페이지
class ProductDetailFragment : DraggableFragment() {
    private val boardViewModel = BoardViewModel()
    private val chatViewModel = ChatViewModel()
    private val authViewModel = AuthViewModel()
    private var totalPage by Delegates.notNull<Int>()
    private var currentPage by Delegates.notNull<Int>()
    private lateinit var pageRecyclerView: RecyclerView
    private lateinit var pageAdapter: ProductDisplayPageAdapter
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

        // 더보기 버튼, 클릭시 나오는 메뉴 구현
        val moreButton = view.findViewById<ImageView>(R.id.more_button)
        var overlayView = view.findViewById<View>(R.id.overlay_view)
        var productMenu = view.findViewById<CardView>(R.id.product_menu)
        var productMenuEdit = view.findViewById<TextView>(R.id.product_menu_edit)
        var productMenuDelete = view.findViewById<TextView>(R.id.product_menu_delete)
        val chatButton = view.findViewById<TextView>(R.id.chat_start_Text)

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

        // 게시물 수정 클릭 시 -> 메뉴 버튼과 바깥 레이아웃 사라지고, 게시글 수정 프래그먼트로 이동
        productMenuEdit.setOnClickListener{
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

        // 게시물 삭제 클릭 시 -> 메뉴 버튼, 레이아웃 사라지고, 게시물 리스트 프레그먼트로 이동
        productMenuDelete.setOnClickListener{
            val alertDialog = AlertDialog.Builder(this.context)
            alertDialog.setTitle("확인 메세지")
            alertDialog.setMessage("삭제 누를시 게시물이 삭제됩니다.")
            alertDialog.setPositiveButton("삭제") { dialog, which ->
                boardUuid?.let { uuid ->
                    boardViewModel.deleteBoard(uuid) { isSuccess ->
                        if (isSuccess) {
                            val transaction = parentFragmentManager.beginTransaction()

                            productMenu.visibility = View.GONE
                            overlayView.visibility = View.GONE

                            transaction.setCustomAnimations(
                                0,
                                R.anim.exit_to_right
                            )
                            transaction.remove(this@ProductDetailFragment)
                            transaction.commit()
                        } else {
                            println("삭제 실패");

                        }
                    }
                }
            }
            alertDialog.setNegativeButton("취소") { dialog, which ->
                productMenu.visibility = View.GONE
                overlayView.visibility = View.GONE
            }
            alertDialog.show()
        }

        // 뒤로가기 버튼 구현
        moveBackFragment(view)

        // Board 정보를 가져와서 UI에 표시
        boardUuid?.let { uuid ->
            boardViewModel.getBoard(uuid) { board ->
                // 상품 정보 UI에 표시
                displayBoardInfo(view, board)

                if(currentUser == null){
                    Toast.makeText(requireContext(), "유저 아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                    return@getBoard
                }

                // 현재 로그인한 유저가 쓴 글이 아닐 경우
                if(currentUser != board.userId){
                    // 더보기 버튼 사라짐
                    moreButton.visibility = View.GONE
                    chatButton.visibility = View.VISIBLE
                }
                // 채팅 버튼
                moveChatFragmentButton(view,boardUuid,currentUser,board.userId)
            }
        }
        return view
    }

    private fun moveChatFragmentButton(view:View, boardUuid: String, myUid : String, theOtherUid: String){
        val chatButton = view.findViewById<TextView>(R.id.chat_start_Text)
        // 채팅 버튼 클릭시 -> 채팅방 화면 프래그먼트 실행 및 이동
        chatButton.setOnClickListener {
            chatViewModel.createChatRoom(boardUuid, myUid, theOtherUid){
                chatRoom->
                authViewModel.getUserName(theOtherUid){
                    userName ->
                    val chatRoomFragment = ChatRoomFragment.newInstance(chatRoom.chatRoomUuid, chatRoom.boardUuid,userName)
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.add(R.id.fragment_container, chatRoomFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
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

        val viewPager: ViewPager = view.findViewById(R.id.viewPager)
        val adapter = ProductImagePagerAdapter(view.context, board.pictures)
        totalPage = board.pictures.size
        currentPage = 0
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                pageAdapter.updatePageImage(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        pageAdapter = ProductDisplayPageAdapter(board.pictures.size,currentPage)
        pageRecyclerView = view.findViewById(R.id.product_detail_page_recyclerview)
        pageRecyclerView.adapter = pageAdapter
        val linearLayoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        pageRecyclerView.layoutManager = linearLayoutManager

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
        priceTextView.text = CommonUtil.priceToString(board.price)
    }

    //게시물 올린시간 계산(1시간 미만시 분, 1일 미만시 시간, 이외에는 며칠인지 표시
    private fun calculateTimeAgo(createdAt: Timestamp): String {
        val currentTime = Timestamp.now()
        val timeDifferenceMillis = currentTime.toDate().time - createdAt.toDate().time

        val minutes = (timeDifferenceMillis / (1000 * 60)).toInt()

        return when {
            minutes < 60 -> "${minutes}분 전"
            minutes < 24 * 60 -> "${minutes / 60}시간 전"
            else -> "${minutes / (24 * 60)}일 전"
        }
    }
}
