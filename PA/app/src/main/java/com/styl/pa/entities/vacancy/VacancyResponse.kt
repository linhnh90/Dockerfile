package com.styl.pa.entities.vacancy

import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.pacesRequest.EventTicket

/**
 * Created by Ngatran on 10/25/2019.
 */
class VacancyResponse {
    @SerializedName("classVacancyList", alternate = ["eventVacancyList", "igVacancyList"])
    var vacancyInfoList: ArrayList<VacancyInfo?>? = null

    @SerializedName("quantity")
    var quantity: Int? = null

    @SerializedName("tickets")
    var tickets: ArrayList<EventTicket>? = null
}