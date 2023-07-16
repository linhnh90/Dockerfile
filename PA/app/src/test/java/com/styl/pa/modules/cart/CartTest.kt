package com.styl.pa.modules.cart

import com.styl.pa.TestBase
import com.styl.pa.entities.cart.Attendee
import com.styl.pa.entities.cart.Cart
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.event.EventParticipant
import com.styl.pa.entities.event.Fields
import com.styl.pa.entities.event.TicketEntity
import com.styl.pa.entities.generateToken.Facility
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.pacesRequest.EventTicket
import com.styl.pa.modules.cart.interactor.CartInteractor
import com.styl.pa.modules.cart.presenter.CartPresenter
import com.styl.pa.services.IKioskServices
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.MockInterceptor
import com.styl.pa.utils.MockServiceGenerator
import com.styl.pa.utils.TestUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap


class CartTest: TestBase() {
    private var cartPresenter: CartPresenter? = null
    private var cartView: ICartContact.IView? = null
    private var cartInteractor: CartInteractor? = null

    @Before
    override fun setUp() {
        super.setUp()
        cartInteractor = CartInteractor()
        cartInteractor?.setKioskService(
                MockServiceGenerator.createTestService(IKioskServices::class.java, mockServer)
        )
    }

    @After
    override fun tearDown() {
        super.tearDown()
    }

    private fun mockCart(customerInfo: CustomerInfo?, hasAttendee: Boolean): Cart {
        val course1: ClassInfo = spy()
        course1.setClassId("62dbfee4-787d-eb11-81b2-00155dad210e")
        course1.sku = "TestCourse1"
        course1.setIndemnityRequired(true)
        val cartItem1 = CartItem(
                UUID.randomUUID().toString(), course1, null,
                null, null, null, null, null
        )
        val course2: ClassInfo = spy()
        course2.setClassId("72dbfee4-787d-eb11-81b2-00155dad210e")
        course2.sku = "TestCourse2"
        course2.setIndemnityRequired(false)
        val cartItem2 = CartItem(
                UUID.randomUUID().toString(), course2, null,
                null, null, null, null, null
        )
        val course3: ClassInfo = spy()
        course3.setClassId("82dbfee4-787d-eb11-81b2-00155dad210e")
        course3.sku = "TestCourse3"
        course3.setIndemnityRequired(true)
        val cartItem3 = CartItem(
                UUID.randomUUID().toString(), course3, null,
                null, null, null, null, null
        )
        val ig: InterestGroup = spy()
        ig.igId = "92dbfee4-787d-eb11-81b2-00155dad210e"
        ig.sku = "TestIg"
        val cartItem4 = CartItem(UUID.randomUUID().toString(), null, null,
            null, null, null, null, null, ig)

        val cart = Cart(arrayListOf(cartItem1, cartItem2, cartItem3, cartItem4), customerInfo, null)
        if (hasAttendee) {
            cart.items!![0].attendees = arrayListOf(Attendee(null, customerInfo, null))
            cart.items!![2].attendees = arrayListOf(Attendee(null, customerInfo, null))
            cart.items!![3].attendees = arrayListOf(Attendee(null, customerInfo, null))
        }
        return cart
    }

