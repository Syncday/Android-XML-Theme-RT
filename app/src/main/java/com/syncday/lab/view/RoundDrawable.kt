package com.syncday.lab.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.Keep

@Keep
class RoundDrawable(
    private var mRoundRadius:Float = 0f,
    private var mShadowLayerRadius:Float = 0f,
    private var mShadowLayerDx:Float = 0f,
    private var mShadowLayerDy:Float = 0f,
    private var mShadowLayerColor:Int = 0x55000000
) : Drawable() {
    private val rectF = RectF()
    private val paint = Paint()

    init {
        paint.apply {
            this.color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
            isDither = true
            setShadowLayer(mShadowLayerRadius,mShadowLayerDx,mShadowLayerDy,mShadowLayerColor)
        }
    }

    fun setElevation(elevation:Float){
        this.mShadowLayerRadius = elevation
        paint.setShadowLayer(mShadowLayerRadius,mShadowLayerDx,mShadowLayerDy,mShadowLayerColor)
    }

    fun setRoundRadius(radius: Float){
        this.mRoundRadius = radius
    }

    fun setBackgroundColor(color: Int){
        paint.color = color
    }


    override fun onBoundsChange(bounds: Rect) {
        rectF.set(bounds)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(rectF,mRoundRadius,mRoundRadius,paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

}