package com.styl.pa.entities.generateToken

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.classes.ClassInfo

class PageByClassInfo : Page() {
    @SerializedName("classList")
    @Expose
    private var classList: ArrayList<ClassInfo>? = ArrayList()

    @SerializedName("hashQueryNext")
    var hashQueryNext: String? = ""
        get() = field
        set(value) {
            field = value
        }

    fun getClassList(): ArrayList<ClassInfo> {
        if (classList == null)
            return ArrayList()
        return classList!!
    }

    fun setClassList(classList: ArrayList<ClassInfo>) {
        this.classList = classList
    }


}