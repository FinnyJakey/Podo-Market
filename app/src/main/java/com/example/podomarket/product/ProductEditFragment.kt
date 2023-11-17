package com.example.podomarket.product

import ThumbnailRecyclerViewAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.podomarket.R
import com.example.podomarket.common.CommonUtil
import com.example.podomarket.model.BoardModel
import com.example.podomarket.viewmodel.BoardViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates


class ProductEditFragment : Fragment() {
    private val boardViewModel = BoardViewModel()
    private lateinit var PictureNumTextView : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ThumbnailRecyclerViewAdapter
    private lateinit var editButton : Button
    private lateinit var loadingProgressBar: ProgressBar
    private var picturesStringList: MutableList<String> = mutableListOf()
    private var picturesFileList: MutableList<File> = mutableListOf()
    private var pictureNum by Delegates.notNull<Int>()
    private var deviceWidth by Delegates.notNull<Int>()
    private var isToastShowing = false

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

        deviceWidth = CommonUtil.getDeviceWidth(this.requireContext())

        // 판매 타입 선택 라디오 버튼
        addSelectSellTypeRadioGroup(view)

        // 판매 완료 선택 라디오 버튼
        addSelectSoldRadioGroup(view)

        // 사진 갯수 표시 텍스트 뷰
        PictureNumTextView = view.findViewById(R.id.image_num)
        recyclerView = view.findViewById(R.id.product_recyclerview)
        loadingProgressBar = view.findViewById(R.id.loading_progress_bar) // 로딩 화면 선언

        // 이미지 추가 버튼
        addImageButton(view)
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
        editButton = view.findViewById<Button>(R.id.product_edit_complete_button)
        editButton.setOnClickListener {
            val content =
                view.findViewById<EditText>(R.id.product_edit_detail_edit_text).text.toString()
            val price: Number? =
                view.findViewById<EditText>(R.id.product_edit_price_edit_text).text.toString()
                    .toIntOrNull()
            val title =
                view.findViewById<EditText>(R.id.product_edit_title_edittext).text.toString()
            showLoading()
            // 데이터 검증
            if (price == null) {
                showEditErrorToast("가격을 입력하세요")
                return@setOnClickListener
            } else if (picturesFileList.size + picturesStringList.size < 1){
                showEditErrorToast("제품 사진을 1개 이상 등록 해야 합니다.")
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

                    boardViewModel.updateBoard(BoardModel(board.uuid, content, board.createdAt, picturesStringList, price, sold, title, board.userId, board.userName),
                        picturesFileList
                    ) { isSuccess ->
                        if (isSuccess) moveDetailFragment()
                        else Toast.makeText(requireContext(), "내용 수정 실패, 내용을 다시 수정해주세요", Toast.LENGTH_SHORT).show()
                        hideLoading()
                    }
                }
            }
        }
    }

    private fun addImageButton(view: View){
        val addImageButton = view.findViewById<LinearLayout>(R.id.product_edit_add_image_button)
        addImageButton.setOnClickListener {
            // 갤러리 열기
            openGalleryForImage()
        }
    }

    // 이미지 관련
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let { intent ->
                    if (intent.clipData != null) {
                        // 여러 이미지 선택
                        val clipData = intent.clipData!!
                        val count = clipData!!.itemCount + pictureNum
                        if(count > 10){
                            Toast.makeText(requireContext(),"사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
                            for (i in 0 until 10-pictureNum) {
                                val uri = clipData.getItemAt(i).uri
                                saveImage(uri)
                            }
                        }else{
                            for (i in 0 until clipData!!.itemCount) {
                                val uri = clipData.getItemAt(i).uri
                                saveImage(uri)
                            }
                        }

                    } else if (intent.data != null) {
                        // 하나의 이미지 선택
                        val uri = intent.data!!
                        saveImage(uri)
                    }
                }
            }
        }

    private fun saveImage(uri: Uri) {
        // URI에서 실제 파일 경로 가져오기
        val selectedImageFile = createImageFile()
        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            selectedImageFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // 리스트에 파일 추가
        picturesFileList.add(selectedImageFile)
        updatePictureNum()
        adapter.notifyDataSetChanged()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // 이미지 파일 이름 생성
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp"
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* 파일명 */
            ".jpg",         /* 확장자 */
            storageDir      /* 저장 디렉토리 */
        )
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
        picturesStringList = board.pictures.toMutableList()
        adapter = ThumbnailRecyclerViewAdapter(picturesStringList,picturesFileList, itemClickListener)
        recyclerView = view.findViewById(R.id.product_recyclerview)
        if (adapter != null && adapter.itemCount > 0) {
            recyclerView.adapter = adapter
            val linearLayoutManager =
                LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = linearLayoutManager
        }
        updatePictureNum()
    }

    private val itemClickListener = object : ThumbnailRecyclerViewAdapter.OnItemClickListener {
        override fun onItemClick(position: Int, stringListSize: Int) {
            deleteImage(position,stringListSize)
        }
    }

    private fun deleteImage(position: Int, stringListSize: Int){
        if (position < stringListSize) {
            picturesStringList.removeAt(position)
        }
        else {
            picturesFileList.removeAt(position-stringListSize)
        }

        updatePictureNum()
        adapter.notifyDataSetChanged()
    }

    private fun updatePictureNum(){
        pictureNum = picturesStringList.size + picturesFileList.size
        PictureNumTextView.setText("${pictureNum}/10")
        val num = (pictureNum -5) * 79 -24
        val dp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            num.toFloat(),
            resources.displayMetrics
        )
        recyclerView.layoutParams.width = (deviceWidth + dp).toInt()
    }



    private fun showEditErrorToast(message:String){
        if (isToastShowing) {
            return
        }
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        // Toast가 사라지면 변수 초기화
        val handler = Handler()
        handler.postDelayed({
            isToastShowing = false
        }, 1000)
        isToastShowing = true
        toast.show()
    }

    private fun showLoading() {
        editButton.isClickable = false
        loadingProgressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        editButton.isClickable = true
        loadingProgressBar.visibility = View.GONE
    }
}
