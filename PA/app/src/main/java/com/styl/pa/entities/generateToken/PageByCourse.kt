package com.styl.pa.entities.generateToken

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PageByCourse : Page() {

    @SerializedName("courseList")
    @Expose
    private var courseList: ArrayList<Course>? = ArrayList()

    fun getCourseList(): ArrayList<Course> {
        return courseList!!
    }

    fun setCourseList(courseList: ArrayList<Course>) {
        this.courseList = courseList
    }
}