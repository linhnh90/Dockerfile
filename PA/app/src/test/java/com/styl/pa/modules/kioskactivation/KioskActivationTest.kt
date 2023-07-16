package com.styl.pa.modules.kioskactivation

import com.styl.pa.TestBase
import com.styl.pa.modules.kioskactivation.interactor.KioskActivationInteractor
import com.styl.pa.modules.kioskactivation.presenter.KioskActivationPresenter
import com.styl.pa.services.IKioskServices
import com.styl.pa.utils.MockServiceGenerator
import org.junit.Test
import org.mockito.kotlin.mock

class KioskActivationTest: TestBase() {

    private var view: IKioskActivationContract.IView? = null
    private var interactor: KioskActivationInteractor? = null
    private var presenter: KioskActivationPresenter? = null

    @Test(expected = Test.None::class)
    fun activateKiosk_view_null_interactor_null() {
        presenter = KioskActivationPresenter(view, interactor)
        presenter?.activateKiosk("TestId", "TestCode")
    }

    @Test(expected = Test.None::class)
    fun activateKiosk_success_view_null() {
        interactor = KioskActivationInteractor(mock())
        interactor?.setKioskService(MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer))
        presenter = KioskActivationPresenter(view, interactor)
        interactor?.output = presenter
        presenter?.activateKiosk("TestId", "TestCode")
    }

    @Test(expected = Test.None::class)
    fun activateKiosk_success_code_empty() {
        interactor = KioskActivationInteractor(mock())
        interactor?.setKioskService(MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer))
        presenter = KioskActivationPresenter(view, interactor)
        interactor?.output = presenter
        presenter?.activateKiosk("TestId", "")
    }

    @Test(expected = Test.None::class)
    fun activateKiosk_success_code_null() {
        interactor = KioskActivationInteractor(mock())
        interactor?.setKioskService(MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer))
        presenter = KioskActivationPresenter(view, interactor)
        interactor?.output = presenter
        presenter?.activateKiosk("TestId", null)
    }

    @Test(expected = Test.None::class)
    fun activateKiosk_success_view_NotNull() {
        view = mock()
        interactor = KioskActivationInteractor(mock())
        interactor?.setKioskService(MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer))
        presenter = KioskActivationPresenter(view, interactor)
        interactor?.output = presenter
        presenter?.activateKiosk("TestId", "TestCode")
    }

    @Test(expected = Test.None::class)
    fun activateKiosk_success_view_NotNull_code_empty() {
        view = mock()
        interactor = KioskActivationInteractor(mock())
        interactor?.setKioskService(MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer))
        presenter = KioskActivationPresenter(view, interactor)
        interactor?.output = presenter
        presenter?.activateKiosk("TestId", "")
    }

    @Test(expected = Test.None::class)
    fun activateKiosk_success_view_NotNull_code_null() {
        view = mock()
        interactor = KioskActivationInteractor(mock())
        interactor?.setKioskService(MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer))
        presenter = KioskActivationPresenter(view, interactor)
        interactor?.output = presenter
        presenter?.activateKiosk("TestId", null)
    }

    @Test(expected = Test.None::class)
    fun activateKiosk_error_view_NotNull() {
        view = mock()
        interactor = KioskActivationInteractor(mock())
        interactor?.setKioskService(MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer))
        presenter = KioskActivationPresenter(view, interactor)
        interactor?.output = presenter
        presenter?.activateKiosk(null, "Test")
    }

    @Test(expected = Test.None::class)
    fun activateKiosk_error_view_Null() {
        interactor = KioskActivationInteractor(mock())
        interactor?.setKioskService(MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer))
        presenter = KioskActivationPresenter(view, interactor)
        interactor?.output = presenter
        presenter?.activateKiosk(null, "Test")
    }

    @Test
    fun onDestroy_interactor_notNull() {
        view = mock()
        interactor = KioskActivationInteractor(mock())
        presenter = KioskActivationPresenter(view, interactor)
        presenter?.onDestroy()
        assert(presenter?.view == null)
    }

    @Test
    fun onDestroy_interactor_Null() {
        view = mock()
        presenter = KioskActivationPresenter(view, null)
        presenter?.onDestroy()
        assert(presenter?.view == null)
    }


}