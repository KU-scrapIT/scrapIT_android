package ku.ux.scrapit.custom_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ku.ux.scrapit.databinding.FragmentWebBinding

class WebFragment(private val url : String) : Fragment() {

    private lateinit var binding : FragmentWebBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWebBinding.inflate(inflater, container, false)

        binding.webView.loadUrl(url)

        return binding.root
    }

}