package ku.ux.scrapit.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
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
import ku.ux.scrapit.etc.ScrapITApplication.Companion.currentFolderId
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
        binding.mainAddScrapBtn.setOnClickListener {
            val intent = Intent(this, AddNewItemActivity::class.java)
            intent.putExtra("scrap", -1)
            intent.putExtra("parentFolder", -1)
            intent.putExtra("folder", 0)
            intent.putExtra("url", "")
            startActivityForResult(intent, 100)
        }

        binding.mainScrapRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mainScrapRecyclerView.adapter = ScrapRVAdapter(currentFolder.scrapList)
    }

    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            binding.mainScrapRecyclerView.adapter?.notifyDataSetChanged()
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
        popupWindow.elevation = 50f
        popupWindow.showAsDropDown(binding.mainKebabBtn)
    }

}