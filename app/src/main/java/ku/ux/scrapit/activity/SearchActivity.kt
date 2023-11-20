package ku.ux.scrapit.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.data.IndexColor
import ku.ux.scrapit.data.Scrap
import ku.ux.scrapit.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySearchBinding

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전 버튼 클릭 이벤트 처리
        binding.searchPrevBtn.setOnClickListener {
            this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        }

        // 검색 버튼 클릭 이벤트 처리
        binding.searchSearchBtn.setOnClickListener {
            //performSearch()
        }
    }

//    private fun performSearch() {
//        val searchKeyword = binding.searchEt.text.toString()
//
//        val filteredScraps = getFilteredScraps(searchKeyword)
//
//        if(filteredScraps.isEmpty()) {
//            binding.searchNoScrapTv.visibility = View.VISIBLE
//        } else {
//            binding.searchNoScrapTv.visibility = View.GONE
//            // TODO: 검색된 스크랩 데이터들 리스트로 표시
//            val resultMessage = "검색 결과 ${filteredScraps.size}개의 스크랩이 있습니다."
//            Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show()
//        }
//    }

//    private fun getFilteredScraps(searchKeyword: String): List<Scrap> {
//        // TODO: 스크랩 리스트를 검색하여 필터링하는 로직 추가
//
//        // 더미데이터
//        val folder1 = Folder("Folder1", "Folder 1 description", IndexColor.RED, true, false, null, ArrayList(), ArrayList())
//        val folder2 = Folder("Folder2", "Folder 2 description", IndexColor.BLUE, false, false, null, ArrayList(), ArrayList())
//        val folder3 = Folder("Folder3", "Folder 3 description", IndexColor.GREEN, true, false, null, ArrayList(), ArrayList())
//
//        val scrap1 = Scrap("지피티야 도와줘", "http://example.com/1", "chatGPT", IndexColor.RED, true, false, folder1, "/path/to/file1")
//        val scrap2 = Scrap("지구오락실", "http://example.com/2", "지락실", IndexColor.BLUE, false, false, folder2, "/path/to/file2")
//        val scrap3 = Scrap("map", "http://example.com/3", "네이버지도", IndexColor.GREEN, true, false, folder3, "/path/to/file3")
//
//        folder1.scrapList.add(scrap1)
//        folder2.scrapList.add(scrap2)
//        folder3.scrapList.add(scrap3)
//
//        val dummyScraps: List<Scrap> = listOf(scrap1, scrap2, scrap3)
//
//        return dummyScraps.filter { it.nickname.contains(searchKeyword, ignoreCase = true) || it.description.contains(searchKeyword, ignoreCase = true) }
//    }
}