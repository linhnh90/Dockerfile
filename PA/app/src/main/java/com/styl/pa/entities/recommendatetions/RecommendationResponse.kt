package com.styl.pa.entities.recommendatetions

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.generateToken.Page

/**
 * Created by Ngatran on 03/12/2019.
 */
class RecommendationResponse : Page() {
    companion object {
        val MAX_ITEM = 8
    }

    @SerializedName("classList")
    @Expose
    var classList: ArrayList<ClassInfo>? = ArrayList()

    @SerializedName("eventList")
    @Expose
    var eventList: ArrayList<EventInfo>? = ArrayList()

    fun generateRecommendationList(): ArrayList<RecommendationItem> {
        val recommendationList = ArrayList<RecommendationItem>()
        if (classList != null && classList!!.size > MAX_ITEM) {
            recommendationList.addAll(generateRecommendationItemFromClass(MAX_ITEM))
        } else {
            if (classList != null)
                recommendationList.addAll(generateRecommendationItemFromClass(classList!!.size))
        }

        if (recommendationList.size < MAX_ITEM) {
            val remain = MAX_ITEM - recommendationList.size
            if (eventList != null && eventList!!.size > remain) {
                recommendationList.addAll(generateRecommendationItemFromEvent(remain))
            } else {
                if (eventList != null)
                    recommendationList.addAll(generateRecommendationItemFromEvent(eventList!!.size))
            }
        }

        return recommendationList
    }

    fun generateRecommendationItemFromClass(size: Int): ArrayList<RecommendationItem> {
        val recommendations = ArrayList<RecommendationItem>()
        for (i in 0 until size) {
            recommendations.add(RecommendationItem(classList!![i]))
        }

        return recommendations
    }

    fun generateRecommendationItemFromEvent(size: Int): ArrayList<RecommendationItem> {
        val recommendations = ArrayList<RecommendationItem>()
        for (i in 0 until size) {
            recommendations.add(RecommendationItem(eventList!![i]))
        }

        return recommendations
    }
}