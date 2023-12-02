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
import ku.ux.scrapit.etc.ScrapITApplication.Companion.generateNewIFolderId
import ku.ux.scrapit.etc.ScrapITApplication.Companion.generateNewIScrapId


class AddNewItemActivity : AppCompatActivity(){

    private var folder = Folder()
    private var scrap = Scrap()
    private var checkedColor : String = IndexColor.RED.colorCode
    private lateinit var realm: Realm
    private lateinit var binding : ActivityAddnewitemBinding
    private lateinit var parentFolder: Folder
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

            if(folderId < 0){
                saveNewFolder()
            }else if(folderId > 0){
                saveExistFolder()
            }else if (scrapId  < 0 ){
                saveNewScrap()
            }else if(scrapId > 0){
                saveExistScrap()
            }

            setResult(Activity.RESULT_OK, null) // 결과 코드와 결과 Intent 설정
            finish()
        }
        binding.newScrapBackIbtn.setOnClickListener {
            finish()
        }
    }

    private fun saveNewScrap() {
        parentFolder =
            realm.where(Folder::class.java).equalTo("folderId", intent.getIntExtra("parentFolder",0)).findFirst()!!
        scrap.description = binding.explainET.text.toString()
        scrap.nickname = binding.nicknameET.text.toString()
        scrap.color = checkedColor
        scrap.url = binding.urlET.text.toString()
        scrap.parentFolder = parentFolder
        realm.beginTransaction() //고정
        parentFolder.scrapList.add(scrap)
        realm.copyToRealmOrUpdate(scrap)
        realm.commitTransaction()
    }
    private fun saveExistScrap() {
        parentFolder =
            realm.where(Folder::class.java).equalTo("folderId", intent.getIntExtra("parentFolder",0)).findFirst()!!
        scrap.description = binding.explainET.text.toString()
        scrap.nickname = binding.nicknameET.text.toString()
        scrap.color = checkedColor
        scrap.url = binding.urlET.text.toString()
        scrap.parentFolder = parentFolder
        realm.beginTransaction() //고정
        realm.copyToRealmOrUpdate(scrap)
        realm.commitTransaction()
    }
    private fun saveNewFolder() {
        parentFolder =
            realm.where(Folder::class.java).equalTo("folderId", intent.getIntExtra("parentFolder",0)).findFirst()!!
        folder.description = binding.explainET.text.toString()
        folder.nickname = binding.nicknameET.text.toString()
        folder.color = checkedColor
        folder.parentFolder = parentFolder
        realm.beginTransaction() //고정
        parentFolder.childFolderList.add(folder)
        realm.copyToRealmOrUpdate(folder)
        realm.commitTransaction()
    }
    private fun saveExistFolder() {
        parentFolder =
            realm.where(Folder::class.java).equalTo("folderId", intent.getIntExtra("parentFolder",0)).findFirst()!!
        folder.description = binding.explainET.text.toString()
        folder.nickname = binding.nicknameET.text.toString()
        folder.color = checkedColor
        folder.parentFolder = parentFolder
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
                    //Toast.makeText(this, "현재 색상: $checkedColor", Toast.LENGTH_SHORT).show()
                } else {
                    // backgroundTintList가 null인 경우에 대한 처리
                    //Toast.makeText(this, "색상 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
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
        for (imageView in imageviews) {
            val backgroundTintList = imageView.backgroundTintList?.defaultColor

            // backgroundTintList가 null이 아닌지 확인
            backgroundTintList?.let {
                // 이미지뷰의 배경색을 16진수 문자열로 변환
                val imageViewColorString = "#" + Integer.toHexString(backgroundTintList).substring(2).toUpperCase()
//                Log.d("tintin", "imageViewColorString: $imageViewColorString")
                Log.d("tintin", "givenColor: $givenColor")
                // 주어진 색상과 이미지뷰의 배경색을 비교
                if (imageViewColorString == (givenColor)) {
//                    Log.d("tintin", "setColor: $imageViewColorString")
                    // 배경색이 주어진 색상과 일치하는 경우
                    selectThemeColor(imageView)
                }
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
        binding.titleTV.text = "새 스크랩"
        setColor(IndexColor.RED.colorCode)
        binding.urlET.setText(isUrl)
        scrap.scrapId = generateNewIScrapId(this)
    }

    private fun initNewScrap(parentFolder : Int) {

//        val realm = Realm.getDefaultInstance()
//        val parentFolder: Folder? = realm.where(Folder::class.java).equalTo("folderId", parentFolder).findFirst()
        binding.titleTV.text = "새 스크랩"
        setColor(IndexColor.RED.colorCode)
        scrap.scrapId = generateNewIScrapId(this)
//        scrap.parentFolder =
//        realm.close()
    }
    private fun initNewFolder(parentFolderId : Int) {
//        val realm = Realm.getDefaultInstance()
//        val parentFolder: Folder? = realm.where(Folder::class.java).equalTo("folderId", parentFolderId).findFirst()
        binding.titleTV.text = "새 폴더"
        setColor(IndexColor.RED.colorCode)
        folder.folderId = generateNewIFolderId(this)
//        folder.parentFolder = parentFolder
        hideUrl()
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
//            realm.beginTransaction()
//            exScrap.parentFolder?.scrapList?.add(scrap)
//            realm.commitTransaction()
            scrap.localPath = exScrap.localPath
            //각 필드에 해당 값 올려두기
            binding.nicknameET.setText(exScrap.nickname)
            binding.explainET.setText(exScrap.description)
            binding.urlET.setText(exScrap.url)
            checkedColor = exScrap.color
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
            checkedColor = existingFolder.color
            setColor(existingFolder.color)
        }
        hideUrl()
    }
    private fun selectThemeColor(imageView: View) {
        // Set the check mark
       (imageView as ImageView).setImageResource(R.drawable.check)
    }
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}


