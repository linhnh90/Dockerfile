package com.styl.pa.modules.rating

import com.styl.pa.TestBase
import com.styl.pa.entities.rating.RatingRequest
import com.styl.pa.modules.rating.interactor.RatingInteractor
import com.styl.pa.modules.rating.presenter.RatingPresenter
import com.styl.pa.services.IKioskServices
import com.styl.pa.utils.MockServiceGenerator
import com.styl.pa.utils.MySharedPref
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import kotlin.math.exp

class RatingTest: TestBase() {

    private var presenter: RatingPresenter? = null
    private var view: IRatingContact.IView? = null
    private var interactor: RatingInteractor? = null

    @Before
    override fun setUp() {
        super.setUp()
        interactor = RatingInteractor()
        interactor?.services = MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer)
    }

    @Test(expected = Test.None::class)
    fun reportRatingFeedBack_view_Null() {
        view = null;
        presenter = RatingPresenter(view, interactor)
        presenter?.reportRatingFeedBack(mock())
    }

    @Test(expected = Test.None::class)
    fun reportRatingFeedBack_token_Null() {
        view = null
        presenter = RatingPresenter(view, interactor)
        val spyPresenter = spy(presenter)
        val sharedPref = mock<MySharedPref>()
        doReturn(sharedPref).`when`(spyPresenter)?.getSharePref()
        Mockito.`when`(sharedPref.eKioskHeader).thenReturn(null)
        spyPresenter?.reportRatingFeedBack(mock())
    }

    @Test(expected = Test.None::class)
    fun reportRatingFeedBack_token_Empty() {
        view = mock()
        presenter = RatingPresenter(view, interactor)
        val spyPresenter = spy(presenter)
        val sharedPref = mock<MySharedPref>()
        Mockito.`when`(view?.getContext()).thenReturn(mock())
        doReturn(sharedPref).`when`(spyPresenter)?.getSharePref()
        Mockito.`when`(sharedPref.eKioskHeader).thenReturn("")
        spyPresenter?.reportRatingFeedBack(mock())
    }

    @Test(expected = Test.None::class)
    fun reportRatingFeedBack_interactor_Null() {
        view = mock()
        presenter = RatingPresenter(view, null)
        val spyPresenter = spy(presenter)
        val sharedPref = mock<MySharedPref>()
        Mockito.`when`(view?.getContext()).thenReturn(mock())
        doReturn(sharedPref).`when`(spyPresenter)?.getSharePref()
        Mockito.`when`(sharedPref.eKioskHeader).thenReturn("Test")
        spyPresenter?.reportRatingFeedBack(mock())
    }

    @Test(expected = Test.None::class)
    fun reportRatingFeedBack_interactor_notNull_success() {
        view = mock()
        presenter = RatingPresenter(view, interactor)
        val spyPresenter = spy(presenter)
        val sharedPref = mock<MySharedPref>()
        Mockito.`when`(view?.getContext()).thenReturn(mock())
        doReturn(sharedPref).`when`(spyPresenter)?.getSharePref()
        Mockito.`when`(sharedPref.eKioskHeader).thenReturn("Test")
        val request = spy<RatingRequest>();
        spyPresenter?.reportRatingFeedBack(request)
    }

    @Test(expected = Test.None::class)
    fun reportRatingFeedBack_interactor_notNull_failed() {
        view = mock()
        presenter = RatingPresenter(view, interactor)
        val spyPresenter = spy(presenter)
        val sharedPref = mock<MySharedPref>()
        Mockito.`when`(view?.getContext()).thenReturn(mock())
        doReturn(sharedPref).`when`(spyPresenter)?.getSharePref()
        Mockito.`when`(sharedPref.eKioskHeader).thenReturn("Test")
        val request = spy<RatingRequest>();
        request.payerId = "TestPayerId";
        spyPresenter?.reportRatingFeedBack(request)
    }
}