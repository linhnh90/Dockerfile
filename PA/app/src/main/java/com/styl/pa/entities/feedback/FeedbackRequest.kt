package com.styl.pa.entities.feedback

import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.rating.RatingRequest


/**
 * Created by Ngatran on 09/27/2019.
 */
class FeedbackRequest : RatingRequest {
    @SerializedName("Comment")
    private var comment: String? = null

    constructor()

    constructor(ratingRequest: RatingRequest?, comment: String?) {
        this.level = ratingRequest?.level
        this.reused = ratingRequest?.reused
        this.age = ratingRequest?.age
        this.gender = ratingRequest?.gender
        this.payerId = ratingRequest?.payerId
        if (!comment.isNullOrEmpty()) {
            this.comment = comment
        }
    }
}