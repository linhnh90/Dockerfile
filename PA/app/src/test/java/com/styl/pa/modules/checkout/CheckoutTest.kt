package com.styl.pa.modules.checkout

import com.styl.pa.TestBase
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.cart.Attendee
import com.styl.pa.entities.cart.Cart
import com.styl.pa.entities.cart.CartData
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.event.EventParticipant
import com.styl.pa.entities.event.TicketEntity
import com.styl.pa.entities.facility.SlotSessionInfo
import com.styl.pa.entities.generateToken.BookingResponse
import com.styl.pa.entities.generateToken.Data
import com.styl.pa.entities.generateToken.Facility
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.entities.pacesRequest.FacilityRequestItem
import com.styl.pa.entities.pacesRequest.ProductRequestItem
import com.styl.pa.entities.product.Product
import com.styl.pa.entities.reservation.ProductInfo
import com.styl.pa.entities.reservation.TotalCostsResponse
import com.styl.pa.entities.wirecard.TransactionResponse
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.checkout.interactor.CheckoutInteractor
import com.styl.pa.modules.checkout.presenter.CheckoutPresenter
import com.styl.pa.modules.checkout.router.CheckoutRouter
import com.styl.pa.modules.payment.view.PaymentFragment
import com.styl.pa.services.IKioskServices
import com.styl.pa.utils.*
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.*
import java.util.*
import kotlin.collections.ArrayList

class CheckoutTest: TestBase() {

    private var checkoutInteractor: CheckoutInteractor? = null
    private var checkoutView: ICheckoutContact.IView? = null
    private var checkoutPresenter: CheckoutPresenter? = null
    private var checkoutRouter: CheckoutRouter? = null

