package com.example.sketch.ui.CustomViews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.MotionEventCompat
import com.example.sketch.ui.models.PathEvent
import com.example.sketch.ui.models.SelectMode


class SketchCanvas@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener {

    private val drawPaint = Paint().apply {
        color = Color.CYAN
        strokeWidth = 20f
        style = Paint.Style.STROKE
    }

    private val erasePaint = Paint().apply{
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 30f
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val pathList = mutableListOf<PathEvent>()

    private val transformedPathList get() = transformPath(pathList)

    private var selectMode = SelectMode.DRAW

    private val scaleGestureDetector = ScaleGestureDetector(context, this)

    private val gestureListener = GestureDetectorCompat(context, this)

    var paintStrokeWidth: Float = 20f

    var paintStrokeColor: String = "#FF0000000"

    var scaleFactor = 1

    var offsetX = 0f

    var offsetY = 0f

    val translateMatrix = Matrix()

    fun transformPath(pathlist:MutableList<PathEvent>): MutableList<PathEvent>{
        translateMatrix.setTranslate(-(offsetX), -(offsetY))
        val transformedPathList = mutableListOf<PathEvent>()
        pathlist.forEach {
            transformedPathList.add(it.copy(
                path = Path().apply {
                    addPath(it.path)
                    transform(translateMatrix)
                }
            ))
        }
        return transformedPathList
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (pathEvent in transformedPathList){
            when(pathEvent.selectMode){
                SelectMode.SELECT -> {

                }
                SelectMode.DRAW -> {
                    drawPaint.strokeWidth = pathEvent.strokeWidth
                    drawPaint.color = Color.parseColor(pathEvent.strokeColor)
                    canvas?.drawPath(pathEvent.path, drawPaint)
                }
                SelectMode.ERASE -> {
                    erasePaint.strokeWidth = pathEvent.strokeWidth
                    canvas?.drawPath(pathEvent.path, erasePaint)
                }
            }
        }
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
            if (selectMode == SelectMode.DRAW || selectMode == SelectMode.ERASE){
                val action = event.actionMasked
                when(event.action){
                    MotionEvent.ACTION_DOWN->{
                        pathList.add(PathEvent(path = Path(), selectMode = selectMode, strokeWidth = paintStrokeWidth, strokeColor = paintStrokeColor))
                        pathList[pathList.size - 1].path.moveTo(event.x + offsetX , event.y + offsetY )
                        invalidate()
                    }
                    MotionEvent.ACTION_MOVE->{
                        pathList[pathList.size - 1].path.lineTo(event.x + offsetX , event.y + offsetY )
                        invalidate()
                    }
                    MotionEvent.ACTION_UP->{
                        pathList[pathList.size - 1].path.lineTo(event.x + offsetX , event.y + offsetY )
                        invalidate()
                    }
                }
            }
            else{
                scaleGestureDetector.onTouchEvent(event)
                gestureListener.onTouchEvent(event)
                invalidate()
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

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return true
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        offsetX += distanceX
        offsetY += distanceY
        Log.v("Vasi testing","scroll detected...${distanceX}...${distanceY}...${offsetX}...${offsetY}")
        return true
    }

    override fun onLongPress(e: MotionEvent) {
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return true
    }

}
