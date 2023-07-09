package com.TechFerbots.sketch.ui.CustomViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.MutableLiveData
import com.TechFerbots.sketch.ui.interfaces.SketchCanvasEventsHandler
import com.TechFerbots.sketch.ui.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.LocalDateTime


class SketchCanvas@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener {

    fun Any?.log(){ Log.e("sketchLog" ,this.toString() ) }

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

    private var transformedPathEventList = mutableListOf<PathEvent>()

    private val undonePathEventList = mutableListOf<PathEvent>()

    private val currentPathList = mutableListOf<PathEvent>()

    private var selectMode = SelectMode.DRAW

    private val scaleGestureDetector = ScaleGestureDetector(context, this)

    private val gestureListener = GestureDetectorCompat(context, this)

    private var paintStrokeWidth: Float = 20f

    private var paintStrokeColor: String = "#FF0000000"

    private var scaleFactor = BigDecimal(1)

    private var offsetX = mutableListOf<Pair<Float, BigDecimal>>()

    private var offsetY = mutableListOf<Pair<Float, BigDecimal>>()

    private val translateMatrix = Matrix()

    private val deTranslateMatrix = Matrix()

    private val scaleMatrix = Matrix()

    private val deScaleMatrix = Matrix()

    private var currentPath: Path? = null

    private var currentSerializablePathEvent: SerializablePathEvent? = null

    var sketchCanvasEventsHandler: SketchCanvasEventsHandler? = null

    private lateinit var pathDataComputationCoroutineScope: CoroutineScope

    private val transformPathLazyJobsFlow =  MutableSharedFlow<Job>()




    fun setCoroutineScope(coroutineScope:CoroutineScope){
        pathDataComputationCoroutineScope = coroutineScope
        pathDataComputationCoroutineScope.launch {
            transformPathLazyJobsFlow.collect {
                Log.v("Vasi testing","job start...${LocalDateTime.now()}")
                it.start()
                it.join()
                Log.v("Vasi testing","job end...${LocalDateTime.now()}")
            }
        }
    }


//    private fun deScalePath(path: Path): Path{
//        val scaledPath = Path()
//        scaledPath.addPath(path)
//        return scaledPath.apply {
//            deScaleMatrix.setScale(1/(scaleFactor.toFloat()),1/(scaleFactor.toFloat()), (width.toFloat()/2), (height.toFloat()/2))
//            deTranslateMatrix.setTranslate(offsetX.toFloat(), offsetY.toFloat())
//            transform(deScaleMatrix)
//            transform(deTranslateMatrix)
//        }
//    }


    private suspend fun transformPath(pathList:MutableList<PathEvent>){
        Log.v("Vasi testing","transform start...${LocalDateTime.now()}")
        val transformedPathList = mutableListOf<PathEvent>()
        val currentOffsetX = calculateOffset(offsetX.toList(), scaleFactor)
        val currentOffsetY = calculateOffset(offsetY.toList(), scaleFactor)
        val transformPathEventDefferedList = mutableListOf<Deferred<PathEvent>>()
        pathList.forEach {
            transformPathEventDefferedList.add(
                pathDataComputationCoroutineScope.async {
                    it.copy(
                        path = Path().apply {
                            addPath(it.path)
                            val pathOffsetX = calculateOffset(it.offsetX, scaleFactor)
                            val pathOffsetY = calculateOffset(it.offsetY, scaleFactor)
                            deScaleMatrix.setScale(scaleFactor.divide(it.scaleFactor, MathContext(10)).toFloat(),scaleFactor.divide(it.scaleFactor, MathContext(10)).toFloat(), (width.toFloat()/2), (height.toFloat()/2))
                            translateMatrix.setTranslate(((currentOffsetX.negate().add(pathOffsetX)).multiply(scaleFactor).toFloat()), ((currentOffsetY.negate().add(pathOffsetY)).multiply(scaleFactor)).toFloat())
                            transform(deScaleMatrix)
                            transform(translateMatrix)
                        }
                    )
                }
            )
        }
        transformPathEventDefferedList.forEach {
            transformedPathList.add(it.await())
        }
        transformedPathEventList =  transformedPathList
        invalidate()

        //            transformedPathList.add(it.copy(
//                path = Path().apply {
//                    addPath(it.path)
//                    val pathOffsetX = calculateOffset(it.offsetX, scaleFactor)
//                    val pathOffsetY = calculateOffset(it.offsetY, scaleFactor)
//                    "c scale...X...${scaleFactor}..".log()
//                    "p scale...X...${it.scaleFactor}..".log()
//                    "descale...X...${scaleFactor.divide(it.scaleFactor, MathContext(10)).toFloat()}..".log()
//                    "translate...X...${((currentOffsetX.negate().add(pathOffsetX)).multiply(scaleFactor).toFloat())}..".log()
//                    deScaleMatrix.setScale(scaleFactor.divide(it.scaleFactor, MathContext(10)).toFloat(),scaleFactor.divide(it.scaleFactor, MathContext(10)).toFloat(), (width.toFloat()/2), (height.toFloat()/2))
//                    translateMatrix.setTranslate(((currentOffsetX.negate().add(pathOffsetX)).multiply(scaleFactor).toFloat()), ((currentOffsetY.negate().add(pathOffsetY)).multiply(scaleFactor)).toFloat())
//                    transform(deScaleMatrix)
//                    transform(translateMatrix)
//                }
//            ))
    }

