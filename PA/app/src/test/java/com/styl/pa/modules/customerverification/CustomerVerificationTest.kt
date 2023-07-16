package com.styl.pa.modules.customerverification

import com.styl.pa.TestBase
import com.styl.pa.modules.customerverification.interactor.CustomerVerificationInteractor
import com.styl.pa.modules.customerverification.presenter.CustomerVerificationPresenter
import com.styl.pa.services.IKioskServices
import com.styl.pa.utils.MockInterceptor
import com.styl.pa.utils.MockServiceGenerator
import org.junit.Test
import org.mockito.kotlin.mock

class CustomerVerificationTest: TestBase() {

    private var view: CustomerVerificationContract.View? = null
    private var presenter: CustomerVerificationPresenter? = null
    private var interactor: CustomerVerificationInteractor? = null

    override fun setUp() {
        super.setUp()
        interactor = CustomerVerificationInteractor(null)
        interactor!!.setKioskServices(MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer))
    }

    @Test(expected = Test.None::class)
    fun verifyCustomer() {
        view = mock()
        presenter = CustomerVerificationPresenter(view, interactor)
        presenter?.verifyCustomer(MockInterceptor.TEST_EKIOSK_HEADER, "TestID")
    }

    @Test(expected = Test.None::class)
    fun verifyCustomer_id_null() {
        view = mock()
        presenter = CustomerVerificationPresenter(view, interactor)
        presenter?.verifyCustomer("", null)
    }

    @Test(expected = Test.None::class)
    fun verifyCustomer_id_empty() {
        view = mock()
        presenter = CustomerVerificationPresenter(view, interactor)
        presenter?.verifyCustomer("", "")
    }


    @Test(expected = Test.None::class)
    fun verifyCustomer_view_null() {
        view = null
        presenter = CustomerVerificationPresenter(view, interactor)
        presenter?.verifyCustomer("", "")
    }

    @Test(expected = Test.None::class)
    fun verifyCustomer_interactor_null() {
        view = null
        presenter = CustomerVerificationPresenter(view, null)
        presenter?.verifyCustomer(MockInterceptor.TEST_EKIOSK_HEADER, "TestID")
    }

    @Test(expected = Test.None::class)
    fun verifyCustomer_should_error() {
        view = mock()
        presenter = CustomerVerificationPresenter(view, interactor)
        presenter?.verifyCustomer(MockInterceptor.TEST_EKIOSK_HEADER, "123")
    }

}