    override fun setUp() {
        super.setUp()
        checkoutInteractor = CheckoutInteractor()
        checkoutRouter = mock()
        checkoutInteractor?.setKioskService(
                MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer)
        )
    }

    @Test
    fun quickBookFacility_should_success() {
        val facility: Facility? = GeneralUtils.convertStringToObject(
                TestUtils.readJsonFile("facility.json")
        )
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
                TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
                UUID.randomUUID().toString(), null, facility,
                null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
                facility?.crmResourceId,
                cartItem.selectedDate,
                facilitySlot.mTimeRageName,
                facilitySlot.mTimeRangeId
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacility(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem))
        verify(output, never()).onError(any())
    }


    @Test
    fun quickBookFacility_success_view_null() {
        val facility: Facility? = GeneralUtils.convertStringToObject(
                TestUtils.readJsonFile("facility.json")
        )
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
                TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
                UUID.randomUUID().toString(), null, facility,
                null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
                facility?.crmResourceId,
                cartItem.selectedDate,
                facilitySlot.mTimeRageName,
                facilitySlot.mTimeRangeId
        )
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacility(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem))
        verify(output, never()).onError(any())
    }

    @Test
    fun quickBookFacility_should_error() {

        val facility: Facility? = null
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
                TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
                UUID.randomUUID().toString(), null, facility,
                null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
                facility?.crmResourceId,
                cartItem.selectedDate,
                facilitySlot.mTimeRageName,
                facilitySlot.mTimeRangeId
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacility(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem))
        verify(output, never()).onSuccess(any())
    }

    @Test
    fun quickBookFacility_view_null() {

        val facility: Facility? = null
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
                TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
                UUID.randomUUID().toString(), null, facility,
                null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
                facility?.crmResourceId,
                cartItem.selectedDate,
                facilitySlot.mTimeRageName,
                facilitySlot.mTimeRangeId
        )
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacility(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem))
        verify(output, never()).onSuccess(any())
    }

    @Test
    fun quickBookFacility_interactor_null() {

        val facility: Facility? = null
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
                TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
                UUID.randomUUID().toString(), null, facility,
                null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
                facility?.crmResourceId,
                cartItem.selectedDate,
                facilitySlot.mTimeRageName,
                facilitySlot.mTimeRangeId
        )
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacility(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem))
        verify(output, never()).onSuccess(any())
    }

    @Test
    fun quickBookFacility_item_empty() {
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacility(MockInterceptor.TEST_CART_ID, arrayListOf())
        verify(output, never()).onSuccess(any())
    }

    @Test
    fun quickBookFacility_token_not_null() {
        checkoutView = mock()
        val customer = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val facility: Facility? = null
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
                TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
                UUID.randomUUID().toString(), null, facility,
                null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
                facility?.crmResourceId,
                cartItem.selectedDate,
                facilitySlot.mTimeRageName,
                facilitySlot.mTimeRangeId
        )
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customer)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        spyPresenter?.quickBookFacility(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem))
        verify(output, never()).onSuccess(any())
    }

    @Test
    fun quickBookFacility_payer_null() {
        checkoutView = mock()
        val facility: Facility? = null
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
                TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
                UUID.randomUUID().toString(), null, facility,
                null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
                facility?.crmResourceId,
                cartItem.selectedDate,
                facilitySlot.mTimeRageName,
                facilitySlot.mTimeRangeId
        )
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(null)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        spyPresenter?.quickBookFacility(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem))
        verify(output, never()).onSuccess(any())
    }

    @Test
    fun updateSessionCode() {
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        checkoutPresenter?.updateSessionCode("Test")
        assert(checkoutPresenter?.sessionCode == "Test")
    }

    @Test(expected = Test.None::class)
    fun updateFreePayment_all_null() {
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        checkoutPresenter?.updateFreePayment(null, null)
    }

    @Test(expected = Test.None::class)
    fun updateFreePayment_totalCost_Null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = mock()
        spyCheckoutPresenter?.updateFreePayment(null, customerInfo)
    }

    @Test(expected = Test.None::class)
    fun updateFreePayment_customer_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val totalCost: TotalCostsResponse = mock()
        spyCheckoutPresenter?.paymentRequest = mock()
        spyCheckoutPresenter?.updateFreePayment(totalCost, null)
    }

    @Test(expected = Test.None::class)
    fun updateFreePayment_request_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val totalCost = GeneralUtils.convertStringToObject<TotalCostsResponse>(
                TestUtils.readJsonFile("totalCost.json")
        )
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = null
        spyCheckoutPresenter?.updateFreePayment(totalCost, customerInfo)
    }

    @Test(expected = Test.None::class)
    fun updateFreePayment_product_list_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val totalCost = TotalCostsResponse()
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = mock()
        spyCheckoutPresenter?.updateFreePayment(totalCost, customerInfo)
    }

    @Test(expected = Test.None::class)
    fun updateFreePayment_product_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val totalCost = TotalCostsResponse()
        totalCost.setProducts(arrayListOf(ProductInfo()))
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = mock()
        spyCheckoutPresenter?.updateFreePayment(totalCost, customerInfo)
    }

    @Test(expected = Test.None::class)
    fun updateFreePayment_normal() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val totalCost = GeneralUtils.convertStringToObject<TotalCostsResponse>(
                TestUtils.readJsonFile("totalCost.json")
        )
        val newListProduct = ArrayList<ProductInfo>()
        val product1 = ProductInfo()
        product1.productId = "Test1"
        newListProduct.add(product1)
        val product2 = ProductInfo()
        product2.productId = "Test2"
        product2.productType = Product.PRODUCT_EVENT
        newListProduct.add(product2)
        val listSelectedTicket = ArrayList<EventTicket>()
        val eventTicket = EventTicket()
        val listTicketEntity = ArrayList<TicketEntity>()
        val ticketEntity = TicketEntity()
        val listParticipant = ArrayList<EventParticipant>()
        val participant = EventParticipant().apply {
            beforeDiscountAmount = 0f
            discountAmount = 0f
            promoDiscountAmount = 0f
            paymentAmount = 0f
        }
        listParticipant.add(participant)
        ticketEntity.listParticipant = listParticipant
        listTicketEntity.add(ticketEntity)
        eventTicket.listTicketParticipantEntity = listTicketEntity
        listSelectedTicket.add(eventTicket)
        product2.listSelectedTicket = listSelectedTicket
        val product3 = ProductInfo()
        product3.productId = "Test3_Test4"
        newListProduct.add(product3)
        val product5 = ProductInfo()
        product5.productId = "Test5"
        newListProduct.add(product5)
        totalCost?.setProducts(newListProduct.toList())
        val course: ClassInfo = spy()
        course.sku = "Test1"
        val facility: Facility = spy()
        facility.outletName = "Test3"
        facility.setResourceSubTypeName("Test4")
        val event: EventInfo = spy()
        event.sku = "Test2"
        val ig: InterestGroup = spy()
        ig.sku = "Test5"
        val cartItem1 = CartItem(
                UUID.randomUUID().toString(), course, null,
                null, null, null, null, null
        )
        val cartItem2 = CartItem(
                UUID.randomUUID().toString(), null, facility,
                null, null, null, null, null
        )
        val cartItem3 = CartItem(
                UUID.randomUUID().toString(), null, null,
                event, null, null, null, null
        )
        val cartItem4 = CartItem(
            UUID.randomUUID().toString(), null, null,
            null, null, null, null, null, ig
        )
        val cart = Cart(arrayListOf(cartItem1, cartItem2, cartItem3, cartItem4), customerInfo, null)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.getCart()).thenReturn(cart)
        spyCheckoutPresenter?.paymentRequest = mock()
        spyCheckoutPresenter?.updateFreePayment(totalCost, customerInfo)
    }

    @Test(expected = Test.None::class)
    fun updatePayment() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val totalCost = GeneralUtils.convertStringToObject<TotalCostsResponse>(
                TestUtils.readJsonFile("totalCost.json")
        )
        val newListProduct = ArrayList<ProductInfo>()
        val product1 = ProductInfo()
        product1.productId = "Test1"
        newListProduct.add(product1)
        val product2 = ProductInfo() //event
        product2.productId = "Test2"
        product2.productType = Product.PRODUCT_EVENT
        val listSelectedTicket = ArrayList<EventTicket>()
        val eventTicket = EventTicket()
        val listTicketEntity = ArrayList<TicketEntity>()
        val ticketEntity = TicketEntity()
        val listParticipant = ArrayList<EventParticipant>()
        val participant = EventParticipant().apply {
            beforeDiscountAmount = 10f
            discountAmount = 0f
            promoDiscountAmount = 0f
            paymentAmount = 10f
        }
        listParticipant.add(participant)
        ticketEntity.listParticipant = listParticipant
        listTicketEntity.add(ticketEntity)
        eventTicket.listTicketParticipantEntity = listTicketEntity
        listSelectedTicket.add(eventTicket)
        product2.listSelectedTicket = listSelectedTicket

        newListProduct.add(product2)
        val product3 = ProductInfo()
        product3.productId = "Test3_Test4"
        newListProduct.add(product3)
        val product5 = ProductInfo()
        product1.productId = "Test5"
        newListProduct.add(product5)
        totalCost?.setProducts(newListProduct.toList())
        val course: ClassInfo = spy()
        course.sku = "Test1"
        val event: EventInfo = spy()
        event.sku = "Test2"
        val facility: Facility = spy()
        facility.outletName = "Test3"
        facility.setResourceSubTypeName("Test4")
        val ig: InterestGroup = spy()
        ig.sku = "Test5"
        val cartItem1 = CartItem(
                UUID.randomUUID().toString(), course, null,
                null, null, null, null, null
        )
        val cartItem2 = CartItem(
                UUID.randomUUID().toString(), null, facility,
                null, null, null, null, null
        )
        val cartItem3 = CartItem(
                UUID.randomUUID().toString(), null, null,
                event, null, null, null, null
        )
        val cartItem4 = CartItem(
            UUID.randomUUID().toString(), null, null,
            null, null, null, null, null, ig
        )
        val cart = Cart(arrayListOf(cartItem1, cartItem2, cartItem3, cartItem4), customerInfo, null)
        val transactionResponse = TransactionResponse()
        Mockito.`when`(checkoutView?.getCart()).thenReturn(cart)
        spyCheckoutPresenter?.paymentRequest = mock()
        spyCheckoutPresenter?.updatePayment(totalCost, cart.items,
                transactionResponse, null, PaymentFragment.CARD_CEPAS, "0", null, customerInfo )
    }

    @Test(expected = Test.None::class)
    fun updatePayment_product_list_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val totalCost = TotalCostsResponse()
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = null
        spyCheckoutPresenter?.updatePayment(totalCost, null,
                null, null, PaymentFragment.CARD_CEPAS, "1", null, customerInfo )
    }

    @Test(expected = Test.None::class)
    fun updatePayment_totalCost_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val totalCost = null
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = mock()
        spyCheckoutPresenter?.updatePayment(totalCost, null,
                null, null, PaymentFragment.CARD_CEPAS, "0", null, null )
    }

    @Test(expected = Test.None::class)
    fun updatePayment_product_failed() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val totalCost = GeneralUtils.convertStringToObject<TotalCostsResponse>(
                TestUtils.readJsonFile("totalCost.json")
        )
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = null
        spyCheckoutPresenter?.updatePayment(totalCost, null,
                null, null, PaymentFragment.CARD_CEPAS, "0", null, null )
    }

    @Test(expected = Test.None::class)
    fun updatePayment_status_failed() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val totalCost = GeneralUtils.convertStringToObject<TotalCostsResponse>(
                TestUtils.readJsonFile("totalCost.json")
        )
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = mock()
        spyCheckoutPresenter?.updatePayment(totalCost, null,
                null, null, PaymentFragment.CARD_CREDIT_CONTACTLESS, "1", null, null )
    }

    @Test(expected = Test.None::class)
    fun updatePayment_paymentRequest_null_status_success() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val totalCost = GeneralUtils.convertStringToObject<TotalCostsResponse>(
                TestUtils.readJsonFile("totalCost.json")
        )
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = null
        spyCheckoutPresenter?.updatePayment(totalCost, null,
                TransactionResponse(), null, PaymentFragment.CARD_CREDIT_CONTACTLESS, "0", null, null )
    }

    @Test(expected = Test.None::class)
    fun updatePayment_to_server_paymentRequest_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = null
        spyCheckoutPresenter?.updatePayment(MockInterceptor.TEST_EKIOSK_HEADER, "Test", "Test")
    }

    @Test(expected = Test.None::class)
    fun updatePayment_to_server_signature_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = mock()
        spyCheckoutPresenter?.updatePayment(MockInterceptor.TEST_EKIOSK_HEADER, null, null)
    }

    @Test(expected = Test.None::class)
    fun updatePayment_to_server_interactor_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = mock()
        spyCheckoutPresenter?.updatePayment(MockInterceptor.TEST_EKIOSK_HEADER, null, null)
    }

    @Test(expected = Test.None::class)
    fun updatePayment_to_server_signature_not_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.paymentRequest = mock()
        spyCheckoutPresenter?.updatePayment(MockInterceptor.TEST_EKIOSK_HEADER, "Test", null)
    }

    @Test(expected = Test.None::class)
    fun submitPayment_submitPayment_true() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        spyCheckoutPresenter?.submitPayment(MockInterceptor.TEST_EKIOSK_HEADER, 1, customerInfo, true)
    }

    @Test(expected = Test.None::class)
    fun submitPayment_submitPayment_false() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        spyCheckoutPresenter?.submitPayment(MockInterceptor.TEST_EKIOSK_HEADER, 1, customerInfo, false)
    }

    @Test(expected = Test.None::class)
    fun submitPayment_submitPayment_should_fail() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.submitPayment(MockInterceptor.TEST_EKIOSK_HEADER, 1, null, true)
    }

    @Test(expected = Test.None::class)
    fun submitPayment_token_empty() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.submitPayment("", 1, null, false)
    }

    @Test(expected = Test.None::class)
    fun submitPayment_token_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        spyCheckoutPresenter?.submitPayment(null, 1, customerInfo, false)
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_cartItems_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(null)
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_facility_only() {
        val facility = spy<Facility>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
                TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem2 = CartItem("Test2", null, facility, null,
                arrayListOf(testAttendee), 0, "", arrayListOf(slot!!)
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem2))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_facility_someone_only() {
        val facility = spy<Facility>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
            TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem2 = CartItem("Test2", null, facility, null,
            arrayListOf(testAttendee), 0, "", arrayListOf(slot!!)
        )
        val someOne = CustomerInfo()
        someOne.mFullName = "Test Full Name"
        someOne.mEmail = "someone@email.com"
        someOne.mMobile = "12345678"
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem2))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_facility_only_view_null() {
        val facility = spy<Facility>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
                TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem2 = CartItem("Test2", null, facility, null,
                arrayListOf(testAttendee), 0, "", arrayListOf(slot!!)
        )
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem2))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_facility_only_slots_null() {
        val facility = spy<Facility>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
                TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem2 = CartItem("Test2", null, facility, null,
                arrayListOf(testAttendee), 0, "", null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem2))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_facility_only_facility_null() {
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
                TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem2 = CartItem("Test2", null, null, null,
                arrayListOf(testAttendee), 0, "", arrayListOf(slot!!)
        )
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem2))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_course_only_quickBook() {
        val course = spy<ClassInfo>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
                TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", course, null, null,
                arrayListOf(testAttendee), 0, "", null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(true)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_course_only_quickBook_interactor_null() {
        val course = spy<ClassInfo>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
                TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", course, null, null,
                arrayListOf(testAttendee), 0, "", null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(true)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_course_only_quickBook_token_not_null() {
        val course = spy<ClassInfo>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
                TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", course, null, null,
                arrayListOf(testAttendee), 0, "", null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(true)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_course_only_view_null() {
        val course = spy<ClassInfo>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
                TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", course, null, null,
                arrayListOf(testAttendee), 0, "", null
        )
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_course_only_token_not_null() {
        val course = spy<ClassInfo>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
                TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", course, null, null,
                arrayListOf(testAttendee), 0, "", null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_course_only_interactor_null() {
        val course = spy<ClassInfo>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
                TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", course, null, null,
                arrayListOf(testAttendee), 0, "", null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }


    @Test(expected = Test.None::class)
    fun bookingProcess_course_only() {
        val course = spy<ClassInfo>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
                TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", course, null, null,
                arrayListOf(testAttendee), 0, "", null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }





    @Test(expected = Test.None::class)
    fun bookingProcess_ig_only_quickBook() {
        val igInfo = spy<InterestGroup>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
            TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", null, null, null,
            arrayListOf(testAttendee), 0, "", null, igInfo
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(true)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_ig_only_quickBook_interactor_null() {
        val igInfo = spy<InterestGroup>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
            TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", null, null, null,
            arrayListOf(testAttendee), 0, "", null, igInfo
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(true)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_ig_only_quickBook_token_not_null() {
        val igInfo = spy<InterestGroup>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
            TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", null, null, null,
            arrayListOf(testAttendee), 0, "", null, igInfo
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(true)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_ig_only_view_null() {
        val igInfo = spy<InterestGroup>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
            TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", null, null, null,
            arrayListOf(testAttendee), 0, "", null, igInfo
        )
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_ig_only_token_not_null() {
        val igInfo = spy<InterestGroup>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
            TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", null, null, null,
            arrayListOf(testAttendee), 0, "", null, igInfo
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_ig_only_interactor_null() {
        val igInfo = spy<InterestGroup>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
            TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", null, null, null,
            arrayListOf(testAttendee), 0, "", null, igInfo
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }


    @Test(expected = Test.None::class)
    fun bookingProcess_ig_only() {
        val igInfo = spy<InterestGroup>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
            TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", null, null, null,
            arrayListOf(testAttendee), 0, "", null, igInfo
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }


    @Test(expected = Test.None::class)
    fun bookingProcess_event_only_quickBook() {
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEventSku123"
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val listSelectedTicket = ArrayList<EventTicket>()
        val eventTicket = EventTicket()
        eventTicket.id = "4567"
        eventTicket.selectedQty = 1
        listSelectedTicket.add(eventTicket)
        eventInfo.listSelectedTicket = listSelectedTicket
        val cartItem1 = CartItem("Test1", null, null, eventInfo,
            null, 0, "", null, null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(true)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_event_only_quickBook_event_null() {
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEventSku123"
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val listSelectedTicket = ArrayList<EventTicket>()
        val eventTicket = EventTicket()
        eventTicket.id = "4567"
        eventTicket.selectedQty = 1
        listSelectedTicket.add(eventTicket)
        eventInfo.listSelectedTicket = listSelectedTicket
        val cartItem1 = CartItem("Test1", null, null, null,
            null, 0, "", null, null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(true)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_event_only_quickBook_eventTicket_have_null_id() {
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = null
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val listSelectedTicket = ArrayList<EventTicket>()
        val eventTicket = EventTicket()
        eventTicket.id = null
        eventTicket.selectedQty = null
        listSelectedTicket.add(eventTicket)
        eventInfo.listSelectedTicket = listSelectedTicket
        val cartItem1 = CartItem("Test1", null, null, eventInfo,
            null, 0, "", null, null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(true)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_event_only_quickBook_interactor_null_view_null() {
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEventSku123"
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val listSelectedTicket = ArrayList<EventTicket>()
        val eventTicket = EventTicket()
        eventTicket.id = "4567"
        eventTicket.selectedQty = 1
        listSelectedTicket.add(eventTicket)
        eventInfo.listSelectedTicket = listSelectedTicket
        val cartItem1 = CartItem("Test1", null, null, eventInfo,
            null, 0, "", null, null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(null, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(true)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_event_only_quickBook_normal() {
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEventSku123"
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val listSelectedTicket = ArrayList<EventTicket>()
        val eventTicket = EventTicket()
        eventTicket.id = "4567"
        eventTicket.selectedQty = 1
        listSelectedTicket.add(eventTicket)
        eventInfo.listSelectedTicket = listSelectedTicket
        val cartItem1 = CartItem("Test1", null, null, eventInfo,
            null, 0, "", null, null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(true)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        Mockito.`when`(checkoutView?.getCartId()).thenReturn("TestCardId123")
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_event_only_quickBook_customerId_null() {
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEventSku123"
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        customerInfo?.mCustomerId = null
        val listSelectedTicket = ArrayList<EventTicket>()
        val eventTicket = EventTicket()
        eventTicket.id = "4567"
        eventTicket.selectedQty = 1
        listSelectedTicket.add(eventTicket)
        eventInfo.listSelectedTicket = listSelectedTicket
        val cartItem1 = CartItem("Test1", null, null, eventInfo,
            null, 0, "", null, null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(true)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        Mockito.`when`(checkoutView?.getCartId()).thenReturn("TestCardId123")
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_event_only_addToCart_normal() {
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEventSku123"
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val listSelectedTicket = ArrayList<EventTicket>()
        val eventTicket = EventTicket()
        eventTicket.id = "4567"
        eventTicket.selectedQty = 1
        listSelectedTicket.add(eventTicket)
        eventInfo.listSelectedTicket = listSelectedTicket
        val cartItem1 = CartItem("Test1", null, null, eventInfo,
            null, 0, "", null, null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(false)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        Mockito.`when`(checkoutView?.getCartId()).thenReturn("TestCardId123")
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_event_only_addToCart_view_null_interactor_null() {
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEventSku123"
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val listSelectedTicket = ArrayList<EventTicket>()
        val eventTicket = EventTicket()
        eventTicket.id = "4567"
        eventTicket.selectedQty = 1
        listSelectedTicket.add(eventTicket)
        eventInfo.listSelectedTicket = listSelectedTicket
        val cartItem1 = CartItem("Test1", null, null, eventInfo,
            null, 0, "", null, null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(null, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(false)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        Mockito.`when`(checkoutView?.getCartId()).thenReturn("TestCardId123")
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_event_only_addToCart_getToken_null_payer_null() {
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEventSku123"
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val listSelectedTicket = ArrayList<EventTicket>()
        val eventTicket = EventTicket()
        eventTicket.id = "4567"
        eventTicket.selectedQty = 1
        listSelectedTicket.add(eventTicket)
        eventInfo.listSelectedTicket = listSelectedTicket
        val cartItem1 = CartItem("Test1", null, null, eventInfo,
            null, 0, "", null, null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(false)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(null)
        Mockito.`when`(checkoutView?.getCartId()).thenReturn("TestCardId123")
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(null)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_event_only_addToCart_customerid_null() {
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEventSku123"
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        customerInfo?.mCustomerId = null
        val listSelectedTicket = ArrayList<EventTicket>()
        val eventTicket = EventTicket()
        eventTicket.id = "4567"
        eventTicket.selectedQty = 1
        listSelectedTicket.add(eventTicket)
        eventInfo.listSelectedTicket = listSelectedTicket
        val cartItem1 = CartItem("Test1", null, null, eventInfo,
            null, 0, "", null, null
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.isQuickBook()).thenReturn(false)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        Mockito.`when`(checkoutView?.getCartId()).thenReturn("TestCardId123")
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1))
    }

    @Test(expected = Test.None::class)
    fun bookingProcess_interactor_null() {
        val facility = spy<Facility>()
        val course = spy<ClassInfo>()
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val slot = GeneralUtils.convertStringToObject<SlotSessionInfo>(
                TestUtils.readJsonFile("facilitySession.json")
        )
        assert(slot != null)
        val testAttendee = Attendee("", customerInfo, null)
        val cartItem1 = CartItem("Test1", course, null, null,
                arrayListOf(testAttendee), 0, "", null
        )
        val cartItem2 = CartItem("Test2", null, facility, null,
                arrayListOf(testAttendee), 0, "", arrayListOf(slot!!)
        )
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.bookingProcess(arrayListOf(cartItem1, cartItem2))
    }

    @Test(expected = Test.None::class)
    fun submitPayment_view_null_interactor_null() {
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        spyCheckoutPresenter?.submitPayment(MockInterceptor.TEST_EKIOSK_HEADER, 1, null, true)
    }

    @Test
    fun getCustomerAge() {
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val cart = TestUtils.mockCart(customerInfo, true)
        cart.payer = customerInfo
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.getCart()).thenReturn(cart)
        val age = spyCheckoutPresenter?.getCustomerAge(customerInfo?.mCustomerId)
        assert(age != null)
    }

    @Test
    fun getCustomerAge_view_null() {
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val age = spyCheckoutPresenter?.getCustomerAge(null)
        assert(age == null)
    }

    @Test
    fun getCustomerAge_cart_null() {
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val cart: Cart? = null
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.getCart()).thenReturn(cart)
        val age = spyCheckoutPresenter?.getCustomerAge(customerInfo?.mCustomerId)
        assert(age == null)
    }

    @Test
    fun getCustomerAge_payer_null() {
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val cart = TestUtils.mockCart(customerInfo, false)
        cart.payer = null
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.getCart()).thenReturn(cart)
        val age = spyCheckoutPresenter?.getCustomerAge("")
        assert(age == null)
    }

    @Test
    fun getCustomerAge_items_null() {
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val cart = TestUtils.mockCart(customerInfo, true)
        cart.items = null
        cart.payer = null
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.getCart()).thenReturn(cart)
        val age = spyCheckoutPresenter?.getCustomerAge(customerInfo?.mCustomerId)
        assert(age == null)
    }

    @Test
    fun getCustomerAge_attendees_null() {
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val cart = TestUtils.mockCart(customerInfo, true)
        cart.items!![0].attendees!![0].customerInfo = null
        cart.payer = null
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        Mockito.`when`(checkoutView?.getCart()).thenReturn(cart)
        val age = spyCheckoutPresenter?.getCustomerAge("Test")
        assert(age == null)
    }

    @Test(expected = Test.None::class)
    fun deleteItem_itemId_Null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        checkoutPresenter?.deleteItem(mock(), "", 0);
    }

    @Test(expected = Test.None::class)
    fun deleteItem_itemId_notNull_Success() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val cartItem = spy<CartItem>()
        val classInfo = spy<ClassInfo>()
        classInfo.sku = "TestCourse2"
        cartItem.classInfo = classInfo
        checkoutPresenter?.deleteItem(cartItem, MockInterceptor.TEST_CART_ID, 0);
    }

    @Test(expected = Test.None::class)
    fun deleteItem_itemId_notNull_Failed() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val cartItem = spy<CartItem>()
        val classInfo = spy<ClassInfo>()
        classInfo.sku = "TestCourse2"
        cartItem.classInfo = classInfo
        Mockito.`when`(checkoutView?.getToken()).thenReturn(null)
        spyCheckoutPresenter?.deleteItem(cartItem, "CartIdTest", 0);
    }

    @Test(expected = Test.None::class)
    fun deleteItem_itemId_notNull_Failed_payer_Null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val cartItem = spy<CartItem>()
        val classInfo = spy<ClassInfo>()
        classInfo.sku = "TestCourse2"
        cartItem.classInfo = classInfo
        Mockito.`when`(checkoutView?.getToken()).thenReturn(null)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(null)
        spyCheckoutPresenter?.deleteItem(cartItem, "CartIdTest", 0);
    }

    @Test(expected = Test.None::class)
    fun deleteItem_itemId_notNull_Failed_payer_notNull() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val cartItem = spy<CartItem>()
        val classInfo = spy<ClassInfo>()
        classInfo.sku = "TestCourse2"
        cartItem.classInfo = classInfo
        Mockito.`when`(checkoutView?.getToken()).thenReturn(null)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(mock())
        spyCheckoutPresenter?.deleteItem(cartItem, "CartIdTest", 0);
    }

    @Test(expected = Test.None::class)
    fun deleteItem_itemId_notNull_Failed_payer_notNull_token_notNull() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val cartItem = spy<CartItem>()
        val classInfo = spy<ClassInfo>()
        classInfo.sku = "TestCourse2"
        cartItem.classInfo = classInfo
        val customerInfo: CustomerInfo = spy()
        Mockito.`when`(checkoutView?.getToken()).thenReturn("Test")
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.deleteItem(cartItem, "CartIdTest", 0);
    }


    @Test(expected = Test.None::class)
    fun deleteItem_itemId_notNull_Failed_payer_notNull_customerId_notNull() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val cartItem = spy<CartItem>()
        val classInfo = spy<ClassInfo>()
        classInfo.sku = "TestCourse2"
        cartItem.classInfo = classInfo
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(checkoutView?.getToken()).thenReturn(null)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.deleteItem(cartItem, "CartIdTest", 0);
    }

    @Test(expected = Test.None::class)
    fun deleteItem_itemId_notNull_Failed_view_null_interactore_null() {
        checkoutPresenter = CheckoutPresenter(null, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val cartItem = spy<CartItem>()
        val classInfo = spy<ClassInfo>()
        classInfo.sku = "TestCourse2"
        cartItem.classInfo = classInfo
        spyCheckoutPresenter?.deleteItem(cartItem, "CartIdTest", 0);
    }



    @Test(expected = Test.None::class)
    fun deleteIgItem_itemId_notNull_Success() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val cartItem = spy<CartItem>()
        val igInfo = spy<InterestGroup>()
        igInfo.sku = "TestIg2"
        cartItem.igInfo = igInfo
        checkoutPresenter?.deleteItem(cartItem, MockInterceptor.TEST_CART_ID, 0);
    }

    @Test(expected = Test.None::class)
    fun deleteIgItem_itemId_notNull_Failed() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val cartItem = spy<CartItem>()
        val igInfo = spy<InterestGroup>()
        igInfo.sku = "TestIg2"
        cartItem.igInfo = igInfo
        Mockito.`when`(checkoutView?.getToken()).thenReturn(null)
        spyCheckoutPresenter?.deleteItem(cartItem, "CartIdTest", 0);
    }

    @Test(expected = Test.None::class)
    fun deleteIgItem_itemId_notNull_Failed_payer_Null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val cartItem = spy<CartItem>()
        val igInfo = spy<InterestGroup>()
        igInfo.sku = "TestIg2"
        cartItem.igInfo = igInfo
        Mockito.`when`(checkoutView?.getToken()).thenReturn(null)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(null)
        spyCheckoutPresenter?.deleteItem(cartItem, "CartIdTest", 0);
    }

    @Test(expected = Test.None::class)
    fun deleteIgItem_itemId_notNull_Failed_payer_notNull() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val cartItem = spy<CartItem>()
        val igInfo = spy<InterestGroup>()
        igInfo.sku = "TestIg2"
        cartItem.igInfo = igInfo
        Mockito.`when`(checkoutView?.getToken()).thenReturn(null)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(mock())
        spyCheckoutPresenter?.deleteItem(cartItem, "CartIdTest", 0);
    }

    @Test(expected = Test.None::class)
    fun deleteIgItem_itemId_notNull_Failed_payer_notNull_token_notNull() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val cartItem = spy<CartItem>()
        val igInfo = spy<InterestGroup>()
        igInfo.sku = "TestIg2"
        cartItem.igInfo = igInfo
        val customerInfo: CustomerInfo = spy()
        Mockito.`when`(checkoutView?.getToken()).thenReturn("Test")
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.deleteItem(cartItem, "CartIdTest", 0);
    }


    @Test(expected = Test.None::class)
    fun deleteIgItem_itemId_notNull_Failed_payer_notNull_customerId_notNull() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val cartItem = spy<CartItem>()
        val igInfo = spy<InterestGroup>()
        igInfo.sku = "TestIg2"
        cartItem.igInfo = igInfo
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(checkoutView?.getToken()).thenReturn(null)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        spyCheckoutPresenter?.deleteItem(cartItem, "CartIdTest", 0);
    }

    @Test(expected = Test.None::class)
    fun deleteIgItem_itemId_notNull_Failed_view_null_interactore_null() {
        checkoutPresenter = CheckoutPresenter(null, null, checkoutRouter)
        val spyCheckoutPresenter = spy(checkoutPresenter)
        val cartItem = spy<CartItem>()
        val igInfo = spy<InterestGroup>()
        igInfo.sku = "TestIg2"
        cartItem.igInfo = igInfo
        spyCheckoutPresenter?.deleteItem(cartItem, "CartIdTest", 0);
    }

    @Test(expected = Test.None::class)
    fun deleteEventItem_itemId_notNull_Success() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val cartItem = spy<CartItem>()
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEvent2"
        cartItem.event = eventInfo
        checkoutPresenter?.deleteItem(cartItem, MockInterceptor.TEST_CART_ID, 0);
    }

    @Test(expected = Test.None::class)
    fun deleteEventItem_itemId_notNull_Failed() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val cartItem = spy<CartItem>()
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEvent2"
        cartItem.event = eventInfo
        Mockito.`when`(checkoutView?.getToken()).thenReturn(null)
        checkoutPresenter?.deleteItem(cartItem, MockInterceptor.TEST_CART_ID, 0);
    }

    @Test(expected = Test.None::class)
    fun deleteEventItem_itemId_notNull_Failed_payer_Null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val cartItem = spy<CartItem>()
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEvent2"
        cartItem.event = eventInfo
        Mockito.`when`(checkoutView?.getToken()).thenReturn(null)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(null)
        checkoutPresenter?.deleteItem(cartItem, MockInterceptor.TEST_CART_ID, 0);
    }

    @Test(expected = Test.None::class)
    fun deleteEventItem_itemId_notNull_Failed_payer_notNull() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val cartItem = spy<CartItem>()
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEvent2"
        cartItem.event = eventInfo
        Mockito.`when`(checkoutView?.getToken()).thenReturn(null)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(mock())
        checkoutPresenter?.deleteItem(cartItem, MockInterceptor.TEST_CART_ID, 0);
    }

    @Test(expected = Test.None::class)
    fun deleteEventItem_itemId_notNull_Failed_payer_notNull_token_notNull() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val cartItem = spy<CartItem>()
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEvent2"
        cartItem.event = eventInfo
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        customerInfo?.mCustomerId = null
        Mockito.`when`(checkoutView?.getToken()).thenReturn("Test")
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        checkoutPresenter?.deleteItem(cartItem, MockInterceptor.TEST_CART_ID, 0);
    }

    @Test(expected = Test.None::class)
    fun deleteEventItem_itemId_notNull_Failed_payer_notNull_customerId_notNull() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val cartItem = spy<CartItem>()
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEvent2"
        cartItem.event = eventInfo
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(checkoutView?.getToken()).thenReturn("Test")
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        checkoutPresenter?.deleteItem(cartItem, MockInterceptor.TEST_CART_ID, 0);
    }

    @Test(expected = Test.None::class)
    fun deleteEventItem_itemId_notNull_Failed_view_null_interactore_null() {
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(null, null, checkoutRouter)
        val cartItem = spy<CartItem>()
        val eventInfo = spy<EventInfo>()
        eventInfo.sku = "TestEvent2"
        cartItem.event = eventInfo
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(checkoutView?.getToken()).thenReturn("Test")
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customerInfo)
        checkoutPresenter?.deleteItem(cartItem, MockInterceptor.TEST_CART_ID, 0);
    }

    @Test(expected = Test.None::class)
    fun loadCart_view_Null_interactor_Null() {
        checkoutPresenter = CheckoutPresenter(null, null, checkoutRouter)
        checkoutPresenter?.loadCart("", "", "" , mock(), false)
    }

    @Test(expected = Test.None::class)
    fun loadCart_view_notNull_interactor_notNull() {
        checkoutPresenter = CheckoutPresenter(mock(), checkoutInteractor, checkoutRouter)
        checkoutPresenter?.loadCart("Test", "Test", "Test" , mock(), false)
    }

    @Test(expected = Test.None::class)
    fun prepareCheckout_view_Null_interactor_null() {
        checkoutPresenter = CheckoutPresenter(null, null, checkoutRouter)
        checkoutPresenter?.prepareCheckout("", "", mock(), "")
    }

    @Test(expected = Test.None::class)
    fun prepareCheckout_view_notNull_interactor_notNull_customerId_Null() {
        checkoutPresenter = CheckoutPresenter(mock(), checkoutInteractor, checkoutRouter)
        checkoutPresenter?.prepareCheckout("", "", mock(), "")
    }

    @Test(expected = Test.None::class)
    fun prepareCheckout_view_notNull_interactor_notNull_customerId_notNull() {
        checkoutPresenter = CheckoutPresenter(mock(), checkoutInteractor, checkoutRouter)
        val customerInfo = spy<CustomerInfo>()
        customerInfo.mCustomerId = "Test"
        checkoutPresenter?.prepareCheckout("", "", customerInfo, "")
    }

    @Test
    fun applyPromoCode_success() {
        val view: ICheckoutContact.IView = mock()
        checkoutPresenter = CheckoutPresenter(view, checkoutInteractor, checkoutRouter)
        Mockito.`when`(view.getToken()).thenReturn("TestToken")
        val customerInfo = spy<CustomerInfo>()
        customerInfo.mCustomerId = "Test"
        Mockito.`when`(view.getPayer()).thenReturn(customerInfo)
        checkoutPresenter?.applyPromoCode("CODE_SUCCESS")
        verify(view).onApplyRemovePromoCodeSuccess(any(), any())
        verify(view, never()).onApplyPromoCodeError(any())
    }

    @Test
    fun applyPromoCode_apply_error() {
        val view: ICheckoutContact.IView = mock()
        checkoutPresenter = CheckoutPresenter(view, checkoutInteractor, checkoutRouter)
        val customerInfo = spy<CustomerInfo>()
        customerInfo.mCustomerId = "Test"
        Mockito.`when`(view.getToken()).thenReturn("TestToken")
        Mockito.`when`(view.getPayer()).thenReturn(customerInfo)
        checkoutPresenter?.applyPromoCode("CODE_ERROR")
        verify(view, never()).onApplyRemovePromoCodeSuccess(any(), any())
        verify(view).onApplyPromoCodeError(any())
    }

    @Test
    fun applyPromoCode_error() {
        val view: ICheckoutContact.IView = mock()
        checkoutPresenter = CheckoutPresenter(view, checkoutInteractor, checkoutRouter)
        val customerInfo = spy<CustomerInfo>()
        customerInfo.mCustomerId = "Test"
        Mockito.`when`(view.getToken()).thenReturn("TestToken")
        Mockito.`when`(view.getPayer()).thenReturn(customerInfo)
        checkoutPresenter?.applyPromoCode("CODE")
        verify(view, never()).onApplyRemovePromoCodeSuccess(any(), any())
        verify(view, never()).onApplyPromoCodeError(any())
        verify(view).showErrorMessage(MockitoHelper.anyObject<BaseResponse<Data<CartData>>>())
    }

    @Test(expected = Test.None::class)
    fun applyPromoCode_viewNull_interactorNull() {
        val view: ICheckoutContact.IView? = null
        checkoutPresenter = CheckoutPresenter(view, null, checkoutRouter)
        checkoutPresenter?.applyPromoCode("CODE_ERROR")
    }

    @Test
    fun applyPromoCode_viewNotNull_interactorNotNull_customerNull() {
        val view: ICheckoutContact.IView = mock()
        checkoutPresenter = CheckoutPresenter(view, checkoutInteractor, checkoutRouter)
        Mockito.`when`(view.getToken()).thenReturn(null)
        Mockito.`when`(view.getPayer()).thenReturn(null)
        checkoutPresenter?.applyPromoCode("CODE_ERROR")
        verify(view, never()).onApplyRemovePromoCodeSuccess(any(), any())
        verify(view).onApplyPromoCodeError(any())
    }

    @Test
    fun applyPromoCode_viewNotNull_interactorNotNull_customerIdNull() {
        val view: ICheckoutContact.IView = mock()
        checkoutPresenter = CheckoutPresenter(view, checkoutInteractor, checkoutRouter)
        val customerInfo = spy<CustomerInfo>()
        customerInfo.mCustomerId = null
        Mockito.`when`(view.getToken()).thenReturn(null)
        Mockito.`when`(view.getPayer()).thenReturn(customerInfo)
        checkoutPresenter?.applyPromoCode("CODE_ERROR")
        verify(view, never()).onApplyRemovePromoCodeSuccess(any(), any())
        verify(view).onApplyPromoCodeError(any())
    }

    @Test
    fun removePromoCode_success() {
        val view: ICheckoutContact.IView = mock()
        checkoutPresenter = CheckoutPresenter(view, checkoutInteractor, checkoutRouter)
        Mockito.`when`(view.getToken()).thenReturn("TestToken")
        Mockito.`when`(view.getCartId()).thenReturn(MockInterceptor.TEST_CART_ID)
        val customerInfo = spy<CustomerInfo>()
        customerInfo.mCustomerId = "Test"
        Mockito.`when`(view.getPayer()).thenReturn(customerInfo)
        checkoutPresenter?.removePromoCode()
        verify(view).onApplyRemovePromoCodeSuccess(any(), any())
        verify(view, never()).showErrorMessage(MockitoHelper.anyObject<BaseResponse<Data<CartData>>>())
    }

    @Test
    fun removePromoCode_error() {
        val view: ICheckoutContact.IView = mock()
        checkoutPresenter = CheckoutPresenter(view, checkoutInteractor, checkoutRouter)
        Mockito.`when`(view.getToken()).thenReturn("TestToken")
        Mockito.`when`(view.getCartId()).thenReturn("")
        checkoutPresenter?.removePromoCode()
        val customerInfo = spy<CustomerInfo>()
        customerInfo.mCustomerId = "Test"
        Mockito.`when`(view.getPayer()).thenReturn(customerInfo)
        verify(view, never()).onApplyRemovePromoCodeSuccess(any(), any())
        verify(view).showErrorMessage(MockitoHelper.anyObject<BaseResponse<Data<CartData>>>())
    }

    @Test(expected = Test.None::class)
    fun removePromoCode_viewNull_interactorNull() {
        val view: ICheckoutContact.IView? = null
        checkoutPresenter = CheckoutPresenter(view, null, checkoutRouter)
        checkoutPresenter?.removePromoCode()
    }

    @Test
    fun removePromoCode_viewNotNull_interactorNotNull_customerNull() {
        val view: ICheckoutContact.IView = mock()
        checkoutPresenter = CheckoutPresenter(view, checkoutInteractor, checkoutRouter)
        Mockito.`when`(view.getToken()).thenReturn(null)
        Mockito.`when`(view.getCartId()).thenReturn("")
        Mockito.`when`(view.getPayer()).thenReturn(null)
        checkoutPresenter?.removePromoCode()
        verify(view, never()).onApplyRemovePromoCodeSuccess(any(), any())
        verify(view).showErrorMessage(MockitoHelper.anyObject<BaseResponse<Data<CartData>>>())
    }

    @Test
    fun removePromoCode_viewNotNull_interactorNotNull_customerIdNull() {
        val view: ICheckoutContact.IView = mock()
        checkoutPresenter = CheckoutPresenter(view, checkoutInteractor, checkoutRouter)
        val customerInfo = spy<CustomerInfo>()
        customerInfo.mCustomerId = null
        Mockito.`when`(view.getToken()).thenReturn(null)
        Mockito.`when`(view.getCartId()).thenReturn("")
        Mockito.`when`(view.getPayer()).thenReturn(customerInfo)
        checkoutPresenter?.removePromoCode()
        verify(view, never()).onApplyRemovePromoCodeSuccess(any(), any())
        verify(view).showErrorMessage(MockitoHelper.anyObject<BaseResponse<Data<CartData>>>())
    }

    @Test(expected = Test.None::class)
    fun getAllAvailablePromoCode_success() {
        val view: ICheckoutContact.IView = mock()
        checkoutPresenter = CheckoutPresenter(view, checkoutInteractor, checkoutRouter)
        Mockito.`when`(view.getToken()).thenReturn("TestToken")
        checkoutPresenter?.getAllAvailablePromoCode()
    }

    @Test(expected = Test.None::class)
    fun getAllAvailablePromoCode_tokenNull() {
        val view: ICheckoutContact.IView = mock()
        checkoutPresenter = CheckoutPresenter(view, checkoutInteractor, checkoutRouter)
        Mockito.`when`(view.getToken()).thenReturn(null)
        checkoutPresenter?.getAllAvailablePromoCode()
    }

    @Test(expected = Test.None::class)
    fun getAllAvailablePromoCode_viewNull_interactorNull() {
        val view: ICheckoutContact.IView? = null
        checkoutPresenter = CheckoutPresenter(view, null, checkoutRouter)
        checkoutPresenter?.getAllAvailablePromoCode()
    }

    @Test
    fun quickBookFacilitySomeone_should_success_with_null_info() {
        val facility: Facility? = GeneralUtils.convertStringToObject(
            TestUtils.readJsonFile("facility.json")
        )
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
            TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
            UUID.randomUUID().toString(), null, facility,
            null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
            facility?.crmResourceId,
            cartItem.selectedDate,
            facilitySlot.mTimeRageName,
            facilitySlot.mTimeRangeId
        )
        val someoneInfo = CustomerInfo()
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacilitySomeone(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem), someoneInfo)
        verify(output, never()).onError(any())
    }

    @Test
    fun quickBookFacilitySomeone_should_success() {
        val facility: Facility? = GeneralUtils.convertStringToObject(
            TestUtils.readJsonFile("facility.json")
        )
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
            TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
            UUID.randomUUID().toString(), null, facility,
            null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
            facility?.crmResourceId,
            cartItem.selectedDate,
            facilitySlot.mTimeRageName,
            facilitySlot.mTimeRangeId
        )
        val someoneInfo = CustomerInfo()
        someoneInfo.mFullName = "test 1"
        someoneInfo.mEmail = "test@email.com"
        someoneInfo.mMobile = "98765432"
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacilitySomeone(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem), someoneInfo)
        verify(output, never()).onError(any())
    }

    @Test
    fun quickBookFacilitySomeone_success_view_null() {
        val facility: Facility? = GeneralUtils.convertStringToObject(
            TestUtils.readJsonFile("facility.json")
        )
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
            TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
            UUID.randomUUID().toString(), null, facility,
            null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
            facility?.crmResourceId,
            cartItem.selectedDate,
            facilitySlot.mTimeRageName,
            facilitySlot.mTimeRangeId
        )
        val someoneInfo = CustomerInfo()
        someoneInfo.mFullName = "test 1"
        someoneInfo.mEmail = "test@email.com"
        someoneInfo.mMobile = "98765432"
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacilitySomeone(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem), someoneInfo)
        verify(output, never()).onError(any())
    }

    @Test
    fun quickBookFacilitySomeone_should_error() {

        val facility: Facility? = null
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
            TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
            UUID.randomUUID().toString(), null, facility,
            null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
            facility?.crmResourceId,
            cartItem.selectedDate,
            facilitySlot.mTimeRageName,
            facilitySlot.mTimeRangeId
        )
        val someoneInfo = CustomerInfo()
        someoneInfo.mFullName = "test 1"
        someoneInfo.mEmail = "test@email.com"
        someoneInfo.mMobile = "98765432"
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacilitySomeone(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem), someoneInfo)
        verify(output, never()).onSuccess(any())
    }

    @Test
    fun quickBookFacilitySomeone_view_null() {

        val facility: Facility? = null
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
            TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
            UUID.randomUUID().toString(), null, facility,
            null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
            facility?.crmResourceId,
            cartItem.selectedDate,
            facilitySlot.mTimeRageName,
            facilitySlot.mTimeRangeId
        )
        val someoneInfo = CustomerInfo()
        someoneInfo.mFullName = "test 1"
        someoneInfo.mEmail = "test@email.com"
        someoneInfo.mMobile = "98765432"
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacilitySomeone(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem),someoneInfo)
        verify(output, never()).onSuccess(any())
    }

    @Test
    fun quickBookFacilitySomeone_interactor_null() {

        val facility: Facility? = null
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
            TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
            UUID.randomUUID().toString(), null, facility,
            null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
            facility?.crmResourceId,
            cartItem.selectedDate,
            facilitySlot.mTimeRageName,
            facilitySlot.mTimeRangeId
        )
        val someoneInfo = CustomerInfo()
        someoneInfo.mFullName = "test 1"
        someoneInfo.mEmail = "test@email.com"
        someoneInfo.mMobile = "98765432"
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacilitySomeone(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem),someoneInfo)
        verify(output, never()).onSuccess(any())
    }

    @Test
    fun quickBookFacilitySomeone_item_empty() {
        val someoneInfo = CustomerInfo()
        someoneInfo.mFullName = "test 1"
        someoneInfo.mEmail = "test@email.com"
        someoneInfo.mMobile = "98765432"
        checkoutView = null
        checkoutPresenter = CheckoutPresenter(checkoutView, null, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        spyPresenter?.quickBookFacilitySomeone(MockInterceptor.TEST_CART_ID, arrayListOf(),someoneInfo)
        verify(output, never()).onSuccess(any())
    }

    @Test
    fun quickBookFacilitySomeone_token_not_null() {
        checkoutView = mock()
        val customer = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val facility: Facility? = null
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
            TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
            UUID.randomUUID().toString(), null, facility,
            null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
            facility?.crmResourceId,
            cartItem.selectedDate,
            facilitySlot.mTimeRageName,
            facilitySlot.mTimeRangeId
        )
        val someoneInfo = CustomerInfo()
        someoneInfo.mFullName = "test 1"
        someoneInfo.mEmail = "test@email.com"
        someoneInfo.mMobile = "98765432"
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(customer)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        spyPresenter?.quickBookFacilitySomeone(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem),someoneInfo)
        verify(output, never()).onSuccess(any())
    }

    @Test
    fun quickBookFacilitySomeone_payer_null() {
        checkoutView = mock()
        val facility: Facility? = null
        val facilitySlot: SlotSessionInfo? = GeneralUtils.convertStringToObject(
            TestUtils.readJsonFile("facilitySession.json")
        )
        val cartItem = CartItem(
            UUID.randomUUID().toString(), null, facility,
            null, null, null, DateUtils.getCurrentDate(), arrayListOf(facilitySlot!!)
        )
        val facilityRequestItem = FacilityRequestItem(
            facility?.crmResourceId,
            cartItem.selectedDate,
            facilitySlot.mTimeRageName,
            facilitySlot.mTimeRangeId
        )
        val someoneInfo = CustomerInfo()
        someoneInfo.mFullName = "test 1"
        someoneInfo.mEmail = "test@email.com"
        someoneInfo.mMobile = "98765432"
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val output : IBaseContract.IBaseInteractorOutput<BookingResponse> = mock()
        Mockito.`when`(spyPresenter?.quickBookFacilityOutput).thenReturn(output)
        Mockito.`when`(checkoutView?.getPayer()).thenReturn(null)
        Mockito.`when`(checkoutView?.getToken()).thenReturn(MockInterceptor.TEST_EKIOSK_HEADER)
        spyPresenter?.quickBookFacilitySomeone(MockInterceptor.TEST_CART_ID, arrayListOf(facilityRequestItem),someoneInfo)
        verify(output, never()).onSuccess(any())
    }

    @Test(expected = Test.None::class)
    fun loadCartEventToGetExternalLineId_view_null_interactor_null(){
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(null, null, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        spyPresenter?.loadCartEventToGetExternalLineId(
            token = "tokenTest",
            userId = "userIdTest",
            cartId = "CartTest",
            eventRequestItems = null,
            isCheckCart = false
        )
    }

    @Test(expected = Test.None::class)
    fun loadCartEventToGetExternalLineId(){
        checkoutView = mock()
        checkoutPresenter = CheckoutPresenter(checkoutView, checkoutInteractor, checkoutRouter)
        val spyPresenter = spy(checkoutPresenter)
        val list = ArrayList<ProductRequestItem>()
        spyPresenter?.loadCartEventToGetExternalLineId(
            token = "tokenTest",
            userId = "userIdTest",
            cartId = "CartTest",
            eventRequestItems = list,
            isCheckCart = false
        )
    }


}