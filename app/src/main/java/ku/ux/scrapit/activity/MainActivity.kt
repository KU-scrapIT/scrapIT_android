package ku.ux.scrapit.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import ku.ux.scrapit.R
import ku.ux.scrapit.data.Folder
import ku.ux.scrapit.data.IndexColor
import ku.ux.scrapit.data.Scrap
import ku.ux.scrapit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainMenuBtn.setOnClickListener {
            binding.mainDrawer.openDrawer(GravityCompat.START)
        }
        binding.drawerBackBtn.setOnClickListener {
            binding.mainDrawer.closeDrawer(GravityCompat.START)
        }
        binding.mainAddScrapBtn.setOnClickListener {
            val intent = Intent(this, AddNewItemActivity::class.java)
            startActivityForResult(intent, RESULT_OK)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RESULT_OK) {

        }
    }

}