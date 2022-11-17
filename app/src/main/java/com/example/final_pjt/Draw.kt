package com.example.final_pjt

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

data class Point(var x:Float, var y:Float, var isContinue:Boolean, var color: Int, var width: Float)

class Draw : View {
    var list = arrayListOf<Point>()
    var left = 0F
    var top = 0F
    var right = 0F
    var bottom = 0F
    val paint = Paint()
    var currentColor = Color.BLACK
    var currentWidth = 10F
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        list.forEachIndexed { index, point ->
            if(index > 0){
                if(point.isContinue){
                    paint.color = point.color
                    paint.strokeWidth = point.width
                    canvas!!.drawLine(list[index-1].x, list[index-1].y, point.x, point.y, paint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                left = event.x
                top = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                list.add(Point(event.x, event.y, true, currentColor, currentWidth))
            }
            MotionEvent.ACTION_UP -> {
                right = event.x
                bottom = event.y
            }
        }
        invalidate()

        //touch이후에 event를 전달할것인가? true: 여기서 종료. false :뒤로 전달.
        // touch -> click -> longclick
        return true
    }

    fun clear(){
        list.clear()
        invalidate()
    }
}