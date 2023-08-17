package com.syncday.skin

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.syncday.skin.SkinUtils.Companion.isColorData
import com.syncday.skin.core.SkinAdapter

class DefaultSkinAdapter: SkinAdapter {

    companion object{
        val INSTANCE = DefaultSkinAdapter()
    }

    override fun adapt(
        view: View,
        theme: Resources.Theme,
        attrName: String,
        value: TypedValue,
        resType: String?
    ): Boolean {
        when(attrName){
            "textColor"->{
                (view as? TextView)?.let {
                    if(value.isColorData()) {
                        it.setTextColor(value.data)
                    }else{
                        it.setTextColor(ResourcesCompat.getColorStateList(theme.resources,value.resourceId,theme))
                    }
                }
            }
            "background"->{
                SkinUtils.resolveTypeValue(theme,value,resType,{
                    view.setBackgroundColor(it)
                },{
                    view.backgroundTintList = it
                },{
                    view.background = it
                })
            }
            "cardBackgroundColor"->{
                if(view is CardView){
                    SkinUtils.resolveTypeValue(theme,value,resType,{
                        view.setCardBackgroundColor(it)
                    },{
                        view.setCardBackgroundColor(it)
                    },{

                    })
                }
            }
        }
        return false //不拦截
    }

}