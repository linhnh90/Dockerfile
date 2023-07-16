package com.styl.pa.entities.pacesRequest

import com.google.gson.annotations.SerializedName

class CourseAvailabilityRequest(
        @SerializedName("classId")
        var classId: ArrayList<String?>? = null
)
