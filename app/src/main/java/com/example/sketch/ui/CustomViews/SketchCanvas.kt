package com.example.sketch.ui.CustomViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.sketch.ui.models.PathEvent
import com.example.sketch.ui.models.SelectMode

class SketchCanvas@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var clickedX = 0f
    var clickedY = 0f
    val drawPaint = Paint().apply {
        color = Color.CYAN
        strokeWidth = 20f
        style = Paint.Style.STROKE
    }
    val erasePaint = Paint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        strokeWidth = 30f
    }
    val pathList = mutableListOf<PathEvent>()

    var selectMode = SelectMode.DRAW

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (pathEvent in pathList){
            Log.v("Vasi","path event...${pathEvent.selectMode}")
            when(pathEvent.selectMode){
                SelectMode.SELECT -> {

                }
                SelectMode.DRAW -> {
                    canvas?.drawPath(pathEvent.path, drawPaint)
                }
                SelectMode.ERASE -> {
                    canvas?.drawPath(pathEvent.path, erasePaint)
                }
            }
        }
    }

    @JvmName("setSelectMode1")
    fun setSelectMode(mode: SelectMode):Unit{
        selectMode = mode
    }



    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            Log.v("Vasi","event occured....${event.action}")
            when(event.action){
                MotionEvent.ACTION_DOWN->{
                    pathList.add(PathEvent(path = Path(),selectMode = selectMode))
                    pathList[pathList.size - 1].path.moveTo(event.x, event.y)
                    invalidate()
                }
                MotionEvent.ACTION_MOVE->{
                    pathList[pathList.size - 1].path.lineTo(event.x, event.y)
                    invalidate()
                }
                MotionEvent.ACTION_UP->{
                    pathList[pathList.size - 1].path.lineTo(event.x, event.y)
                    invalidate()
                }
            }
        }
        return true
    }



}