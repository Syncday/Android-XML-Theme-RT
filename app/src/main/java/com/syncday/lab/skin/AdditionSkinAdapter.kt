package com.syncday.lab.skin

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import com.syncday.lab.view.RoundDrawable
import com.syncday.skin.SkinUtils.Companion.isColorData
import com.syncday.skin.core.SkinAdapter

class AdditionSkinAdapter:SkinAdapter {

    companion object{
        val INSTANCE = AdditionSkinAdapter()
    }

    override fun adapt(
        view: View,
        theme: Resources.Theme,
        attrName: String,
        value: TypedValue,
        resType: String?
    ): Boolean {
        if("roundDrawableBackgroundColor"==attrName){
            ensureRoundDrawable(view).apply {
                if(value.isColorData()) {
                    setBackgroundColor(value.data)
                    view.postInvalidate()
                    return true
                }
            }
        }
        return false
    }

    private fun ensureRoundDrawable(view: View): RoundDrawable {
        return (view.background as? RoundDrawable) ?: RoundDrawable().also {
            view.background = it
        }
    }

}