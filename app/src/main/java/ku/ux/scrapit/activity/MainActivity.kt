package ku.ux.scrapit.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.data.Scrap
import ku.ux.scrapit.databinding.ActivityMainBinding
import ku.ux.scrapit.databinding.PopupKebabMenuBinding
import ku.ux.scrapit.etc.FolderRVAdapter
import ku.ux.scrapit.etc.FolderTreeRVAdapter
import ku.ux.scrapit.etc.ItemTouchCallback
import ku.ux.scrapit.etc.ScrapITApplication.Companion.currentFolderId
import ku.ux.scrapit.etc.ScrapITApplication.Companion.favoritesFolderId
import ku.ux.scrapit.etc.ScrapITApplication.Companion.rootFolder
import ku.ux.scrapit.etc.ScrapRVAdapter
import java.nio.file.Files.delete
import java.nio.file.Files.move

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var currentFolder : Folder

    private var mode = 0

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
        binding.drawerAllScrapBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, AllScrapActivity::class.java)
            startActivity(intent)
        }
        binding.drawerFavoritesBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity::class.java)
            currentFolderId = favoritesFolderId
            startActivity(intent)
        }
        binding.drawerTrashBinBtn.setOnClickListener {
            val intent = Intent(this, TrashBinActivity::class.java)
            startActivityForResult(intent, 100)
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

        val scrapRvAdapter = ScrapRVAdapter(currentFolder.scrapList)
        binding.mainScrapRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mainScrapRecyclerView.adapter = scrapRvAdapter
        val itemTouchHelper = ItemTouchHelper(ItemTouchCallback(scrapRvAdapter))
        itemTouchHelper.attachToRecyclerView(binding.mainScrapRecyclerView)

        val folderRVAdapter = FolderRVAdapter(currentFolder.childFolderList)
        binding.mainFolderRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mainFolderRecyclerView.adapter = folderRVAdapter
        val itemTouchHelper2 = ItemTouchHelper(ItemTouchCallback(folderRVAdapter))
        itemTouchHelper2.attachToRecyclerView(binding.mainFolderRecyclerView)

        binding.drawerFolderTreeRv.layoutManager = LinearLayoutManager(this)
        binding.drawerFolderTreeRv.adapter = FolderTreeRVAdapter(rootFolder)
        (binding.drawerFolderTreeRv.adapter as FolderTreeRVAdapter).setOnItemClickedListener(object : FolderTreeRVAdapter.OnItemClickedListener {
            override fun itemClicked(folder: Folder) {
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                currentFolderId = folder.folderId
                startActivity(intent)
            }
        })

        (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).setOnClickListener(object : ScrapRVAdapter.OnClickListener {
            override fun onClick(pos: Int) {
                val intent = Intent(this@MainActivity, StackActivity::class.java)
                intent.putExtra("folderId", currentFolderId)
                intent.putExtra("index", pos)
                startActivityForResult(intent, 100)
            }
        })

        (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).setOnClickListener(object : FolderRVAdapter.OnClickListener {
            override fun onClick(folderId: Int) {
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                currentFolderId = folderId
                startActivity(intent)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            binding.mainDrawer.closeDrawer(GravityCompat.START)
            endEditMode()
            (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).updateList()
            (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).updateList()
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
        if(mode == 1)
            endEditMode()
        else
            super.onBackPressed()
    }

    private fun startEditMode() {
        mode = 1
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

        binding.mainCheckAllCb.setOnCheckedChangeListener { _, b ->
            (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).isCheckAllItem(b)
            (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).isCheckAllItem(b)
        }
    }

    private fun endEditMode() {
        mode = 0
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
            (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).updateList()
            (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).updateList()
            (binding.mainScrapRecyclerView.adapter as ScrapRVAdapter).notifyDataSetChanged()
            (binding.mainFolderRecyclerView.adapter as FolderRVAdapter).notifyDataSetChanged()
        }
    }

    private fun move(scrapList : List<Int>, folderList : List<Int>) {

    }

    private fun edit(scrapList : List<Int>, folderList : List<Int>) {
        Log.d("isoo", "edit: ${scrapList.size}, ${folderList.size}")
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
        val favoriteFolder = realm.where(Folder::class.java).equalTo("folderId", favoritesFolderId).findFirst()
        for(scrapId in scrapList) {
            val result = realm.where(Scrap::class.java).equalTo("scrapId", scrapId).findFirst()
            realm.beginTransaction()
            result?.isFavorites = !result?.isFavorites!!
            if(result.isFavorites)
                favoriteFolder?.scrapList?.add(result)
            else
                favoriteFolder?.scrapList?.remove(result)
            realm.commitTransaction()
        }
        for(folderId in folderList) {
            val result = realm.where(Folder::class.java).equalTo("folderId", folderId).findFirst()
            realm.beginTransaction()
            result?.isFavorites = !result?.isFavorites!!
            if(result.isFavorites)
                favoriteFolder?.childFolderList?.add(result)
            else
                favoriteFolder?.childFolderList?.remove(result)
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