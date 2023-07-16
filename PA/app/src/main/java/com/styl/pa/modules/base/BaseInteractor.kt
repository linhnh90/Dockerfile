package com.styl.pa.modules.base

import android.database.Observable
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.GeneralException
import com.styl.pa.entities.cart.CartData
import com.styl.pa.entities.generateToken.Data
import com.styl.pa.entities.participant.ParticipantResponse
import com.styl.pa.entities.payment.PaymentData
import com.styl.pa.entities.proxy.ProxyResponse
import com.styl.pa.utils.LogManager
import com.styl.pa.utils.StoreLogsUtils
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by trangpham on 8/29/2018
 */
@ExcludeFromJacocoGeneratedReport
abstract class BaseInteractor {
    companion object {
        const val CONVERT_ERROR = 1415
        const val SERVICE_NOT_AVAILABLE = "Service temporarily unavailable. Please approach our CC staff for further assistance."
    }

    fun <T> onResponse(
            resultType: Response<T>,
            interactorOutput: IBaseContract.IBaseInteractorOutput<T>?
    ) {
        if (resultType.isSuccessful) {
            try {
                val errorCode = Integer.parseInt(resultType.headers().get("errorCode"))
                val errorMessage = resultType.headers().get("errorMessage")

                if (errorCode == 0) {
                    val data = resultType.body()
                    interactorOutput?.onSuccess(data)
                } else {
                    val baseResponse = BaseResponse<T>(errorCode, errorMessage, null)
                    StoreLogsUtils.logErrorMessage(errorCode, errorMessage)
                    interactorOutput?.onError(baseResponse)
                }
            } catch (e: NumberFormatException) {
                LogManager.i("Format number failed!")
            }
        } else {
            val errorCode = resultType.code()
            val errorMessage = resultType.message()
            StoreLogsUtils.logErrorMessage(errorCode, errorMessage)
            val baseResponse = BaseResponse<T>(errorCode, errorMessage, null)
            interactorOutput?.onError(baseResponse)
        }
    }

    fun <T> isMapSuccess(resultType: Response<T>): Boolean {
        if (resultType.isSuccessful) {
            val errorCode = Integer.parseInt(resultType.headers().get("errorCode"))
            val errorMessage = resultType.headers().get("errorMessage")

            if (errorCode == 0) {
                return true
            } else {
                throw GeneralException(errorCode, errorMessage)
            }
        } else {
            throw GeneralException(resultType.code(), resultType.message())
        }
    }

    fun <T> onError(t: Throwable, interactorOutput: IBaseContract.IBaseInteractorOutput<T>?) {
        val errorCode = -1
        var errorCodeLog = -1
        var errorMessageLog = ""
        if (t is GeneralException) {
            val baseResponse = BaseResponse<T>(
                    t.code!!, t.message, 0
            )
            errorCodeLog = t.code ?: -1
            errorMessageLog = "General Error ${t.code}"
            interactorOutput?.onError(baseResponse)
        } else if (t is ConnectException || t is SocketTimeoutException ||
                t is UnknownHostException || t is IOException
        ) {
            val baseResponse = BaseResponse<T>(
                    errorCode, null,
                    R.string.cannot_connect_server
            )
            errorCodeLog = errorCode
            errorMessageLog = SERVICE_NOT_AVAILABLE
            interactorOutput?.onError(baseResponse)
        } else if (t is JsonSyntaxException || t is IllegalStateException) {
            val errorMessage = SERVICE_NOT_AVAILABLE
            val baseResponse = BaseResponse<T>(CONVERT_ERROR, errorMessage, 0)
            errorCodeLog = CONVERT_ERROR
            errorMessageLog = "Json syntax error or something wrong."
            interactorOutput?.onError(baseResponse)
        } else {
            val errorMessage = t.message
            val baseResponse = BaseResponse<T>(errorCode, errorMessage, 0)
            errorCodeLog = errorCode
            errorMessageLog = "An error occurred. Please check the response."
            interactorOutput?.onError(baseResponse)
        }
        StoreLogsUtils.logErrorMessage(errorCodeLog, errorMessageLog)
    }