    val precision = MathContext(10)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        Log.v("Vasi testing","on draw start...${LocalDateTime.now()}")
        for (pathEvent in transformedPathEventList){
            val scaleDiff = scaleFactor.divide(pathEvent.scaleFactor, precision).toFloat()
            when(pathEvent.selectMode){
                SelectMode.SELECT -> {

                }
                SelectMode.DRAW -> {
                    drawPaint.strokeWidth = (pathEvent.strokeWidth * scaleDiff)
                    drawPaint.color = Color.parseColor(pathEvent.strokeColor)
                    canvas?.drawPath(pathEvent.path, drawPaint)
                }
                SelectMode.ERASE -> {
                    erasePaint.strokeWidth = (pathEvent.strokeWidth * scaleDiff)
                    canvas?.drawPath(pathEvent.path, erasePaint)
                }
            }
        }
        Log.v("Vasi testing","on draw end...${LocalDateTime.now()}")
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

//    fun setDeScaledPathEventList(serializablePathEventsList: SerializablePathEventsList){
//        val tempDeScaledPathEventsList = mutableListOf<PathEvent>()
//        serializablePathEventsList.pathEventsList.forEach { serializablePathEvent->
//            val tempPath = Path()
//            serializablePathEvent.path.forEach { pathUserAction ->
//                when (pathUserAction.actionType){
//                    PathUserActionType.MOVE_TO->{
//                        tempPath.moveTo(pathUserAction.point.x, pathUserAction.point.y)
//                    }
//                    PathUserActionType.LINE_TO->{
//                        tempPath.lineTo(pathUserAction.point.x, pathUserAction.point.y)
//                    }
//                }
//            }
//            tempDeScaledPathEventsList.add(
//                PathEvent(
//                    path = deScaleCustomUserPath(
//                        path = tempPath,
//                        customScaleFactor = serializablePathEvent.scaleFactor,
//                        customOffsetX = serializablePathEvent.offsetX,
//                        customOffsetY = serializablePathEvent.offsetY,
//                        customWidth = serializablePathEvent.width,
//                        customHeight = serializablePathEvent.height
//                    ),
//                    selectMode = serializablePathEvent.selectMode,
//                    strokeWidth = serializablePathEvent.strokeWidth,
//                    strokeColor = serializablePathEvent.strokeColor
//                )
//            )
//
//        }
//        deScaledPathEventList = tempDeScaledPathEventsList
//        invalidate()
//    }

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

