package com.syncday.lab.view

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.Shader
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest


class RoundTransform(
    private val rtl: Float,
    private val rtr: Float,
    private val rbl: Float,
    private val rbr: Float,
): BitmapTransformation() {

    companion object{
        private val ID = "com.syncday.lab.view.RoundTransform"
        private val ID_BYTES = ID.toByteArray(CHARSET)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
        val radiusData = ByteBuffer.allocate(4*4)
            .putFloat(rtl)
            .putFloat(rtr)
            .putFloat(rbr)
            .putFloat(rbl).array()
        messageDigest.update(radiusData)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {

        val result = pool[toTransform.width, toTransform.height, toTransform.config]
        val shader = BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = shader

        val width = toTransform.width.toFloat()
        val height = toTransform.height.toFloat()

        val canvas = Canvas(result)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val path = Path()
        path.moveTo(0f,rtl)
        path.quadTo(0f,0f,rtl,0f)

        path.lineTo(width-rtr,0f)
        path.quadTo(width,0f,width,rtr)

        path.lineTo(width,height-rbr)
        path.quadTo(width,height,width-rbr,height)

        path.lineTo(rbl,height)
        path.quadTo(0f,height,0f,height-rbl)

        path.close()

        canvas.drawPath(path, paint)

        canvas.setBitmap(null)



        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other is RoundTransform) {
            return rtl == other.rtl && rtr == other.rtr && rbr == other.rbr && rbl == other.rbl
        }
        return false
    }

    override fun hashCode(): Int {
        return Util.hashCode(ID.hashCode())
    }

}