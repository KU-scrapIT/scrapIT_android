package ku.ux.scrapit.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import io.realm.Realm
import ku.ux.scrapit.R
import ku.ux.scrapit.custom_view.WebFragment
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.databinding.ActivityStackBinding
import ku.ux.scrapit.etc.ScrapITApplication
import ku.ux.scrapit.etc.StackBtnAdapter
import kotlin.math.log

class StackActivity : AppCompatActivity() {

    private lateinit var binding : ActivityStackBinding

    private var startX: Float = 0.toFloat()
    private var startY: Float = 0.toFloat()

    private var isMoving = false
    private var isOpen = false

    // FAB 열기 전 원래 위치
    private var originalX: Float = 0.toFloat()
    private var originalY: Float = 0.toFloat()

    private var urlList = listOf("https://www.google.com", "https://translate.google.co.kr", "https://www.google.com")
    private lateinit var folderIds : List<Int>
    private lateinit var folderNames : List<String>
    private lateinit var folderColors : List<String>

    private var recentFolderId: Int? = null
    private var recentPosition: Int? = null
    private var currentFolderId: Int? = null

    private var time = 0L

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 스택 웹뷰에 들어갈 즐겨찾기 폴더 초기화
        folderIds = getFavoriteFolders()
        folderNames = getFolderNames()
        folderColors = getFolderColors()

        // 최근폴더에 들어갈 폴더 초기화
//        나중에 수정해주기!!!
//        recentFolderId = 3
//        recentPosition = 1
//        currentFolderId = recentFolderId

        // 폴더 이름 데이터 생성
//        val folderNames = listOf("폴더 1", "폴더 2", "폴더 3", "폴더 4")

        // 웹뷰 세팅
        currentFolderId = intent.getIntExtra("folderId", -1)
        recentFolderId = currentFolderId
        recentPosition = intent.getIntExtra("index", 0)
        val realm = Realm.getDefaultInstance()
        val currentFolder = realm.where(Folder::class.java).equalTo("folderId", currentFolderId).findFirst()
        urlList = currentFolder!!.scrapList.flatMap { listOf(it.url) }
        setViewPager(urlList, 0)


        // 리사이클러뷰에 어댑터 설정
        binding.stackRecyclerView.adapter = StackBtnAdapter(folderNames, folderColors)

