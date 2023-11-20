package ku.ux.scrapit.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ku.ux.scrapit.R
import ku.ux.scrapit.databinding.ActivityAddnewitemBinding


class AddNewItemActivity : AppCompatActivity(){

    lateinit var nickname_editText: EditText
    lateinit var nickname_textView: TextView

    lateinit var explain_editText: EditText
    lateinit var explain_textView: TextView

    private lateinit var binding : ActivityAddnewitemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddnewitemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //뒤로 가기 버튼
        binding.newScrapBackIbtn.setOnClickListener {
            finish()
        }

        //현재 글자 수 / 최대 글자 수 나타내기
        nickname_editText = findViewById(R.id.nickname_ET) // EditText의 ID
        nickname_textView = findViewById(R.id.nickname_textcount_TV)
        val nickname_maxCharCount = 15 // 최대 입력 문자 수

        nickname_editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                nickname_textView.setText("0 / "+nickname_maxCharCount)
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val input = nickname_editText.text.toString()
                nickname_textView.setText(input.length.toString() + " / "+nickname_maxCharCount)
            }
            override fun afterTextChanged(s: Editable) {}
        })

        //현재 글자 수 / 최대 글자 수 나타내기
        explain_editText = findViewById(R.id.nickname_ET) // EditText의 ID
        explain_textView = findViewById(R.id.nickname_textcount_TV)
        val explain_maxCharCount = 20 // 최대 입력 문자 수

        explain_editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val input = explain_editText.text.toString()
                explain_textView.setText(input.length.toString() + " / "+explain_maxCharCount)
            }
            override fun afterTextChanged(s: Editable) {}
        })

    }
}