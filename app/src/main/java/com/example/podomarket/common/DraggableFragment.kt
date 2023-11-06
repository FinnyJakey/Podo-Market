package com.example.podomarket.common

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import com.example.podomarket.R

abstract class DraggableFragment : Fragment() {
    private var initialX: Float = 0F
    private var lastX: Float = 0F
    private val screenWidth: Int by lazy {
        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getRealSize(size)
        size.x
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val draggableLayout = view.findViewById<View>(R.id.draggable_layout)

        draggableLayout.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = draggableLayout.x
                    lastX = event.rawX
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - lastX
                    val position = initialX + dx
                    if (position > 0) {
                        draggableLayout.x = initialX + dx
                    }
                }
                MotionEvent.ACTION_UP -> {
                    v.performClick()
                    if (draggableLayout.x > 0 && draggableLayout.x < 500) {
                        draggableLayout.animate()
                            .x(0F)
                            .setDuration(200)
                            .start()
                    }
                    if (draggableLayout.x >= 500) {
                        val anim = ObjectAnimator.ofFloat(draggableLayout, "x", screenWidth.toFloat())
                        anim.duration = 150
                        anim.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {}
                            override fun onAnimationEnd(animation: Animator) {
                                val fragmentManager = requireActivity().supportFragmentManager
                                fragmentManager.popBackStack()
                            }
                            override fun onAnimationCancel(animation: Animator) {}
                            override fun onAnimationRepeat(animation: Animator) {}
                        })
                        anim.start()
                    }
                }
            }
            true
        }
    }
}
