package com.styl.pa.modules.search

import com.styl.pa.TestBase
import com.styl.pa.entities.advancedSearch.AdvancedSearchRequest
import com.styl.pa.entities.product.Product
import com.styl.pa.entities.search.PriceClass
import com.styl.pa.enums.SearchType
import com.styl.pa.modules.search.interactor.SearchInteractor
import com.styl.pa.modules.search.presenter.SearchPresenter
import com.styl.pa.services.IKioskServices
import com.styl.pa.utils.MockServiceGenerator
import org.junit.Test
import org.mockito.kotlin.mock

class SearchTest: TestBase() {

    private var view: ISearchContact.IView? = null
    private var interactor: SearchInteractor? = null
    private var presenter: SearchPresenter? = null

    override fun setUp() {
        super.setUp()
        interactor = SearchInteractor(null, mock())
        interactor!!.servicesKiosk = MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer)
    }

    @Test(expected = Test.None::class)
    fun searchProduct_interactor_null() {
        presenter = SearchPresenter(view, mock(), null)
        presenter!!.searchProduct("", null, PriceClass(0, 40), null, null)
    }

    @Test(expected = Test.None::class)
    fun searchProduct_interactor_notNull() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter!!.searchProduct("Test", null, PriceClass(0, 40), null, null)
    }

    @Test(expected = Test.None::class)
    fun searchProduct_err_resp() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter!!.searchProduct(null, null, PriceClass(0, 40), null, null)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_searchClass_interactor_null() {
        presenter = SearchPresenter(view, mock(), null)
        presenter?.searchByClass("", null, arrayListOf("ANG MO KIO CC"), null, null, null)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_searchClass_interactor_notNull() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchByClass("", null, arrayListOf("ANG MO KIO CC"), null, null, null)
    }


    @Test(expected = Test.None::class)
    fun searchByClass_searchClass_interactor_notNull_err_resp() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchByClass(null, null, arrayListOf("ANG MO KIO CC"), null, null, null)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_searchEvent_interactor_null() {
        presenter = SearchPresenter(view, mock(), null)
        presenter?.searchByClass("", null, arrayListOf("ANG MO KIO CC"), null, null, Product.PRODUCT_EVENT)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_searchEvent_interactor_notNull() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchByClass("", null, arrayListOf("ANG MO KIO CC"), null, null, Product.PRODUCT_EVENT)
    }


    @Test(expected = Test.None::class)
    fun searchByClass_searchEvent_interactor_notNull_err_resp() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchByClass(null, null, arrayListOf("ANG MO KIO CC"), null, null, Product.PRODUCT_EVENT)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_searchFacility_interactor_null() {
        presenter = SearchPresenter(view, mock(), null)
        presenter?.searchByClass("", null, arrayListOf("ANG MO KIO CC"), null, null, Product.PRODUCT_FACILITY)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_searchFacility_interactor_notNull() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchByClass("", null, arrayListOf("ANG MO KIO CC"), null, null, Product.PRODUCT_FACILITY)
    }


    @Test(expected = Test.None::class)
    fun searchByClass_searchFacility_interactor_notNull_err_resp() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchByClass(null, null, arrayListOf("ANG MO KIO CC"), null, null, Product.PRODUCT_FACILITY)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_searchInterestGroup_interactor_null() {
        presenter = SearchPresenter(view, mock(), null)
        presenter?.searchByClass("", null, arrayListOf("ANG MO KIO CC"), null, null, Product.PRODUCT_INTEREST_GROUP)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_searchInterestGroup_interactor_notNull() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchByClass("", null, arrayListOf("ANG MO KIO CC"), null, null, Product.PRODUCT_INTEREST_GROUP)
    }


    @Test(expected = Test.None::class)
    fun searchByClass_searchInterestGroup_interactor_notNull_err_resp() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchByClass(
            null,
            null,
            arrayListOf("ANG MO KIO CC"),
            null,
            null,
            Product.PRODUCT_INTEREST_GROUP
        )
    }

    @Test(expected = Test.None::class)
    fun searchByClass_nearestLocation_Event_interactor_null() {
        presenter = SearchPresenter(view, mock(), null)
        presenter?.searchByClass(null, "Test", 3, null, arrayListOf(0), Product.PRODUCT_EVENT)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_nearestLocation_Event_interactor_notNull() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchByClass(null, "Test", 3, null, arrayListOf(0), Product.PRODUCT_EVENT)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_nearestLocation_Facility_interactor_null() {
        presenter = SearchPresenter(view, mock(), null)
        presenter?.searchByClass(null, "Test", 3, null, arrayListOf(0), Product.PRODUCT_FACILITY)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_nearestLocation_Facility_interactor_notNull() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchByClass(null, "Test", 3, null, arrayListOf(0), Product.PRODUCT_FACILITY)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_nearestLocation_InterestGroup_interactor_null() {
        presenter = SearchPresenter(view, mock(), null)
        presenter?.searchByClass(null, "Test", 3, null, arrayListOf(0), Product.PRODUCT_INTEREST_GROUP)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_nearestLocation_InterestGroup_interactor_notNull() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchByClass(null, "Test", 3, null, arrayListOf(0), Product.PRODUCT_INTEREST_GROUP)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_nearestLocation_Class_interactor_null() {
        presenter = SearchPresenter(view, mock(), null)
        presenter?.searchByClass(null, "Test", 3, null, arrayListOf(0), null)
    }

    @Test(expected = Test.None::class)
    fun searchByClass_nearestLocation_Class_interactor_notNull() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchByClass(null, "Test", 3, null, arrayListOf(0), null)
    }

    @Test(expected = Test.None::class)
    fun getMoreOutlet_test() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.getMoreOutlet("")
    }

    @Test(expected = Test.None::class)
    fun getOutletByOutletType_interactor_null() {
        presenter = SearchPresenter(view, mock(), null)
        presenter?.getOutletByOutletType("")
    }

    @Test(expected = Test.None::class)
    fun getOutletByOutletType_interactor_notNull() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.reloadSearch()
        presenter?.getOutletByOutletType("")
    }

    @Test(expected = Test.None::class)
    fun loadMore() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.loadMore()
        val outletNames = arrayListOf("ANG MO KIO CC")
        presenter?.loadMore("", null, outletNames, PriceClass(0, 500), ArrayList(), Product.PRODUCT_COURSE)
    }

    @Test(expected = Test.None::class)
    fun loadMore_nearbyClass() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.loadMore()
        presenter?.loadMore("", "Test", 1, PriceClass(0, 500), ArrayList(), Product.PRODUCT_FACILITY)
    }


    @Test(expected = Test.None::class)
    fun advanceSearch_null_input() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.advancedSearch(null, null)
    }

    @Test(expected = Test.None::class)
    fun advanceSearch_Class_type_null() {
        presenter = SearchPresenter(view, mock(), interactor)
        val advanceSearchRequest = AdvancedSearchRequest("", arrayListOf("123"), arrayListOf("ANG MO KIO CC"), PriceClass(0, 500), ArrayList(), 1, 10)
        presenter?.advancedSearch(advanceSearchRequest, null)
    }

    @Test(expected = Test.None::class)
    fun advanceSearch_Class() {
        presenter = SearchPresenter(view, mock(), interactor)
        val advanceSearchRequest = AdvancedSearchRequest("", arrayListOf("123"), arrayListOf("ANG MO KIO CC"), PriceClass(0, 500), ArrayList(), 1, 10)
        presenter?.advancedSearch(advanceSearchRequest, SearchType.COURSES.toString())
    }

    @Test(expected = Test.None::class)
    fun advanceSearch_Event() {
        presenter = SearchPresenter(view, mock(), interactor)
        val advanceSearchRequest = AdvancedSearchRequest("", arrayListOf("123"), arrayListOf("ANG MO KIO CC"), PriceClass(0, 500), ArrayList(), 1, 10)
        presenter?.advancedSearch(advanceSearchRequest, SearchType.EVENTS.toString())
    }

    @Test(expected = Test.None::class)
    fun advanceSearch_InterestGroup() {
        presenter = SearchPresenter(view, mock(), interactor)
        val advanceSearchRequest = AdvancedSearchRequest("", arrayListOf("123"), arrayListOf("ANG MO KIO CC"), PriceClass(0, 500), ArrayList(), 1, 10)
        presenter?.advancedSearch(advanceSearchRequest, SearchType.INTEREST_GROUPS.toString())
    }

    @Test(expected = Test.None::class)
    fun loadMore_advanceSearch_null_Type_null() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.loadMore(null, null)
    }

    @Test(expected = Test.None::class)
    fun loadMore_advanceSearch_has_value_Type_Class() {
        presenter = SearchPresenter(view, mock(), interactor)
        val advanceSearchRequest = AdvancedSearchRequest("", arrayListOf("123"), arrayListOf("ANG MO KIO CC"), PriceClass(0, 500), ArrayList(), 1, 10)
        presenter?.loadMore(advanceSearchRequest, SearchType.COURSES.toString())
    }

    @Test(expected = Test.None::class)
    fun loadMore_advanceSearch_has_value_Type_Event() {
        presenter = SearchPresenter(view, mock(), interactor)
        val advanceSearchRequest = AdvancedSearchRequest("", arrayListOf("123"), arrayListOf("ANG MO KIO CC"), PriceClass(0, 500), ArrayList(), 1, 10)
        presenter?.loadMore(advanceSearchRequest, SearchType.EVENTS.toString())
    }

    @Test(expected = Test.None::class)
    fun loadMore_advanceSearch_has_value_Type_Ig() {
        presenter = SearchPresenter(view, mock(), interactor)
        val advanceSearchRequest = AdvancedSearchRequest("", arrayListOf("123"), arrayListOf("ANG MO KIO CC"), PriceClass(0, 500), ArrayList(), 1, 10)
        presenter?.loadMore(advanceSearchRequest, SearchType.INTEREST_GROUPS.toString())
    }

    @Test(expected = Test.None::class)
    fun loadMore_multi_input_null() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.loadMore(null,null, PriceClass(0, 500), null, ArrayList(), null,null)
    }

    @Test(expected = Test.None::class)
    fun loadMore_multi_input_have_value() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.loadMore("",Product.PRODUCT_COURSE, PriceClass(0, 500), null, ArrayList(), null,null)
    }

    @Test(expected = Test.None::class)
    fun searchProximity_course_view_null() {
        presenter = SearchPresenter(null, mock(), interactor)
        presenter?.searchProximity(null, "1234", 1, null)
    }

    @Test(expected = Test.None::class)
    fun searchProximity_course_interactor_null() {
        presenter = SearchPresenter(view, mock(), null)
        presenter?.searchProximity(null, "1234", 1, null)
    }

    @Test(expected = Test.None::class)
    fun searchProximity_course_null() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchProximity(null, "1234", 1, null)
    }

    @Test(expected = Test.None::class)
    fun searchProximity_course_has_values() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchProximity("", "1234", 1, PriceClass(0, 500))
    }

    @Test(expected = Test.None::class)
    fun searchProximity_with_product_type_view_null_empty_input() {
        presenter = SearchPresenter(null, mock(), interactor)
        presenter?.searchProximity(null, "1234", 1, null, null)
    }

    @Test(expected = Test.None::class)
    fun searchProximity_with_product_type_interactor_null_empty_input() {
        presenter = SearchPresenter(view, mock(), null)
        presenter?.searchProximity(null, "1234", 1, null, null)
    }

    @Test(expected = Test.None::class)
    fun searchProximity_with_product_type_view_null_input() {
        presenter = SearchPresenter(null, mock(), interactor)
        presenter?.searchProximity("", "1234", 1, PriceClass(0, 500), Product.PRODUCT_INTEREST_GROUP)
    }

    @Test(expected = Test.None::class)
    fun searchProximity_with_product_type_interest_group() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchProximity("", "1234", 1, PriceClass(0, 500), Product.PRODUCT_INTEREST_GROUP)
    }

    @Test(expected = Test.None::class)
    fun searchProximity_with_product_type_class() {
        presenter = SearchPresenter(view, mock(), interactor)
        presenter?.searchProximity("", "1234", 1, PriceClass(0, 500), Product.PRODUCT_COURSE)
    }
}