package com.styl.pa.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.entities.healthDevice.HealthDeviceRequest
import com.styl.pa.entities.healthDevice.InfoHealthDevice
import com.styl.pa.modules.main.presenter.MainPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by Ngatran on 12/09/2019.
 */
@ExcludeFromJacocoGeneratedReport
class RangeTimeHealthStatusUtils {
    companion object {
        var startTime = 22
        var endTime = 7
    }

    private var healthStatusWaitingList: ArrayList<InfoHealthDevice>? = null
    private var compositeDisposable: CompositeDisposable? = null

    private var mainPresenter: MainPresenter? = null

    constructor(mainPresenter: MainPresenter) {
        this.mainPresenter = mainPresenter
    }

    private fun checkExistsHealthStatus(status: InfoHealthDevice): Boolean {
        var exists: Boolean = false
        for (item in healthStatusWaitingList ?: ArrayList()) {
            if (!status.getName().isNullOrEmpty() && !item.getName().isNullOrEmpty() &&
                    true == status.getName()?.equals(item.getName())) {
                if (status.getStatus() == item.getStatus()) {
                    exists = true
                }

                if (item.isDisconnect() && status.isConnect() ||
                        item.isConnect() && status.isDisconnect()) {
                    healthStatusWaitingList?.remove(item)
                    return exists
                }
            }
        }

        return exists
    }

    fun checkHealthStatusWaitingService(request: HealthDeviceRequest) {
        if (!request.getDevices().isNullOrEmpty()) {

            if (healthStatusWaitingList == null) {
                healthStatusWaitingList = ArrayList()

                startHealthStatusWaitingService()
            }

            for (status in request.getDevices() ?: ArrayList()) {
                if (!checkExistsHealthStatus(status)) {
                    healthStatusWaitingList?.add(status)
                }
            }

        }

    }

    private fun startHealthStatusWaitingService() {
        if (compositeDisposable == null) {
            compositeDisposable = CompositeDisposable()
        }

        compositeDisposable?.clear()

        val disposable = Observable.timer(getWaitTime(), TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = @ExcludeFromJacocoGeneratedReport {
                            if (!healthStatusWaitingList.isNullOrEmpty()) {
                                val typeToken = @ExcludeFromJacocoGeneratedReport object : TypeToken<ArrayList<InfoHealthDevice>>() {}
                                val type = typeToken.type
                                val list = Gson().fromJson<ArrayList<InfoHealthDevice>>(Gson().toJson(healthStatusWaitingList), type)
                                mainPresenter?.deviceHealthUpdate(HealthDeviceRequest(list))
                            }

                            mainPresenter?.destroyRangeTimeHealthStatusUtils()
                        }
                )
        compositeDisposable?.add(disposable)
    }

    private fun getWaitTime(): Long {
        val currentTime = System.currentTimeMillis() / 1000

        val c = Calendar.getInstance()

        c.set(Calendar.HOUR_OF_DAY, endTime)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        val endTime = c.timeInMillis / 1000

        if (currentTime >= endTime) {
            c.add(Calendar.DAY_OF_YEAR, 1)
            return (((c.timeInMillis / 1000) - currentTime) + 10)
        }

        return ((endTime - currentTime) + 10)
    }

    fun destroyHealthStatusWaitingService() {
        healthStatusWaitingList?.clear()
        healthStatusWaitingList = null

        compositeDisposable?.clear()
        compositeDisposable?.dispose()
        compositeDisposable = null
    }
}