    fun <T> handleResponse(result: Response<Data<T>>,
                           listener: IBaseContract.IBaseInteractorOutput<T>?) {
        if (result.isSuccessful) {
            var data = result.body() as Data<T>
            if (TextUtils.isEmpty(data.getErrorMessages())) {
                listener?.onSuccess(data.getEntity())
            } else {
                val baseResponse = BaseResponse<T>(result.code(), result.message(), null)
                listener?.onError(baseResponse)
            }
        } else {
            val baseResponse = BaseResponse<T>(result.code(), result.message(), null)
            listener?.onError(baseResponse)
        }
    }

    fun handleLoadCartResponse(result: Response<ProxyResponse<LinkedTreeMap<String, Any?>>>,
                               listener: IBaseContract.IBaseInteractorOutput<Data<CartData>>?) {

        if (result.isSuccessful) {
            val data = result.body() as ProxyResponse<LinkedTreeMap<String, Any?>>
            if (data.httpStatusCode == HttpURLConnection.HTTP_OK) {
                val gson = Gson()
                val type = object : TypeToken<Data<CartData>>(){}.type
                val cartResponse = gson.fromJson<Data<CartData>>(
                        gson.toJson(data.body),
                        type
                )
                listener?.onSuccess(cartResponse)
            } else {
                val errorCode = data.httpStatusCode ?: -1
                val errorMessage = SERVICE_NOT_AVAILABLE
                val baseResponse = BaseResponse<Data<CartData>>(
                        errorCode,
                        errorMessage,
                        R.string.service_temporarily_unavailable
                )
                StoreLogsUtils.logErrorMessage(errorCode, SERVICE_NOT_AVAILABLE)
                listener?.onError(baseResponse)
            }
        } else {
            val baseResponse = BaseResponse<Data<CartData>>(result.code(), result.message(), null)
            StoreLogsUtils.logErrorMessage(result.code(), result.message())
            listener?.onError(baseResponse)
        }
    }

    fun <T> handlePaymentResponseProxy(result: Response<ProxyResponse<PaymentData<T>>>,
                                listener: IBaseContract.IBaseInteractorOutput<T>?) {
        if (result.isSuccessful) {
            val data = result.body() as ProxyResponse<PaymentData<T>>

            if (data.httpStatusCode == HttpURLConnection.HTTP_OK) {
                if (data.body?.success != null && data.body?.success!!)
                    listener?.onSuccess(data.body?.result)
                else {
                    var errorCode = 0
                    if (data.body?.errorCode != null)
                        errorCode = data.body?.errorCode!!

                    var baseResponse = BaseResponse<T>(errorCode, data.body?.message, null)

                    if (data.body?.message.isNullOrEmpty()) {
                        errorCode = -1
                        baseResponse = BaseResponse(errorCode, data.body?.message, R.string.unknown_error)
                    }

                    StoreLogsUtils.logErrorMessage(errorCode, data.body?.message)
                    listener?.onError(baseResponse)
                }
            } else {
                val baseResponse = BaseResponse<T>(-1, null, R.string.service_temporarily_unavailable)
                StoreLogsUtils.logErrorMessage(-1, SERVICE_NOT_AVAILABLE)
                listener?.onError(baseResponse)
            }
        } else {
            val baseResponse = BaseResponse<T>(result.code(), result.message(), null)
            StoreLogsUtils.logErrorMessage(result.code(), result.message())
            listener?.onError(baseResponse)
        }
    }

