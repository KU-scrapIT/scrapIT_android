package ku.ux.scrapit.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import ku.ux.scrapit.databinding.ActivityStackBinding

class StackActivity : AppCompatActivity() {

    private lateinit var binding : ActivityStackBinding

    private var startX: Float = 0.toFloat()
    private var startY: Float = 0.toFloat()

    private var isMoving = false
    private var isOpen = false

    // FAB 열기 전 원래 위치
    private var originalX: Float = 0.toFloat()
    private var originalY: Float = 0.toFloat()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 플로팅 버튼 이벤트 처리
        binding.stackFabBtn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 터치가 시작된 경우, 터치 지점과 버튼의 현재 위치를 저장
                    startX = event.rawX - binding.stackFabBtn.x
                    startY = event.rawY - binding.stackFabBtn.y
                    isMoving = false
                }
                MotionEvent.ACTION_MOVE -> {
                    if(!isOpen){
                        // 터치가 이동 중인 경우, 버튼을 이동시킴
                        binding.stackFabBtn.x = event.rawX - startX
                        binding.stackFabBtn.y = event.rawY - startY
                        updateConnectedViewPosition()
                        isMoving = true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    // 클릭 이벤트 처리
                    if(!isMoving) {
                        if(!isOpen) {
                            originalX = binding.stackFabBtn.x
                            originalY = binding.stackFabBtn.y
                        }
                        toggleFabContent()
                    }
                    isMoving = false
                }
            }
            true // 터치 이벤트 소비
        }

    }

    private fun updateConnectedViewPosition() {
        val fabBtnX = binding.stackFabBtn.x
        val fabBtnY = binding.stackFabBtn.y

        // 연결된 뷰들의 위치 업데이트
        binding.stackFabOpen.translationX = fabBtnX
        binding.stackFabOpen.translationY = fabBtnY
    }

    // 플로팅 버튼 열고 닫기
    private fun toggleFabContent() {
        isOpen = !isOpen

        var targetX: Float = originalX // 목표로 하는 X 위치
        var targetY: Float = originalY // 목표로 하는 Y 위치

        if (isOpen) { // 열린 상태 정의
            targetX = 0f
            updateConnectedViewPosition()
            binding.stackFabOpen.visibility = View.VISIBLE
        } else { // 닫힘 상태 정의
            binding.stackFabOpen.visibility = View.GONE

            // TODO: 즐겨찾기 등록 폴더 웹뷰로 이동하는 코드 추가
        }

        binding.stackFabBtn.animate()
            .x(targetX)
            .y(targetY)
            .setDuration(300)
            .start()
    }
}
