package com.styl.pa.entities.generateToken

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data<T> {
    @SerializedName("responseStatusCode")
    @Expose
    private var responseStatusCode: Int? = -1
    @SerializedName("hasError")
    @Expose
    private var hasError: Boolean? = false
    @SerializedName("errorMessages")
    @Expose
    private var errorMessages: String? = ""
    @SerializedName(value = "entity",  alternate = ["response"])
    @Expose
    private var entity: T? = null

    fun getResponseStatusCode(): Int? {
        return responseStatusCode
    }

    fun setResponseStatusCode(responseStatusCode: Int?) {
        this.responseStatusCode = responseStatusCode
    }

    fun getHasError(): Boolean? {
        return hasError
    }

    fun setHasError(hasError: Boolean?) {
        this.hasError = hasError
    }

    fun getErrorMessages(): String? {
        return errorMessages
    }

    fun setErrorMessages(errorMessages: String) {
        this.errorMessages = errorMessages
    }

    fun getEntity(): T? {
        return entity
    }

    fun setEntity(entity: T) {
        this.entity = entity
    }
}