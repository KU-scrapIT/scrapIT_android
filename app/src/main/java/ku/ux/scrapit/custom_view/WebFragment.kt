package ku.ux.scrapit.custom_view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import ku.ux.scrapit.databinding.FragmentWebBinding

class WebFragment(private val url : String) : Fragment() {

    private lateinit var binding : FragmentWebBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWebBinding.inflate(inflater, container, false)

        binding.webView.webViewClient = MyWebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(url)

        return binding.root
    }

    private class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            // 모든 링크를 WebView 내부에서 열도록 설정
            view?.loadUrl(request?.url.toString())
            return true
        }
    }

}