    fun <T> handleResponseProxy(result: Response<ProxyResponse<Data<T>>>,
                                listener: IBaseContract.IBaseInteractorOutput<T>?) {
        if (result.isSuccessful) {
            val data = result.body() as ProxyResponse<Data<T>>

            if (data.httpStatusCode == HttpURLConnection.HTTP_OK) {
                if (data.body?.getHasError() != null && !data.body?.getHasError()!!)
                    listener?.onSuccess(data.body?.getEntity())
                else {
                    var errorCode = 0
                    if (data.body?.getResponseStatusCode() != null)
                        errorCode = data.body?.getResponseStatusCode()!!

                    var baseResponse = BaseResponse<T>(errorCode, data.body?.getErrorMessages(), null)

                    if (data.body?.getErrorMessages().isNullOrEmpty()) {
                        errorCode = -1
                        baseResponse = BaseResponse(errorCode, data.body?.getErrorMessages(), R.string.unknown_error)
                    }

                    if (data.body?.getEntity() is ParticipantResponse) {
                        baseResponse = BaseResponse(errorCode, data.body?.getErrorMessages(), data.body?.getEntity())
                    }

                    StoreLogsUtils.logErrorMessage(errorCode, data.body?.getErrorMessages())
                    listener?.onError(baseResponse)
                }
            } else {
                val errorCode = data.httpStatusCode ?: -1
                val errorMessage = data.body?.getErrorMessages()
                val baseResponse = BaseResponse<T>(
                        errorCode,
                        errorMessage ?: SERVICE_NOT_AVAILABLE,
                        R.string.service_temporarily_unavailable
                )
                StoreLogsUtils.logErrorMessage(errorCode, SERVICE_NOT_AVAILABLE)
                listener?.onError(baseResponse)
            }
        } else {
            val baseResponse = BaseResponse<T>(result.code(), result.message(), null)
            StoreLogsUtils.logErrorMessage(result.code(), result.message())
            listener?.onError(baseResponse)
        }
    }

    fun <T> handleResponseProxyGenerateReceipt(result: Response<ProxyResponse<Data<T>>>,
                                               listener: IBaseContract.IBaseInteractorOutput<T>?) {
        if (result.isSuccessful) {
            val data = result.body() as ProxyResponse<Data<T>>

            if (data.httpStatusCode == HttpURLConnection.HTTP_OK) {
                if (data.body?.getHasError() != null && !data.body?.getHasError()!!)
                    listener?.onSuccess(data.body?.getEntity())
                else {
                    var errorCode = 0
                    if (data.body?.getResponseStatusCode() != null)
                        errorCode = data.body?.getResponseStatusCode()!!

                    var baseResponse = BaseResponse<T>(errorCode, data.body?.getErrorMessages(), null)

                    if (data.body?.getErrorMessages().isNullOrEmpty()) {
                        errorCode = -1
                        baseResponse = BaseResponse(errorCode, data.body?.getErrorMessages(), R.string.unknown_error)
                    }

                    StoreLogsUtils.logErrorMessage(errorCode, data.body?.getErrorMessages())
                    listener?.onError(baseResponse)
                }
            } else {
                var errorCode = -1
                var errorMessage: String? = null
                if (data.body?.getResponseStatusCode() != null) {
                    errorCode = data.body?.getResponseStatusCode()!!
                }

                errorMessage = data.body?.getErrorMessages()

                StoreLogsUtils.logErrorMessage(errorCode, errorMessage
                        ?: SERVICE_NOT_AVAILABLE)
                val baseResponse = BaseResponse<T>(errorCode, errorMessage, R.string.service_temporarily_unavailable)
                listener?.onError(baseResponse)
            }
        } else {
            val baseResponse = BaseResponse<T>(result.code(), result.message(), null)
            StoreLogsUtils.logErrorMessage(result.code(), result.message())
            listener?.onError(baseResponse)
        }
    }


    fun <T> handleResponseSearch(result: Response<T>,
                                 listener: IBaseContract.IBaseInteractorOutput<T>?) {
        if (result.isSuccessful) {


            val baseResponse = BaseResponse<T>(result.headers())

            if (baseResponse != null && baseResponse!!.errorCode !== 0) {
                listener?.onError(baseResponse)
                return
            }

            listener?.onSuccess(result.body())
        } else {
            val baseResponse = BaseResponse<T>(result.code(), result.message(), null)
            StoreLogsUtils.logErrorMessage(result.code(), result.message())
            listener?.onError(baseResponse)
        }
    }


    fun <T> customConcatMap(result: Response<Data<T>>, observable: Observable<R>): Observable<R> {
        if (result.isSuccessful) {
            val data = result.body() as Data<T>
            if (TextUtils.isEmpty(data.getErrorMessages())) {
                val entity = data.getEntity()
                if (entity != null) {
                    return observable
                } else {
                    throw Exception()
                }
            } else {
                throw GeneralException(result.code(), result.message())
            }
        } else {
            throw GeneralException(result.code(), result.message())
        }
    }
}