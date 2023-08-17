# Android-XML-Theme-RT
Android动态切换基于XML的主题，适合UI和主题是XML配置的传统项目

支持**主题Style**以及**网络主题**的切换（例如颜色和图标等）

<p align="center">
    <img src="https://github.com/Syncday/Android-XML-Theme-RT/blob/main/cover.gif" width="240px" alt="预览"/>
</p>

## 使用
建议在Application中使用，通过registerActivityLifecycleCallbacks自动替换，以减少对项目的入侵。
```kotlin
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
```


## Tips
适配特殊的Vie：实现SkinAdapter接口以支持view

修改资源类型：仅限attr，并且是使用apk加载资源的方式，例如替换color为drawable

注意SkinApplyListner的监听是在切换主题时立即执行的，注意初始化的顺序（参考上面的代码）


## 思路
无需重建Activity而切换Views的颜色等属性的核心是：已经创建过的view直接修改其需要的属性值，而属性值是可以动态获取的。
所以实现步骤分为3步：
 1. 收集view的属性信息
 2. 实现资源的获取
 3. 遍历创建的view以修改属性

以上对应项目的几个核心类**SkinFactory**、**SkinManager**、**SkinView**与**SkinAdapter**
