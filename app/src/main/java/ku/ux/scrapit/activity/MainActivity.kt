package ku.ux.scrapit.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.PopupWindow
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import ku.ux.scrapit.R
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.data.IndexColor
import ku.ux.scrapit.data.Scrap
import ku.ux.scrapit.databinding.ActivityMainBinding
import ku.ux.scrapit.databinding.PopupKebabMenuBinding
import ku.ux.scrapit.etc.FolderRVAdapter
import ku.ux.scrapit.etc.FolderTreeRVAdapter
import ku.ux.scrapit.etc.ScrapITApplication.Companion.currentFolderId
import ku.ux.scrapit.etc.ScrapITApplication.Companion.rootFolder
import ku.ux.scrapit.etc.ScrapRVAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var currentFolder : Folder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCurrentFolder()

        binding.mainFolderTitleTv.text = currentFolder.nickname
        binding.mainKebabBtn.setOnClickListener {
            showKebabMenu()
        }
        binding.mainMenuBtn.setOnClickListener {
            binding.mainDrawer.openDrawer(GravityCompat.START)
        }
        binding.drawerBackBtn.setOnClickListener {
            binding.mainDrawer.closeDrawer(GravityCompat.START)
        }
        binding.drawerTrashBinBtn.setOnClickListener {
            val intent = Intent(this, TrashBinActivity::class.java)
            startActivity(intent)
        }
        binding.mainAddScrapBtn.setOnClickListener {
            val intent = Intent(this, AddNewItemActivity::class.java)
            intent.putExtra("scrap", -1)
            intent.putExtra("parentFolder", currentFolderId)
            intent.putExtra("folder", 0)
            intent.putExtra("url", "")
            startActivityForResult(intent, 100)
        }

        initEditModeBar()

        binding.mainScrapRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mainScrapRecyclerView.adapter = ScrapRVAdapter(currentFolder.scrapList)

        binding.mainFolderRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mainFolderRecyclerView.adapter = FolderRVAdapter(currentFolder.childFolderList)

        binding.drawerFolderTreeRv.layoutManager = LinearLayoutManager(this)
        binding.drawerFolderTreeRv.adapter = FolderTreeRVAdapter(rootFolder)
    }

    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            binding.mainScrapRecyclerView.adapter?.notifyDataSetChanged()
            binding.mainFolderRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun getCurrentFolder() {
        val realm = Realm.getDefaultInstance()
        currentFolder = realm.where(Folder::class.java).equalTo("folderId", currentFolderId).findFirst()!!
    }

    private fun showKebabMenu() {
        val popupBinding = PopupKebabMenuBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupBinding.kebabAddFolderMenu.setOnClickListener {
            val intent = Intent(this, AddNewItemActivity::class.java)
            intent.putExtra("scrap", 0)
            intent.putExtra("parentFolder", currentFolderId)
            intent.putExtra("folder", -1)
            intent.putExtra("url", "")
            startActivityForResult(intent, 100)
            popupWindow.dismiss()
        }
        popupBinding.kebabEditMenu.setOnClickListener {
            startEditMode()
            popupWindow.dismiss()
        }
        popupWindow.elevation = 50f
        popupWindow.showAsDropDown(binding.mainKebabBtn)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        endEditMode()
    }

    private fun startEditMode() {
        binding.mainMenuBtn.visibility = View.INVISIBLE
        binding.mainFolderTitleTv.visibility = View.INVISIBLE
        binding.mainSearchBtn.visibility = View.INVISIBLE
        binding.mainKebabBtn.visibility = View.INVISIBLE
        binding.mainCheckAllCb.visibility = View.VISIBLE
        binding.mainCheckAllTv.visibility = View.VISIBLE
        binding.mainEditBar.visibility = View.VISIBLE
        binding.mainAddScrapBtn.visibility = View.INVISIBLE
        (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).turnOnEditMode()
        (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).turnOnEditMode()

        binding.mainEditBtn.setOnClickListener {}
        binding.mainCheckAllCb.setOnCheckedChangeListener { _, b ->
            (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).isCheckAllItem(b)
            (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).isCheckAllItem(b)
        }
    }

    private fun endEditMode() {
        binding.mainMenuBtn.visibility = View.VISIBLE
        binding.mainFolderTitleTv.visibility = View.VISIBLE
        binding.mainSearchBtn.visibility = View.VISIBLE
        binding.mainKebabBtn.visibility = View.VISIBLE
        binding.mainCheckAllCb.visibility = View.GONE
        binding.mainCheckAllTv.visibility = View.GONE
        binding.mainEditBar.visibility = View.GONE
        binding.mainAddScrapBtn.visibility = View.VISIBLE
        (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).turnOffEditMode()
        (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).turnOffEditMode()
    }

    private fun initEditModeBar() {
        binding.mainMoveBtn.setOnClickListener {
            val scrapList = (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).getCheckedScraps()
            val folderList = (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).getCheckedFolders()
            move(scrapList, folderList)
            (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).notifyDataSetChanged()
            (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).notifyDataSetChanged()
        }
        binding.mainEditBtn.setOnClickListener {
            val scrapList = (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).getCheckedScraps()
            val folderList = (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).getCheckedFolders()
            edit(scrapList, folderList)
            (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).notifyDataSetChanged()
            (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).notifyDataSetChanged()
        }
        binding.mainFavoritesBtn.setOnClickListener {
            val scrapList = (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).getCheckedScraps()
            val folderList = (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).getCheckedFolders()
            addFavorites(scrapList, folderList)
            (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).notifyDataSetChanged()
            (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).notifyDataSetChanged()
        }
        binding.mainDeleteBtn.setOnClickListener {
            val scrapList = (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).getCheckedScraps()
            val folderList = (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).getCheckedFolders()
            delete(scrapList, folderList)
            (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).notifyDataSetChanged()
            (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).notifyDataSetChanged()
        }
    }

    private fun move(scrapList : List<Int>, folderList : List<Int>) {

    }

    private fun edit(scrapList : List<Int>, folderList : List<Int>) {
        if(scrapList.size + folderList.size > 1) return
        if(scrapList.isNotEmpty()) {
            val intent = Intent(this, AddNewItemActivity::class.java)
            intent.putExtra("scrap", scrapList[0])
            intent.putExtra("parentFolder", currentFolderId)
            intent.putExtra("folder", 0)
            intent.putExtra("url", "")
            startActivityForResult(intent, 100)
        } else if(folderList.isNotEmpty()) {
            val intent = Intent(this, AddNewItemActivity::class.java)
            intent.putExtra("scrap", 0)
            intent.putExtra("parentFolder", currentFolderId)
            intent.putExtra("folder", folderList[0])
            intent.putExtra("url", "")
            startActivityForResult(intent, 100)
        }
    }

    private fun addFavorites(scrapList : List<Int>, folderList : List<Int>) {
        val realm = Realm.getDefaultInstance()
        for(scrapId in scrapList) {
            val result = realm.where(Scrap::class.java).equalTo("scrapId", scrapId).findFirst()
            realm.beginTransaction()
            result?.isFavorites = !result?.isFavorites!!
            realm.commitTransaction()
        }
        for(folderId in folderList) {
            val result = realm.where(Folder::class.java).equalTo("folderId", folderId).findFirst()
            realm.beginTransaction()
            result?.isFavorites = !result?.isFavorites!!
            realm.commitTransaction()
        }
        realm.close()
    }

    private fun delete(scrapList : List<Int>, folderList : List<Int>) {
        val realm = Realm.getDefaultInstance()
        for(scrapId in scrapList) {
            val result = realm.where(Scrap::class.java).equalTo("scrapId", scrapId).findFirst()
            realm.beginTransaction()
            result?.isDeleted = true
            realm.commitTransaction()
        }
        for(folderId in folderList) {
            val result = realm.where(Folder::class.java).equalTo("folderId", folderId).findFirst()
            realm.beginTransaction()
            result?.isDeleted = true
            realm.commitTransaction()
        }
        realm.close()
    }

}