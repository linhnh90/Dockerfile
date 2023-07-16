package com.styl.pa.modules.paymentsuccessful

import com.styl.pa.TestBase
import com.styl.pa.entities.cart.Attendee
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.classes.ClassSession
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.facility.SlotSessionInfo
import com.styl.pa.entities.generateToken.Facility
import com.styl.pa.entities.interestgroup.InterestGroup
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.reservation.BookingDetail
import com.styl.pa.entities.reservation.ProductInfo
import com.styl.pa.entities.reservation.TotalCostsResponse
import com.styl.pa.entities.wirecard.TransactionResponse
import com.styl.pa.modules.paymentSuccessful.IPaymentSuccessfulContract
import com.styl.pa.modules.paymentSuccessful.presenter.PaymentSuccessfulPresenter
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.TestUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import kotlin.collections.ArrayList

class PaymentSuccessfulTest: TestBase() {

    private var presenter: PaymentSuccessfulPresenter? = null
    private var view: IPaymentSuccessfulContract.IView? = null

    @Test
    fun cutString_str_empty() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val s = presenter?.cutString("", 10)
        assert(s?.size == 0)
    }

    @Test
    fun cutString_2() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val s = presenter?.cutString("Test cut string", 10)
        assert(s?.size == 2)
    }

    @Test
    fun cutString_4() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val s = presenter?.cutString("Test cut string", 4)
        assert(s?.size == 4)
    }

    @Test
    fun getSpace_normal() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val space = presenter?.getSpace(1)
        assert(space == "  ")
    }

    @Test
    fun getSpace_negative_number() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val space = presenter!!.getSpace(-1)
        assert(space.isEmpty())
    }

    @Test
    fun getOutletNameKiosk() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(TestUtils.readJsonFile("kioskInfo.json"))
        assert(presenter!!.getOutletNameKiosk(kioskInfo) == "ANG MO KIO CC")
    }

    @Test
    fun getOutletNameKiosk_kioskInfo_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        assert(presenter?.getOutletNameKiosk(null) == null)
    }

    @Test
    fun getOutletNameKiosk_outlet_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(TestUtils.readJsonFile("kioskInfo.json"))
        kioskInfo?.outlet = null
        assert(presenter?.getOutletNameKiosk(kioskInfo) == null)
    }

    @Test
    fun getReceiptNo_receiptId_not_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        assert(presenter?.getReceiptNo("Test", bookingDetail = null) == "Test")
    }

    @Test
    fun getReceiptNo_bookingDetails_not_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetails = spy<BookingDetail>()
        bookingDetails.receiptNo = "Test"
        assert(presenter?.getReceiptNo("Test1", bookingDetails) == "Test")
    }

    @Test
    fun getReceiptNo_bookingDetails_not_null_receiptNo_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetails = spy<BookingDetail>()
        bookingDetails.receiptNo = null
        assert(presenter?.getReceiptNo("Test", bookingDetails) == "Test")
    }

    @Test
    fun getPostalCode() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(TestUtils.readJsonFile("kioskInfo.json"))
        assert(presenter?.getPostalCode(kioskInfo) == "00000")
    }

    @Test
    fun getPostalCode_kioskInfo_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        assert(presenter?.getPostalCode(null) == null)
    }

    @Test
    fun getPostalCode_outlet_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(TestUtils.readJsonFile("kioskInfo.json"))
        kioskInfo?.outlet = null
        assert(presenter?.getPostalCode(kioskInfo) == null)
    }

    @Test
    fun getCustomerName_bookingDetails_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val customer = CustomerInfo()
        customer.mFullName = "Test"
        assert(presenter?.getCustomerName(customer, null) == "Test")
    }

    @Test
    fun getCustomerName_customer_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = BookingDetail()
        bookingDetail.customerName = "Test"
        assert(presenter?.getCustomerName(null, bookingDetail) == "Test")
    }

    @Test
    fun getCustomerName_customer_null_bookingDetails_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        assert(presenter?.getCustomerName(null, null) == null)
    }

    @Test
    fun getCardNo_txn_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        assert(presenter?.getCardNo(null) == "N/A")
    }

    @Test
    fun getCardNo_txn_not_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val txnResp = TransactionResponse()
        txnResp.panNumber = "0000"
        assert(presenter?.getCardNo(txnResp) == "0000")
    }

    @Test
    fun getCardNo_txn_not_null_cartNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val txnResp = TransactionResponse()
        txnResp.panNumber = null
        assert(presenter?.getCardNo(txnResp) == "N/A")
    }

    @Test
    fun getOutletName_product_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val name = presenter?.getOutletName(null, null, 0, null)
        assert(name == "N/A")
    }

    @Test
    fun getOutletName_product_notNull_outlet_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val productInfo = ProductInfo()
        productInfo.setOutletName(null)
        val name = presenter?.getOutletName(null, productInfo, 0, null)
        assert(name == "N/A")
    }

    @Test
    fun getOutletName_product_notNull_outlet_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val productInfo = ProductInfo()
        productInfo.setOutletName("Test")
        val name = presenter?.getOutletName(null, productInfo, 0, null)
        assert(name == "Test")
    }

    @Test
    fun getOutletName_attendee_notNull_product_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val attendee = Attendee(true)
        attendee.productInfo = null
        val name = presenter?.getOutletName(null, null, 0, attendee)
        assert(name == "N/A")
    }

    @Test
    fun getOutletName_attendee_notNull_product_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val attendee = Attendee(true)
        attendee.productInfo = ProductInfo()
        val name = presenter?.getOutletName(null, null, 0, attendee)
        assert(name == "N/A")
    }

    @Test
    fun getOutletName_attendee_notNull_product_notNull_outlet_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val attendee = Attendee(true)
        val productInfo = ProductInfo()
        productInfo.setOutletName("Test")
        attendee.productInfo = productInfo

        val name = presenter?.getOutletName(null, null, 0, attendee)
        assert(name == "Test")
    }

    @Test
    fun getGSTNo_product_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val name = presenter?.getGSTNo(null, null, 0, null)
        assert(name == "N/A")
    }

    @Test
    fun getGSTNo_product_notNull_gst_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val productInfo = ProductInfo()
        val name = presenter?.getGSTNo(null, productInfo, 0, null)
        assert(name == "N/A")
    }

    @Test
    fun getGSTNo_product_notNull_gst_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val productInfo = ProductInfo()
        productInfo.setGSTRegNo("Test")
        val name = presenter?.getGSTNo(null, productInfo, 0, null)
        assert(name == "Test")
    }

    @Test
    fun getGSTNo_attendee_notNull_product_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val attendee = Attendee(true)
        attendee.productInfo = null
        val name = presenter?.getGSTNo(null, null, 0, attendee)
        assert(name == "N/A")
    }

    @Test
    fun getGSTNo_attendee_notNull_product_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val attendee = Attendee(true)
        attendee.productInfo = ProductInfo()
        val name = presenter?.getGSTNo(null, null, 0, attendee)
        assert(name == "N/A")
    }

    @Test
    fun getGSTNo_attendee_notNull_product_notNull_gst_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val attendee = Attendee(true)
        val productInfo = ProductInfo()
        productInfo.setGSTRegNo("Test")
        attendee.productInfo = productInfo

        val name = presenter?.getGSTNo(null, null, 0, attendee)
        assert(name == "Test")
    }

    @Test
    fun getBeforeDiscountAmount_product_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val name = presenter?.getBeforeDiscountAmount(null, null, 0, null)
        assert(name == null)
    }

    @Test
    fun getBeforeDiscountAmount_product_notNull_beforeDiscountAmount_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val productInfo = ProductInfo()
        val name = presenter?.getBeforeDiscountAmount(null, productInfo, 0, null)
        assert(name == null)
    }

    @Test
    fun getBeforeDiscountAmount_product_notNull_beforeDiscountAmount_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val productInfo = ProductInfo()
        productInfo.setBeforeDiscountAmount(100f)
        val name = presenter?.getBeforeDiscountAmount(null, productInfo, 0, null)
        assert(name == 100f)
    }

    @Test
    fun getBeforeDiscountAmount_attendee_notNull_product_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val attendee = Attendee(true)
        attendee.productInfo = null
        val name = presenter?.getBeforeDiscountAmount(null, null, 0, attendee)
        assert(name == null)
    }

    @Test
    fun getBeforeDiscountAmount_attendee_notNull_product_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val attendee = Attendee(true)
        attendee.productInfo = ProductInfo()
        val name = presenter?.getBeforeDiscountAmount(null, null, 0, attendee)
        assert(name == null)
    }

    @Test
    fun getBeforeDiscountAmount_attendee_notNull_product_notNull_beforeDiscountAmount_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val attendee = Attendee(true)
        val productInfo = ProductInfo()
        productInfo.setBeforeDiscountAmount(100f)
        attendee.productInfo = productInfo

        val name = presenter?.getBeforeDiscountAmount(null, null, 0, attendee)
        assert(name == 100f)
    }

    @Test
    fun getTotalPayment_bookingDetails_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val total = presenter?.getTotalPayment(null, null)
        assert(total == null)
    }

    @Test
    fun getTotalPayment_bookingDetails_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = BookingDetail()
        val total = presenter?.getTotalPayment(null, bookingDetail)
        assert(total == 0f)
    }

    @Test
    fun getTotalPayment_bookingDetails_notNull_totalPayment_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = BookingDetail()
        bookingDetail.totalPayment = 100f
        val total = presenter?.getTotalPayment(null, bookingDetail)
        assert(total == 100f)
    }

    @Test
    fun getTotalPayment_totalCost_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val totalCost = TotalCostsResponse()
        totalCost.setTotalPaymentAmount(100f)
        val total = presenter?.getTotalPayment(totalCost, null)
        assert(total == 100f)
    }

    @Test
    fun getTotalGST_bookingDetails_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val total = presenter?.getTotalGST(null, null)
        assert(total == null)
    }

    @Test
    fun getTotalGST_bookingDetails_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = BookingDetail()
        val total = presenter?.getTotalGST(null, bookingDetail)
        assert(total == 0f)
    }

    @Test
    fun getTotalGST_bookingDetails_notNull_totalPayment_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = BookingDetail()
        bookingDetail.totalGST = 100f
        val total = presenter?.getTotalGST(null, bookingDetail)
        assert(total == 100f)
    }

    @Test
    fun getTotalGST_totalCost_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val totalCost = TotalCostsResponse()
        totalCost.setTotalGST(100.0)
        val total = presenter?.getTotalGST(totalCost, null)
        assert(total == 100f)
    }

    @Test
    fun getTotalGST_totalCost_notNull_gst_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val totalCost = TotalCostsResponse()
        totalCost.setTotalGST(null)
        val total = presenter?.getTotalGST(totalCost, null)
        assert(total == null)
    }

    @Test
    fun getTotalAdjustment_bookingDetails_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val total = presenter?.getTotalAdjustment(null, null)
        assert(total == null)
    }

    @Test
    fun getTotalAdjustment_bookingDetails_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = BookingDetail()
        val total = presenter?.getTotalAdjustment(null, bookingDetail)
        assert(total == 0f)
    }

    @Test
    fun getTotalAdjustment_bookingDetails_notNull_totalPayment_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = BookingDetail()
        bookingDetail.totalAdjustment = 100f
        val total = presenter?.getTotalAdjustment(null, bookingDetail)
        assert(total == 100f)
    }

    @Test
    fun getTotalAdjustment_totalCost_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val totalCost = TotalCostsResponse()
        totalCost.setTotalAdjustmentAmount(100f)
        val total = presenter?.getTotalAdjustment(totalCost, null)
        assert(total == 100f)
    }

    @Test
    fun getTotalDiscount_bookingDetails_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val total = presenter?.getTotalDiscount(null, null)
        assert(total == null)
    }

    @Test
    fun getTotalDiscount_bookingDetails_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = BookingDetail()
        val total = presenter?.getTotalDiscount(null, bookingDetail)
        assert(total == 0f)
    }

    @Test
    fun getTotalDiscount_bookingDetails_notNull_totalPayment_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = BookingDetail()
        bookingDetail.totalDiscount = 100f
        val total = presenter?.getTotalDiscount(null, bookingDetail)
        assert(total == 100f)
    }

    @Test
    fun getTotalDiscount_totalCost_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val totalCost = TotalCostsResponse()
        totalCost.setTotalDiscountAmount(100f)
        val total = presenter?.getTotalDiscount(totalCost, null)
        assert(total == 100f)
    }

    @Test
    fun getTotalFee_bookingDetails_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val total = presenter?.getTotalFee(null, null)
        assert(total == null)
    }

    @Test
    fun getTotalFee_bookingDetails_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = BookingDetail()
        val total = presenter?.getTotalFee(null, bookingDetail)
        assert(total == 0f)
    }

    @Test
    fun getTotalFee_bookingDetails_notNull_totalPayment_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = BookingDetail()
        bookingDetail.totalBeforeDiscount = 100f
        val total = presenter?.getTotalFee(null, bookingDetail)
        assert(total == 100f)
    }

    @Test
    fun getTotalFee_totalCost_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val totalCost = TotalCostsResponse()
        totalCost.setTotalBeforeDiscountAmount(100f)
        val total = presenter?.getTotalFee(totalCost, null)
        assert(total == 100f)
    }

    @Test
    fun formatBalance_str_null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val balance = presenter?.formatBalance(null)
        assert(balance == "N/A")
    }

    @Test
    fun formatBalance_str_empty() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val balance = presenter?.formatBalance("")
        assert(balance == "N/A")
    }

    @Test
    fun formatBalance_str_isNotDouble() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val balance = presenter?.formatBalance("test")
        assert(balance == "N/A")
    }

    @Test
    fun formatBalance_str_normal() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val balance = presenter?.formatBalance("540")
        assert(balance == "$5.40")
    }

    @Test
    fun getResourceName_cartItem_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val name = presenter?.getResourceName(null)
        assert(name == "")
    }

    @Test
    fun getResourceName_classInfo_NotNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val classInfo = spy<ClassInfo>()
        classInfo.setClassTitle("Test")
        val cartItem = CartItem(null, classInfo, null, null, null, null, null, null)
        val name = presenter?.getResourceName(cartItem)
        assert(name == "Test")
    }

    @Test
    fun getResourceName_igInfo_NotNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val classInfo = spy<InterestGroup>()
        classInfo.igTitle = "Test"
        val cartItem = CartItem(null, null, null, null, null, null, null, null, classInfo)
        val name = presenter?.getResourceName(cartItem)
        assert(name == "Test")
    }

    @Test
    fun getResourceName_facility_NotNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val facility = spy<Facility>()
        facility.setResourcetName("Test")
        val cartItem = CartItem(null, null, facility, null, null, null, null, null)
        val name = presenter?.getResourceName(cartItem)
        assert(name == "Test")
    }

    @Test
    fun getResourceName_event_NotNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val event = spy<EventInfo>()
        event.eventTitle = "Test"
        val cartItem = CartItem(null, null, null, event, null, null, null, null)
        val name = presenter?.getResourceName(cartItem)
        assert(name == "Test")
    }

    @Test
    fun getResourceId_cartItem_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val name = presenter?.getResourceId(null)
        assert(name == "")
    }

    @Test
    fun getResourceId_classInfo_NotNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val classInfo = spy<ClassInfo>()
        classInfo.setClassCode("Test")
        val cartItem = CartItem(null, classInfo, null, null, null, null, null, null)
        val name = presenter?.getResourceId(cartItem)
        assert(name == "Test")
    }

    @Test
    fun getResourceId_igInfo_NotNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val classInfo = spy<InterestGroup>()
        classInfo.igCode = "Test"
        val cartItem = CartItem(null, null, null, null, null, null, null, null, classInfo)
        val name = presenter?.getResourceId(cartItem)
        assert(name == "Test")
    }

    @Test
    fun getResourceId_facility_NotNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val facility = spy<Facility>()
        facility.setResourceID("Test")
        val cartItem = CartItem(null, null, facility, null, null, null, null, null)
        val name = presenter?.getResourceId(cartItem)
        assert(name == "Test")
    }

    @Test
    fun getResourceId_event_NotNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val event = spy<EventInfo>()
        event.eventCode = "Test"
        val cartItem = CartItem(null, null, null, event, null, null, null, null)
        val name = presenter?.getResourceId(cartItem)
        assert(name == "Test")
    }

    @Test
    fun getTitleClassReceipt_productInfo_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val title = presenter?.getTitleClassReceipt(null, null, 0)
        assert(title == "")
    }


    @Test
    fun getTitleClassReceipt_productInfo_NotNull_productCode_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val productInfo = spy<ProductInfo>()
        productInfo.setProductCode(null)
        val title = presenter?.getTitleClassReceipt(null, productInfo, 0)
        assert(title == null)
    }

    @Test
    fun getTitleClassReceipt_productInfo_NotNull_productCode_Empty() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val productInfo = spy<ProductInfo>()
        productInfo.setProductCode("")
        val title = presenter?.getTitleClassReceipt(null, productInfo, 0)
        assert(title == null)
    }

    @Test
    fun getTitleClassReceipt_productInfo_NotNull_productCode_normal() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val productInfo = spy<ProductInfo>()
        productInfo.setProductCode("TestCode")
        productInfo.setProductTitle("TestTitle")
        val title = presenter?.getTitleClassReceipt(null, productInfo, 0)
        assert(title == "TestCode - TestTitle")
    }

    @Test
    fun getTitleClassReceipt_classInfo_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val classInfo = spy<ClassInfo>()
        classInfo.setClassCode("TestCode")
        classInfo.setClassTitle("TestTitle")
        val cartItem = CartItem(null, classInfo, null, null, null, null,null, null)
        val title = presenter?.getTitleClassReceipt(cartItem, null, 0)
        assert(title == "TestCode - TestTitle")
    }

    @Test
    fun getTitleClassReceipt_igInfo_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val classInfo = spy<InterestGroup>()
        classInfo.igCode = "TestCode"
        classInfo.igTitle = "TestTitle"
        val cartItem = CartItem(null, null, null, null, null, null,null, null, classInfo)
        val title = presenter?.getTitleClassReceipt(cartItem, null, 0)
        assert(title == "TestCode - TestTitle")
    }

    @Test
    fun getTitleClassReceipt_facility_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val facility = spy<Facility>()
        facility.setResourcetName("TestTitle")
        val cartItem = CartItem(null, null, facility, null, null, null,null, null)
        val title = presenter?.getTitleClassReceipt(cartItem, null, 0)
        assert(title == "TestTitle")
    }

    @Test
    fun getTitleClassReceipt_eventInfo_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val event = spy<EventInfo>()
        event.eventCode = "TestCode"
        event.eventTitle = "TestTitle"
        val cartItem = CartItem(null, null, null, event, null, null,null, null)
        val title = presenter?.getTitleClassReceipt(cartItem, null, 0)
        assert(title == "TestCode - TestTitle")
    }

    @Test
    fun getPayAmount_bookingDetails_Null_nagative_Index_totalCost_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val payAmount = presenter?.getPayAmount(null, null, -1)
        assert(payAmount == null)
    }

    @Test
    fun getPayAmount_bookingDetails_notNull_nagative_Index_totalCost_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = spy<BookingDetail>()
        val payAmount = presenter?.getPayAmount(null, bookingDetail, -1)
        assert(payAmount == null)
    }

    @Test
    fun getPayAmount_bookingDetails_notNull_nagetive_index_product_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = spy<BookingDetail>()
        val products = ArrayList<ProductInfo>()
        products.add(spy())
        bookingDetail.products = products
        val payAmount = presenter?.getPayAmount(null, bookingDetail, -1)
        assert(payAmount == null)
    }

    @Test
    fun getPayAmount_bookingDetails_notNull_index_0_product_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = spy<BookingDetail>()
        val products = ArrayList<ProductInfo>()
        products.add(spy())
        bookingDetail.products = products
        val payAmount = presenter?.getPayAmount(null, bookingDetail, 0)
        assert(payAmount == null)
    }

    @Test
    fun getPayAmount_bookingDetails_notNull_index_2_product_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val bookingDetail = spy<BookingDetail>()
        val products = ArrayList<ProductInfo>()
        products.add(spy())
        bookingDetail.products = products
        val payAmount = presenter?.getPayAmount(null, bookingDetail, 2)
        assert(payAmount == null)
    }

    @Test
    fun getPayAmount_bookingDetails_totalCost_notNull() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val totalCost = spy<TotalCostsResponse>()
        val payAmount = presenter?.getPayAmount(totalCost, null, -1)
        assert(payAmount == null)
    }

    @Test
    fun getDate_item_Null() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val date = presenter?.getDate(null)
        assert(date.isNullOrEmpty())
    }

    @Test
    fun getDate_class() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val classInfo = spy<ClassInfo>()
        classInfo.setStartDate("2022-01-24T10:00:00Z")
        classInfo.setEndDate("2022-01-25T10:00:00Z")
        cartItem.classInfo = classInfo
        val date = presenter?.getDate(cartItem)
        assertEquals("24 Jan 2022 - 25 Jan 2022", date)
    }

    @Test
    fun getDate_ig() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val classInfo = spy<InterestGroup>()
        classInfo.startDate = "2022-01-24T10:00:00Z"
        classInfo.endDate = "2022-01-25T10:00:00Z"
        cartItem.igInfo = classInfo
        val date = presenter?.getDate(cartItem)
        assertEquals("24 Jan 2022 - 25 Jan 2022", date)
    }

    @Test
    fun getDate_facility() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val facility = spy<Facility>()
        cartItem.facility = facility
        cartItem.selectedDate = "2022-01-25T10:00:00Z"
        val date = presenter?.getDate(cartItem)
        assertEquals("25 Jan 2022", date)
    }

    @Test
    fun getDate_event() {
        view = mock()
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val event = spy<EventInfo>()
        event.dateFrom = "2022-01-24T10:00:00Z"
        event.dateTo = "2022-01-25T10:00:00Z"
        cartItem.event = event
        val date = presenter?.getDate(cartItem)
        assertEquals("24 Jan 2022 - 25 Jan 2022", date)
    }

    @Test
    fun marginLeft() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val m = presenter?.marginLeft(2)
        assert(m == "   ")
    }

    @Test
    fun formatTitle_normal_value_null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val title = presenter?.formatTitle(null, 2)
        assert(title == "")
    }

    @Test
    fun formatTitle_normal_value_Empty() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val title = presenter?.formatTitle("", 2)
        assert(title == "")
    }

    @Test
    fun formatTitle_normal() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val title = presenter?.formatTitle("This is a text", 20)
        assert(title == "This is a text       : ")
    }

    @Test
    fun getLine_negative_length() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        presenter?.lengthDefault = -1
        val value = presenter?.getLine()
        assert(value == "")
    }

    @Test
    fun getLine_normal() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val line = presenter?.getLine()
        assert(line == "-----------------------------------------")
    }

    @Test
    fun alignRow_value_Null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val value = presenter?.alignRow(null)
        assert(value == "")
    }

    @Test
    fun alignRow_value_Empty() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val value = presenter?.alignRow("")
        assert(value == "")
    }

    @Test
    fun alignRow_value_Normal() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        presenter?.lengthDefault = 10
        val value = presenter?.alignRow("This is text value")
        assert(value == " This is  \ntext value  \n")
    }

    @Test
    fun getDateTimeReceipt_class() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val classInfo: ClassInfo = spy()
        classInfo.setStartDate("2021-12-16T17:00:00")
        cartItem.classInfo = classInfo
        val datetime = presenter?.getDateTimeReceipt(cartItem, null, -1)
        assertEquals("16 Dec 2021", datetime)
    }

    @Test
    fun getDateTimeReceipt_ig() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val classInfo: InterestGroup = spy()
        classInfo.startDate = "2021-12-16T17:00:00"
        cartItem.igInfo = classInfo
        val datetime = presenter?.getDateTimeReceipt(cartItem, null, -1)
        assertEquals("16 Dec 2021", datetime)
    }

    @Test
    fun getDateTimeReceipt_facility() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val facility: Facility = spy()
        cartItem.selectedDate = "2021-12-16T17:00:00"
        cartItem.facility = facility
        val datetime = presenter?.getDateTimeReceipt(cartItem, null, -1)
        assertEquals("16 Dec 2021", datetime)
    }

    @Test
    fun getDateTimeReceipt_event() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val eventInfo: EventInfo = spy()
        eventInfo.dateFrom = "2021-12-16T17:00:00"
        cartItem.event = eventInfo
        val datetime = presenter?.getDateTimeReceipt(cartItem, null, -1)
        assertEquals("16 Dec 2021", datetime)
    }

    @Test
    fun getDateTimeReceipt_empty() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val datetime = presenter?.getDateTimeReceipt(cartItem, null, -1)
        assertEquals("", datetime)
    }

    @Test
    fun getDateTimeReceipt_cartItem_Null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val datetime = presenter?.getDateTimeReceipt(null, null, -1)
        assertEquals("", datetime)
    }

    @Test
    fun getVenue_productInfo_notNull() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val productInfo: ProductInfo = spy()
        productInfo.setVenue("Test")
        val venue = presenter?.getVenue(null, null, productInfo, 1)
        assertEquals("Test", venue)
    }

    @Test
    fun getVenue_cartItem_Null_productInfo_Null_kioskInfo_Null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val venue = presenter?.getVenue(null, null, null, 1)
        assert(venue == null)
    }

    @Test
    fun getVenue_kioskInfo_notNull_outlet_Null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(
                TestUtils.readJsonFile("kioskInfo.json")
        )
        kioskInfo?.outlet = null
        val venue = presenter?.getVenue(null, kioskInfo, null, 1)
        assert(venue == null)
    }

    @Test
    fun getVenue_kioskInfo_notNull_outlet_notNull() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val kioskInfo = GeneralUtils.convertStringToObject<KioskInfo>(
                TestUtils.readJsonFile("kioskInfo.json")
        )
        val venue = presenter?.getVenue(null, kioskInfo, null, 1)
        assert(venue == "ANG MO KIO COMMUNITY CLUB MANAGEMENT COMMITTEE CC")
    }

    @Test
    fun getVenue_class_session_Null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val classInfo: ClassInfo = spy()
        cartItem.classInfo = classInfo
        val venue = presenter?.getVenue(cartItem, null, null, 1)
        assert(null == venue)
    }

    @Test
    fun getVenue_class_session_Empty() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val classInfo: ClassInfo = spy()
        classInfo.setClassSessions(ArrayList())
        cartItem.classInfo = classInfo
        val venue = presenter?.getVenue(cartItem, null, null, 1)
        assert(null == venue)
    }

    @Test
    fun getVenue_class_external_Venue_Empty() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val classInfo: ClassInfo = spy()
        val classSessions: ArrayList<ClassSession> = ArrayList()
        val classSession: ClassSession = spy()
        classSession.setExternalVenue("")
        classSessions.add(classSession)
        classInfo.setClassSessions(classSessions)
        cartItem.classInfo = classInfo
        val venue = presenter?.getVenue(cartItem, null, null, 1)
        assert(null == venue)
    }

    @Test
    fun getVenue_class_external_Venue_Null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val classInfo: ClassInfo = spy()
        val classSessions: ArrayList<ClassSession> = ArrayList()
        classInfo.setClassSessions(classSessions)
        cartItem.classInfo = classInfo
        val venue = presenter?.getVenue(cartItem, null, null, 1)
        assert(null == venue)
    }

    @Test
    fun getVenue_class_external_Venue_normal() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val classInfo: ClassInfo = spy()
        val classSessions: ArrayList<ClassSession> = ArrayList()
        classInfo.setClassSessions(classSessions)
        val classSession: ClassSession = spy()
        classSession.setExternalVenue("Test")
        classSessions.add(classSession)
        cartItem.classInfo = classInfo
        val venue = presenter?.getVenue(cartItem, null, null, 1)
        assertEquals("Test", venue)
    }

    @Test
    fun getVenue_facility() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val facility: Facility = spy()
        facility.outletName = "Test"
        cartItem.facility = facility
        val venue = presenter?.getVenue(cartItem, null, null, 1)
        assertEquals("Test", venue)
    }

    @Test
    fun getVenue_event() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val event: EventInfo = spy()
        event.outletName = "Test"
        event.venue = "Test"
        cartItem.event = event
        val venue = presenter?.getVenue(cartItem, null, null, 1)
        assertEquals("Test", venue)
    }

    @Test
    fun getVenue_ig() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val event: InterestGroup = spy()
        event.outletName = "Test"
        cartItem.igInfo = event
        val venue = presenter?.getVenue(cartItem, null, null, 1)
        assertEquals("Test", venue)
    }

    @Test
    fun getTime_cartItem_null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val time = presenter?.getTime(null)
        assert(time == "")
    }

    @Test
    fun getTime_class_session_null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val classInfo: ClassInfo = spy()
        cartItem.classInfo = classInfo
        val time = presenter?.getTime(cartItem)
        assert(time == "")
    }

    @Test
    fun getTime_class_session_empty() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val classInfo: ClassInfo = spy()
        classInfo.setClassSessions(ArrayList())
        cartItem.classInfo = classInfo
        val time = presenter?.getTime(cartItem)
        assert(time == "")
    }

    @Test
    fun getTime_class_session_normal() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val classInfo: ClassInfo = spy()
        val classSession: ClassSession = spy()
        classSession.setStartTime("2021-12-16T17:00:00")
        classSession.setEndTime("2021-12-16T18:00:00")
        classInfo.setClassSessions(arrayListOf(classSession))
        cartItem.classInfo = classInfo
        val time = presenter?.getTime(cartItem)
        assert(time == "05:00 PM - 06:00 PM")
    }

    @Test
    fun getTime_facility_slot_null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val facility: Facility = spy()
        cartItem.facility = facility
        val time = presenter?.getTime(cartItem)
        assert(time == "")
    }

    @Test
    fun getTime_facility_slot_empty() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val facility: Facility = spy()
        cartItem.slotList = ArrayList()
        cartItem.facility = facility
        val time = presenter?.getTime(cartItem)
        assert(time == "")
    }

    @Test
    fun getTime_facility_slot_normal() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem: CartItem = spy()
        val facility: Facility = spy()
        val slot : SlotSessionInfo = spy()
        slot.mTimeRageName = "8:00 AM - 10:00 AM"
        cartItem.slotList = arrayListOf(slot)
        cartItem.facility = facility
        val time = presenter?.getTime(cartItem)
        assert(time == "8:00 AM - 10:00 AM")
    }

    @Test
    fun getSessionInfoReceipt_cart_Null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val info = presenter?.getSessionInfoReceipt(null, false)
        assert(info == "")
    }

    @Test
    fun getSessionInfoReceipt_class_session_null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val classInfo = spy<ClassInfo>()
        cartItem.classInfo = classInfo
        val info = presenter?.getSessionInfoReceipt(cartItem, false)
        assert(info == "")
    }

    @Test
    fun getSessionInfoReceipt_class_session_empty() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val classInfo = spy<ClassInfo>()
        classInfo.setClassSessions(ArrayList())
        cartItem.classInfo = classInfo
        val info = presenter?.getSessionInfoReceipt(cartItem, false)
        assert(info == "")
    }

    @Test
    fun getSessionInfoReceipt_class_session_normal() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val classInfo = spy<ClassInfo>()
        val classSession = spy<ClassSession>()
        classSession.setStartTime("2021-12-16T17:00:00")
        classSession.setEndTime("2021-12-16T18:00:00")
        classInfo.setClassSessions(arrayListOf(classSession))
        cartItem.classInfo = classInfo
        val info = presenter?.getSessionInfoReceipt(cartItem, false)
        assert(info == "05:00 PM - 06:00 PM\n")
    }

    @Test
    fun getSessionInfoReceipt_ig_session_normal() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val igInfo = spy<InterestGroup>()
        val classSession = spy<ClassSession>()
        classSession.setStartTime("2021-12-16T17:00:00")
        classSession.setEndTime("2021-12-16T18:00:00")
        igInfo.igSessions = arrayListOf(classSession)
        cartItem.igInfo = igInfo
        val info = presenter?.getSessionInfoReceipt(cartItem, false)
        assert(info == "05:00 PM - 06:00 PM\n")
    }

    @Test
    fun getSessionInfoReceipt_ig_session_null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val igInfo = spy<InterestGroup>()
        val classSession = spy<ClassSession>()
        classSession.setStartTime("2021-12-16T17:00:00")
        classSession.setEndTime("2021-12-16T18:00:00")
        igInfo.igSessions = null
        cartItem.igInfo = igInfo
        val info = presenter?.getSessionInfoReceipt(cartItem, false)
        assert(info == "")
    }
    @Test
    fun getSessionInfoReceipt_ig_session_empty() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val igInfo = spy<InterestGroup>()
        val classSession = spy<ClassSession>()
        classSession.setStartTime("2021-12-16T17:00:00")
        classSession.setEndTime("2021-12-16T18:00:00")
        igInfo.igSessions = arrayListOf()
        cartItem.igInfo = igInfo
        val info = presenter?.getSessionInfoReceipt(cartItem, false)
        assert(info == "")
    }

    @Test
    fun getSessionInfoReceipt_facility_slot_Null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val facility = spy<Facility>()
        cartItem.facility = facility
        val info = presenter?.getSessionInfoReceipt(cartItem, false)
        assert(info == "")
    }

    @Test
    fun getSessionInfoReceipt_facility_slot_Empty() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val facility = spy<Facility>()
        cartItem.facility = facility
        cartItem.slotList = ArrayList()
        val info = presenter?.getSessionInfoReceipt(cartItem, false)
        assert(info == "")
    }

    @Test
    fun getSessionInfoReceipt_facility_slot_normal_cepas_false() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val facility = spy<Facility>()
        val slot1 = spy<SlotSessionInfo>()
        slot1.mTimeRageName = "8:00 AM - 10:00 PM"
        val slot2 = spy<SlotSessionInfo>()
        slot2.mTimeRageName = "10:00 AM - 12:00 PM"
        cartItem.facility = facility
        cartItem.slotList = arrayListOf(slot1, slot2)
        val info = presenter?.getSessionInfoReceipt(cartItem, false)
        assert(info == "\n" +
                "               8:00 AM - 10:00 PM\n" +
                "               10:00 AM - 12:00 PM\n")
    }

    @Test
    fun getSessionInfoReceipt_facility_slot_normal_cepas_true() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val facility = spy<Facility>()
        val slot1 = spy<SlotSessionInfo>()
        slot1.mTimeRageName = "8:00 AM - 10:00 PM"
        val slot2 = spy<SlotSessionInfo>()
        slot2.mTimeRageName = "10:00 AM - 12:00 PM"
        cartItem.facility = facility
        cartItem.slotList = arrayListOf(slot1, slot2)
        val info = presenter?.getSessionInfoReceipt(cartItem, true)
        assert(info == "8:00 AM - 10:00 PM\n" +
                "           10:00 AM - 12:00 PM\n")
    }

    @Test
    fun getSessionInfoReceipt_event_dateFrom_null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val event = spy<EventInfo>()
        cartItem.event = event
        val info = presenter?.getSessionInfoReceipt(cartItem, true)
        assert(info == "")
    }

    @Test
    fun getSessionInfoReceipt_event_dateFrom_empty() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val event = spy<EventInfo>()
        event.dateFrom = ""
        cartItem.event = event
        val info = presenter?.getSessionInfoReceipt(cartItem, true)
        assert(info == "")
    }

    @Test
    fun getSessionInfoReceipt_event_dateTo_null() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val event = spy<EventInfo>()
        event.dateFrom = "2021-12-16T17:00:00"
        cartItem.event = event
        val info = presenter?.getSessionInfoReceipt(cartItem, true)
        assert(info == "")
    }

    @Test
    fun getSessionInfoReceipt_event_dateTo_empty() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val event = spy<EventInfo>()
        event.dateFrom = "2021-12-16T17:00:00"
        event.dateTo = ""
        cartItem.event = event
        val info = presenter?.getSessionInfoReceipt(cartItem, true)
        assert(info == "")
    }

    @Test
    fun getSessionInfoReceipt_event_normal() {
        presenter = PaymentSuccessfulPresenter(view, mock())
        val cartItem = spy<CartItem>()
        val event = spy<EventInfo>()
        event.dateFrom = "2021-12-16T17:00:00"
        event.dateTo = "2021-12-16T19:00:00"
        cartItem.event = event
        val info = presenter?.getSessionInfoReceipt(cartItem, true)
        assert(info == "05:00 PM - 07:00 PM\n")
    }
}