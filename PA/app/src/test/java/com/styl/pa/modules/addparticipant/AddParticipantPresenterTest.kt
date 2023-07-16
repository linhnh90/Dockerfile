package com.styl.pa.modules.addparticipant

import com.styl.pa.TestBase
import com.styl.pa.modules.addParticipant.AddParticipantContract
import com.styl.pa.modules.addParticipant.presenter.AddParticipantPresenter
import com.styl.pa.modules.addParticipant.router.AddParticipantRouter
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class AddParticipantPresenterTest: TestBase() {
    private var view: AddParticipantContract.View? = null
    private var presenter: AddParticipantPresenter? = null
    private var router: AddParticipantRouter? = null

    override fun setUp() {
        super.setUp()
        router = mock()
    }

    @Test
    fun `test validateInput is ok`(){
        view = mock()
        presenter = AddParticipantPresenter(view,router)
        presenter?.validateInput(
            fullName = "test",
            email = "test@email.com",
            phone = "98765432"
        )
        verify(router)?.navigateToCartPage(requestCode = any(), participantInfo = any())
    }

    @Test(expected = Test.None::class)
    fun `test validateInput view null`(){
        presenter = AddParticipantPresenter(null,router)
        presenter?.validateInput(
            fullName = "",
            email = "test@email.com",
            phone = "98765432"
        )
    }

    @Test
    fun `test validateInput return error`(){
        view = mock()
        presenter = AddParticipantPresenter(view,router)
        presenter?.validateInput(
            fullName = "",
            email = "",
            phone = ""
        )
        verify(view)?.showErrorInput(
            errorFullName = eq(AddParticipantPresenter.ERROR_STR),
            errorEmail = eq(AddParticipantPresenter.ERROR_STR),
            errorPhone = eq(AddParticipantPresenter.ERROR_STR)
        )
    }

    @Test
    fun `test validateInput email empty`(){
        view = mock()
        presenter = AddParticipantPresenter(view,router)
        presenter?.validateInput(
            fullName = "test123",
            email = "",
            phone = "98765432"
        )
        verify(view)?.showErrorInput(
            errorFullName = eq(""),
            errorPhone = eq(""),
            errorEmail = eq(AddParticipantPresenter.ERROR_STR)
        )
    }

    @Test
    fun `test validateInput phone empty`(){
        view = mock()
        presenter = AddParticipantPresenter(view,router)
        presenter?.validateInput(
            fullName = "test123",
            email = "test@email.com",
            phone = ""
        )
        verify(view)?.showErrorInput(
            errorFullName = eq(""),
            errorPhone = eq(AddParticipantPresenter.ERROR_STR),
            errorEmail = eq("")
        )
    }
}