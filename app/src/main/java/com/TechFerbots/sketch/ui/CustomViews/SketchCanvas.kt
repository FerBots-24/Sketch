package com.TechFerbots.sketch.ui.CustomViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.TechFerbots.sketch.ui.interfaces.SketchCanvasEventsHandler
import com.TechFerbots.sketch.ui.models.*


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

    private var deScaledPathEventList = mutableListOf<PathEvent>()

    private val transformedPathEventList get() = transformPath(deScaledPathEventList)

    private val undonePathEventList = mutableListOf<PathEvent>()

    private var selectMode = SelectMode.DRAW

    private val scaleGestureDetector = ScaleGestureDetector(context, this)

    private val gestureListener = GestureDetectorCompat(context, this)

    private var paintStrokeWidth: Float = 20f

    private var paintStrokeColor: String = "#FF0000000"

    private var scaleFactor = 1f

    private var offsetX = 0f

    private var offsetY = 0f

    private val translateMatrix = Matrix()

    private val deTranslateMatrix = Matrix()

    private val scaleMatrix = Matrix()

    private val deScaleMatrix = Matrix()

    private var currentPath: Path? = null

    private var currentSerializablePathEvent: SerializablePathEvent? = null

    var sketchCanvasEventsHandler: SketchCanvasEventsHandler? = null

    private fun deScalePath(path: Path): Path{
        val scaledPath = Path()
        scaledPath.addPath(path)
        return scaledPath.apply {
            deScaleMatrix.setScale(1/scaleFactor,1/scaleFactor, (width.toFloat()/2), (height.toFloat()/2))
            deTranslateMatrix.setTranslate(offsetX, offsetY)
            transform(deScaleMatrix)
            transform(deTranslateMatrix)
        }
    }

    private fun transformPath(pathlist:MutableList<PathEvent>): MutableList<PathEvent>{
        translateMatrix.setTranslate(-(offsetX), -(offsetY))
        val transformedPathList = mutableListOf<PathEvent>()
        pathlist.forEach {
            transformedPathList.add(it.copy(
                path = Path().apply {
                    addPath(it.path)
                    scaleMatrix.setScale(scaleFactor,scaleFactor, (width.toFloat()/2), (height.toFloat()/2))
                    transform(translateMatrix)
                    transform(scaleMatrix)
                }
            ))
        }
        return transformedPathList
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (pathEvent in transformedPathEventList){
            when(pathEvent.selectMode){
                SelectMode.SELECT -> {

                }
                SelectMode.DRAW -> {
                    drawPaint.strokeWidth = (pathEvent.strokeWidth * scaleFactor)
                    drawPaint.color = Color.parseColor(pathEvent.strokeColor)
                    canvas?.drawPath(pathEvent.path, drawPaint)
                }
                SelectMode.ERASE -> {
                    erasePaint.strokeWidth = (pathEvent.strokeWidth * scaleFactor)
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

    fun undo(){
        deScaledPathEventList.lastOrNull()?.let {
            undonePathEventList.add(it)
            deScaledPathEventList.removeLastOrNull()
            invalidate()
        }
    }

    fun redo(){
        undonePathEventList.lastOrNull()?.let {
            deScaledPathEventList.add(it)
            undonePathEventList.removeLastOrNull()
            invalidate()
        }
    }

    fun setDeScaledPathEventList(serializablePathEventsList: SerializablePathEventsList){
        val tempDeScaledPathEventsList = mutableListOf<PathEvent>()
        serializablePathEventsList.pathEventsList.forEach { serializablePathEvent->
            val tempPath = Path()
            serializablePathEvent.path.forEach { pathUserAction ->
                when (pathUserAction.actionType){
                    PathUserActionType.MOVE_TO->{
                        tempPath.moveTo(pathUserAction.point.x, pathUserAction.point.y)
                    }
                    PathUserActionType.LINE_TO->{
                        tempPath.lineTo(pathUserAction.point.x, pathUserAction.point.y)
                    }
                }
            }
            tempDeScaledPathEventsList.add(
                PathEvent(
                    path = deScaleCustomUserPath(
                        path = tempPath,
                        customScaleFactor = serializablePathEvent.scaleFactor,
                        customOffsetX = serializablePathEvent.offsetX,
                        customOffsetY = serializablePathEvent.offsetY,
                        customWidth = serializablePathEvent.width,
                        customHeight = serializablePathEvent.height
                    ),
                    selectMode = serializablePathEvent.selectMode,
                    strokeWidth = serializablePathEvent.strokeWidth,
                    strokeColor = serializablePathEvent.strokeColor
                )
            )

        }
        deScaledPathEventList = tempDeScaledPathEventsList
        invalidate()
    }

    private fun deScaleCustomUserPath(path: Path, customScaleFactor:Float, customOffsetX:Float, customOffsetY:Float, customWidth:Int, customHeight:Int): Path{
        val scaledPath = Path()
        scaledPath.addPath(path)
        return scaledPath.apply {
            deScaleMatrix.setScale(1/customScaleFactor,1/customScaleFactor, (customWidth.toFloat()/2), (customHeight.toFloat()/2))
            deTranslateMatrix.setTranslate(customOffsetX, customOffsetY)
            transform(deScaleMatrix)
            transform(deTranslateMatrix)
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            if (selectMode == SelectMode.DRAW || selectMode == SelectMode.ERASE){
                when(event.action){
                    MotionEvent.ACTION_DOWN->{
                        undonePathEventList.clear()
                        currentPath = Path()
                        currentPath?.moveTo(event.x , event.y)
                        currentSerializablePathEvent = SerializablePathEvent(
                            path = mutableListOf(PathUserAction(point = TouchPoint(event.x, event.y), actionType = PathUserActionType.MOVE_TO)),
                            selectMode = selectMode,
                            strokeWidth = paintStrokeWidth / scaleFactor,
                            strokeColor = paintStrokeColor,
                            scaleFactor = scaleFactor,
                            offsetX = offsetX,
                            offsetY = offsetY,
                            width = width,
                            height = height
                        )
                        deScaledPathEventList.add(PathEvent(path = deScalePath(currentPath!!), selectMode = selectMode, strokeWidth = paintStrokeWidth / scaleFactor, strokeColor = paintStrokeColor) )
                        invalidate()
                    }
                    MotionEvent.ACTION_MOVE->{
                        currentPath?.lineTo(event.x , event.y)
                        currentSerializablePathEvent?.path?.add(PathUserAction(point = TouchPoint(event.x, event.y), actionType = PathUserActionType.LINE_TO))
                        deScaledPathEventList[deScaledPathEventList.size - 1] = deScaledPathEventList[deScaledPathEventList.size - 1].copy(path = deScalePath(currentPath!!))
                        invalidate()
                    }
                    MotionEvent.ACTION_UP->{
                        currentPath?.lineTo(event.x , event.y)
                        currentSerializablePathEvent?.path?.add(PathUserAction(point = TouchPoint(event.x, event.y), actionType = PathUserActionType.LINE_TO))
                        deScaledPathEventList[deScaledPathEventList.size - 1] = deScaledPathEventList[deScaledPathEventList.size - 1].copy(path = deScalePath(currentPath!!))
                        sketchCanvasEventsHandler?.addPathEventToRoom(currentSerializablePathEvent!!)
                        currentSerializablePathEvent = null
                        currentPath = null
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
        scaleFactor *= detector.scaleFactor
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
        offsetX += (distanceX/scaleFactor)
        offsetY += (distanceY/scaleFactor)
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
