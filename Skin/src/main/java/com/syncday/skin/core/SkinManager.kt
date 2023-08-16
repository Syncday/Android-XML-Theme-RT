package com.syncday.skin.core

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.content.res.Resources.Theme
import android.util.Log
import android.util.TypedValue
import org.json.JSONObject

open class SkinManager {

    companion object{
        val INSTANCE = SkinManager()
    }

    private val TAG = "SkinManager"
    private val DEBUG = true
    private lateinit var defaultTheme: Theme
    private lateinit var defaultPackageName: String
    private lateinit var themeName:String
    private var skinTheme: Theme? = null
    private var skinPackageName: String? = null
    private val skinApplyListeners = HashSet<SkinApplyListener>()
    private var apkSkinPath:String? = null

    fun getThemeName()  = themeName
    fun getDefaultPackageName() = defaultPackageName
    fun getApkSkinPath() = apkSkinPath
    fun getSkinPackageName() = skinPackageName


    fun init(context: Context){
        this.defaultTheme = context.theme
        this.defaultPackageName = context.packageName
        this.themeName = context.resources.getResourceEntryName(context.applicationInfo.theme)
    }

    fun applyTheme(newThemeName:String):Boolean{
        skinTheme?.also {
            val themeId = it.resources.getIdentifier(newThemeName,"style",skinPackageName)
            return if(themeId!=0){
                //避免ResourcesCompat获取资源时出现缓存不一致的问题
                skinTheme = it.resources.newTheme().apply {
                    applyStyle(themeId,true)
                }
                this.themeName = newThemeName
                onSkinApply()
                true
            }else{
                if(DEBUG)
                    Log.e(TAG, "applyTheme fail: [$newThemeName] was not found in skin theme:$skinTheme" )
                false
            }
        }
        val themeId = defaultTheme.resources.getIdentifier(newThemeName,"style",defaultPackageName)
        return if(themeId!=0){
            //不单独设置为skinTheme的优势之一是省略查询资源id的过程，重新赋值以避免ResourcesCompat获取资源时出现缓存不一致的问题
            defaultTheme = defaultTheme.resources.newTheme().apply {
                applyStyle(themeId,true)
            }
            this.themeName = newThemeName
            onSkinApply()
            true
        }else{
            if(DEBUG)
                Log.e(TAG, "applyTheme fail: [$newThemeName] was not found in default theme:$defaultTheme" )
            false
        }
    }

    fun applySkinByApk(apkPath: String, apkPackageName: String, apkThemeName: String?){
        val assetManager = AssetManager::class.java.newInstance()
        AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java).also {
            it.isAccessible = true
            it.invoke(assetManager, apkPath)
        }
        val theme = Resources(assetManager,
            defaultTheme.resources.displayMetrics,
            defaultTheme.resources.configuration).newTheme()
        val applyThemeName = apkThemeName ?: themeName
        val themeId = theme.resources.getIdentifier(applyThemeName,"style",apkPackageName)
        if(themeId!=0){
            this.themeName = applyThemeName
            theme.applyStyle(themeId,true)
        }else{
            if(DEBUG)
                Log.e(TAG, "applySkinByApk fail: [$applyThemeName] was not found in skin theme:$apkPath" )
        }
        this.skinTheme = theme
        this.skinPackageName = apkPackageName
        this.apkSkinPath = apkPath
        onSkinApply()
    }

    fun removeSkin(){
        this.skinTheme = null
        this.skinPackageName = null
        this.apkSkinPath = null
        applyTheme(this.themeName)
    }

    @Throws(NotFoundException::class)
    fun resolveAttribute(resId:Int,
                         resRef:String,
                         resRefIsAttr: Boolean,
                         typedValue: TypedValue
    ):Theme{
        skinTheme?.also {
            try {
                val skinId = it.resources.getIdentifier(skinPackageName+resRef, "string", null)
                if(skinId!=0){
                    if(resRefIsAttr){
                        if(!it.resolveAttribute(skinId,typedValue,true))
                            throw NotFoundException("Resource ${skinPackageName+resRef} ID #0x${skinId.toString(16)}")
                    } else{
                        it.resources.getValue(skinId,typedValue,true)
                    }
                    return it
                }else{
                    if(DEBUG)
                        Log.e(TAG, "resolveAttribute: Resource ${skinPackageName+resRef} was not found in skin theme")
                }
            }catch (e:NotFoundException){
                if(DEBUG)
                    Log.e(TAG, "resolveAttribute: $e")
            }
        }
        try {
            if(resRefIsAttr)
                defaultTheme.resolveAttribute(resId,typedValue,true)
            else
                defaultTheme.resources.getValue(resId,typedValue,true)
        }catch (e:NotFoundException){
            //最后也没能找到资源
            throw NotFoundException("[${defaultPackageName+resRef}] was not found in $themeName")
        }
        return defaultTheme
    }

    private fun onSkinApply(){
        skinApplyListeners.forEach {
            it.onSkinApply(this)
        }
    }

    fun addListener(skinApplyListener: SkinApplyListener){
        skinApplyListeners.add(skinApplyListener)
    }

    fun removeListener(skinApplyListener: SkinApplyListener){
        skinApplyListeners.remove(skinApplyListener)
    }

    fun getRemainInfo():JSONObject{
        return JSONObject().apply {
            put("themeName",themeName)
            if(apkSkinPath!=null && skinPackageName!=null) {
                put("skinApkPath",apkSkinPath)
                put("skinPackageName",skinPackageName)
            }
        }
    }

    fun loadByRemainInfo(info:JSONObject):Boolean{
        return try {
            val themeName = info.getString("themeName")
            if(info.isNull("skinApkPath")||info.isNull("skinPackageName")){
                applyTheme(themeName)
            }else{
                applySkinByApk(info.getString("skinApkPath"),
                    info.getString("skinPackageName"),
                    themeName)
            }
            true
        }catch (e:Exception){
            if(DEBUG)
                Log.e(TAG, "loadByRemainInfo fail: $e")
            false
        }

    }

}