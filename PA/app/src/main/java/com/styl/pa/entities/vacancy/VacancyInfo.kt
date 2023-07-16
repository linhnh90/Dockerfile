package com.styl.pa.entities.vacancy

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngatran on 10/25/2019.
 */
class VacancyInfo {
    @SerializedName("classid", alternate = ["classId, eventid, igId"])
    var id: String? = null

    @SerializedName("vacancy")
    var vacancy: Int = 0

    @SerializedName("MaxVacancy", alternate = ["maxVacancy"])
    var maxVacancy: Int = 0
}