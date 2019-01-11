package com.a9itgroup.crossstitchart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.content_main.view.*
import java.io.*
import android.graphics.Bitmap
import android.widget.Toast


class CrossView : View, PixelLimitAdapter.PixelLimitAdapterListener {
    override fun changeColorLimit(position: Int, limit: Int) {
        val hexColor = String.format("#%06X", 0xFFFFFF and colorData[position].getColor())
        Log.d("ColorLimit", "Change $hexColor limit to "+limit.toString())
        colorData[position].setLimit(limit)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var gridSizeX = 50
    private var gridSizeY = 50
    private var penSizeX = 1
    private var penSizeY = 1
    private var pixelSizeOnScreenX = (1 / gridSizeX.toFloat())
    private var pixelSizeOnScreenY = (1 / gridSizeY.toFloat())

    private lateinit var squares: Array<Array<MySquare>>
    private lateinit var squareData: Array<Array<Int>>
    private lateinit var colorData: ArrayList<MyColor>

    private val paint = Paint()
    private val highLightPaint = Paint()
    private var currentColor: Int = Color.parseColor("#C0C0C0")

    private var rectIndex = Pair(0, 0)
    private var touching: Boolean = false
    private var aPixelClicked = false

    private var squarePressListener: SquarePressedListener? = null

    fun getGridSize():Pair<Int,Int>{
        return Pair(gridSizeX, gridSizeY)
    }

    fun getPenSize():Pair<Int,Int>{
        return Pair(penSizeX,penSizeY)
    }

    fun changePenSize(x: Int, y: Int){
        penSizeX = if (x>gridSizeX) 1 else x
        penSizeY = if (y>gridSizeY) 1 else y
    }

    fun changeGridSize(x: Int, y: Int){
        gridSizeX = x
        gridSizeY = y
        pixelSizeOnScreenX = (1 / gridSizeX.toFloat())
        pixelSizeOnScreenY = (1 / gridSizeY.toFloat())

        if (gridSizeX<penSizeX) penSizeX = 1
        if (gridSizeY<penSizeY) penSizeY = 1

        initializePixelSquares()
        initializeColorData()

        Log.d("GridSize","Change to "+x+"x"+y)
    }

    interface SquarePressedListener {
        fun onSquarePressed(i: Int, j: Int)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        crossView.isDrawingCacheEnabled = true
        init()
    }

    @SuppressLint("PrivateResource")
    private fun init(){
        paint.color = Color.BLACK//ContextCompat.getColor(context, R.color.colorPrimary)
        paint.isAntiAlias = true
        //paint.style = Paint.Style.STROKE
        paint.strokeWidth = resources.displayMetrics.density //* 5//line width

        highLightPaint.color = ContextCompat.getColor(context, R.color.ripple_material_light)
        highLightPaint.style = Paint.Style.FILL
        highLightPaint.isAntiAlias = true

        initializePixelSquares()
        initializeColorData()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawPixelStates(canvas)
        paint.color=Color.BLACK
        drawVerticalLines(canvas)
        drawHorizontalLines(canvas)

        if (aPixelClicked) {
            drawHighlightRectangle()
            aPixelClicked = false
        }
    }

    private fun drawPixelStates(canvas: Canvas) {
        for ((i, textArray) in squareData.withIndex()) {
            for ((j, color) in textArray.withIndex()) {
                if (color!=Color.WHITE) {
                    addColorToRectangle(canvas,squares[i][j],color)
                }
            }
        }
    }

    private fun drawVerticalLines(canvas: Canvas) {
        for (i in 1 until gridSizeX){
            canvas.drawLine(width * (i * pixelSizeOnScreenX), 0f, width * (i * pixelSizeOnScreenX), height.toFloat(), paint)
        }
    }

    private fun drawHorizontalLines(canvas: Canvas) {
        for (i in 1 until gridSizeY){
            canvas.drawLine(0f, height * (i * pixelSizeOnScreenY), width.toFloat(), height * (i * pixelSizeOnScreenY), paint)
        }
    }

    private fun initializePixelSquares() {
        squares = Array(gridSizeX) { Array(gridSizeY) { MySquare() } }
        squareData = Array(gridSizeX) { Array(gridSizeY) { Color.WHITE } }

        val xUnit = (width * pixelSizeOnScreenX) // one unit on x-axis
        val yUnit = (height * pixelSizeOnScreenY) // one unit on y-axis

        for (j in 0 until gridSizeY) {
            for (i in 0 until gridSizeX) {
                squares[i][j] = MySquare(i * xUnit, j * yUnit, (i + 1) * xUnit, (j + 1) * yUnit)
            }
        }
    }

    private fun initializeColorData(){
        val myColorList = arrayOf(
            "#C0C0C0","#808080","#000000","#FF0000",
            "#800000","#FFFF00","#808000","#00FF00",
            "#008000","#00FFFF","#008080","#0000FF",
            "#000080","#FF00FF","#800080","#FFA500")
        //SILVER //GRAY //BLACK //RED
        //MAROON //YELLOW //OLIVE //LIME
        //GREEN //AQUA //TEAL //BLUE
        // NAVY //FUCHSIA //PURPLE //ORANGE
        colorData = ArrayList()
        for (colorStr in myColorList){
            val color = MyColor(Color.parseColor(colorStr))
            colorData.add(color)
        }
    }

    private fun addColorToRectangle(canvas: Canvas, mySquare: MySquare, color: Int) {
        paint.color = color
        canvas.drawRect(mySquare.left,mySquare.top,mySquare.right,mySquare.bottom, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                rectIndex = getRectIndexesFor(x, y)
//                Log.d("Pair", rectIndex.first.toString()+" "+ rectIndex.second.toString()+" "+x+" "+y)
                touching = true
//                invalidate(squares[rectIndex.first][rectIndex.second])
            }
            MotionEvent.ACTION_UP -> {
                touching = false
//                invalidate(squares[rectIndex.first][rectIndex.second])
                crossView.invalidate()
                val (finalX1, finalY1) = getRectIndexesFor(x, y)
                if ((finalX1 == rectIndex.first) && (finalY1 == rectIndex.second)) { // if initial touch and final touch is in same rectangle or not
                    squarePressListener?.onSquarePressed(rectIndex.first, rectIndex.second)
                    aPixelClicked = true
                }
            }
        }
        return true
    }

