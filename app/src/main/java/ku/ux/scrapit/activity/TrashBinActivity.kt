package ku.ux.scrapit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.data.Scrap
import ku.ux.scrapit.databinding.ActivityTrashBinBinding
import ku.ux.scrapit.etc.ScrapITApplication.Companion.currentFolderId
import ku.ux.scrapit.etc.TrashBinRVAdapter


//스크랩이랑 폴더랑 같이 배치하는 방법?
//onCreateViewHolder 에서 타입을 확인한 뒤 배치하면 될 것 같은데 폴더를 상단에, 스크랩을 하단에 배치하는 방법은?
//비우기랑 삭제랑 어떤 차이? (비우기는 폴더 비우기, 삭제는 그냥 스크랩 삭제?)

//체크된 애들 가져오는 방법?
//램 삭제 방법

class TrashBinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrashBinBinding
    private lateinit var currentFolder : Folder
//    private lateinit var realm: Realm
    private lateinit var deletedFolderList : MutableList<Folder>
    private lateinit var deletedScrapList : MutableList<Scrap>
    private lateinit var trashbinadapter: TrashBinRVAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrashBinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getCurrent()
        binding.trashbinScrapRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.trashbinScrapRecyclerView.adapter = TrashBinRVAdapter(deletedScrapList, deletedFolderList)

        binding.trashbinDeleteBtn.setOnClickListener{
            trashbinadapter.deleteChecked()
        }

        binding.trashbinClearBtn.setOnClickListener{
            //모두 선택 이후 delete
            trashbinadapter.selectAll()

            trashbinadapter.deleteChecked()

        }
        binding.trashbinRestoreBtn.setOnClickListener{

        }

        binding.trashbinBackToMainBtn.setOnClickListener{
            finish()
        }
    }

    private fun getCurrent() {
        val realm = Realm.getDefaultInstance()
        //current가 루트 폴더인거겠죠?
        deletedFolderList = realm.where(Folder::class.java).equalTo("isDeleted", true).findAll().toMutableList()
        deletedScrapList = realm.where(Scrap::class.java).equalTo("isDeleted", true).findAll().toMutableList()
    }

//    private fun retrieveDeletedItems(tempFolder: Folder, deletedFolderList: MutableList<Folder>, deletedScrapList: MutableList<Scrap>) {
//        if (tempFolder.isDeleted) {
//            deletedFolderList.add(tempFolder)
//        }
//        // 현재 폴더의 자식 폴더에 대한 처리
//        for (childFolder in tempFolder.childFolderList) {
//            retrieveDeletedItems(childFolder, deletedFolderList, deletedScrapList)
//        }
//        // 현재 폴더의 스크랩에 대한 처리
//        for (scrap in tempFolder.scrapList) {
//            if (scrap.isDeleted) {
//                deletedScrapList.add(scrap)
//            }
//        }
//    }
}
