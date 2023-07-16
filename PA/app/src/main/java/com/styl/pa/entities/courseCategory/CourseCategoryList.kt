package com.styl.pa.entities.courseCategory

import android.os.Parcelable
import com.styl.pa.entities.generateToken.Page
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Ngatran on 10/11/2018.
 */
class CourseCategoryList : Page(), Parcelable {
    @SerializedName("courseCategoryList", alternate = ["items"])
    @Expose
    private var courseCategoryList: ArrayList<CourseCategory>? = null


    fun getCourseCategoryList(): ArrayList<CourseCategory>? {
        return courseCategoryList
    }

    fun setCourseCategoryList(courseCategoryList: ArrayList<CourseCategory>) {
        this.courseCategoryList = courseCategoryList
    }
}