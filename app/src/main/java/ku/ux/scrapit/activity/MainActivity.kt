package ku.ux.scrapit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ku.ux.scrapit.R
import ku.ux.scrapit.data.IndexColor
import ku.ux.scrapit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}