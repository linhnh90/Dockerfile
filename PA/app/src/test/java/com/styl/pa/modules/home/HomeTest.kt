package com.styl.pa.modules.home

import android.content.Context
import com.styl.pa.TestBase
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.modules.home.interactor.HomeInteractor
import com.styl.pa.modules.home.presenter.HomePresenter
import com.styl.pa.modules.main.presenter.MainPresenter
import com.styl.pa.services.IKioskServices
import com.styl.pa.utils.MockServiceGenerator
import com.styl.pa.utils.MySharedPref
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy

class HomeTest: TestBase() {
    private var interactor: HomeInteractor? = null
    private var presenter: HomePresenter? = null
    private var view: IHomeContact.IView? = null

    @Test(expected = Test.None::class)
    fun getRecommendation() {
        view = mock()
        val context: Context = mock()
        presenter = HomePresenter(view, context)
        val spyPresenter = spy(presenter)
        spyPresenter?.interactor?.servicesKiosk = MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer)
        spyPresenter?.getRecommendation()
    }

    @Test(expected = Test.None::class)
    fun getRecommendation_interactor_null() {
        view = mock()
        val context: Context = mock()
        presenter = HomePresenter(view, context)
        val spyPresenter = spy(presenter)
        spyPresenter?.interactor = null
        spyPresenter?.getRecommendation()
    }

    @Test(expected = Test.None::class)
    fun checkClassVacancy_view_null() {
        view = null
        val context: Context = mock()
        presenter = HomePresenter(view, context)
        val spyPresenter = spy(presenter)
        val classInfo: ClassInfo = spy()
        spyPresenter?.checkClassVacancy(classInfo, true, false)
    }

    @Test(expected = Test.None::class)
    fun checkClassVacancy() {
        view = mock()
        val context: Context = mock()
        presenter = HomePresenter(view, context)
        val spyPresenter = spy(presenter)
        spyPresenter?.interactor?.servicesKiosk = MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer)
        val classInfo: ClassInfo = spy()
        spyPresenter?.checkClassVacancy(classInfo, true, false)
    }

    @Test(expected = Test.None::class)
    fun checkIgVacancy_view_null() {
        view = null
        val context: Context = mock()
        presenter = HomePresenter(view, context)
        val spyPresenter = spy(presenter)
        val igInfo: InterestGroup = spy()
        spyPresenter?.checkIgVacancy(igInfo, true, false)
    }

    @Test(expected = Test.None::class)
    fun checkIgVacancy() {
        view = mock()
        val context: Context = mock()
        presenter = HomePresenter(view, context)
        val spyPresenter = spy(presenter)
        spyPresenter?.interactor?.servicesKiosk = MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer)
        val igInfo: InterestGroup = spy()
        spyPresenter?.checkIgVacancy(igInfo, true, false)
    }

}