package com.styl.pa.modules.vacancyChecking.presenter

import android.content.Context
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.api.API
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.proxy.ProxyRequestHeader
import com.styl.pa.entities.vacancy.VacancyResponse
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.home.interactor.HomeInteractor
import com.styl.pa.modules.vacancyChecking.IVacancyCheckingContact
import com.styl.pa.utils.MySharedPref
import java.util.*

/**
 * Created by Ngatran on 10/15/2019.
 */
open class VacancyCheckingPresenter : IVacancyCheckingContact.IPresenter {
    private var interactor: HomeInteractor? = null
    private var vacancyView: IVacancyCheckingContact.IView? = null
    private var vacancyContext: Context? = null

    fun initVacancyPresenter(
        vacancyView: IVacancyCheckingContact.IView?,
        vacancyContext: Context?,
        interactor: HomeInteractor?
    ) {
        this.vacancyView = vacancyView
        this.vacancyContext = vacancyContext
        this.interactor = interactor
    }

    //check vacancy
    private fun getClassResult(
        classInfo: ClassInfo,
        isAddCart: Boolean,
        isQuickBook: Boolean
    ): IBaseContract.IBaseInteractorOutput<VacancyResponse> {
        return object : IBaseContract.IBaseInteractorOutput<VacancyResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: VacancyResponse?) {
                vacancyView?.dismissLoading()

                if (data != null && data.vacancyInfoList?.isNullOrEmpty() == false) {
                    val vacancyInfoList = data.vacancyInfoList!!
                    for (item in vacancyInfoList) {
                        if (item != null && item.vacancy > 0 && item.maxVacancy > 0) {
                            classInfo.setMaxVacancy(item.maxVacancy)
                            classInfo.setVacancies(item.vacancy.toString())
                            vacancyView?.handleCourseEvent(classInfo, isAddCart, isQuickBook)
                            return
                        }
                    }
                }

                vacancyView?.showErrorMessage(R.string.sorry, R.string.booking_full_for_course)
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<VacancyResponse>) {
                vacancyView?.dismissLoading()
                vacancyView?.showErrorMessage(data)
            }

        }
    }

    override fun checkClassVacancy(classInfo: ClassInfo, isAddCart: Boolean, isQuickBook: Boolean) {
        vacancyView?.showLoading()
        interactor?.getCourseAvailability(
            MySharedPref(vacancyContext).eKioskHeader,
            arrayListOf(classInfo.crmResourceId),
            getClassResult(classInfo, isAddCart, isQuickBook)
        )
    }

    override fun checkIgVacancy(igInfo: InterestGroup, isAddCart: Boolean, isQuickBook: Boolean) {
        vacancyView?.showLoading()
        interactor?.getIgAvailability(
            MySharedPref(vacancyContext).eKioskHeader,
            arrayListOf(igInfo.crmresourceId),
            getCheckIgVacancyCallback(igInfo, isAddCart, isQuickBook)
        )
    }

    //check IG vacancy
    private fun getCheckIgVacancyCallback(
        igInfo: InterestGroup,
        isAddCart: Boolean,
        isQuickBook: Boolean
    ): IBaseContract.IBaseInteractorOutput<VacancyResponse> {
        return object : IBaseContract.IBaseInteractorOutput<VacancyResponse> {
            @ExcludeFromJacocoGeneratedReport
            override fun onSuccess(data: VacancyResponse?) {
                vacancyView?.dismissLoading()

                if (data != null && data.vacancyInfoList?.isNullOrEmpty() == false) {
                    val vacancyInfoList = data.vacancyInfoList!!
                    for (item in vacancyInfoList) {
                        if (item != null &&
                            ((item.vacancy > 0 && item.maxVacancy > 0) || item.maxVacancy == 0) //maxVacancy = 0  is unlimited
                        ) {
                            igInfo.maxVacancy = item.maxVacancy
                            igInfo.vacancies = item.vacancy.toString()
                            vacancyView?.handleIgEvent(igInfo, isAddCart, isQuickBook)
                            return
                        }
                    }
                }

                vacancyView?.showErrorMessage(R.string.sorry, R.string.booking_full_for_ig)
            }

            @ExcludeFromJacocoGeneratedReport
            override fun onError(data: BaseResponse<VacancyResponse>) {
                vacancyView?.dismissLoading()
                vacancyView?.showErrorMessage(data)
            }

        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun getVacancyRequest(
        productIds: ArrayList<String?>,
        isClass: Boolean
    ): ProxyRequest<TreeMap<String, String>> {
        val header = ProxyRequestHeader(ProxyRequestHeader.JSON_TYPE)
        val url: String
        val map = TreeMap<String, String>()
        val format: String
        if (isClass) {
            format = "classList[%s]"
            url = API.uriGetClassVacancy
        } else {
            format = "eventList[%s]"
            url = API.uriGetEventVacancy
        }

        for (i in 0 until productIds.size) {
            map.put(String.format(format, i), productIds[i] ?: "")
        }

        return ProxyRequest(
            header, map, ProxyRequest.POST_METHOD,
            "", url, true
        )
    }
}