    var prevPoint: TouchPoint? = null


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            if (selectMode == SelectMode.DRAW || selectMode == SelectMode.ERASE){
                when(event.action){
                    MotionEvent.ACTION_DOWN->{
                        undonePathEventList.clear()
                        currentPath = Path()
                        currentPath?.moveTo(event.x , event.y)
                        prevPoint = TouchPoint(event.x, event.y)
//                        currentSerializablePathEvent = SerializablePathEvent(
//                            path = mutableListOf(PathUserAction(point = TouchPoint(event.x, event.y), actionType = PathUserActionType.MOVE_TO)),
//                            selectMode = selectMode,
//                            strokeWidth = paintStrokeWidth / scaleFactor,
//                            strokeColor = paintStrokeColor,
//                            scaleFactor = scaleFactor,
//                            offsetX = offsetX,
//                            offsetY = offsetY,
//                            width = width,
//                            height = height
//                        )
                        //deScaledPathEventList.add(PathEvent(path = deScalePath(currentPath!!), selectMode = selectMode, strokeWidth = paintStrokeWidth, strokeColor = paintStrokeColor, scaleFactor = scaleFactor, offsetX = offsetX, offsetY = offsetY) )
                        currentPathList.add(PathEvent(path = currentPath!!, selectMode = selectMode, strokeWidth = paintStrokeWidth, strokeColor = paintStrokeColor, scaleFactor = scaleFactor, offsetX = offsetX.toList(), offsetY = offsetY.toList()))
                        val transformJob = pathDataComputationCoroutineScope.launch(Dispatchers.IO, start = CoroutineStart.LAZY) {
                            transformPath(currentPathList)
                        }
                        GlobalScope.launch {
                            transformPathLazyJobsFlow.emit(transformJob)
                        }
                    }
                    MotionEvent.ACTION_MOVE->{
                        Log.v("move event","move event...${currentPathList.size - 1}...${event.x}...${event.y}")

                        prevPoint?.let {
                            currentPath?.quadTo(it.x, it.y, event.x , event.y)
                        }
                        prevPoint = TouchPoint(event.x, event.y)
                        currentPathList[currentPathList.size - 1] = currentPathList[currentPathList.size - 1].copy(path = currentPath!!)
                        val transformJob = pathDataComputationCoroutineScope.launch(Dispatchers.IO, start = CoroutineStart.LAZY) {
                            transformPath(currentPathList)
                        }
                        GlobalScope.launch {
                            transformPathLazyJobsFlow.emit(transformJob)
                        }
                        //currentSerializablePathEvent?.path?.add(PathUserAction(point = TouchPoint(event.x, event.y), actionType = PathUserActionType.LINE_TO))
                        //deScaledPathEventList[deScaledPathEventList.size - 1] = deScaledPathEventList[deScaledPathEventList.size - 1].copy(path = deScalePath(currentPath!!))
                    }
                    MotionEvent.ACTION_UP->{
                        currentPath?.lineTo(event.x , event.y)
                        //currentSerializablePathEvent?.path?.add(PathUserAction(point = TouchPoint(event.x, event.y), actionType = PathUserActionType.LINE_TO))
                        //deScaledPathEventList[deScaledPathEventList.size - 1] = deScaledPathEventList[deScaledPathEventList.size - 1].copy(path = deScalePath(currentPath!!))
                        currentPathList[currentPathList.size - 1] = currentPathList[currentPathList.size - 1].copy(path = currentPath!!)
                        //sketchCanvasEventsHandler?.addPathEventToRoom(currentSerializablePathEvent!!)
                        //currentSerializablePathEvent = null
                        currentPath = null
                        val transformJob = pathDataComputationCoroutineScope.launch(Dispatchers.IO, start = CoroutineStart.LAZY) {
                            transformPath(currentPathList)
                        }
                        GlobalScope.launch {
                            transformPathLazyJobsFlow.emit(transformJob)
                        }
                    }
                }
            }
            else{
                scaleGestureDetector.onTouchEvent(event)
                gestureListener.onTouchEvent(event)
            }
        }
        return true
    }

    val coroutineExceptionHandler = CoroutineExceptionHandler{ scope, throwable->
        Log.v("Vasi testing", "${throwable.message} occured.")

    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {

        val scale = (scaleFactor.toString().length - scaleFactor.scale()) + 8
        val factor = BigDecimal(detector.scaleFactor.toDouble())
        if (scaleFactor.multiply(factor).toFloat() > 1f){
            scaleFactor = scaleFactor.multiply(factor).setScale(scale, RoundingMode.UP)
        }
        else{
            scaleFactor = scaleFactor.multiply(factor, MathContext(7))
        }
        val transformJob = pathDataComputationCoroutineScope.launch(Dispatchers.IO, start = CoroutineStart.LAZY) {
            transformPath(currentPathList)
        }
        GlobalScope.launch {
            transformPathLazyJobsFlow.emit(transformJob)
        }
        "current scale factor...${scaleFactor}".log()
        "current scale factor scale...${scaleFactor.scale()}".log()
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
        //"offset before change...dx...${distanceX/scaleFactor}...dy...${distanceY/scaleFactor}...X...${offsetX}...Y...${offsetY}".log()
        offsetX.add(Pair(distanceX,scaleFactor))
        offsetY.add(Pair(distanceY,scaleFactor))
        val transformJob = pathDataComputationCoroutineScope.launch(Dispatchers.IO, start = CoroutineStart.LAZY) {
            transformPath(currentPathList)
        }
        GlobalScope.launch {
            transformPathLazyJobsFlow.emit(transformJob)
        }
//        offsetX.add( scaleFactor.divide(BigDecimal(1/distanceX.toDouble())))
//        offsetY.add( scaleFactor.divide(BigDecimal(1/distanceY.toDouble())))
//        "current offset...X...${offsetX}...Y...${offsetY}".log()
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


    fun calculateOffset(offsetedList: List<Pair<Float, BigDecimal>>, scale: BigDecimal): BigDecimal{
        var totalOffset = BigDecimal(0)
        offsetedList.forEach {
            totalOffset = totalOffset.add( BigDecimal((it.first).toDouble()).divide(it.second, scale.scale() + 5, RoundingMode.CEILING))
        }
        return totalOffset
    }

}
