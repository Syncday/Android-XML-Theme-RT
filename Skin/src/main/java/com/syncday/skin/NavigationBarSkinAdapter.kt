package com.syncday.skin

import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.util.Xml
import android.view.Menu
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEach
import com.google.android.material.navigation.NavigationBarView
import com.syncday.skin.core.SkinAdapter
import org.xmlpull.v1.XmlPullParser

class NavigationBarSkinAdapter:SkinAdapter {

    companion object{

        val INSTANCE = NavigationBarSkinAdapter()

        @JvmStatic
        private val XML_MENU = "menu"

        @JvmStatic
        private val XML_ITEM = "item"

        fun collectMenuIcon(id:Int, theme: Resources.Theme): HashMap<String, Drawable>{
            val map = HashMap<String,Drawable>()
            try {
                theme.resources.getLayout(id).use {
                    parseMenuResource(it, Xml.asAttributeSet(it), theme, map)
                }
            }catch (e: Resources.NotFoundException){
                e.printStackTrace()
            }
            return map
        }

        fun replaceMenuResource(menu: Menu, drawableMap:HashMap<String,Drawable>, resources: Resources){
            menu.forEach {
                try {
                    val idName = resources.getResourceEntryName(it.itemId)
                    it.icon = drawableMap[idName]
                }catch (ignore: Resources.NotFoundException){

                }
            }
        }

        private fun parseMenuResource(parser: XmlResourceParser,
                                      attributeSet: AttributeSet,
                                      theme: Resources.Theme,
                                      drawableList:HashMap<String,Drawable>
        ) {
            var eventType = parser.eventType
            val tagName: String
            // This loop will skip to the menu start tag
            do {
                if (eventType == XmlPullParser.START_TAG) {
                    tagName = parser.name
                    if (tagName == XML_MENU) {
                        // Go to next tag
                        eventType = parser.next()
                        break
                    }
                    throw RuntimeException("Expecting menu, got $tagName")
                }
                eventType = parser.next()
            } while (eventType != XmlPullParser.END_DOCUMENT)

            while (true){
                when(eventType){
                    XmlPullParser.START_TAG->{
                        when(parser.name){
                            XML_ITEM->{
                                var idName:String? = null
                                var iconDrawable:Drawable? = null
                                for (i in 0 until attributeSet.attributeCount){
                                    try {
                                        if("id"==attributeSet.getAttributeName(i)){
                                            idName = theme.resources.getResourceEntryName(
                                                attributeSet.getAttributeValue(i)
                                                    .substring(1)
                                                    .toInt())
                                        }
                                        if("icon"==attributeSet.getAttributeName(i)){
                                            val drawableId = attributeSet.getAttributeValue(i)
                                                .substring(1)
                                                .toInt()
                                            iconDrawable = ResourcesCompat.getDrawable(theme.resources,drawableId,theme)
                                        }
                                    }catch (ignore: Resources.NotFoundException){
                                        break
                                    }
                                    if(idName!=null && iconDrawable!=null)
                                        drawableList[idName] = iconDrawable
                                }
                            }
                            XML_MENU->{
                                parseMenuResource(parser, attributeSet,theme,drawableList)
                            }
                        }
                    }
                    XmlPullParser.END_TAG->{
                        when(parser.name){
                            XML_MENU->{
                                break
                            }
                        }
                    }
                    XmlPullParser.END_DOCUMENT-> {
                        drawableList.clear()
                        throw RuntimeException("Unexpected end of document")
                    }
                }
                eventType = parser.next()
            }
        }
    }

    override fun adapt(
        view: View,
        theme: Resources.Theme,
        attrName: String,
        value: TypedValue,
        resType: String?
    ): Boolean {
        if(view is NavigationBarView){
            when(attrName){
                "itemTextColor" -> {
                    view.itemTextColor = ResourcesCompat.getColorStateList(theme.resources,value.resourceId,theme)
                    return true
                }
                "itemIconTint" -> {
                    view.itemIconTintList = ResourcesCompat.getColorStateList(theme.resources,value.resourceId,theme)
                    return true
                }
                "menu" ->{
                    if(value.resourceId!=0) {
                        replaceMenuResource(view.menu,
                            collectMenuIcon(value.resourceId,theme),
                            view.resources
                        )
                    }
                    return true
                }
            }
        }
        return false
    }

}