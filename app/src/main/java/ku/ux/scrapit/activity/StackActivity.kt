package ku.ux.scrapit.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import io.realm.Realm
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
        recentFolderId = 3
        recentPosition = 1


        // 폴더 이름 데이터 생성
//        val folderNames = listOf("폴더 1", "폴더 2", "폴더 3", "폴더 4")

        // 리사이클러뷰에 어댑터 설정
        binding.stackRecyclerView.adapter = StackBtnAdapter(folderNames, folderColors)

        // 레이아웃 매니저 설정
        binding.stackRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 웹뷰 세팅
        setViewPager(urlList, 0)

//        // URL 리스트로 WebFragment를 생성하여 ViewPager에 설정
//        val pagerAdapter = object : FragmentStateAdapter(this) {
//            override fun getItemCount() = urlList.size
//
//            override fun createFragment(position: Int): Fragment {
//                return WebFragment(urlList[position])
//            }
//        }
//        // 마지막url -> 첫url 이동
//        binding.stackViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                if(position == pagerAdapter.itemCount - 1) {
//                    binding.stackViewPager.post {
//                        //binding.stackViewPager.setCurrentItem(0, false)
//                    }
//                }
//            }
//        })
//        binding.stackViewPager.adapter = pagerAdapter

        // 플로팅 버튼 이벤트 처리
        binding.stackFabBtn.setOnTouchListener { _, event ->
            Log.d("isoo", "onCreate: ${event}")
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 터치가 시작된 경우, 터치 지점과 버튼의 현재 위치를 저장
                    startX = event.rawX - binding.stackFabBtn.x
                    startY = event.rawY - binding.stackFabBtn.y
                    isMoving = false
                    time = System.currentTimeMillis()
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.d("isoo", "onCreate: move!!")
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
                    Log.d("isoo", "onCreate: up!! $isMoving")
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

        binding.stackFabRecentBtn.setOnClickListener {

        }

        // 웹뷰 업데이트
        updateViewPagerContent()


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
            // TODO: 즐겨찾기 등록 폴더 웹뷰로 이동하는 코드 추가
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
        // 마지막url -> 첫url 이동
        binding.stackViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if(position == pagerAdapter.itemCount - 1) {
                    binding.stackViewPager.post {
                        // binding.stackViewPager.setCurrentItem(0, false)
                    }
                }
            }
        })

        binding.stackViewPager.adapter = pagerAdapter

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

                setViewPager(targetFolderUrls, 0)

//                // 가져온 URL 리스트로 WebFragment를 생성하여 ViewPager에 설정
//                val pagerAdapter = object : FragmentStateAdapter(this@StackActivity) {
//                    override fun getItemCount() = targetFolderUrls.size
//
//                    override fun createFragment(position: Int): Fragment {
//                        return WebFragment(targetFolderUrls[position])
//                    }
//                }
//                // 마지막url -> 첫url 이동
//                binding.stackViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                    override fun onPageSelected(position: Int) {
//                        if(position == pagerAdapter.itemCount - 1) {
//                            binding.stackViewPager.post {
//                                //binding.stackViewPager.setCurrentItem(0, false)
//                            }
//                        }
//                    }
//                })
//
//                binding.stackViewPager.adapter = pagerAdapter
//
//                // 원하는 페이지(첫 페이지)로 이동
//                binding.stackViewPager.post {
//                    binding.stackViewPager.setCurrentItem(0, false)
//                }
            }
        })
    }
}
