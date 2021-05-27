package uz.android.universalvideoplayer.utils

import android.app.Activity
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import kotlin.math.abs


class GestureDetection(private val context: Activity, sgl: SimpleGestureListener) : SimpleOnGestureListener() {
    private val swipe_Min_Distance = 100
    private val swipe_Max_Distance = 350
    private val swipe_Min_Velocity = 100
    private val mode = MODE_DYNAMIC
    private val running = true
    private var tapIndicator = false
    private val detector: GestureDetector = GestureDetector(context, this)
    private val listener: SimpleGestureListener = sgl
    fun onTouchEvent(event: MotionEvent) {
        if (!running) return
        val result = detector.onTouchEvent(event)
        if (mode == MODE_SOLID) event.action = MotionEvent.ACTION_CANCEL else if (mode == MODE_DYNAMIC) {
            if (event.action == ACTION_FAKE) event.action = MotionEvent.ACTION_UP else if (result) event.action = MotionEvent.ACTION_CANCEL else if (tapIndicator) {
                event.action = MotionEvent.ACTION_DOWN
                tapIndicator = false
            }
        }
        // else just do nothing, it's Transparent
    }

    override fun onFling(
            e1: MotionEvent, e2: MotionEvent, velocityX: Float,
            velocityY: Float,
    ): Boolean {
        var velocityX = velocityX
        var velocityY = velocityY
        val xDistance = abs(e1.x - e2.x)
        val yDistance = abs(e1.y - e2.y)
        if (xDistance > swipe_Max_Distance
                || yDistance > swipe_Max_Distance) return false
        velocityX = abs(velocityX)
        velocityY = abs(velocityY)
        var result = false
        if (velocityX > swipe_Min_Velocity
                && xDistance > swipe_Min_Distance) {
            if (e1.x > e2.x) // right to left
                listener.onSwipe(SWIPE_RIGHT) else listener.onSwipe(SWIPE_LEFT)
            result = true
        } else if (velocityY > swipe_Min_Velocity
                && yDistance > swipe_Min_Distance) {
            if (e1.y > e2.y) // bottom to up
                listener.onSwipe(SWIPE_UP) else listener.onSwipe(SWIPE_DOWN)
            result = true
        }
        return result
    }

    override fun onSingleTapConfirmed(arg: MotionEvent): Boolean {
        if (mode == MODE_DYNAMIC) {
            arg.action = ACTION_FAKE
            context.dispatchTouchEvent(arg)
        }
        return false
    }

    interface SimpleGestureListener {
        fun onSwipe(direction: Int)
    }

    companion object {
        const val SWIPE_UP = 1
        const val SWIPE_DOWN = 2
        const val SWIPE_LEFT = 3
        const val SWIPE_RIGHT = 4
        const val MODE_SOLID = 1
        const val MODE_DYNAMIC = 2
        private const val ACTION_FAKE = -13 // just an unlikely number
    }

}