    @Test
    fun setCart_cart_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        cartPresenter?.setCart(null)
        assert(cartPresenter?.newCart == null)
    }

    @Test
    fun setCart_cart_not_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val cart = mockCart(null, false)
        cartPresenter?.setCart(cart)
        assert(cartPresenter?.newCart != null)
        assert(cartPresenter?.newCart?.items != null)
        assert(cartPresenter?.newCart?.items?.size == cart.items?.size)
    }

    @Test
    fun updateCartAttendee_new_attendee_with_indemnity() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )

        val cart = mockCart(customerInfo, false)
        cartPresenter?.setCart(cart)
        val productIndemnity = LinkedHashMap<String?, Boolean>()
        productIndemnity["62dbfee4-787d-eb11-81b2-00155dad210e"] = true
        productIndemnity["72dbfee4-787d-eb11-81b2-00155dad210e"] = true
        cartPresenter?.updateAttendee(
                MockInterceptor.TEST_EKIOSK_HEADER,
                customerInfo,
                productIndemnity
        )
        assert(cartPresenter?.newCart?.items != null)
        assert(cartPresenter?.newCart?.items!![0].attendees != null)
    }

    @Test
    fun updateCartAttendee_new_attendee_without_indemnity() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )

        cartPresenter?.setCart(mockCart(customerInfo, false))
        val productIndemnity = LinkedHashMap<String?, Boolean>()
        productIndemnity["62dbfee4-787d-eb11-81b2-00155dad210e"] = false
        productIndemnity["72dbfee4-787d-eb11-81b2-00155dad210e"] = false
        productIndemnity["82dbfee4-787d-eb11-81b2-00155dad210e"] = false
        cartPresenter?.updateAttendee(
                MockInterceptor.TEST_EKIOSK_HEADER,
                customerInfo,
                productIndemnity
        )
        assert(cartPresenter?.newCart?.items != null)
        assert(cartPresenter?.newCart?.items!![0].attendees != null)
    }

    @Test
    fun updateCartAttendee_attendee_already_exists() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )

        val cart = mockCart(customerInfo, true)
        cart.hasReservation = true
        cartPresenter?.setCart(cart)
        val productIndemnity = LinkedHashMap<String?, Boolean>()
        productIndemnity["82dbfee4-787d-eb11-81b2-00155dad210e"] = true
        productIndemnity["72dbfee4-787d-eb11-81b2-00155dad210e"] = false
        Mockito.`when`(cartView?.getPayer()).thenReturn(customerInfo)
        cartPresenter?.updateAttendee(
                MockInterceptor.TEST_EKIOSK_HEADER,
                customerInfo,
                productIndemnity
        )
        assert(cartPresenter?.newCart?.items != null)
        assert(cartPresenter?.newCart?.items!![0].attendees != null)
        assert(cartPresenter?.newCart?.items!![0].attendees!!.size == 0)
        assert(cartPresenter?.newCart?.items!![2].attendees != null)
        assert(cartPresenter?.newCart?.items!![2].attendees!!.size == 1)
    }

    @Test
    fun updateCartAttendee_attendee_already_exists_payer_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )

        cartPresenter?.setCart(mockCart(customerInfo, true))
        val productIndemnity = LinkedHashMap<String?, Boolean>()
        productIndemnity["62dbfee4-787d-eb11-81b2-00155dad210e"] = true
        productIndemnity["72dbfee4-787d-eb11-81b2-00155dad210e"] = false
        Mockito.`when`(cartView?.getPayer()).thenReturn(null)
        cartPresenter?.updateAttendee(
                MockInterceptor.TEST_EKIOSK_HEADER,
                customerInfo,
                productIndemnity
        )
        assert(cartPresenter?.newCart?.items != null)
        assert(cartPresenter?.newCart?.items!![0].attendees != null)
        assert(cartPresenter?.newCart?.items!![0].attendees!!.size == 1)
        assert(cartPresenter?.newCart?.items!![2].attendees != null)
        assert(cartPresenter?.newCart?.items!![2].attendees!!.size == 0)
    }

    @Test(expected = Test.None::class)
    fun updateCartAttendee_cart_not_null_view_null_customer_null() {
        cartView = null
        cartPresenter = CartPresenter(cartView, cartInteractor)
        cartPresenter?.setCart(mockCart(null, true))
        val productIndemnity = LinkedHashMap<String?, Boolean>()
        productIndemnity["62dbfee4-787d-eb11-81b2-00155dad210e"] = true
        productIndemnity["72dbfee4-787d-eb11-81b2-00155dad210e"] = false
        cartPresenter?.updateAttendee(
                MockInterceptor.TEST_EKIOSK_HEADER,
                null,
                productIndemnity
        )
    }

    @Test(expected = Test.None::class)
    fun updateCartAttendee_cart_null_view_null() {
        cartView = null
        cartPresenter = CartPresenter(cartView, cartInteractor)
        cartPresenter?.setCart(null)
        val productIndemnity = LinkedHashMap<String?, Boolean>()
        productIndemnity["62dbfee4-787d-eb11-81b2-00155dad210e"] = true
        productIndemnity["82dbfee4-787d-eb11-81b2-00155dad210e"] = true
        cartPresenter?.updateAttendee(
                MockInterceptor.TEST_EKIOSK_HEADER,
                null,
                productIndemnity
        )
    }

    @Test()
    fun updateCartAttendee_success() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        cartPresenter?.setCart(mockCart(customerInfo, false))
        val productIndemnity = LinkedHashMap<String?, Boolean>()
        productIndemnity["62dbfee4-787d-eb11-81b2-00155dad210e"] = true
        Mockito.`when`(cartView?.getPayer()).thenReturn(customerInfo)
        cartPresenter?.updateAttendee(
                MockInterceptor.TEST_EKIOSK_HEADER,
                customerInfo,
                productIndemnity
        )
        assert(cartPresenter?.newCart?.items != null)
        assert(cartPresenter?.newCart?.items!![0].attendees != null)
        assert(cartPresenter?.newCart?.items!![0].attendees!!.size == 1)
    }

    @Test
    fun updateCartAttendee_delete_error() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        customerInfo?.mEmail = null
        cartPresenter?.setCart(mockCart(customerInfo, true))
        val productIndemnity = LinkedHashMap<String?, Boolean>()
        productIndemnity["62dbfee4-787d-eb11-81b2-00155dad210e"] = true
        Mockito.`when`(cartView?.getPayer()).thenReturn(customerInfo)
        cartPresenter?.updateAttendee(
                MockInterceptor.TEST_EKIOSK_HEADER,
                customerInfo,
                productIndemnity
        )
        assert(cartPresenter?.newCart?.items!![0].attendees!!.size == 1)
    }

    @Test
    fun updateIndemnity_valid_index() {
        val cart = mockCart(null, true)
        cart.items!![0].attendees!![0].isIndemnity = false
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        cartPresenter?.updateIndemnity(cart, 0, 0, true)
        assert(cart.items!![0].attendees!![0].isIndemnity)
    }

    @Test
    fun updateIndemnity_invalid_index() {
        val cart = mockCart(null, true)
        cart.items!![0].attendees!![0].isIndemnity = false
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        cartPresenter?.updateIndemnity(cart, 0, -1, true)
        assert(!cart.items!![0].attendees!![0].isIndemnity)
    }

    @Test(expected = Test.None::class)
    fun updateIndemnity_cart_null() {
        val cart = null
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        cartPresenter?.updateIndemnity(cart, 0, -1, true)
    }

    @Test
    fun removeItemFromCart() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.setCart(mockCart(null, true))
        spyCartPresenter?.removeItemFromCart(1)
        assert(spyCartPresenter?.newCart?.items?.size ?: 0 == 3)
    }

    @Test
    fun removeItemFromCart_remove_all() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.setCart(mockCart(null, true))
        assert(spyCartPresenter?.newCart?.items != null)
        for (i in spyCartPresenter?.newCart?.items!!.indices) {
            spyCartPresenter.removeItemFromCart(0)
        }
        assert(spyCartPresenter.newCart?.items?.size ?: 0 == 0)
    }

    @Test(expected = Test.None::class)
    fun removeItemFromCart_cart_null() {
        cartView = null
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.cartAdapterView = mock()
        spyCartPresenter?.setCart(null)
        spyCartPresenter?.removeItemFromCart(0)
    }

    @Test(expected = Test.None::class)
    fun removeItemFromCart_cart_item_null() {
        cartView = null
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.cartAdapterView = mock()
        val cart = Cart(null, null, null)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(0)
    }

    @Test
    fun getItemId_classId() {
        val classInfo = ClassInfo()
        classInfo.sku = "TestClassId"
        val cartItem = CartItem(
                null,
                classInfo,
                null,
                null,
                null,
                null,
                null,
                null
        )
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        Assert.assertEquals(cartPresenter?.getItemId(cartItem), "TestClassId")
    }

    @Test
    fun getItemId_facilityId() {
        val facility = Facility()
        facility.sku = "TestFacilityId"
        val cartItem = CartItem(
                null,
                null,
                facility,
                null,
                null,
                null,
                null,
                null
        )
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        Assert.assertEquals(cartPresenter?.getItemId(cartItem), "TestFacilityId")
    }

    @Test
    fun getItemId_eventId() {
        val eventInfo = EventInfo()
        eventInfo.sku = "TestEventId"
        val cartItem = CartItem(
                null,
                null,
                null,
                eventInfo,
                null,
                null,
                null,
                null
        )
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        Assert.assertEquals(cartPresenter?.getItemId(cartItem), "TestEventId")
    }

    @Test
    fun getItemId_null() {
        val cartItem = CartItem(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        )
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        assert(cartPresenter?.getItemId(cartItem) == null)
    }

    @Test
    fun getItemId_cartItem_null() {
        val cartItem = null
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        assert(cartPresenter?.getItemId(cartItem) == null)
    }

    @Test(expected = Test.None::class)
    fun deleteParticipant_null_cart() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.deleteParticipant(MockInterceptor.TEST_EKIOSK_HEADER, null, 0)
    }

    @Test
    fun deleteParticipant_success() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.deleteParticipant(MockInterceptor.TEST_EKIOSK_HEADER, cart.items!![0], 0)
        assert(cart.items!![0].attendees?.size == 0)
    }

    @Test
    fun deleteParticipant_item_not_exist() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        val cartItem: CartItem = mock()
        spyCartPresenter?.deleteParticipant(MockInterceptor.TEST_EKIOSK_HEADER, cartItem, 0)
        assert(cart.items!![0].attendees?.size == 1)
    }

    @Test
    fun deleteParticipant_invalid_position() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.deleteParticipant(MockInterceptor.TEST_EKIOSK_HEADER, cart.items!![0], 2)
        assert(cart.items!![0].attendees?.size == 1)
    }

    @Test
    fun deleteParticipant_payer_not_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(spyCartPresenter?.view?.getPayer()).thenReturn(customerInfo)
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.deleteParticipant(MockInterceptor.TEST_EKIOSK_HEADER, cart.items!![0], 0)
        assert(cart.items!![0].attendees?.size == 0)
    }

    @Test(expected = Test.None::class)
    fun deleteParticipant_payer_not_null_interactor_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, null)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(spyCartPresenter?.view?.getPayer()).thenReturn(customerInfo)
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.deleteParticipant(MockInterceptor.TEST_EKIOSK_HEADER, cart.items!![0], 0)
    }

    @Test(expected = Test.None::class)
    fun deleteParticipant_payer_not_null_ig() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(spyCartPresenter?.view?.getPayer()).thenReturn(customerInfo)
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.deleteParticipant(MockInterceptor.TEST_EKIOSK_HEADER, cart.items!![3], 0)
    }

    @Test(expected = Test.None::class)
    fun deleteParticipant_payer_not_null_ig_but_null_interactor() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, null)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(spyCartPresenter?.view?.getPayer()).thenReturn(customerInfo)
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.deleteParticipant(MockInterceptor.TEST_EKIOSK_HEADER, cart.items!![3], 0)
    }

    @Test
    fun deleteParticipant_payer_not_null_response_failed() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(spyCartPresenter?.view?.getPayer()).thenReturn(customerInfo)
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.deleteParticipant(MockInterceptor.TEST_EKIOSK_HEADER, cart.items!![2], 0)
        assert(cart.items!![0].attendees?.size == 1)
    }

    @Test
    fun getOldListSize_cart_not_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, false)
        spyCartPresenter?.setCart(cart)
        val size = spyCartPresenter?.getOldListSize()
        assert(size == 4)
    }

    @Test
    fun getOldListSize_cart_not_null_items_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, false)
        cart.items = null
        spyCartPresenter?.setCart(cart)
        val size = spyCartPresenter?.getOldListSize()
        assert(size == 0)
    }

    @Test
    fun getOldListSize_cart_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = null as Cart?
        spyCartPresenter?.setCart(cart)
        val size = spyCartPresenter?.getOldListSize()
        assert(size == 0)
    }

    @Test(expected = Test.None::class)
    fun clearCart_cart_not_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, false)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.clearCart()
    }

    @Test(expected = Test.None::class)
    fun clearCart_cart_not_null_item_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, false)
        cart.items = null
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.clearCart()
    }

    @Test(expected = Test.None::class)
    fun removeItemFromCart_api_wrong_position() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                MockInterceptor.TEST_CART_ID,
                -1
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemFromCart_api_wrong_position_2() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                MockInterceptor.TEST_CART_ID,
                5
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemFromCart_cart_is_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.setCart(null)
        spyCartPresenter?.removeItemFromCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                MockInterceptor.TEST_CART_ID,
                5
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemFromCart_view_is_null() {
        cartView = null
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.setCart(null)
        spyCartPresenter?.removeItemFromCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                MockInterceptor.TEST_CART_ID,
                5
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemFromCart_interactor_is_null() {
        cartView = null
        cartPresenter = CartPresenter(cartView, null)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.setCart(null)
        spyCartPresenter?.removeItemFromCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                MockInterceptor.TEST_CART_ID,
                5
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemFromCart_items_null() {
        cartView = null
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        cart.items = null
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                MockInterceptor.TEST_CART_ID,
                1
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemFromCart_success() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                MockInterceptor.TEST_CART_ID,
                1
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemIgFromCart_success() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
            MockInterceptor.TEST_EKIOSK_HEADER,
            MockInterceptor.TEST_CART_ID,
            3
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemIgFromCart_interactor_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, null)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
            MockInterceptor.TEST_EKIOSK_HEADER,
            MockInterceptor.TEST_CART_ID,
            3
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemIgFromCart_payer_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
            MockInterceptor.TEST_EKIOSK_HEADER,
            MockInterceptor.TEST_CART_ID,
            3
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemIgFromCart_customerId_payer_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        customerInfo?.mCustomerId = null
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
            MockInterceptor.TEST_EKIOSK_HEADER,
            MockInterceptor.TEST_CART_ID,
            3
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemEventFromCart_success() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val eventInfo: EventInfo = spy()
        eventInfo.eventId = "102dbfee4-787d-eb11-81b2-00155dad210e"
        eventInfo.sku = "TestEvent1"
        val cartItem = CartItem(
            UUID.randomUUID().toString(), null, null,
            eventInfo, null, null, null, null
        )
        val cart = Cart(arrayListOf(cartItem), customerInfo, null)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
            MockInterceptor.TEST_EKIOSK_HEADER,
            MockInterceptor.TEST_CART_ID,
            0
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemEventFromCart_interactor_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, null)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val eventInfo: EventInfo = spy()
        eventInfo.eventId = "102dbfee4-787d-eb11-81b2-00155dad210e"
        eventInfo.sku = "TestEvent1"
        val cartItem = CartItem(
            UUID.randomUUID().toString(), null, null,
            eventInfo, null, null, null, null
        )
        val cart = Cart(arrayListOf(cartItem), customerInfo, null)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
            MockInterceptor.TEST_EKIOSK_HEADER,
            MockInterceptor.TEST_CART_ID,
            0
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemEventFromCart_payer_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, null)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val eventInfo: EventInfo = spy()
        eventInfo.eventId = "102dbfee4-787d-eb11-81b2-00155dad210e"
        eventInfo.sku = "TestEvent1"
        val cartItem = CartItem(
            UUID.randomUUID().toString(), null, null,
            eventInfo, null, null, null, null
        )
        val cart = Cart(arrayListOf(cartItem), null, null)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
            MockInterceptor.TEST_EKIOSK_HEADER,
            MockInterceptor.TEST_CART_ID,
            0
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemEventFromCart_payer_customer_id_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, null)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        customerInfo?.mCustomerId = null
        val eventInfo: EventInfo = spy()
        eventInfo.eventId = "102dbfee4-787d-eb11-81b2-00155dad210e"
        eventInfo.sku = "TestEvent1"
        val cartItem = CartItem(
            UUID.randomUUID().toString(), null, null,
            eventInfo, null, null, null, null
        )
        val cart = Cart(arrayListOf(cartItem), customerInfo, null)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
            MockInterceptor.TEST_EKIOSK_HEADER,
            MockInterceptor.TEST_CART_ID,
            0
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemFromCart_success_view_null() {
        cartView = null
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                MockInterceptor.TEST_CART_ID,
                1
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemFromCart_failed_view_null() {
        cartView = null
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                "Test",
                1
        )
    }

    @Test(expected = Test.None::class)
    fun removeItemFromCart_failed() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.removeItemFromCart(
                MockInterceptor.TEST_EKIOSK_HEADER,
                "Test",
                1
        )
    }


    @Test(expected = Test.None::class)
    fun clearCart_cart_null() {
        cartView = null
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.setCart(null)
        spyCartPresenter?.clearCart()
    }

    @Test
    fun canGoToProceedCheckout_should_return_false() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, false)
        spyCartPresenter?.setCart(cart)
        val canProceed = spyCartPresenter?.canGoToProceedCheckout()
        assert(canProceed == false)
    }

    @Test
    fun canGoToProceedCheckout_should_return_true() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        cart.items?.removeAt(1)
        spyCartPresenter?.setCart(cart)
        val canProceed = spyCartPresenter?.canGoToProceedCheckout()
        assert(canProceed == true)
    }

    @Test
    fun canGoToProceedCheckout_cart_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.setCart(null)
        val canProceed = spyCartPresenter?.canGoToProceedCheckout()
        assert(canProceed == false)
    }

    @Test
    fun canGoToProceedCheckout_items_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        cart.items = null
        spyCartPresenter?.setCart(cart)
        val canProceed = spyCartPresenter?.canGoToProceedCheckout()
        assert(canProceed == false)
    }

    @Test
    fun canProceed_cart_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.setCart(null)
        val canProceed = spyCartPresenter?.canProceed()
        assert(canProceed == false)
    }

    @Test
    fun canProceed_cart_payer_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, false)
        spyCartPresenter?.setCart(cart)
        val canProceed = spyCartPresenter?.canProceed()
        assert(canProceed == false)
    }
    @Test
    fun canProceed_cart_items_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(customerInfo, false)
        cart.items = null
        spyCartPresenter?.setCart(cart)
        val canProceed = spyCartPresenter?.canProceed()
        assert(canProceed == true)
    }

    @Test
    fun canProceed_return_true() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(customerInfo, true)
        cart.items?.removeAt(1) //mockCart item 1 doesn't have attendee
        spyCartPresenter?.setCart(cart)
        val canProceed = spyCartPresenter?.canProceed()
        assert(canProceed == true)
    }

    @Test
    fun canProceed_return_false_because_item_does_not_have_attendee() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        val canProceed = spyCartPresenter?.canProceed()
        assert(canProceed == false)
    }

    @Test
    fun canProceed_event_listSelectedTicket_empty(){
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val eventInfo: EventInfo = spy()
        eventInfo.eventId = "192dbfee4-787d-eb11-81b2-00155dad210e"
        eventInfo.sku = "TestIg"
        eventInfo.listSelectedTicket = ArrayList()
        val cartItem4 = CartItem(UUID.randomUUID().toString(), null, null,
            eventInfo, null, null, null, null, null)

        val cart = Cart(arrayListOf(cartItem4), customerInfo, null)
        spyCartPresenter?.setCart(cart)
        val canProceed = spyCartPresenter?.canProceed()
        assert(canProceed == false)
    }

    @Test
    fun canProceed_event_participant_not_full_fill(){
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val eventInfo = EventInfo()
        eventInfo.eventId = "192dbfee4-787d-eb11-81b2-00155dad210e"
        eventInfo.sku = "TestIg"
        val eventTicket = EventTicket()
        eventTicket.isAllParticipantRequired = false
        eventTicket.ticketMapCount = 1
        val ticketEntity = TicketEntity()
        ticketEntity.listParticipant = ArrayList()
        val participant = EventParticipant()
        participant.participantInfo = Fields()
        participant.isNew = true
        participant.isFullFillInfo = false
        ticketEntity.listParticipant.add(participant)
        eventTicket.listTicketParticipantEntity.add(ticketEntity)
        eventInfo.listSelectedTicket.add(eventTicket)
        val cartItem4 = CartItem(UUID.randomUUID().toString(), null, null,
            eventInfo, null, null, null, null, null)

        val cart = Cart(arrayListOf(cartItem4), customerInfo, null)
        spyCartPresenter?.setCart(cart)
        val canProceed = spyCartPresenter?.canProceed()
        assert(canProceed == false)
    }

    @Test
    fun canProceed_event_not_enough_participant(){
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val eventInfo = EventInfo()
        eventInfo.eventId = "192dbfee4-787d-eb11-81b2-00155dad210e"
        eventInfo.sku = "TestIg"
        val eventTicket = EventTicket()
        eventTicket.isAllParticipantRequired = true
        eventTicket.ticketMapCount = 2
        val ticketEntity = TicketEntity()
        ticketEntity.listParticipant = ArrayList()
        val participant = EventParticipant()
        participant.participantInfo = Fields()
        participant.isNew = true
        participant.isFullFillInfo = false
        ticketEntity.listParticipant.add(participant)
        eventTicket.listTicketParticipantEntity.add(ticketEntity)
        eventInfo.listSelectedTicket.add(eventTicket)
        val cartItem4 = CartItem(UUID.randomUUID().toString(), null, null,
            eventInfo, null, null, null, null, null)

        val cart = Cart(arrayListOf(cartItem4), customerInfo, null)
        spyCartPresenter?.setCart(cart)
        val canProceed = spyCartPresenter?.canProceed()
        assert(canProceed == false)
    }

    @Test
    fun canProceed_event_return_true(){
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val eventInfo = EventInfo()
        eventInfo.eventId = "192dbfee4-787d-eb11-81b2-00155dad210e"
        eventInfo.sku = "TestIg"
        val eventTicket = EventTicket()
        eventTicket.isAllParticipantRequired = true
        eventTicket.ticketMapCount = 1
        val ticketEntity = TicketEntity()
        ticketEntity.listParticipant = ArrayList()
        val participant = EventParticipant()
        participant.participantInfo = Fields()
        participant.isNew = false
        participant.isFullFillInfo = true
        ticketEntity.listParticipant.add(participant)
        eventTicket.listTicketParticipantEntity.add(ticketEntity)
        eventInfo.listSelectedTicket.add(eventTicket)
        val cartItem4 = CartItem(UUID.randomUUID().toString(), null, null,
            eventInfo, null, null, null, null, null)

        val cart = Cart(arrayListOf(cartItem4), customerInfo, null)
        spyCartPresenter?.setCart(cart)
        val canProceed = spyCartPresenter?.canProceed()
        assert(canProceed == true)
    }

    @Test
    fun getCartItemCount_cart_not_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        val size = spyCartPresenter?.getCartItemCount()
        assert(size == 4)
    }

    @Test
    fun getCartItemCount_cart_not_null_items_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        cart.items = null
        spyCartPresenter?.setCart(cart)
        val size = spyCartPresenter?.getCartItemCount()
        assert(size == 0)
    }

    @Test
    fun getCartItemCount_cart_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.setCart(null)
        val size = spyCartPresenter?.getCartItemCount()
        assert(size == 0)
    }

    @Test
    fun getAllCustomerFromAttendees_cart_not_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
                TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        val customers = spyCartPresenter?.getAllCustomerFromAttendees()
        assert(customers?.size == 1)
    }

    @Test
    fun getAllCustomerFromAttendees_items_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, true)
        cart.items = null
        spyCartPresenter?.setCart(cart)
        val customers = spyCartPresenter?.getAllCustomerFromAttendees()
        assert(customers?.isEmpty() == true)
    }

    @Test
    fun getAllCustomerFromAttendees_attendees_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val cart = mockCart(null, false)
        spyCartPresenter?.setCart(cart)
        val customers = spyCartPresenter?.getAllCustomerFromAttendees()
        assert(customers?.isEmpty() == true)
    }


    @Test
    fun getAllCustomerFromAttendees_cart_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.setCart(null)
        val customers = spyCartPresenter?.getAllCustomerFromAttendees()
        assert(customers?.isEmpty() == true)
    }

    @Test
    fun getItemName_with_cart_item_null() {
        cartPresenter = CartPresenter(null, null)
        val name = cartPresenter!!.getItemName(null)
        assert(name.equals(""))
    }

    @Test
    fun getItemName_course() {
        val classInfo = ClassInfo()
        classInfo.setClassTitle("Test")
        val cartItem = CartItem(null, classInfo, null, null, null, null, null, null)
        cartPresenter = CartPresenter(null, null)
        val name = cartPresenter!!.getItemName(cartItem)
        assert(name.equals("Test"))
    }

    @Test
    fun getItemName_ig() {
        val igInfo = InterestGroup()
        igInfo.igTitle = "Test"
        val cartItem = CartItem(null, null, null, null, null, null, null, null, igInfo)
        cartPresenter = CartPresenter(null, null)
        val name = cartPresenter!!.getItemName(cartItem)
        assert(name.equals("Test"))
    }

    @Test
    fun getItemName_event() {
        val eventInfo = EventInfo()
        eventInfo.eventTitle = "Test"
        val cartItem = CartItem(null, null, null, eventInfo, null, null, null, null)
        cartPresenter = CartPresenter(null, null)
        val name = cartPresenter!!.getItemName(cartItem)
        assert(name.equals("Test"))
    }

    @Test
    fun getItemName_should_return_empty() {
        val eventInfo = EventInfo()
        eventInfo.eventTitle = "Test"
        val cartItem = CartItem(null, null, null, null, null, null, null, null)
        cartPresenter = CartPresenter(null, null)
        val name = cartPresenter!!.getItemName(cartItem)
        assert(name?.isEmpty() == true)
    }

    @Test
    fun isCartChange() {
        cartPresenter = CartPresenter(null, null)
        val cart = null
        cartPresenter?.setCart(mockCart(null, false))
        assert(cartPresenter?.isCartChange(cart) == true)
    }

    @Test
    fun isExitsIndemnity_course_true() {
        cartPresenter = CartPresenter(null, null)
        val cart = mockCart(null, true)
        val testAttendee = Attendee(false)
        testAttendee.isIndemnity = false
        cart.items!![0].classInfo?.setIndemnityRequired(true)
        cart.items!![0].attendees!!.add(testAttendee)
        cartPresenter?.setCart(cart)
        val isExists = cartPresenter?.isExitsIndemnity()
        assert(isExists == true)
    }

    @Test
    fun isExitsIndemnity_ig_true() {
        cartPresenter = CartPresenter(null, null)
        val cart = mockCart(null, true)
        val testAttendee = Attendee(false)
        testAttendee.isIndemnity = false
        cart.items!![3].igInfo?.indemnityRequired = true
        cart.items!![3].attendees!!.add(testAttendee)
        cartPresenter?.setCart(cart)
        val isExists = cartPresenter?.isExitsIndemnity()
        assert(isExists == true)
    }

    @Test
    fun isExitsIndemnity_event_true() {
        cartPresenter = CartPresenter(null, null)
        val cart = mockCart(null, false)
        val eventInfo = EventInfo()
        val testAttendee = Attendee(false)
        testAttendee.isIndemnity = false
        eventInfo.indemnityRequired = true
        val cartItem = CartItem(null, null, null, eventInfo, null, null, null, null)
        cartItem.attendees = arrayListOf(testAttendee)
        cart.items?.add(cartItem)
        cartPresenter?.setCart(cart)
        val isExists = cartPresenter?.isExitsIndemnity()
        assert(isExists == true)
    }

    @Test
    fun isExitsIndemnity_event_true_2() {
        cartPresenter = CartPresenter(null, null)
        val cart = Cart(mutableListOf(), null, null)
        val eventInfo = EventInfo()
        val testAttendee = Attendee(false)
        testAttendee.isIndemnity = false
        eventInfo.indemnityRequired = true
        val cartItem = CartItem(null, null, null, eventInfo, null, null, null, null)
        cartItem.attendees = arrayListOf(testAttendee)
        cartItem.noOfEvent = 1
        cart.items?.add(cartItem)
        cartPresenter?.setCart(cart)
        val isExists = cartPresenter?.isExitsIndemnity()
        assert(isExists == true)
    }

    @Test
    fun isExitsIndemnity_cart_null() {
        cartPresenter = CartPresenter(null, null)
        cartPresenter?.setCart(null)
        val isExists = cartPresenter?.isExitsIndemnity()
        assert(isExists == false)
    }

    @Test
    fun isExitsIndemnity_items_null() {
        cartPresenter = CartPresenter(null, null)
        val cart = mockCart(null, false)
        cart.items = null
        cartPresenter?.setCart(cart)
        val isExists = cartPresenter?.isExitsIndemnity()
        assert(isExists == false)
    }

    @Test
    fun isExitsIndemnity_items_empty() {
        cartPresenter = CartPresenter(null, null)
        val cart = mockCart(null, false)
        cart.items?.clear()
        cartPresenter?.setCart(cart)
        val isExists = cartPresenter?.isExitsIndemnity()
        assert(isExists == false)
    }

    @Test(expected = Test.None::class)
    fun deleteParticipant_2_param_with_cart_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(spyCartPresenter?.view?.getPayer()).thenReturn(customerInfo)
        spyCartPresenter?.setCart(null)
        spyCartPresenter?.deleteParticipant(null, 0)
    }

    @Test(expected = Test.None::class)
    fun deleteParticipant_2_param_with_items_in_cart_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(spyCartPresenter?.view?.getPayer()).thenReturn(customerInfo)
        val cart = Cart(null, customerInfo, null)
        spyCartPresenter?.setCart(cart)
