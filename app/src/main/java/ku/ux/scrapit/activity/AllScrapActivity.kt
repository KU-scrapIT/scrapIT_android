package ku.ux.scrapit.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import ku.ux.scrapit.data.Scrap
import ku.ux.scrapit.databinding.ActivityAllScrapBinding
import ku.ux.scrapit.etc.ScrapITApplication
import ku.ux.scrapit.etc.ScrapRVAdapter

class AllScrapActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAllScrapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllScrapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val realm = Realm.getDefaultInstance()
        val list = realm.where(Scrap::class.java).findAll().toMutableList()

        binding.allScrapRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.allScrapRecyclerView.adapter = ScrapRVAdapter(list)

        binding.allScrapBackToMainBtn.setOnClickListener {
            finish()
        }
    }

}