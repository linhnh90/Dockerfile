package com.styl.pa.modules.payment

import com.styl.pa.TestBase
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.pacesRequest.PaymentRefRequest
import com.styl.pa.modules.payment.interactor.PaymentInteractor
import com.styl.pa.modules.payment.presenter.PaymentPresenter
import com.styl.pa.services.IKioskServices
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.MockInterceptor
import com.styl.pa.utils.MockServiceGenerator
import com.styl.pa.utils.TestUtils
import org.junit.Test
import org.mockito.kotlin.mock

class PaymentTest: TestBase() {

    private var view: IPaymentContract.IView? = null
    private var interactor: PaymentInteractor? = null
    private var presenter: PaymentPresenter? = null

    override fun setUp() {
        super.setUp()
        interactor = PaymentInteractor(null)
        interactor?.setServices(MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer))
    }

    @Test(expected = Test.None::class)
    fun createPaymentReference_view_null_interactor_null() {
        presenter = PaymentPresenter(null, null)
        presenter?.createPaymentReference(MockInterceptor.TEST_EKIOSK_HEADER, PaymentRefRequest(null, null, null, null))
    }

    @Test(expected = Test.None::class)
    fun createPaymentReference_view_notNull_interactor_NotNull() {
        view = mock()
        presenter = PaymentPresenter(view, interactor)
        val paymentRequest = PaymentRefRequest("Test", "TestTxnNo", 0f, "2021-10-10")
        presenter?.createPaymentReference(MockInterceptor.TEST_EKIOSK_HEADER, paymentRequest)
    }

    @Test(expected = Test.None::class)
    fun createPaymentReference_error_resp() {
        view = mock()
        presenter = PaymentPresenter(view, interactor)
        val paymentRequest = PaymentRefRequest(null, "TestTxnNo", 0f, "2021-10-10")
        presenter?.createPaymentReference(MockInterceptor.TEST_EKIOSK_HEADER, paymentRequest)
    }

    @Test(expected = Test.None::class)
    fun prepareCheckout_view_Null_interactor_Null() {
        presenter = PaymentPresenter(null, null)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        assert(customerInfo != null)
        presenter?.prepareCheckout(MockInterceptor.TEST_EKIOSK_HEADER, "TestCartId", customerInfo!!, null, null)
    }

    @Test(expected = Test.None::class)
    fun prepareCheckout_view_NotNull_interactor_NotNull() {
        view = mock()
        presenter = PaymentPresenter(view, interactor)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        assert(customerInfo != null)
        presenter?.prepareCheckout(MockInterceptor.TEST_EKIOSK_HEADER, "TestCartId", customerInfo!!, null, null)
    }

    @Test(expected = Test.None::class)
    fun prepareCheckout_error_resp() {
        view = mock()
        presenter = PaymentPresenter(view, interactor)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        assert(customerInfo != null)
        customerInfo!!.mCustomerId = null
        customerInfo.mMobile = null
        presenter?.prepareCheckout(MockInterceptor.TEST_EKIOSK_HEADER, "TestCartId", customerInfo, null, null)
    }

    @Test(expected = Test.None::class)
    fun onTerminalChanged_connected() {
        view = mock()
        presenter = PaymentPresenter(view, interactor)
        presenter?.onTerminalChanged(true)
    }

    @Test(expected = Test.None::class)
    fun onTerminalChanged_view_NotNull() {
        view = mock()
        presenter = PaymentPresenter(view, interactor)
        presenter?.onTerminalChanged(false)
    }

    @Test(expected = Test.None::class)
    fun onTerminalChanged_view_Null() {
        view = null
        presenter = PaymentPresenter(view, interactor)
        presenter?.onTerminalChanged(false)
    }


    @Test
    fun onDestroy_interactor_Null() {
        view = mock()
        presenter = PaymentPresenter(view, null)
        presenter?.onDestroy()
        assert(presenter?.view == null)
    }

    @Test
    fun onDestroy_interactor_NotNull() {
        view = mock()
        presenter = PaymentPresenter(view, interactor)
        presenter?.onDestroy()
        assert(presenter?.view == null)
    }

}