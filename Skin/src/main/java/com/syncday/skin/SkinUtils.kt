package com.syncday.skin

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.core.content.res.ResourcesCompat


class SkinUtils {
    companion object{
        fun LayoutInflater.replaceFactory(factory: LayoutInflater.Factory2):Boolean{
            return try {
                LayoutInflater::class.java.getDeclaredField("mFactory2").also{
                    it.isAccessible = true
                    it.set(this,factory)
                }
                true
            }catch (e:Exception){
                e.printStackTrace()
                false
            }
        }

        fun TypedValue.isColorData():Boolean = type>= TypedValue.TYPE_FIRST_COLOR_INT && type<= TypedValue.TYPE_LAST_COLOR_INT

        fun resolveTypeValue(theme: Resources.Theme,
                             value: TypedValue,
                             resType: String?,
                             isColor:(color:Int)->Unit,
                             isColorStatueList:(color:ColorStateList?)->Unit,
                             isDrawable:(drawable:Drawable?)->Unit){
            if(value.isColorData()) {
                isColor(value.data)
            }else{
                if("color"==resType)
                    isColorStatueList(ResourcesCompat.getColorStateList(theme.resources,value.resourceId,theme))
                else if("drawable"==resType)
                    isDrawable(ResourcesCompat.getDrawable(theme.resources,value.resourceId,theme))
            }
        }

        fun getApkPackageName(apkPath:String,context: Context):String?{
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageArchiveInfo(apkPath,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong()))?.packageName
            }else{
                context.packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES)?.packageName
            }
        }
    }
}