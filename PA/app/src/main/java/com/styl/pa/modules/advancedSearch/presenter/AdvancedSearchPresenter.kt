package com.styl.pa.modules.advancedSearch.presenter

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.advancedSearch.AdvancedSearchRequest
import com.styl.pa.enums.SearchType
import com.styl.pa.modules.advancedSearch.IAdvancedSearchContact
import com.styl.pa.modules.advancedSearch.interactor.AdvancedSearchInteractor
import com.styl.pa.modules.advancedSearch.router.AdvancedSearchRouter
import com.styl.pa.modules.base.CustomBaseDialogFragment
import com.styl.pa.modules.dialog.MessageDialogFragment

/**
 * Created by Ngatran on 10/11/2018.
 */
@ExcludeFromJacocoGeneratedReport
class AdvancedSearchPresenter(var view: IAdvancedSearchContact.IView?, var context: Context?) : IAdvancedSearchContact.IPresenter {

    private var interactor: AdvancedSearchInteractor? = AdvancedSearchInteractor()

    private var router: AdvancedSearchRouter? = AdvancedSearchRouter(view as CustomBaseDialogFragment)

    override fun onDestroy() {
        interactor?.onDestroy()
        interactor = null
        view = null
        router = null
    }

    fun goToAdvancedSearch(keyword: String?, date: String?, outletIds: ArrayList<String>?,
                           outletNames: ArrayList<String>?, categories: ArrayList<String>?,
                           suitables: ArrayList<String>?, days: ArrayList<Int>?, searchType: SearchType) {
        val request = AdvancedSearchRequest()
        request.keyword = keyword ?: ""
        request.startDate = date ?: ""
        request.outletId = outletIds
        request.outletNames = outletNames
        request.categories = categories
        request.suitable = suitables
        request.day = days

        router?.navigationSearchPage(searchType.toString(), request)
    }

    fun pleaseChooseLocation() {
        val dialog = MessageDialogFragment.newInstance(R.string.please_refine_your_search,
                R.string.choose_locations, true)
        dialog.show((context as FragmentActivity).supportFragmentManager, MessageDialogFragment::class.java.simpleName)
    }
}