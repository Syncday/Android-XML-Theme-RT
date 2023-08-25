package com.syncday.lab.ui

import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syncday.lab.R
import com.syncday.lab.databinding.ActivityMainBinding
import com.syncday.lab.homeBeanList
import com.syncday.skin.SkinUtils
import com.syncday.skin.core.SkinApplyListener
import com.syncday.skin.core.SkinManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : BaseActivity() {

    private lateinit var binding:ActivityMainBinding

    private val skinApplyListener = object :SkinApplyListener{
        override fun onSkinApply(skinManager: SkinManager) {
            changeThemeIcon(skinManager.getThemeName())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initListener()
    }


    private fun initView(){

        binding.list.apply {
            adapter = MainAdapter().apply {
                submitList(homeBeanList)
            }
            layoutManager = LinearLayoutManager(this@MainActivity,RecyclerView.HORIZONTAL,false)
        }

        binding.themeStyle.setOnClickListener {
            val barHeight = binding.contentRoot.paddingTop
            binding.bloomView.setPivot(it.x+it.width/2f,barHeight+it.y+it.height/2f)
            when(SkinManager.INSTANCE.getThemeName()){
                "Theme.Lab"->{
                    binding.bloomView.isExpand(true)
                    binding.bloomView.watchViewOnce(binding.contentRoot)
                    SkinManager.INSTANCE.applyTheme("Theme.Lab.Night")
                }
                "Theme.Lab.Night"->{
                    binding.bloomView.isExpand(false)
                    binding.bloomView.watchViewOnce(binding.contentRoot)
                    SkinManager.INSTANCE.applyTheme("Theme.Lab")
                }
                else->{

                }
            }
        }

        binding.searchWrap.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        changeThemeIcon(SkinManager.INSTANCE.getThemeName())

    }

    private fun changeThemeIcon(themeName:String){
        when(themeName){
            "Theme.Lab"->{
                binding.themeStyle.setImageResource(R.drawable.ic_sun)
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            "Theme.Lab.Night"->{
                binding.themeStyle.setImageResource(R.drawable.ic_moon)
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }

    private fun initListener(){
        SkinManager.INSTANCE.addListener(skinApplyListener)

        binding.bloomView.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable?) {
                    binding.themeStyle.isClickable = false
                }
                override fun onAnimationEnd(drawable: Drawable?) {
                    binding.themeStyle.isClickable = true
                }

            })

        binding.navigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home ->{
                    SkinManager.INSTANCE.removeSkin()
                }
                R.id.bbs ->{
                    copyApkToCache(R.raw.animal_skin,"animal_skin")?.let { path->
                        SkinUtils.getApkPackageName(path,this)?.let {name->
                            SkinManager.INSTANCE.applySkinByApk(path,name,null)
                        }
                    }
                }
            }
            true
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        SkinManager.INSTANCE.removeListener(skinApplyListener)
    }

    private fun copyApkToCache(id:Int,name:String):String?{
        val file = File(externalCacheDir,name)
        if(file.exists())
            return file.absolutePath
        try {
            FileOutputStream(file).use { ous->
                resources.openRawResource(id).use { ins->
                    val swap = ByteArray(1024)
                    var len:Int
                    while (true){
                        len = ins.read(swap)
                        if(len<0)
                            break
                        ous.write(swap,0,len)
                    }
                    ous.flush()
                }
            }
        }catch (e:IOException){
            e.printStackTrace()
            return null
        }
        return file.absolutePath
    }

}