//        spyCartPresenter?.deleteParticipant(cart.items!![3], 0)
        spyCartPresenter?.deleteParticipant(null, 0)
    }

    @Test(expected = Test.None::class)
    fun deleteParticipant_2_param_with_attendee_in_cart_null() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(spyCartPresenter?.view?.getPayer()).thenReturn(customerInfo)
        val cart = mockCart(customerInfo, false)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.deleteParticipant(cart.items!![3], 0)
    }

    @Test(expected = Test.None::class)
    fun deleteParticipant_2_param() {
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(spyCartPresenter?.view?.getPayer()).thenReturn(customerInfo)
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.deleteParticipant(cart.items!![3], 0)
    }

    @Test
    fun getNewListSize_newCart_null(){
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.setCart(null)
        val result = spyCartPresenter?.getNewListSize()
        assert((result?: 0) == 0)
    }

    @Test
    fun getNewListSize_items_null(){
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(spyCartPresenter?.view?.getPayer()).thenReturn(customerInfo)
        val cart = mockCart(customerInfo, true)
        cart.items = null
        spyCartPresenter?.setCart(cart)
        val result = spyCartPresenter?.getNewListSize()
        assert((result?: 0) == 0)
    }

    @Test
    fun getNewListSize(){
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        Mockito.`when`(spyCartPresenter?.view?.getPayer()).thenReturn(customerInfo)
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        val result = spyCartPresenter?.getNewListSize()
        assert((result?: 0) == 4)
    }

    @Test(expected = Test.None::class)
    fun checkEventVacancy_view_null_interactor_null(){
        cartPresenter = CartPresenter(null, null)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.checkEventVacancy(
            MockInterceptor.TEST_EKIOSK_HEADER,
            eventInfo = null,
            isResident = true
        )
    }
    @Test(expected = Test.None::class)
    fun checkEventVacancy_event_info_cart_null(){
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        spyCartPresenter?.setCart(null)
        spyCartPresenter?.checkEventVacancy(
            MockInterceptor.TEST_EKIOSK_HEADER,
            eventInfo = null,
            isResident = true
        )
    }

    @Test(expected = Test.None::class)
    fun checkEventVacancy_event_code_null(){
        val eventInfo = EventInfo()
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.checkEventVacancy(
            MockInterceptor.TEST_EKIOSK_HEADER,
            eventInfo = eventInfo,
            isResident = true
        )
    }

    @Test(expected = Test.None::class)
    fun checkEventVacancy() {
        val eventInfo = EventInfo().apply {
            eventCode = "123456"
        }
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.checkEventVacancy(
            MockInterceptor.TEST_EKIOSK_HEADER,
            eventInfo = eventInfo,
            isResident = true
        )
    }

    @Test(expected = Test.None::class)
    fun checkEventVacancy_payer_null(){
        val eventInfo = EventInfo().apply {
            eventCode = "123456"
        }
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.checkEventVacancy(
            MockInterceptor.TEST_EKIOSK_HEADER,
            eventInfo = eventInfo,
            isResident = true
        )
    }

    @Test(expected = Test.None::class)
    fun checkEventVacancy_payer_customerId_null(){
        val eventInfo = EventInfo().apply {
            eventCode = "123456"
        }
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        customerInfo?.mCustomerId = null
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.checkEventVacancy(
            MockInterceptor.TEST_EKIOSK_HEADER,
            eventInfo = eventInfo,
            isResident = true
        )
    }

    @Test(expected = Test.None::class)
    fun checkResident_view_null_interactor_null(){
        cartPresenter = CartPresenter(null, null)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(null)
        spyCartPresenter?.checkResidency(
            MockInterceptor.TEST_EKIOSK_HEADER,
            eventInfo = null
        )
    }

    @Test(expected = Test.None::class)
    fun checkResident_event_postcode_null(){
        val event = EventInfo()
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.checkResidency(
            MockInterceptor.TEST_EKIOSK_HEADER,
            eventInfo = event
        )
    }

    @Test(expected = Test.None::class)
    fun checkResident(){
        val event = EventInfo().apply {
            postCode = "123445"
        }
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.checkResidency(
            MockInterceptor.TEST_EKIOSK_HEADER,
            eventInfo = event
        )
    }

    @Test(expected = Test.None::class)
    fun checkResident_cart_null(){
        val event = EventInfo().apply {
            postCode = "123445"
        }
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(null)
        spyCartPresenter?.checkResidency(
            MockInterceptor.TEST_EKIOSK_HEADER,
            eventInfo = event
        )
    }

    @Test(expected = Test.None::class)
    fun checkResident_payer_null(){
        val event = EventInfo().apply {
            postCode = "123445"
        }
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        val cart = mockCart(null, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.checkResidency(
            MockInterceptor.TEST_EKIOSK_HEADER,
            eventInfo = event
        )
    }

    @Test(expected = Test.None::class)
    fun checkResident_payer_customerId_null(){
        val event = EventInfo().apply {
            postCode = "123445"
        }
        cartView = mock()
        cartPresenter = CartPresenter(cartView, cartInteractor)
        val spyCartPresenter = spy(cartPresenter)
        val customerInfo = GeneralUtils.convertStringToObject<CustomerInfo>(
            TestUtils.readJsonFile("customer.json")
        )
        customerInfo?.mCustomerId = null
        val cart = mockCart(customerInfo, true)
        spyCartPresenter?.setCart(cart)
        spyCartPresenter?.checkResidency(
            MockInterceptor.TEST_EKIOSK_HEADER,
            eventInfo = event
        )
    }

}