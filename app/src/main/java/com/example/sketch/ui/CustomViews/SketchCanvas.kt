package com.example.sketch.ui.CustomViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import androidx.core.view.MotionEventCompat
import com.example.sketch.ui.models.PathEvent
import com.example.sketch.ui.models.SelectMode


class SketchCanvas@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ScaleGestureDetector.OnScaleGestureListener {

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

    val scaleGestureDetector = ScaleGestureDetector(context, this)

    var paintStrokeWidth: Float = 20f

    var paintStrokeColor: String = "#FF6600"

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (pathEvent in pathList){
            when(pathEvent.selectMode){
                SelectMode.SELECT -> {

                }
                SelectMode.DRAW -> {
                    drawPaint.strokeWidth = pathEvent.strokeWidth
                    drawPaint.color = Color.parseColor(pathEvent.strokeColor)
                    canvas?.drawPath(pathEvent.path, drawPaint)
                }
                SelectMode.ERASE -> {
                    canvas?.drawPath(pathEvent.path, erasePaint)
                }
            }
        }
        canvas?.save();
        canvas?.translate(500f, 500f);
        canvas?.scale(2.0f, 2.0f)
    }

    @JvmName("setSelectMode1")
    fun setSelectMode(mode: SelectMode){
        selectMode = mode
    }

    fun setStrokeWidth(width:Float){
        paintStrokeWidth = width
    }

    fun setStrokeColor(color:String){
        paintStrokeColor = color
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {

            scaleGestureDetector.onTouchEvent(event)

            if (!scaleGestureDetector.isInProgress() && (selectMode == SelectMode.DRAW || selectMode == SelectMode.ERASE)){
                Log.v("Vasi test","event occured....${event.action}")
                val action = event.actionMasked
                when(event.action){
                    MotionEvent.ACTION_DOWN->{
                        pathList.add(PathEvent(path = Path(), selectMode = selectMode, strokeWidth = paintStrokeWidth, strokeColor = paintStrokeColor))
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
        }
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        Log.v("Vasi test","scale...${detector.scaleFactor}")
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
    }

}

//class ScaleGestureListener : SimpleOnScaleGestureListener() {
//    override fun onScale(detector: ScaleGestureDetector): Boolean {
//
//        // Don't let the object get too small or too large.
//        if (Deal.on === false) {
//            mScaleFactor *= detector.scaleFactor
//            mScaleFactor = Math.max(1f, Math.min(mScaleFactor, 20.0f))
//        }
//        invalidate()
//        return true
//    }
//}