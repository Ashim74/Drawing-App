package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View


class DrawingView(context: Context, attrs:AttributeSet) : View(context,attrs) {

    private var mDrawPath : CustomPath? = null
    //an variable of CustomPath inner class to use it further
    private var mCanvasBitmap : Bitmap?=null
    // an instance of the Bitmap
    private var mDrawPaint: Paint?=null
    // THe paint class holds the style and color information about how to draw
    private var mCanvasPaint : Paint? = null
    // Instance of canvas paint view
    private var mBrushSize : Float = 0.toFloat()
    // A variable for stroke / brush size to draw
    private var color = Color.BLACK
    private var canvas : Canvas? = null
    private var mPaths = ArrayList<CustomPath>()  //array list for paths
    private var mUndoPath = ArrayList<CustomPath>()




    init {
        setupDrawing()
    }

    fun onClickUndo(){
        if(mPaths.size>0){
            mUndoPath.add(mPaths.removeAt(mPaths.size-1))
            invalidate()

        }
    }
    fun onClickRedo(){
        if(mUndoPath.size>0){
           mPaths.add(mUndoPath.get(mUndoPath.size-1))
            mUndoPath.removeAt(mUndoPath.size-1)
            invalidate()

        }
    }

    private fun setupDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint!!.color = color

        mDrawPaint!!.style = Paint.Style.STROKE     //this is to draw a  STROKE style
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND    //This is for store join
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND    // this is for stroke cap

        mCanvasPaint = Paint(Paint.DITHER_FLAG)     //here the default or we can initial brush/stroke
       //  mBrushSize = 20.toFloat()        //here the default  or we can inital brush stroke size

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }
    // Change Canvas go Canvas ? id fails
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)

        for(path in mPaths){
            mDrawPaint!!.strokeWidth=path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path!!,mDrawPaint!!)
        }

        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth=mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!,mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color= color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                if (touchY != null) {
                    if (touchX != null) {
                        mDrawPath!!.moveTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE ->{
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP ->{
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color , mBrushSize)
            }
            else -> return false

        }
        invalidate()


        return true
    }
    fun setSizeForBrush(newSize: Float){
         mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
               newSize,resources.displayMetrics
            )
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    fun setColor(newColor: String){
        color = Color.parseColor(newColor)
        mDrawPaint!!.color=color
    }

    internal inner class CustomPath(var color: Int,var brushThickness: Float) : Path() {



    }


}