    private fun getRectIndexesFor(x: Float, y: Float): Pair<Int, Int> {
        for ((i, rects) in squares.withIndex()){
            for ((j,rect) in rects.withIndex()){
//                Log.d("Pair",i.toString()+" "+j.toString()+" "+ squares[i][j].left.toString()+" "+ squares[i][j].top.toString()+" "+ squares[i][j].right.toString()+" "+ squares[i][j].bottom.toString())
                if (rect.left<x&&rect.top<y&&rect.right>x&&rect.bottom>y)
//                    Log.d("Pair",i.toString()+" "+j.toString()+" "+x.toString()+" "+y.toString())
                return Pair(i, j)
            }
        }

//        Log.d("Pair","-1 -1")
        return Pair(-1, -1) // x, y do not lie in our view
    }

    private fun isInsideGrid(x: Int, y: Int): Boolean = (x>-1) && (x<gridSizeY) && (y>-1) && (y<gridSizeX)

    private fun drawHighlightRectangle() {
        val centerPenX: Int = penSizeX / 2
        val centerPenY: Int = penSizeY / 2
        var countInside = 0

        for (i in 0 until penSizeX){
            var lineTmp=""
            for (j in 0 until penSizeY){
                val x = rectIndex.first+i-centerPenX+1
                val y = rectIndex.second+j-centerPenY+1
                lineTmp+="("+x.toString()+","+y.toString()+") "
                if (isInsideGrid(x,y)) countInside++
            }
            Log.d("PenGrid",lineTmp)
        }

        if (squareData[rectIndex.first][rectIndex.second]==Color.WHITE){

            for ((i, color) in colorData.withIndex()){
                if (color.isEqual(currentColor)){
//                    Log.d("DEBUGK","BINGO")
                    if (colorData[i].isUsable(countInside)){
//                        Log.d("DEBUGK",colorData[i].getCount().toString()+"/"+colorData[i].getLimit().toString())
                        for (i1 in 0 until penSizeX){
                            for (j in 0 until penSizeY){
                                val x = rectIndex.first+i1-centerPenX+1
                                val y = rectIndex.second+j-centerPenY+1
                                if (isInsideGrid(x,y)) squareData[x][y] = currentColor
                            }
                        }
//                        squareData[rectIndex.first][rectIndex.second] = currentColor
//                        paint.color = currentColor
                        colorData[i].increaseCount(countInside)
                        crossView.invalidate()
//                        canvas.drawRect(squares[rectIndex.first][rectIndex.second], paint)
                    } else {
                        Log.d("DEBUGK","UNUSABLE")
                    }

                    break
                }
            }
        } else {
            for ((i, color) in colorData.withIndex()) {
                if (color.isEqual(squareData[rectIndex.first][rectIndex.second])) {
                    colorData[i].decreaseCount(countInside)
                    break
                }
            }
            for (i1 in 0 until penSizeX){
                for (j in 0 until penSizeY){
                    val x = rectIndex.first+i1-centerPenX+1
                    val y = rectIndex.second+j-centerPenY+1
                    if (isInsideGrid(x,y)) squareData[x][y] = Color.WHITE
                }
            }
//            squareData[rectIndex.first][rectIndex.second] = Color.WHITE
//            paint.color = Color.WHITE
//            canvas.drawRect(squares[rectIndex.first][rectIndex.second], paint)
            crossView.invalidate()
        }
    }

    fun toggleColorAtPosition() {
//        squareData[x][y] = currentColor
//        invalidate(squares[x][y])
    }

    fun changeColor(color: Int){
        currentColor = color
        paint.color = currentColor
    }

