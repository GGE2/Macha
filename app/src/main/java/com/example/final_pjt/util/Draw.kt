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
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient


class Draw : View {
    var list = arrayListOf<PointWithRoomId>()
    var left = 0F
    var top = 0F
    var right = 0F
    var bottom = 0F
    val paint = Paint()
    var currentColor = Color.BLACK
    var currentWidth = 10F
    var roomId = ""
    var nowDrawer = false
    private val url = "ws://13.209.5.95:8080/stomp/game/websocket"
    private val stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
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
        if(nowDrawer){
            when(event?.action){
                MotionEvent.ACTION_DOWN -> {
                    left = event.x
                    top = event.y
                    val pointWithRoomId = PointWithRoomId(roomId, Point(event.x, event.y, false, currentColor, currentWidth))
                    list.add(pointWithRoomId)
                    val data = JSONObject()
                    data.put("roomId", roomId)
                    val point = JSONObject()
                    point.put("x", event.x)
                    point.put("y", event.y)
                    point.put("isContinue", false)
                    point.put("color", currentColor)
                    point.put("width", currentWidth)
                    data.put("point", point)
                    stompClient.send("/pub/canvas/message", data.toString()).subscribe()
                }
                MotionEvent.ACTION_MOVE -> {
                    val pointWithRoomId = PointWithRoomId(roomId, Point(event.x, event.y, true, currentColor, currentWidth))
                    list.add(pointWithRoomId)
                    val data = JSONObject()
                    data.put("roomId", roomId)
                    val point = JSONObject()
                    point.put("x", event.x)
                    point.put("y", event.y)
                    point.put("isContinue", true)
                    point.put("color", currentColor)
                    point.put("width", currentWidth)
                    data.put("point", point)
                    stompClient.send("/pub/canvas/message", data.toString()).subscribe()
                }
                MotionEvent.ACTION_UP -> {
                    right = event.x
                    bottom = event.y
                    val pointWithRoomId = PointWithRoomId(roomId, Point(event.x, event.y, true, currentColor, currentWidth))
                    list.add(pointWithRoomId)
                    val data = JSONObject()
                    data.put("roomId", roomId)
                    val point = JSONObject()
                    point.put("x", event.x)
                    point.put("y", event.y)
                    point.put("isContinue", true)
                    point.put("color", currentColor)
                    point.put("width", currentWidth)
                    data.put("point", point)
                    stompClient.send("/pub/canvas/message", data.toString()).subscribe()
                }
            }
            invalidate()
        }

        //touch이후에 event를 전달할것인가? true: 여기서 종료. false :뒤로 전달.
        // touch -> click -> longclick
        return true
    }

    fun clear(){
        list.clear()
        invalidate()
    }

    fun addPoint(pointWithRoomId: PointWithRoomId){
        list.add(pointWithRoomId)
        invalidate()
    }
}