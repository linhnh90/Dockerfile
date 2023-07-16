package com.styl.pa.modules.feedback

import com.styl.pa.TestBase
import com.styl.pa.entities.feedback.FeedbackRequest
import com.styl.pa.modules.feedback.interactor.FeedbackInteractor
import com.styl.pa.modules.feedback.presenter.FeedbackPresenter
import com.styl.pa.services.IKioskServices
import com.styl.pa.utils.MockServiceGenerator
import com.styl.pa.utils.MySharedPref
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy

class FeedbackTest: TestBase() {

    private var presenter: FeedbackPresenter? = null
    private var view: IFeedbackContact.IView? = null
    private var interactor: FeedbackInteractor? = null

    @Before
    override fun setUp() {
        super.setUp()
        interactor = FeedbackInteractor()
        interactor?.services = MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer)
    }

    @Test(expected = Test.None::class)
    fun reportFeedbackFeedBack_view_Null() {
        view = null;
        presenter = FeedbackPresenter(view, interactor)
        presenter?.reportFeedBack(mock())
    }

    @Test(expected = Test.None::class)
    fun reportFeedbackFeedBack_token_Null() {
        view = null
        presenter = FeedbackPresenter(view, interactor)
        val spyPresenter = spy(presenter)
        val sharedPref = mock<MySharedPref>()
        doReturn(sharedPref).`when`(spyPresenter)?.getSharePref()
        Mockito.`when`(sharedPref.eKioskHeader).thenReturn(null)
        spyPresenter?.reportFeedBack(mock())
    }

    @Test(expected = Test.None::class)
    fun reportFeedbackFeedBack_token_Empty() {
        view = mock()
        presenter = FeedbackPresenter(view, interactor)
        val spyPresenter = spy(presenter)
        val sharedPref = mock<MySharedPref>()
        Mockito.`when`(view?.getContext()).thenReturn(mock())
        doReturn(sharedPref).`when`(spyPresenter)?.getSharePref()
        Mockito.`when`(sharedPref.eKioskHeader).thenReturn("")
        spyPresenter?.reportFeedBack(mock())
    }

    @Test(expected = Test.None::class)
    fun reportFeedbackFeedBack_interactor_Null() {
        view = mock()
        presenter = FeedbackPresenter(view, null)
        val spyPresenter = spy(presenter)
        val sharedPref = mock<MySharedPref>()
        Mockito.`when`(view?.getContext()).thenReturn(mock())
        doReturn(sharedPref).`when`(spyPresenter)?.getSharePref()
        Mockito.`when`(sharedPref.eKioskHeader).thenReturn("Test")
        spyPresenter?.reportFeedBack(mock())
    }

    @Test(expected = Test.None::class)
    fun reportFeedbackFeedBack_interactor_notNull_success() {
        view = mock()
        presenter = FeedbackPresenter(view, interactor)
        val spyPresenter = spy(presenter)
        val sharedPref = mock<MySharedPref>()
        Mockito.`when`(view?.getContext()).thenReturn(mock())
        doReturn(sharedPref).`when`(spyPresenter)?.getSharePref()
        Mockito.`when`(sharedPref.eKioskHeader).thenReturn("Test")
        val request = spy<FeedbackRequest>();
        spyPresenter?.reportFeedBack(request)
    }

    @Test(expected = Test.None::class)
    fun reportFeedbackFeedBack_interactor_notNull_failed() {
        view = mock()
        presenter = FeedbackPresenter(view, interactor)
        val spyPresenter = spy(presenter)
        val sharedPref = mock<MySharedPref>()
        Mockito.`when`(view?.getContext()).thenReturn(mock())
        doReturn(sharedPref).`when`(spyPresenter)?.getSharePref()
        Mockito.`when`(sharedPref.eKioskHeader).thenReturn("Test")
        val request = spy<FeedbackRequest>();
        request.payerId = "TestFeedBackId";
        spyPresenter?.reportFeedBack(request)
    }
}