    fun getColorData(): ArrayList<MyColor> {
        return colorData
    }

    private fun getColorUsageForPixelCounter(): ArrayList<MyColor> {
        val colorUsage = ArrayList<MyColor>()
        for (color in colorData){
            if (color.isUsed()||color.isLimit())
                colorUsage.add(color)
        }
        return colorUsage
    }

    fun initPixelCounterRecyclerView(context: Context, view:View){
        val viewManagerStep = GridLayoutManager(context,2)
        val viewAdapterStep = PixelCounterAdapter(getColorUsageForPixelCounter())
        val recyclerViewStep = view.findViewById<RecyclerView>(R.id.recycler_view_pixel_counter)

        recyclerViewStep.setHasFixedSize(true)
        recyclerViewStep.layoutManager = viewManagerStep
        recyclerViewStep.adapter = viewAdapterStep
    }

    fun initPixelLimitRecyclerView(context: Context, view:View){
        val viewManagerStep = GridLayoutManager(context,2)
        val viewAdapterStep = PixelLimitAdapter(getColorLimitForPixelLimit(),this)
        val recyclerViewStep = view.findViewById<RecyclerView>(R.id.recycler_view_pixel_limit)

        recyclerViewStep.setHasFixedSize(true)
        recyclerViewStep.layoutManager = viewManagerStep
        recyclerViewStep.adapter = viewAdapterStep
    }

    private fun getColorLimitForPixelLimit(): ArrayList<MyColor> {
        val colorUsage = ArrayList<MyColor>()
        for (color in colorData){
//            if (color.isUsed()||color.isLimit())
                colorUsage.add(color)
        }
        Log.d("PixelLimit",colorUsage.size.toString())
        return colorUsage
    }

    fun new(){
        initializePixelSquares()
        initializeColorData()
        invalidate()
    }

    fun saveToFile(file: FileOutputStream){
        file.write(("This is a Cross Stitch Art project. Say Hello!\n").toByteArray())
        file.write((gridSizeX.toString()+" "+gridSizeY.toString()+" ").toByteArray())
        file.write((penSizeX.toString()+" "+penSizeY.toString()+"\n").toByteArray())
        for (color in colorData){
            file.write((color.getCount().toString()+" "+color.getLimit().toString()+" ").toByteArray())
        }
        file.write(("\n").toByteArray())
        for (line in squareData){
            for (pixel in line){
                file.write((pixel.toString()+" ").toByteArray())
            }
        }
    }

    fun readFromFile(filePath: String){
        try {
            val text = StringBuilder()
            val fis = FileInputStream (filePath)
            val br = BufferedReader(InputStreamReader(BufferedInputStream(fis)))

            var line = br.readLine()
            var count = 0
            while (line != null) {
                text.append(line)
                text.append("\n")
                when (count){
                    1 ->{
                        // gridSizeX, gridSizeY, penSizeX, penSizeY
                        val array = line.split(" ")
                        Log.d("OpenFile",array.size.toString())
                        changeGridSize(array[0].toInt(),array[1].toInt())
                        changePenSize(array[2].toInt(),array[3].toInt())
                    }
                    2 -> {
                        // colorCount, colorLimit x16
                        val array = line.split(" ")
                        Log.d("OpenFile",array.size.toString())
                        var i = 0
                        for (color in colorData){
                            color.setCount(array[i].toInt())
                            color.setLimit(array[i+1].toInt())
                            i+=2
                        }
                    }
                    3 -> {
                        // colorGrid gridSizeX x gridSizeY
                        val array = line.split(" ")
                        Log.d("OpenFile2",array.size.toString())
                        Log.d("OpenFile2",line)
                        for (i in 0 until gridSizeX){
                            for (j in 0 until gridSizeY){
                                Log.d("OpenFile2",i.toString()+" "+j.toString()+" "+array[i*j+j])
                                squareData[i][j] = array[i*gridSizeY+j].toInt()
                            }
                        }
                        invalidate()
                    }
                    0 -> {
                        if (line!="This is a Cross Stitch Art project. Say Hello!"){
                            Toast.makeText(crossView.context,"Not a CSA file for Cross Stitch Art!",Toast.LENGTH_SHORT).show()
                            br.close()
                            fis.close()
                            return
                        }
                    }
                }
                line = br.readLine()
                count++
            }
            br.close()
            fis.close()
            Log.d("OpenFile", text.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun exportFile(file: FileOutputStream, fileType: String){
//        Log.d("ExportFile","Called")
//        val bitmap = crossView.drawingCache
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
//        view.layout(0,0,gridSizeX*penSizeX,gridSizeY*penSizeY)
        this.draw(canvas)
        when (fileType){
            "jpeg"->{
                Log.d("ExportFile", bitmap.compress(Bitmap.CompressFormat.JPEG, 100, file).toString())
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, file)
            }
            "png"->{
                Log.d("ExportFile", bitmap.compress(Bitmap.CompressFormat.PNG, 100, file).toString())
            }
        }
    }
}