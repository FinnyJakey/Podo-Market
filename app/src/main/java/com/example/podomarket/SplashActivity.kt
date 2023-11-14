package com.example.podomarket

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity(){
    private val SPLASH_DELAY: Long = 1000 // 1초 지연
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoImage = findViewById<ImageView>(R.id.logo_image)
        val logoLayout = findViewById<LinearLayout>(R.id.logo)
        val fadeIn: Animation = AnimationUtils.loadAnimation(this, R.anim.splash_fade_in)
        val fadeOut: Animation = AnimationUtils.loadAnimation(this, R.anim.splash_fade_out)
        val moveDown: Animation = AnimationUtils.loadAnimation(this, R.anim.splash_move_down)

        logoImage.startAnimation(moveDown)
        moveDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // 애니메이션이 시작될 때의 동작
                logoLayout.startAnimation(fadeIn)
            }

            override fun onAnimationEnd(animation: Animation?) {
                // 애니메이션이 끝난 후의 동작
                Handler().postDelayed(Runnable {
                    logoLayout.startAnimation(fadeOut)
                }, SPLASH_DELAY)
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // 애니메이션이 반복될 때의 동작
            }
        })

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // 애니메이션이 시작될 때의 동작
            }

            override fun onAnimationEnd(animation: Animation?) {
                // 애니메이션이 끝난 후의 동작
                // 예를 들어, 메인 액티비티로 전환하는 코드를 추가할 수 있습니다.
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // 애니메이션이 반복될 때의 동작
            }
        })
    }
}