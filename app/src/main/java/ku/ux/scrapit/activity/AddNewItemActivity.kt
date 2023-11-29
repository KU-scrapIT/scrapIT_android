package ku.ux.scrapit.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import ku.ux.scrapit.R
import ku.ux.scrapit.data.Scrap
import ku.ux.scrapit.databinding.ActivityAddnewitemBinding
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.data.IndexColor



class AddNewItemActivity : AppCompatActivity(){


    private var folder = Folder()
    private var scrap = Scrap()
    private lateinit var checkedColor : String

    private lateinit var realm: Realm

    private lateinit var binding : ActivityAddnewitemBinding


    // 이미지 선택 창
    private lateinit var imageviews: List<ImageView>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddnewitemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        realm = Realm.getDefaultInstance()

        // Initialize the list of ImageViews
        imageviews = listOf(binding.settingThemeRedIv1, binding.settingThemeOrangeIv2, binding.settingThemeYellowIv3,
            binding.settingThemeGreenIv4, binding.settingThemeBlueIv5,binding.settingThemePurpleIv6,
            binding.settingThemePinkIv7, binding.settingThemeGrayIv8, binding.settingThemeDarkGrayIv9)

        //뒤로 가기 버튼
        setInitByIntent()
        textSizeUpdate()
        colorSelect()
        //버튼 mk_scrap_comp_btn 클릭 시 완료
        binding.mkScrapCompBtn.setOnClickListener{
//            val intent = intent
            val folderId = intent.getIntExtra("folder", 0)
            val scrapId = intent.getIntExtra("scrap", 0)

            if(folderId != 0) saveFolder()
            else if (scrapId !=0 ) saveScrap()
            setResult(Activity.RESULT_OK, null) // 결과 코드와 결과 Intent 설정
            finish()
        }
        binding.newScrapBackIbtn.setOnClickListener {
            finish()
        }
    }

    private fun saveScrap() {
        scrap.description = binding.explainET.text.toString()
        scrap.nickname = binding.nicknameET.text.toString()
        scrap.color = checkedColor
        scrap.url = binding.urlET.text.toString()

        realm.beginTransaction() //고정
        realm.copyToRealmOrUpdate(scrap)
        realm.commitTransaction()
    }
    private fun saveFolder() {
        folder.description = binding.explainET.text.toString()
        folder.nickname = binding.nicknameET.text.toString()
        folder.color = checkedColor

        realm.beginTransaction() //고정
        realm.copyToRealmOrUpdate(folder)
        realm.commitTransaction()
    }
    private fun colorSelect() {
        for (imageView in imageviews) {
            imageView.setOnClickListener {
                // 클릭된 ImageView에만 체크 표시 적용
                imageView.setImageResource(R.drawable.check)

                for (otherImageView in imageviews) {
                    if (otherImageView != imageView) {
                        otherImageView.setImageDrawable(null)
                    }
                }
                val curColor: Int? = imageView.backgroundTintList?.defaultColor

                // null 체크를 수행하여 안전하게 처리
                if (curColor != null) {
                    // ColorStateList에서 현재 색상을 가져옵니다.
//                    val curColor = cur_color.toColor()
                    checkedColor = String.format("#%06X", 0xFFFFFF and curColor)
                    // Toast 메시지로 현재 색상을 표시
                    Toast.makeText(this, "현재 색상: $checkedColor", Toast.LENGTH_SHORT).show()
                } else {
                    // backgroundTintList가 null인 경우에 대한 처리
                    Toast.makeText(this, "색상 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun textSizeUpdate() {// 텍스트 개수 변화 시 나타내기
//        nickname_editText = findViewById(R.id.nickname_ET) // EditText의 ID
//        nickname_textView = findViewById(R.id.nickname_textcount_TV)
        val nicknameMaxCharCount = 15 // 최대 입력 문자 수

        binding.nicknameET.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                binding.nicknameTextcountTV.text = "0 / $nicknameMaxCharCount"
            }
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val input = binding.nicknameET.text.toString()
                binding.nicknameTextcountTV.setText(input.length.toString() + " / "+nicknameMaxCharCount)
            }
            override fun afterTextChanged(s: Editable) {}
        })

        //현재 글자 수 / 최대 글자 수 나타내기
        val explainMaxCharCount = 20 // 최대 입력 문자 수

        binding.explainET.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                binding.explainTextcountTV.text = "0 / $explainMaxCharCount"
            }
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val input = binding.explainET.text.toString()
                binding.explainTextcountTV.text = input.length.toString() + " / "+explainMaxCharCount
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setColor(givenColor: String) {
        //기본적으로 새로 만들때에는 default로 RED 설정하는데 편집 시에는 변수를 입력하여야 함.
        Toast.makeText(this, "현재 색상: $givenColor", Toast.LENGTH_SHORT).show()
        val parsedColor = Color.parseColor(givenColor)
        for (imageView in imageviews) {
            // 들어온 값에 체크 표시
            val colorDrawable = imageView.background

            if (colorDrawable is ColorDrawable) {
                val colorParsedToInt = colorDrawable.color

                if (colorParsedToInt == parsedColor) {
                    imageView.setImageResource(R.drawable.check)
                } else {
                    // 배경색이 일치하지 않으면 이미지 초기화
                    imageView.setImageDrawable(null)
                }
            } else {
                // 배경이 ColorDrawable이 아닌 경우에도 이미지 초기화
                imageView.setImageDrawable(null)
            }
        }
    }

    private fun setInitByIntent() {
        val folderId = intent.getIntExtra("folder", 0)
        val scrapId = intent.getIntExtra("scrap", 0)
        val isUrl = intent.getStringExtra("url")
        val parentFolder = intent.getIntExtra("parentFolder",0)

        if(folderId > 0){ // 이미 존재하는 folder
            initExistFolder(folderId)
        }else if(scrapId > 0){//이미 존재하는 scrap
            initExistScrap(scrapId)
        }else if(scrapId < 0){//새로운 스크랩 생성
            if(!isUrl.isNullOrEmpty()) { // URL을 가진 채로 시작
                initNewScrapWithUrl(isUrl, parentFolder)
            }else{  //  URL 없이 시작
                initNewScrap(parentFolder)
            }
        }else if(folderId < 0){ //새로운 폴더 생성
            initNewFolder(parentFolder)
        }
    }

    private fun initNewScrapWithUrl(isUrl: String, parentFolder : Int) {
//        val realm = Realm.getDefaultInstance()
        val parentFolder: Folder? = realm.where(Folder::class.java).equalTo("folderId", parentFolder).findFirst()

        binding.titleTV.text = "새 스크랩"
        setColor(IndexColor.RED.colorCode)
        binding.urlET.setText(isUrl)
        scrap.scrapId = 100 //  임시
        scrap.parentFolder = parentFolder
        realm.beginTransaction()
        parentFolder?.scrapList?.add(scrap)
        realm.commitTransaction()
//        realm.close()
    }

    private fun initNewScrap(parentFolder : Int) {

//        val realm = Realm.getDefaultInstance()
        val parentFolder: Folder? = realm.where(Folder::class.java).equalTo("folderId", parentFolder).findFirst()
        binding.titleTV.text = "새 스크랩"
        setColor(IndexColor.RED.colorCode)
        scrap.scrapId = 100 //  임시
        scrap.parentFolder = parentFolder
        realm.beginTransaction()
        parentFolder?.scrapList?.add(scrap)
        realm.commitTransaction()
//        realm.close()
    }
    private fun initNewFolder(parentFolderId : Int) {
//        val realm = Realm.getDefaultInstance()
        val parentFolder: Folder? = realm.where(Folder::class.java).equalTo("folderId", parentFolderId).findFirst()

        binding.titleTV.text = "새 폴더"
        setColor(IndexColor.RED.colorCode)
        folder.folderId = 100
        folder.parentFolder = parentFolder
        realm.beginTransaction()
        parentFolder?.childFolderList?.add(folder)
        realm.commitTransaction()
        hideUrl()
//        realm.close()
    }
    private fun hideUrl(){
        binding.urlET.visibility = View.GONE
        binding.urlTV.visibility = View.GONE
    }
    private fun initExistScrap(scrapId: Int) {

//        val realm = Realm.getDefaultInstance()
        val exScrap = realm.where(Scrap::class.java).equalTo("scrapId",scrapId).findFirst()
        binding.titleTV.text = "스크랩"
        if (exScrap != null) {
            //기본정보 미리 저장
            scrap.scrapId = exScrap.scrapId
            scrap.isFavorites = exScrap.isFavorites
            scrap.isDeleted = exScrap.isDeleted
            scrap.parentFolder = exScrap.parentFolder
            realm.beginTransaction()
            exScrap.parentFolder?.scrapList?.add(scrap)
            realm.commitTransaction()
            scrap.localPath = exScrap.localPath
            //각 필드에 해당 값 올려두기
            binding.nicknameET.setText(exScrap.nickname)
            binding.explainET.setText(exScrap.description)
            binding.urlET.setText(exScrap.url)
            setColor(exScrap.color)
        }
//        realm.close()
    }
    private fun initExistFolder(folderId: Int) {
        binding.titleTV.text = "폴더"
//        val realm = Realm.getDefaultInstance()
        val existingFolder = realm.where(Folder::class.java).equalTo("folderId",folderId).findFirst()
        if (existingFolder != null) {
            //기본정보 미리 저장
            folder.folderId = existingFolder.folderId
            folder.isFavorites = existingFolder.isFavorites
            folder.isDeleted = existingFolder.isDeleted
            folder.parentFolder = existingFolder.parentFolder
            folder.childFolderList = existingFolder.childFolderList
            folder.scrapList = existingFolder.scrapList
            //각 필드에 해당 값 올려두기
            binding.nicknameET.setText(existingFolder.nickname)
            binding.explainET.setText(existingFolder.description)
            setColor(existingFolder.color)
        }
        hideUrl()
    }
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}


