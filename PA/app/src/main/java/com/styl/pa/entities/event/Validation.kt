package com.styl.pa.entities.event

import com.google.gson.annotations.SerializedName

class Validation {
    @SerializedName("Editable")
    var editable: Boolean? = null

    @SerializedName("Required")
    var required: Boolean? = null

    @SerializedName("ErrorMessage")
    var errorMessage: String? = null

    @SerializedName("Min")
    var min: String? = null

    @SerializedName("Max")
    var max: String? = null

    @SerializedName("Regex")
    var regex: String? = null

    @SerializedName("ValidationErrorMessage")
    var validationErrorMessage: String? = null

}