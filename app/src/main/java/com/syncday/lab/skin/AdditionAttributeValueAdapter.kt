package com.syncday.lab.skin

import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import com.syncday.lab.R
import com.syncday.lab.view.RoundDrawable
import com.syncday.skin.core.SkinFactory

class AdditionAttributeValueAdapter: SkinFactory.AttributeValueAdapter {
    override fun adapt(view: View, attrName: String, attrs: AttributeSet, index: Int) {
        when (attrName) {
            "roundDrawableRadius" -> {
                view.context.obtainStyledAttributes(attrs, R.styleable.RoundDrawable).use {
                    ensureRoundDrawable(view).setRoundRadius(it.getDimension(R.styleable.RoundDrawable_roundDrawableRadius,0f))
                    view.postInvalidate()
                }
            }
            "roundDrawableElevation" -> {
                view.context.obtainStyledAttributes(attrs, R.styleable.RoundDrawable).use {
                    ensureRoundDrawable(view).setElevation(it.getDimension(R.styleable.RoundDrawable_roundDrawableElevation,0f))
                    view.postInvalidate()
                }
            }
            "roundDrawableBackgroundColor" -> {
                ensureRoundDrawable(view).setBackgroundColor(attrs.getAttributeIntValue(index,0))
                view.postInvalidate()
            }
        }
    }

    private fun ensureRoundDrawable(view: View): RoundDrawable {
        return (view.background as? RoundDrawable) ?: RoundDrawable().also {
            view.background = it
        }
    }
}