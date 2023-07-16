package com.styl.pa.entities.generateToken

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Course {
    @SerializedName("courseId")
    @Expose
    private var courseId: String? = null
    @SerializedName("courseCode")
    @Expose
    private var courseCode: String? = null
    @SerializedName("courseCategoryId")
    @Expose
    private var courseCategoryId: String? = null
    @SerializedName("outletId")
    @Expose
    private var outletId: String? = null
    @SerializedName("courseTitle")
    @Expose
    private var courseTitle: String? = null
    @SerializedName("courseStatus")
    @Expose
    private var courseStatus: String? = null
    @SerializedName("courseDescription")
    @Expose
    private var courseDescription: String? = null
    @SerializedName("imageUrl")
    @Expose
    private var imageUrl: String? = null

    fun getCourseId(): String? {
        return courseId
    }

    fun setCourseId(courseId: String) {
        this.courseId = courseId
    }

    fun getCourseCode(): String? {
        return courseCode
    }

    fun setCourseCode(courseCode: String) {
        this.courseCode = courseCode
    }

    fun getCourseCategoryId(): String? {
        return courseCategoryId
    }

    fun setCourseCategoryId(courseCategoryId: String) {
        this.courseCategoryId = courseCategoryId
    }

    fun getOutletId(): String? {
        return outletId
    }

    fun setOutletId(outletId: String) {
        this.outletId = outletId
    }

    fun getCourseTitle(): String? {
        return courseTitle
    }

    fun setCourseTitle(courseTitle: String) {
        this.courseTitle = courseTitle
    }

    fun getCourseStatus(): String? {
        return courseStatus
    }

    fun setCourseStatus(courseStatus: String) {
        this.courseStatus = courseStatus
    }

    fun getCourseDescription(): String? {
        return courseDescription
    }

    fun setCourseDescription(courseDescription: String) {
        this.courseDescription = courseDescription
    }

    fun getImageUrl(): String? {
        return imageUrl
    }

    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }
}