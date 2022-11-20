package com.example.final_pjt.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.final_pjt.dto.Point
import com.example.final_pjt.dto.PointWithRoomId


class Draw : View {
    var list = arrayListOf<PointWithRoomId>()
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
        list.forEachIndexed { index, pointWithRoomId ->
            if(index > 0){
                if(pointWithRoomId.point.isContinue){
                    paint.color = pointWithRoomId.point.color
                    paint.strokeWidth = pointWithRoomId.point.width
                    canvas!!.drawLine(list[index-1].point.x, list[index-1].point.y, pointWithRoomId.point.x, pointWithRoomId.point.y, paint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                left = event.x
                top = event.y
                list.add(PointWithRoomId("", Point(event.x, event.y, false, currentColor, currentWidth)))
            }
            MotionEvent.ACTION_MOVE -> {
                list.add(PointWithRoomId("", Point(event.x, event.y, true, currentColor, currentWidth)))
            }
            MotionEvent.ACTION_UP -> {
                right = event.x
                bottom = event.y
                list.add(PointWithRoomId("", Point(event.x, event.y, true, currentColor, currentWidth)))
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