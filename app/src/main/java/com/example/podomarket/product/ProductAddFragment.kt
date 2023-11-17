package com.example.podomarket.product

import ThumbnailRecyclerViewAdapter
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.podomarket.R
import com.example.podomarket.viewmodel.AuthViewModel
import com.example.podomarket.viewmodel.BoardViewModel
import com.google.firebase.Timestamp
import java.io.File
import java.util.*
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.TypedValue
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.podomarket.common.CommonUtil
import java.io.IOException
import java.text.SimpleDateFormat
import kotlin.properties.Delegates


// 판매글 추가 화면
class ProductAddFragment : Fragment() {
    private val authViewModel = AuthViewModel()
    private val boardViewModel = BoardViewModel()
    private lateinit var PictureNumTextView : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ThumbnailRecyclerViewAdapter
    private var picturesFileList: MutableList<File> = mutableListOf()
    private var pictureNum by Delegates.notNull<Int>()
    private var deviceWidth by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_add, container, false)
        // 나가기 버튼 구현
        addExitButton(view)

        deviceWidth = CommonUtil.getDeviceWidth(this.requireContext()) // 디바이스 가로 계산
        PictureNumTextView = view.findViewById(R.id.image_num) // 이미지 개수 텍스트 뷰 호출
        setRecyclerAdapter(view) //

        // 판매 타입 선택 라디오 버튼
        addSelectSellTypeRadioButton(view)

        // 이미지 추가 버튼
        addImageButton(view)

        // 판매 물품 등록
        addProductButton(view)

        return view
    }

    private fun addExitButton(view:View){
        val exitIcon = view.findViewById<ImageView>(R.id.exit_icon)
        exitIcon.setOnClickListener {
            if(!hasInput(view)){
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
                return@setOnClickListener
            }
            val alertDialog = AlertDialog.Builder(this.context)
            alertDialog.setTitle("확인 메세지")
            alertDialog.setMessage("페이지를 벗어나면 작성 중인 내용이 사라집니다.")
            alertDialog.setPositiveButton("확인") { dialog, which ->
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
            alertDialog.setNegativeButton("취소") { dialog, which ->
            }
            alertDialog.show()
        }
    }

    private fun addSelectSellTypeRadioButton(view:View){
        val radioGroup = view.findViewById<RadioGroup>(R.id.product_sell_radiogroup)
        // 다른 라디오 버튼 선택 시
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
            // 판매 타입이 product_sell_type_checkbox_share이면
            if (checkedId == R.id.product_sell_type_checkbox_share) {
                // 가격 입력란을 0으로 설정하고 편집 불가능하게 만듦
                val priceEditText = view.findViewById<EditText>(R.id.product_sell_price_edit_text)
                priceEditText.setText("0")
                priceEditText.isEnabled = false
            } else {
                // 다른 경우에는 가격 입력란을 초기화하고 편집 가능하게 만듦
                val priceEditText = view.findViewById<EditText>(R.id.product_sell_price_edit_text)
                priceEditText.text.clear()
                priceEditText.isEnabled = true
            }
        }
    }

    private fun addProductButton(view:View){
        view.findViewById<Button>(R.id.product_sell_enroll).setOnClickListener {
            val content = view.findViewById<EditText>(R.id.product_sell_detail_edit_text).text.toString()
            val price: Number? = view.findViewById<EditText>(R.id.product_sell_price_edit_text).text.toString().toIntOrNull()
            val title = view.findViewById<EditText>(R.id.product_sell_title_edittext).text.toString()
            val userId: String? = authViewModel.getCurrentUserUid()

            // 데이터 검증
            if (userId == null) {
                Toast.makeText(requireContext(), "유효하지 않은 유저입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                // content, pictures, title 유효성 검사
                val productAddUiState = ProductAddUiState(content, picturesFileList, title)
                if (!productAddUiState.isPicturesValid()) {
                    Toast.makeText(requireContext(), "사진을 첨부해주세요", Toast.LENGTH_SHORT).show()
                } else if (price == null) {
                    Toast.makeText(requireContext(), "가격을 입력하세요", Toast.LENGTH_SHORT).show()
                } else if (!productAddUiState.isTitleValid()) {
                    Toast.makeText(requireContext(), "제목을 입력하세요", Toast.LENGTH_SHORT).show()
                } else if (!productAddUiState.isContentValid()) {
                    Toast.makeText(requireContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    val createdAt = Timestamp(Date())
                    boardViewModel.addBoard(content, createdAt, picturesFileList, price, title) { isSuceess ->
                        if (isSuceess) moveListFragment()
                        else Toast.makeText(requireContext(), "업로드 실패, 내용을 추가 또는 수정해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun moveListFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val productListFragment = ProductListFragment()
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, productListFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun addImageButton(view: View){
        val addImageButton = view.findViewById<LinearLayout>(R.id.product_sell_add_image_button)
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

    private fun hasInput(view: View): Boolean {
        val content = view.findViewById<EditText>(R.id.product_sell_detail_edit_text).text
        val price = view.findViewById<EditText>(R.id.product_sell_price_edit_text).text
        val title = view.findViewById<EditText>(R.id.product_sell_title_edittext).text
        return content.isNotEmpty() || price.isNotEmpty() || title.isNotEmpty()
    }

    private fun setRecyclerAdapter(view:View){
        adapter = ThumbnailRecyclerViewAdapter(emptyList(),picturesFileList, itemClickListener)
        recyclerView = view.findViewById(R.id.product_recyclerview)
        recyclerView.adapter = adapter
        val linearLayoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = linearLayoutManager

        updatePictureNum()
    }

    private val itemClickListener = object : ThumbnailRecyclerViewAdapter.OnItemClickListener {
        override fun onItemClick(position: Int, stringListSize: Int) {
            deleteImage(position,stringListSize)
        }
    }

    private fun deleteImage(position: Int, stringListSize: Int){
        picturesFileList.removeAt(position-stringListSize)
        updatePictureNum()
        adapter.notifyDataSetChanged()
    }

    private fun updatePictureNum(){
        pictureNum = picturesFileList.size
        PictureNumTextView.setText("${pictureNum}/10")
        val num = (pictureNum -5) * 79 -24
        val dp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            num.toFloat(),
            resources.displayMetrics
        )
        recyclerView.layoutParams.width = (deviceWidth + dp).toInt()
    }
}
