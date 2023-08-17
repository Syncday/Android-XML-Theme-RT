package com.syncday.skin.core

import android.view.View
import java.lang.ref.WeakReference

class SkinView(view: View):WeakReference<View>(view){
    val attributes = HashMap<String,Triple<Int,String,Boolean>>(1)

    fun addAttribute(attr:String, resId:Int, resRef:String, resRefIsAttr: Boolean){
        attributes[attr] = Triple(resId,resRef,resRefIsAttr)
    }

    override fun equals(other: Any?): Boolean {
        if(other is SkinView){
            return other.get() == this.get()
        }else if(other is View){
            return other == this.get()
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return get()?.hashCode() ?: 0
    }

    override fun toString(): String {
        val sb = StringBuilder().append("SkinView\t[")
        val view = get()
        if(view!=null)
            sb.append(view.javaClass.simpleName).append("@").append(view.hashCode().toString(16))
        else
            sb.append("NULL")
        sb.append("]\t{")
        attributes.forEach {
            sb.append(it.key).append("<").append(it.value.second).append(">").append(",")
        }
        sb.deleteCharAt(sb.lastIndex)
        sb.append("}")
        return sb.toString()
    }

}