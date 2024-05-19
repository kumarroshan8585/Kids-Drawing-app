package com.example.freehanddrawing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.lang.reflect.Type


class DrwaingView(context: Context, attrs: AttributeSet): View(context, attrs){ //Inheriting from pre built view class
    private var mDrawPath: CustomPath?=null
    private var mCanvasBitmap: Bitmap?=null
    private var mDrawPaint: Paint?=null
    private var mCanvasPaint: Paint?=null
    private var mBrushSize: Float=0.toFloat()
    private var color= Color.BLACK
    private var canvas: Canvas?=null
    private val mPaths=ArrayList<CustomPath>()  //List itself is immutable but its own list element can be changed.

    init {
        setUpDrawing()
    }
    private fun setUpDrawing(){
        mDrawPaint=Paint()
        mDrawPath=CustomPath(color, mBrushSize)
        mDrawPath!!.color=color
        mDrawPaint!!.style=Paint.Style.STROKE
        mDrawPaint!!.strokeJoin=Paint.Join.ROUND
        mDrawPaint!!.strokeCap=Paint.Cap.ROUND
        mCanvasPaint=Paint(Paint.DITHER_FLAG)
//        mBrushSize=20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {  //when our view will be displayed
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap=Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888) //Amount of colors that we want to use
        canvas=Canvas(mCanvasBitmap!!)
    }
// Change Canvas to Canvas? if fails
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

    for(path in mPaths){
        mDrawPaint!!.strokeWidth=path.brushThickness //Beacus hum draw karte time bich mai bhi brush ki thickness change kar sakte hai
        mDrawPaint!!.color=path!!.color
        canvas.drawPath(path!!, mDrawPaint!!)
    }
    if(!mDrawPath!!.isEmpty){ //What should happen when we draw
        mDrawPaint!!.strokeWidth=mDrawPath!!.brushThickness  //How thick the paint should be
        mDrawPaint!!.color=mDrawPath!!.color
        canvas.drawPath(mDrawPath!!, mDrawPaint!!)

    }



    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX=event?.x
        val touchY=event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN->{ //What should happen when we press down on screen
                mDrawPath!!.color=color
                mDrawPath!!.brushThickness=mBrushSize //How thick the path is

                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX!!, touchY!!)
            }

            MotionEvent.ACTION_MOVE->{  //What should happen when we drag over screen
                mDrawPath!!.lineTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_UP->{
                mPaths.add(mDrawPath!!)
                mDrawPath=CustomPath(color, mBrushSize)
            }
            else -> return false
        }
            invalidate()

        return true
    }

    fun setSizeForBrush(newSize: Float){
        //Adjusting our brush on the metircs of our display
        mBrushSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth=mBrushSize
    }

    fun setColor(newColor: String){
        color=Color.parseColor(newColor)
        mDrawPaint!!.color=color

    }

    //An inner class for custom path with two params as color and stroke
    internal inner class CustomPath(var color: Int,
        var brushThickness: Float) : Path(){

    }

}