package com.styl.pa.entities.courseCategory

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.generateToken.Page

/**
 * Created by Ngatran on 03/19/2019.
 */
class EventCategoryList : Page(), Parcelable {
    @SerializedName("eventCategoryList")
    @Expose
    var eventCategoryList: ArrayList<CourseCategory>? = null
}