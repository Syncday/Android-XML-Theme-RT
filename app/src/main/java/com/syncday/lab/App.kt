package com.syncday.lab

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.syncday.skin.DefaultSkinAdapter
import com.syncday.skin.NavigationBarSkinAdapter
import com.syncday.skin.SkinUtils.Companion.replaceFactory
import com.syncday.skin.core.SkinApplyListener
import com.syncday.skin.core.SkinFactory
import com.syncday.skin.core.SkinManager
import org.json.JSONObject

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initSkin()
    }

    private fun initSkin(){
        //初始化
        SkinManager.INSTANCE.init(this)
        //将SkinFactory添加到监听新Skin加载
        SkinManager.INSTANCE.addListener(SkinFactory.INSTANCE)
        //添加View的属性适配器
        SkinFactory.INSTANCE.addSkinAdapter(
            DefaultSkinAdapter.INSTANCE,
            NavigationBarSkinAdapter.INSTANCE
        )
        //通过持久化信息加载主题
        PreferenceManager.getDefaultSharedPreferences(this).getString("skin",null)?.run {
            SkinManager.INSTANCE.loadByRemainInfo(JSONObject(this))
        }
        //添加记录新主题的监听器
        SkinManager.INSTANCE.addListener(object :SkinApplyListener{
            override fun onSkinApply(skinManager: SkinManager) {
                PreferenceManager.getDefaultSharedPreferences(this@App).edit {
                    putString("skin",skinManager.getRemainInfo().toString())
                }
            }
        })

        registerActivityLifecycleCallbacks(object :ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.layoutInflater.replaceFactory(SkinFactory.INSTANCE)
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}

        })

    }
}