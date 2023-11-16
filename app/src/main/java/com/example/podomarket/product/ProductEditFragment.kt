package com.example.podomarket.product

import android.app.AlertDialog
import ThumbnailRecyclerViewAdapter
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.podomarket.R
import com.example.podomarket.model.BoardModel
import com.example.podomarket.viewmodel.BoardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductEditFragment : Fragment() {
    private val boardViewModel = BoardViewModel()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ThumbnailRecyclerViewAdapter

    companion object {
        private const val ARG_BOARD_UUID = "arg_board_uuid"

        fun newInstance(boardUuid: String): ProductEditFragment {
            val fragment = ProductEditFragment()
            val args = Bundle()
            args.putString(ARG_BOARD_UUID, boardUuid)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_edit, container, false)

        // 판매 타입 선택 라디오 버튼
        addSelectSellTypeRadioGroup(view)

        // 판매 완료 선택 라디오 버튼
        addSelectSoldRadioGroup(view)


        // Board 정보 가져와서 사용
        val boardUuid = arguments?.getString(ARG_BOARD_UUID)
        boardUuid?.let { uuid ->
            boardViewModel.getBoard(uuid) { board ->
                // 나가기 버튼
                addExitIcon(view,board)
                // 상품 정보 데이터 레이아웃에 표시하는 함수
                displayBoardInfo(view, board)
                // 이미지 불러오는 함수
                setThumbnailArray(view, board)
                // 수정 완료 버튼 이벤트
                editCompleteButton(view, board)
            }
        }

        return view
    }

    private fun addExitIcon(view:View, board: BoardModel){
        val exitIcon = view.findViewById<ImageView>(R.id.exit_icon)
        exitIcon.setOnClickListener {
            if(!hasChanges(view, board)){
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
                return@setOnClickListener
            }
            val alertDialog = AlertDialog.Builder(this.context)
            alertDialog.setTitle("확인 메세지")
            alertDialog.setMessage("페이지를 벗어나면 수정 중인 내용이 사라집니다.")
            alertDialog.setPositiveButton("확인") { dialog, which ->
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
            alertDialog.setNegativeButton("취소") { dialog, which ->
            }
            alertDialog.show()

        }
    }

    private fun addSelectSellTypeRadioGroup(view:View){
        val radioGroup = view.findViewById<RadioGroup>(R.id.product_edit_radiogroup)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)
            // 선택된 버튼의 폰트 변경
            selectedRadioButton.setTypeface(null, Typeface.BOLD)
            selectedRadioButton.setTextColor(getResources().getColor(R.color.background))
            // 선택 되지 않은 나머지 버튼의 폰트 변경
            for (i in 0 until group.childCount) {
                val radioButton = group.getChildAt(i) as RadioButton
                if (radioButton.id != checkedId) {
                    radioButton.setTypeface(null, Typeface.NORMAL)
                    radioButton.setTextColor(getResources().getColor(R.color.text_01))
                }
            }

            if (checkedId == R.id.product_edit_type_checkbox_share) {
                // 가격 입력란을 0으로 설정하고 편집 불가능하게 만듦
                val priceEditText = view.findViewById<EditText>(R.id.product_edit_price_edit_text)
                priceEditText.setText("0")
                priceEditText.isEnabled = false
            } else {
                // 다른 경우에는 가격 입력란을 초기화하고 편집 가능하게 만듦
                val priceEditText = view.findViewById<EditText>(R.id.product_edit_price_edit_text)
                priceEditText.text.clear()
                priceEditText.isEnabled = true
            }
        }
    }

    private fun addSelectSoldRadioGroup(view:View){
        val radioSoldGroup = view.findViewById<RadioGroup>(R.id.product_edit_situation_radiogroup)

        radioSoldGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)
            // 선택된 버튼의 폰트 변경
            selectedRadioButton.setTypeface(null, Typeface.BOLD)
            selectedRadioButton.setTextColor(getResources().getColor(R.color.background))
            // 선택 되지 않은 나머지 버튼의 폰트 변경
            for (i in 0 until group.childCount) {
                val radioButton = group.getChildAt(i) as RadioButton
                if (radioButton.id != checkedId) {
                    radioButton.setTypeface(null, Typeface.NORMAL)
                    radioButton.setTextColor(getResources().getColor(R.color.text_01))
                }
            }
        }

    }

    private fun displayBoardInfo(view: View, board: BoardModel) {
        val titleTextView = view.findViewById<TextView>(R.id.product_edit_title_edittext)
        val priceTextView = view.findViewById<TextView>(R.id.product_edit_price_edit_text)
        val contentTextView = view.findViewById<TextView>(R.id.product_edit_detail_edit_text)
        titleTextView.text = board.title
        contentTextView.text = board.content
        priceTextView.text = "${board.price}"

        // sold상태에 따라서 버튼 클릭 상태 나타내기
        displaySoldButton(view, board)
    }

    private fun displaySoldButton(view: View, board: BoardModel){
        val radioSoldGroup = view.findViewById<RadioGroup>(R.id.product_edit_situation_radiogroup)
        val situationSellingButton = view.findViewById<RadioButton>(R.id.product_edit_situation_checkbox_selling)
        val situationSoldButton = view.findViewById<RadioButton>(R.id.product_edit_situation_checkbox_sold)

        if (board.sold) {
            // 판매완료 상태인 경우 판매완료 버튼을 선택한 상태로 변경
            radioSoldGroup.check(situationSoldButton.id)
        } else {
            // 판매중 상태인 경우 판매중 버튼을 선택한 상태로 변경
            radioSoldGroup.check(situationSellingButton.id)
        }
    }

    private fun editCompleteButton(view: View, board: BoardModel) {
        view.findViewById<Button>(R.id.product_edit_complete_button).setOnClickListener() {
            val content =
                view.findViewById<EditText>(R.id.product_edit_detail_edit_text).text.toString()
            val price: Number? =
                view.findViewById<EditText>(R.id.product_edit_price_edit_text).text.toString()
                    .toIntOrNull()
            val title =
                view.findViewById<EditText>(R.id.product_edit_title_edittext).text.toString()

            // 데이터 검증
            if (price == null) {
                Toast.makeText(requireContext(), "가격을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                //content, title 유효성 검사
                val productEditUiState = ProductEditUiState(content, title)
                if (!productEditUiState.isTitleValid()) {
                    Toast.makeText(requireContext(), "제목을 입력하세요", Toast.LENGTH_SHORT).show()
                } else if (!productEditUiState.isContentValid()) {
                    Toast.makeText(requireContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    // 라디오 그룹 버튼에 따라서 false줄지 true줄지 변경
                    val radioSoldGroup = view.findViewById<RadioGroup>(R.id.product_edit_situation_radiogroup)
                    val sold = when (radioSoldGroup.checkedRadioButtonId) {
                        R.id.product_edit_situation_checkbox_selling -> false
                        R.id.product_edit_situation_checkbox_sold -> true
                        else -> false
                    }
                    // 검증 통과 후 판매글 업로드
                    CoroutineScope(Dispatchers.Main).launch{
                        boardViewModel.updateBoard(BoardModel(board.uuid, content, board.createdAt, board.pictures, price, sold, title, board.userId, board.userName),
                            emptyList()
                        ) { isSuccess ->
                            if (isSuccess) moveDetailFragment()
                            else Toast.makeText(requireContext(), "내용 수정 실패, 내용을 다시 수정해주세요", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun moveDetailFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val productListFragment = ProductListFragment()
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, productListFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun hasChanges(view: View, board: BoardModel): Boolean{
        val content = view.findViewById<EditText>(R.id.product_edit_detail_edit_text).text.toString()
        val price: Number? =
            view.findViewById<EditText>(R.id.product_edit_price_edit_text).text.toString()
                .toIntOrNull()
        val title =
            view.findViewById<EditText>(R.id.product_edit_title_edittext).text.toString()
        return board.content != content || board.price.toDouble() != price?.toDouble() || board.title != title
    }
    private fun setThumbnailArray(view:View, board: BoardModel){
        val imageArray = board.pictures
        adapter = ThumbnailRecyclerViewAdapter(imageArray, itemClickListener)
        recyclerView = view.findViewById(R.id.product_recyclerview)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private val itemClickListener = object : ThumbnailRecyclerViewAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {

        }

    }
}
