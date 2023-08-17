package com.syncday.skin.core

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatViewInflater
import androidx.collection.SimpleArrayMap
import java.lang.reflect.Constructor
import java.util.WeakHashMap


class SkinFactory: LayoutInflater.Factory2, SkinApplyListener {
    private val DEBUG = true
    private val TAG = "SkinFactory"
    private val skinAdapters = HashSet<SkinAdapter>()
    private val skinViews = WeakHashMap<View?,SkinView>()
    private var skinManager: SkinManager? = null
    private val tempTypedValue = TypedValue()
    private val appCompatViewInflater = AppCompatViewInflater()

    private val mConstructorArgs = arrayOfNulls<Any>(2)

    companion object{
        private val TEMP_VALUE = TypedValue()
        private val sClassPrefixList = arrayOf(
            "android.widget.",
            "android.view.",
            "android.webkit."
        )
        private val sConstructorMap = SimpleArrayMap<String, Constructor<out View>>()
        private val sConstructorSignature = arrayOf(
            Context::class.java, AttributeSet::class.java
        )
        @JvmStatic
        val INSTANCE = SkinFactory()
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        val view = appCompatViewInflater.createView(parent, name, context, attrs,false,
            false,true,false) ?: onCreateView(name,context, attrs)
        if(view!=null)
            collectViewAttributes(attrs, view)
        return view
    }

    override fun onCreateView(viewName: String, context: Context, attrs: AttributeSet): View? {
        var name = viewName
        if (name == "view") {
            name = attrs.getAttributeValue(null, "class")
        }

        return try {
            mConstructorArgs[0] = context
            mConstructorArgs[1] = attrs
            if (-1 == name.indexOf('.')) {
                for (i in sClassPrefixList.indices) {
                    val view: View? = createViewByPrefix(context, name, sClassPrefixList[i])
                    if (view != null) {
                        return view
                    }
                }
                null
            } else {
                createViewByPrefix(context, name, null)
            }
        } catch (e: Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            null
        } finally {
            // Don't retain references on context.
            mConstructorArgs[0] = null
            mConstructorArgs[1] = null
        }
    }

    @Throws(ClassNotFoundException::class, InflateException::class)
    private fun createViewByPrefix(context: Context, name: String, prefix: String?): View? {
        var constructor: Constructor<out View>? = sConstructorMap.get(name)
        return try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                val clazz = Class.forName(
                    if (prefix != null) prefix + name else name,
                    false,
                    context.classLoader
                ).asSubclass(View::class.java)
                constructor = clazz.getConstructor(*sConstructorSignature)
                sConstructorMap.put(name, constructor)
            }
            constructor!!.isAccessible = true
            constructor.newInstance(*mConstructorArgs)
        } catch (e: java.lang.Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            null
        }
    }

    private fun collectViewAttributes(attrs: AttributeSet, view: View?) {
        if (view == null)
            return
        var skinView: SkinView? = null
        for (i in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(i)
            if("id"==attributeName)
                continue
            val prefix:Char = try {
                attrs.getAttributeValue(i)[0]
            }catch (ignore:IndexOutOfBoundsException){
                continue
            }
            val resRefIsAttr = ('?' == prefix)
            if(resRefIsAttr || '@' == prefix){
                val resId = attrs.getAttributeValue(i).substring(1).toInt()
                try {
                    val resourceName = view.resources.getResourceName(resId)
                    //Log.e(TAG, "collectViewAttributes: ${attrs.getAttributeName(i)},${attrs.getAttributeValue(i)},$resourceName" )
                    //无需替换Android的内部样式
                    if(resourceName.startsWith("android"))
                        continue
                    val index = resourceName.indexOf(':')
                    if(index==-1)
                        continue
                    if(skinView==null)
                        skinView = SkinView(view)
                    //记录属性的资源名称，使用时由主题管理器拼接
                    val resRefName = resourceName.substring(index)
                    //忽略资源引用为 @id/ 的属性
                    if(resRefName.startsWith(":id/"))
                        continue
                    skinView.addAttribute(attributeName,resId,resRefName,resRefIsAttr)
                }catch (e: NotFoundException){
                    //resources.getResourceName(id)可能找不到对应id的值而产生报错
                }
            }
        }
        if(skinView!=null) {
            skinViews[view] = skinView
            skinManager?.run {
                applySkinToView(this,skinView)
            }
        }
    }

    private fun applySkinToView(skinManager: SkinManager, skinView: SkinView){
        val view = skinView.get() ?: return
        skinView.attributes.forEach {
            try {
                val theme = skinManager.resolveAttribute(it.value.first,it.value.second,it.value.third,tempTypedValue)
                val adapterIterator = skinAdapters.iterator()
                val resType:String? = if(tempTypedValue.resourceId!=0) //由theme自己获取
                    theme.resources.getResourceTypeName(tempTypedValue.resourceId) else null
                while (adapterIterator.hasNext()){
                    val adapter = adapterIterator.next()
                    try {
                        if(adapter.adapt(view,theme,it.key,tempTypedValue,resType))
                            break
                    }catch (e:Exception){
                        if (DEBUG)
                            Log.e(TAG, "applySkinToView: error at [$adapter] when adapt [${it.value}]: $e")
                    }
                }
            }catch (e:NotFoundException){
                e.printStackTrace()
                if (DEBUG)
                    Log.e(TAG, "applySkinToView: $e")
            }finally {
                tempTypedValue.setTo(TEMP_VALUE)
            }
        }
    }

    fun addSkinAdapter(vararg skinAdapter: SkinAdapter){
        this.skinAdapters.addAll(skinAdapter)
    }

    override fun onSkinApply(skinManager: SkinManager) {
        this.skinManager = skinManager
        if(skinAdapters.isEmpty() || skinViews.isEmpty()){
            return
        }
        val iterator = skinViews.iterator()
        while (iterator.hasNext()){
            val entry = iterator.next()
            if(entry.key != null && entry.value.get()!=null){
                applySkinToView(skinManager, entry.value)
            } else{
                iterator.remove()
            }
        }
    }

}