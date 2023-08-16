package com.syncday.skin.core

import android.content.res.Resources
import android.content.res.Resources.Theme
import android.util.TypedValue
import android.view.View

interface SkinAdapter {
    @Throws(Resources.NotFoundException::class)
    fun adapt(view: View,theme: Theme,attrName:String,value: TypedValue,resType:String?):Boolean
}