package com.styl.pa.modules.advancedSearch.router

import androidx.fragment.app.FragmentManager
import com.styl.pa.R
import com.styl.pa.entities.advancedSearch.AdvancedSearchRequest
import com.styl.pa.enums.TagName
import com.styl.pa.modules.advancedSearch.IAdvancedSearchContact
import com.styl.pa.modules.base.CustomBaseDialogFragment
import com.styl.pa.modules.search.view.SearchPageFragment

/**
 * Created by Ngatran on 10/11/2018.
 */
class AdvancedSearchRouter(var view: CustomBaseDialogFragment?) : IAdvancedSearchContact.IRouter {
    override fun navigationSearchPage(searchType: String, request: AdvancedSearchRequest?) {
        view?.fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val f = SearchPageFragment.newInstance(searchType, request)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.SearchPageFragment.value)
        ft?.addToBackStack(TagName.SearchPageFragment.value)
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }
}