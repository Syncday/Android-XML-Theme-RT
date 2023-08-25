package com.syncday.lab.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.Picture
import android.graphics.Region
import android.graphics.drawable.Animatable2
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.sqrt

class BloomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Animatable2 {

    private var animatorDuration = 300L
    private var isExpand = true

    private var pivotX:Float = 0f
    private var pivotY:Float = 0f
    private var radius:Float = 0f
    private val path = Path()
    private val interpolator = AccelerateDecelerateInterpolator()

    private var isRunning = AtomicBoolean(false)
    private var animatorStartTime = 0L
    private val picture = Picture()
    private val listeners:CopyOnWriteArrayList<Animatable2.AnimationCallback> by lazy{
        CopyOnWriteArrayList()
    }

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun setPivot(x:Float,y:Float): BloomView {
        if(this.pivotX!=x || this.pivotY!=y){
            this.pivotX = x
            this.pivotY = y
            calculateMaxRadius()
        }
        return this
    }

    fun setAnimatorDuration(duration: Long): BloomView {
        assert(duration>0)
        animatorDuration = duration
        return this
    }

    fun isExpand(expand:Boolean): BloomView {
        this.isExpand = expand
        return this
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateMaxRadius()
    }

    private fun calculateFraction():Float{
        return if(isExpand)
            (System.currentTimeMillis()-animatorStartTime).toFloat() / animatorDuration
        else
            1 - (System.currentTimeMillis()-animatorStartTime).toFloat() / animatorDuration
    }

    private fun calculateMaxRadius(){
        val vx =  if(pivotX>width/2f) 0f else width.toFloat()
        val vy =  if(pivotY>height/2f) 0f else height.toFloat()
        radius = sqrt((vx-pivotX).square()+(vy-pivotY).square())
    }

    override fun onDraw(canvas: Canvas) {
        if(isRunning.get()){
            path.rewind()
            val fraction = calculateFraction()
            path.addCircle(pivotX,pivotY,radius*interpolator.getInterpolation(fraction),Path.Direction.CW)
            canvas.clipPath(path,if(isExpand) Region.Op.DIFFERENCE else Region.Op.INTERSECT)
            picture.draw(canvas)

            if(fraction<=0.0 || fraction>=1.0)
                stop()
            postInvalidateOnAnimation()
        }
    }

    fun watchViewOnce(view: View):Boolean{
        if(isRunning.compareAndSet(false,true)){
            view.draw(picture.beginRecording(view.width,view.height))
            picture.endRecording()
            view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener{
                override fun onPreDraw(): Boolean {
                    view.viewTreeObserver.removeOnPreDrawListener(this)
                    start()
                    return true
                }
            })
            return true
        }
        return false
    }

    private fun Float.square() = this*this

    override fun start() {
        animatorStartTime = System.currentTimeMillis()
        listeners.forEach {
            it.onAnimationStart(null)
        }
        invalidate()
    }

    override fun stop() {
        if(listeners.size>0){
            listeners.toArray().forEach {
                (it as Animatable2.AnimationCallback).onAnimationEnd(null)
            }
        }
        isRunning.set(false)
    }

    override fun isRunning(): Boolean {
        return isRunning.get()
    }

    override fun registerAnimationCallback(callback: Animatable2.AnimationCallback) {
        listeners.add(callback)
    }

    override fun unregisterAnimationCallback(callback: Animatable2.AnimationCallback): Boolean {
        return listeners.remove(callback)
    }

    override fun clearAnimationCallbacks() {
        listeners.clear()
    }

}