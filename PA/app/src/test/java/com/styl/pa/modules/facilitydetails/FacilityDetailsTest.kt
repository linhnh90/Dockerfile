package com.styl.pa.modules.facilitydetails

import com.styl.pa.TestBase
import com.styl.pa.modules.facilityDetails.IFacilityDetailsContact
import com.styl.pa.modules.facilityDetails.interactor.FacilityDetailsInteractor
import com.styl.pa.modules.facilityDetails.presenter.FacilityDetailsPresenter
import com.styl.pa.services.IKioskServices
import com.styl.pa.utils.MockServiceGenerator
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock

class FacilityDetailsTest: TestBase() {

    private var view: IFacilityDetailsContact.IView? = null
    private var interactor: FacilityDetailsInteractor? = null
    private var presenter: FacilityDetailsPresenter? = null

    override fun setUp() {
        super.setUp()
    }

    @Test(expected = Test.None::class)
    fun getFacilityAvailability_view_null() {
        presenter = FacilityDetailsPresenter(null, mock())
        presenter?.getFacilityAvailability("Test", "2021-10-19")
    }

    @Test(expected = Test.None::class)
    fun getFacilityAvailability_view_NotNull_token_Null() {
        view = mock()
        presenter = FacilityDetailsPresenter(view, mock())
        Mockito.`when`(view?.getToken()).thenReturn(null)
        presenter?.getFacilityAvailability("Test", "2021-10-19")
    }

    @Test(expected = Test.None::class)
    fun getFacilityAvailability_view_NotNull_token_Empty() {
        view = mock()
        presenter = FacilityDetailsPresenter(view, mock())
        Mockito.`when`(view?.getToken()).thenReturn("")
        presenter?.getFacilityAvailability("Test", "2021-10-19")
    }

    @Test(expected = Test.None::class)
    fun getFacilityAvailability_facilityId_null_selectedDate_null() {
        view = mock()
        presenter = FacilityDetailsPresenter(view, mock())
        Mockito.`when`(view?.getToken()).thenReturn("Test")
        presenter?.getFacilityAvailability(null, null)
    }

    @Test(expected = Test.None::class)
    fun getFacilityAvailability_facilityId_notNull_selectedDate_null() {
        view = mock()
        presenter = FacilityDetailsPresenter(view, mock())
        Mockito.`when`(view?.getToken()).thenReturn("Test")
        presenter?.getFacilityAvailability("Test", null)
    }

    @Test(expected = Test.None::class)
    fun getFacilityAvailability_facilityId_null_selectedDate_NotNull() {
        view = mock()
        presenter = FacilityDetailsPresenter(view, mock())
        Mockito.`when`(view?.getToken()).thenReturn("Test")
        presenter?.getFacilityAvailability(null, "2021-10-11")
    }

    @Test(expected = Test.None::class)
    fun getFacilityAvailability_facilityId_notNull_selectedDate_NotNull() {
        view = mock()
        presenter = FacilityDetailsPresenter(view, mock())
        Mockito.`when`(view?.getToken()).thenReturn("Test")
        presenter?.getFacilityAvailability("Test", "2021-10-11")
    }

    @Test(expected = Test.None::class)
    fun getFacilityAvailability_facilityId_notNull_selectedDate_NotNull_interactor_null() {
        view = mock()
        presenter = FacilityDetailsPresenter(view, mock())
        presenter?.setInteractor(null)
        Mockito.`when`(view?.getToken()).thenReturn("Test")
        presenter?.getFacilityAvailability("Test", "2021-10-11")
    }

    @Test(expected = Test.None::class)
    fun getRuleAndRegulations_view_null() {
        presenter = FacilityDetailsPresenter(view, mock())
        presenter?.getRuleAndRegulations()
    }

    @Test(expected = Test.None::class)
    fun getRuleAndRegulations_token_null() {
        view = mock()
        presenter = FacilityDetailsPresenter(view, mock())
        Mockito.`when`(view?.getToken()).thenReturn(null)
        presenter?.getRuleAndRegulations()
    }

    @Test(expected = Test.None::class)
    fun getRuleAndRegulations_token_empty() {
        view = mock()
        presenter = FacilityDetailsPresenter(view, mock())
        Mockito.`when`(view?.getToken()).thenReturn("")
        presenter?.getRuleAndRegulations()
    }

    @Test(expected = Test.None::class)
    fun getRuleAndRegulations_interactor_notNull() {
        view = mock()
        presenter = FacilityDetailsPresenter(view, mock())
        interactor = FacilityDetailsInteractor()
        interactor!!.services = MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer)
        presenter?.setInteractor(interactor)
        Mockito.`when`(view?.getToken()).thenReturn("Test")
        presenter?.getRuleAndRegulations()
    }

    @Test(expected = Test.None::class)
    fun getRuleAndRegulations_interactor_Null() {
        view = mock()
        presenter = FacilityDetailsPresenter(view, mock())
        interactor = null
        presenter?.setInteractor(interactor)
        Mockito.`when`(view?.getToken()).thenReturn("Test")
        presenter?.getRuleAndRegulations()
    }

}