        // 레이아웃 매니저 설정
        binding.stackRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        // 플로팅 버튼 이벤트 처리
        binding.stackFabBtn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 터치가 시작된 경우, 터치 지점과 버튼의 현재 위치를 저장
                    startX = event.rawX - binding.stackFabBtn.x
                    startY = event.rawY - binding.stackFabBtn.y
                    isMoving = false
                    time = System.currentTimeMillis()
                }
                MotionEvent.ACTION_MOVE -> {
                    if(!isOpen){
                        // 터치가 이동 중인 경우, 버튼을 이동시킴
                        binding.stackFabBtn.x = event.rawX - startX
                        binding.stackFabBtn.y = event.rawY - startY
                        updateConnectedViewPosition()
                        isMoving = true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    // 클릭 이벤트 처리
                    if(System.currentTimeMillis() - time < 100) {
                        if(!isOpen) {
                            originalX = binding.stackFabBtn.x
                            originalY = binding.stackFabBtn.y
                        }
                        toggleFabContent()
                    }
                    isMoving = false
                }
            }
            true // 터치 이벤트 소비
        }

        // 최근 폴더 버튼 이벤트 처리
        binding.stackFabRecentBtn.setOnClickListener {
            val recentUrls = getUrls(recentFolderId!!)
            setViewPager(recentUrls, recentPosition!!)
            currentFolderId = recentFolderId
        }

        // 스크랩 추가 버튼 이벤트 처리
        binding.stackFabAddBtn.setOnClickListener {
            val currentUrl = findViewById<WebView>(R.id.web_view).url
            Log.d("cyl", "Current URL: $currentUrl")

            val intent = Intent(this, AddNewItemActivity::class.java)
            intent.putExtra("scrap", -1)
            intent.putExtra("parentFolder", currentFolderId)
            intent.putExtra("folder", 0)
            intent.putExtra("url", currentUrl)
            startActivityForResult(intent, 100)
        }

        // 나가기 버튼 이벤트 처리
        binding.stackFabOutBtn.setOnClickListener {
            finish()
        }

        // 웹뷰 업데이트
        updateViewPagerContent()


    }

    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            // 새로 업데이트된 realm을 반영해서 페이저 새로 구성
            folderIds = getFavoriteFolders()
            folderNames = getFolderNames()
            folderColors = getFolderColors()

            val currentFolderUrls = getUrls(currentFolderId ?: -1)

            setViewPager(currentFolderUrls, 0)
        }
    }

    private fun updateConnectedViewPosition() {
        // 연결된 뷰들의 위치 업데이트
        val fabBtnX = binding.stackFabBtn.x
        val fabBtnY = binding.stackFabBtn.y

        // 연결된 뷰들의 위치 업데이트
        binding.stackScrollView.translationX = fabBtnX
        binding.stackScrollView.translationY = fabBtnY
    }

    // 플로팅 버튼 열고 닫기
    private fun toggleFabContent() {
        isOpen = !isOpen

        var targetX: Float = originalX // 목표로 하는 X 위치
        var targetY: Float = originalY // 목표로 하는 Y 위치

        if (isOpen) { // 열린 상태 정의
            targetX = 20f
        } else { // 닫힘 상태 정의
            binding.stackScrollView.visibility = View.GONE
        }

        binding.stackFabBtn.animate()
            .x(targetX)
            .y(targetY)
            .setDuration(300)
            .withEndAction {
                if(isOpen) {
                    updateConnectedViewPosition()
                    binding.stackScrollView.visibility = View.VISIBLE
                }
            }
            .start()
    }

    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun getFavoriteFolders(): List<Int> {
        val realm = Realm.getDefaultInstance()
        val favoriteFolders = realm.where(Folder::class.java)
            .equalTo("isFavorites", true)
            .findAll()
        val favoriteFolderIds = mutableListOf<Int>()

        for (folder in favoriteFolders) {
            favoriteFolderIds.add(folder.folderId)
        }

        return favoriteFolderIds
    }

    fun getUrls(targetFolderId: Int): List<String> {
        val realm = Realm.getDefaultInstance()
        val targetFolder = realm.where(Folder::class.java)
            .equalTo("folderId", targetFolderId)
            .findFirst()

        if(targetFolder != null) {
            return targetFolder.scrapList.map { it.url }
        } else {
            return emptyList() // 폴더가 존재하지 않으면 빈 리스트 반환
        }

    }

    private fun getFolderNames(): List<String> {
        val realm = Realm.getDefaultInstance()
        val folderNameList = mutableListOf<String>()

        for (id in folderIds) {
            val folder = realm.where(Folder::class.java)
                .equalTo("folderId", id)
                .findFirst()

            // folderId에 해당하는 폴더가 존재하면 folderNameList에 추가
            if (folder != null) {
                folderNameList.add(folder.nickname)
            }
        }
        return folderNameList
    }

    private fun getFolderColors(): List<String> {
        val realm = Realm.getDefaultInstance()
        val folderColorList = mutableListOf<String>()

        for (id in folderIds) {
            val folder = realm.where(Folder::class.java)
                .equalTo("folderId", id)
                .findFirst()

            // folderId에 해당하는 폴더가 존재하면 folderColorList에 추가
            if (folder != null) {
                folderColorList.add(folder.color)
            }
        }
        return folderColorList
    }

    private fun setViewPager(urls: List<String>, openPos: Int) {
        // 가져온 URL 리스트로 WebFragment를 생성하여 ViewPager에 설정
        val pagerAdapter = object : FragmentStateAdapter(this@StackActivity) {
            override fun getItemCount() = urls.size

            override fun createFragment(position: Int): Fragment {
                return WebFragment(urls[position])
            }
        }
        binding.stackViewPager.adapter = pagerAdapter

        // 마지막url -> 첫url 이동
        binding.stackViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(position == pagerAdapter.itemCount - 1) {
                    binding.stackViewPager.post {
//                         binding.stackViewPager.setCurrentItem(0, false)
                    }
                }
            }
        })

        // 원하는 페이지(첫 페이지)로 이동
        binding.stackViewPager.post {
            binding.stackViewPager.setCurrentItem(openPos, false)
        }
    }

    private fun updateViewPagerContent() {
        (binding.stackRecyclerView.adapter as StackBtnAdapter).setOnItemClickListener(object : StackBtnAdapter.OnItemClickListener {
            override fun itemClicked(pos : Int) {
                val targetFolderId = folderIds[pos]
                val targetFolderUrls = getUrls(targetFolderId)

                currentFolderId = targetFolderId
                // 웹뷰 세팅
                setViewPager(targetFolderUrls, 0)
            }
        })
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}
