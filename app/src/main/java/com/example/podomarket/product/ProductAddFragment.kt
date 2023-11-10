package com.example.podomarket.product

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import java.io.IOException
import java.text.SimpleDateFormat


// 판매글 추가 화면
class ProductAddFragment : Fragment() {
    private val authViewModel = AuthViewModel()
    private val boardViewModel = BoardViewModel()

    private val pictures: MutableList<File> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_add, container, false)
        // 나가기 버튼 구현
        val exitIcon = view.findViewById<ImageView>(R.id.exit_icon)
        exitIcon.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
        // 판매 타입 선택용 라디오 그룹 구현
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
        }

        // 이미지 추가 버튼
        val addImageButton = view.findViewById<LinearLayout>(R.id.product_sell_add_image_button)
        addImageButton.setOnClickListener {
            // 갤러리 열기
            openGalleryForImage()
        }

        //판매 물품 등록
        view.findViewById<Button>(R.id.product_sell_enroll).setOnClickListener {
            val content =
                view.findViewById<EditText>(R.id.product_sell_detail_edit_text).text.toString()
            val price: Number? =
                view.findViewById<EditText>(R.id.product_sell_price_edit_text).text.toString()
                    .toIntOrNull()
            val title =
                view.findViewById<EditText>(R.id.product_sell_title_edittext).text.toString()
            val userId: String? = authViewModel.getCurrentUserUid()

            //데이터 검증
            if (userId == null) {
                Toast.makeText(requireContext(), "유효하지 않은 유저입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (price == null) {
                Toast.makeText(requireContext(), "가격을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                //content, pictures, title 유효성 검사
                val productAddUiState = ProductAddUiState(content, pictures, title)
                if (!productAddUiState.isTitleValid()) {
                    Toast.makeText(requireContext(), "제목을 입력하세요", Toast.LENGTH_SHORT).show()
                } else if (!productAddUiState.isPicturesValid()) {
                    Toast.makeText(requireContext(), "사진을 첨부해주세요", Toast.LENGTH_SHORT).show()
                } else if (!productAddUiState.isContentValid()) {
                    Toast.makeText(requireContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    val createdAt = Timestamp(Date())
                    val sold = false
                    var userName = ""
                    authViewModel.getUser(userId) { email, name ->
                        userName = name

                        Toast.makeText(requireContext(), "userName: $userName", Toast.LENGTH_SHORT)

                        //검증 통과후 판매글 업로드(addBoard함수가 suspend처리되어있어 코루틴 내에서 호출해야한다)
                        CoroutineScope(Dispatchers.Main).launch {
                            boardViewModel.addBoard(
                                content,
                                createdAt,
                                pictures,
                                price,
                                sold,
                                title,
                                userId,
                                userName
                            ) { isSuceess ->
                                if (isSuceess) moveListFragment()
                                else Toast.makeText(
                                    requireContext(),
                                    "업로드 실패, 내용을 추가 또는 수정해주세요",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
        return view
    }

    private fun moveListFragment(){
        val fragmentManager = requireActivity().supportFragmentManager
        val productListFragment = ProductListFragment()
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, productListFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private val imagePickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    // URI에서 실제 파일 경로 가져오기
                    val selectedImageFile = createImageFile()
                    requireContext().contentResolver.openInputStream(uri)?.use { input ->
                        selectedImageFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    // 리스트에 파일 추가
                    pictures.add(selectedImageFile)
                }
            }
        }
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // 이미지 파일 이름 생성
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp"
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* 파일명 */
            ".jpg",         /* 확장자 */
            storageDir      /* 저장 디렉토리 */
        )
    }
}
