package com.example.habit.ui.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import com.example.habit.R

class ProgressView(val context : Context) : View(context) {

    var ovalSpace=RectF()
    private val parentArcPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = context.resources.getColor(R.color.orange_op_20)
        strokeWidth = 40f
    }

    private val fillArcPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = context.resources.getColor(R.color.orange)
        strokeWidth = 40f
        strokeCap = Paint.Cap.ROUND
    }


    override fun onDraw(canvas: Canvas?) {
        setOvalSpace()
        canvas?.let {
            drawBackgroundCircle(it)
            drawProgressCircle(it)
        }

    }

    private fun drawProgressCircle(it: Canvas) {
        it.drawArc(ovalSpace,260f,90f,false,fillArcPaint)
    }

    private fun drawBackgroundCircle(it: Canvas) {
        it.drawArc(ovalSpace,0f,360f,false,parentArcPaint)
    }

    private fun setOvalSpace() {
        val horizontalCenter=width.div(2).toFloat()
        val verticalCenter=height.div(2).toFloat()
        val ovalSize=100
        ovalSpace.set(
            horizontalCenter-ovalSize,
            verticalCenter-ovalSize,
            horizontalCenter+ovalSize,
            verticalCenter+ovalSize

        )
    }

}