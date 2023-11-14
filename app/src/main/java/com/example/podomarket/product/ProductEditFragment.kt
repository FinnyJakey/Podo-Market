package com.example.podomarket.product

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.podomarket.R
import com.example.podomarket.model.BoardModel
import com.example.podomarket.viewmodel.BoardViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProductEditFragment : Fragment() {
    private val boardViewModel = BoardViewModel()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_edit, container, false)
        // 나가기 버튼 구현
        val exitIcon = view.findViewById<ImageView>(R.id.exit_icon)
        exitIcon.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
        // 판매 타입 선택용 라디오 그룹 구현
        val radioGroup = view.findViewById<RadioGroup>(R.id.product_edit_radiogroup)
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
        val radioSoldGroup = view.findViewById<RadioGroup>(R.id.product_edit_situation_radiogroup)
        //판매중, 판매완료 버튼 클릭리스너 생성
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

        //Board 정보를 가져와서 각 란에 표시
        val boardUuid = arguments?.getString(ARG_BOARD_UUID)

        boardUuid?.let { uuid ->
            boardViewModel.getBoard(uuid) { board ->
                // 상품 정보 UI에 표시
                displayBoardInfo(view, board)
                editCompleteButton(view, board)

            }
        }

        return view
    }

    private fun displayBoardInfo(view: View, board: BoardModel) {
        val titleTextView = view.findViewById<TextView>(R.id.product_edit_title_edittext)
        val priceTextView = view.findViewById<TextView>(R.id.product_edit_price_edit_text)
        val contentTextView = view.findViewById<TextView>(R.id.product_edit_detail_edit_text)

        titleTextView.text = board.title
        contentTextView.text = board.content
        priceTextView.text = "${board.price} 원"
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

            //데이터 검증
            if (price == null) {
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
                    //라디오 그룹 버튼에 따라서 false줄지 true줄지 변경
                    val sold = false

                    // 검증 통과 후 판매글 업로드
                    boardViewModel.updateBoard(board.uuid, BoardModel(board.uuid, content, board.createdAt, pictures, price, sold, title, board.userId, board.userName)
                    ) { isSuccess ->
                        if (isSuccess) moveDetailFragment()
                        else Toast.makeText(
                            requireContext(),
                            "내용 수정 실패, 내용을 다시 수정해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun addIamgeButton(view: View) {
        val addImageButton = view.findViewById<LinearLayout>(R.id.product_edit_add_image_button)
        addImageButton.setOnClickListener {
            // 갤러리 열기
            openGalleryForImage()
        }

    }

    //이미지 관련
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    //이미지 관련 메서드
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
}
