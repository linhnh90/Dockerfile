package com.styl.pa.modules.paymentSuccessful.presenter

import android.content.Context
import android.graphics.Bitmap
import android.util.Patterns
import androidx.annotation.VisibleForTesting
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.itextpdf.text.Element
import com.styl.castle_terminal_upt1000_api.define.FieldData
import com.styl.pa.ExcludeFromJacocoGeneratedReport
import com.styl.pa.R
import com.styl.pa.entities.BaseResponse
import com.styl.pa.entities.cart.Attendee
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.TicketEntity
import com.styl.pa.entities.healthDevice.InfoHealthDevice
import com.styl.pa.entities.kioskactivation.KioskInfo
import com.styl.pa.entities.reservation.BookingDetail
import com.styl.pa.entities.reservation.ProductInfo
import com.styl.pa.entities.reservation.TotalCostsResponse
import com.styl.pa.entities.sendEmail.SendEmailRequest
import com.styl.pa.entities.sendEmail.SendEmailResponse
import com.styl.pa.entities.sendmail.SendMailRequest
import com.styl.pa.entities.sendmail.SendMailResponse
import com.styl.pa.entities.wirecard.TransactionResponse
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.paymentSuccessful.IPaymentSuccessfulContract
import com.styl.pa.modules.paymentSuccessful.interactor.PaymentSuccessfulInteractor
import com.styl.pa.modules.peripheralsManager.peripheralsService.Peripheral
import com.styl.pa.modules.printer.IPrinterFontConfig
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.ERROR_NO_PAPER
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.LOW_PAPER
import com.styl.pa.modules.printer.customPrinterService.PrinterErrorType.Companion.VALID_CODE
import com.styl.pa.utils.GeneralUtils
import com.styl.pa.utils.LogManager
import com.styl.pa.utils.MySharedPref
import com.styl.pa.utils.PdfUtils
import it.custom.printer.api.android.PrinterFont
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by trangpham on 9/30/2018
 */
class PaymentSuccessfulPresenter(var view: IPaymentSuccessfulContract.IView?, var context: Context?) : IPaymentSuccessfulContract.IPresenter, IPaymentSuccessfulContract.IInteractorOutput {

    private var interactor: IPaymentSuccessfulContract.IInteractor? = PaymentSuccessfulInteractor(this)
    private var printerFontConfig: IPrinterFontConfig? = null

    private val CEPAS = "01"
    private val CREDIT_CARD = "02"
    private val NETS = "03"
    private val CUP = "04"

    private var stt = 0

    @ExcludeFromJacocoGeneratedReport
    override fun isMailValid(email: String?): Boolean {
        if (email.isNullOrBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view?.showInvalidMail()
            return false
        }
        return true
    }

    @ExcludeFromJacocoGeneratedReport
    override fun sendMail(token: String?, receivers: List<String>?, subject: String?, content: String?) {
        view?.showLoading()

        val request = SendMailRequest(receivers, subject, content)
        interactor?.sendMail(token, request)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onSuccess(data: SendMailResponse?) {
        view?.dismissLoading()
        view?.onSendSuccess()
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onError(data: BaseResponse<SendMailResponse>) {
        view?.dismissLoading()
        view?.showErrorMessage(data)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun onDestroy() {
        interactor?.onDestroy()
        view = null
        interactor = null
    }

    fun cutString(str: String, maxLength: Int): ArrayList<String> {
        var arrayResult: ArrayList<String> = ArrayList()
        var resultItem = ""
        if (str.isNotEmpty()) {
            var arrayString = str.split(" ")


            for (i in 0..(arrayString.size - 1)) {
                if (arrayString[i].length > maxLength) {
                    arrayResult.add(resultItem + " " + arrayString[i].substring(0, (maxLength - 1 - resultItem.length)))
                    resultItem = ""
                    resultItem += arrayString[i].substring(maxLength - 1 - resultItem.length)

                } else {
                    if ((resultItem + " " + arrayString[i]).length > maxLength) {
                        arrayResult.add(resultItem)
                        resultItem = ""
                        resultItem += arrayString[i]
                    } else {
                        resultItem += " " + arrayString[i]
                    }
                }
            }

            if (resultItem.trim().isNotEmpty()) {
                arrayResult.add(resultItem)
            }
        }

        return arrayResult
    }

    fun getSpace(number: Int): String {
        var data = ""
        for (i in 0..number) {
            data += " "
        }
        return data
    }

    @ExcludeFromJacocoGeneratedReport
    private fun getPaymentModeByType(data: String?): String? {
        when (data) {
            CEPAS, Integer.toHexString(FieldData.TxnType.PAYMENT_BY_EZL.toInt()) -> {
                return context?.resources?.getString(R.string.ezl_card)
            }
            Integer.toHexString(FieldData.TxnType.PAYMENT_BY_NETS_NFP.toInt())-> {
                return context?.resources?.getString(R.string.nets_flashpay)
            }
            CREDIT_CARD, Integer.toHexString(FieldData.TxnType.PAYMENT_BY_SCHEME_CREDIT.toInt()) -> {
                return context?.resources?.getString(R.string.credit_card)
            }
            NETS, Integer.toHexString(FieldData.TxnType.PAYMENT_BY_NETS_EFT.toInt()) -> {
                return context?.resources?.getString(R.string.nets)
            }
            CUP -> {
                return context?.resources?.getString(R.string.cup)
            }
        }
        return "N/A"
    }

    fun marginLeft(dp: Int): String {
        var v = ""
        for (i in 0..dp) {
            v += " "
        }
        return v
    }

    fun formatTitle(data: String?, maxLength: Int): String {
        if (data.isNullOrEmpty())
            return ""
        var value = data
        for (index in data.length..maxLength) {
            value += " "
        }
        return value + ": "
    }

    fun getLine(): String {
        var value = ""
        for (i in 0..lengthDefault)
            value += "-"
        return value
    }

    var lengthDefault = Peripheral.CUSTOM_PRINTER_MAX_CHARACTERS
    fun alignRow(data: String?): String {
        if (data.isNullOrEmpty())
            return ""

        var value = ""
        for (d in cutString(data, lengthDefault)) {
            value += d + "  \n"
        }

        return value
    }

    fun getOutletNameKiosk(kioskInfo: KioskInfo?): String? {
        return kioskInfo?.outlet?.getFriendlyName()
    }

    fun getReceiptNo(receiptId: String?, bookingDetail: BookingDetail?): String? {
        return bookingDetail?.receiptNo ?: receiptId
    }

    @ExcludeFromJacocoGeneratedReport
    fun getDateTimePayment(payment: TransactionResponse?, bookingDetail: BookingDetail?): String? {
        val sdf2 = SimpleDateFormat("dd MMM yyyy hh:mm:ss aa", Locale.ENGLISH)
        if (!bookingDetail?.receiptDateTime.isNullOrBlank()) {
            return bookingDetail?.receiptDateTime
        } else if (!payment?.dateTime.isNullOrBlank()) {
            try {
                val sdfTerminal = SimpleDateFormat("yyyyMMdd HHmmss", Locale.ENGLISH)
                return sdf2.format(sdfTerminal.parse(payment?.dateTime))
            } catch (e: ParseException) {
                return sdf2.format(Calendar.getInstance().time)
            }
        } else {
            return sdf2.format(Calendar.getInstance().time)
        }
    }

    fun getCustomerName(customer: CustomerInfo?, bookingDetail: BookingDetail?): String? {
        if (bookingDetail != null)
            return bookingDetail.customerName

        return customer?.mFullName
    }

    fun getPostalCode(kioskInfo: KioskInfo?): String? {
        return kioskInfo?.outlet?.getPostcode()
    }

    @ExcludeFromJacocoGeneratedReport
    fun headerReceipt(kioskInfo: KioskInfo?, receiptId: String?, payment: TransactionResponse?,
                      customer: CustomerInfo?, bookingDetail: BookingDetail?) {
        var mainActivity = (context as MainActivity)

        //friendlyName'Outlet of kiosk
        var location = getOutletNameKiosk(kioskInfo)

        if (!isPrintReceipt) {
            location += "\n\n"
            PdfUtils.writeText(location, Element.ALIGN_CENTER, PdfUtils.boldFont1, 0f, 0f)
        } else {
            mainActivity.printText(alignRow(location), printerFontConfig?.setAlignCenter(), printerFontConfig?.setSize1X(),
                    true, false, false)
        }

        var address = kioskInfo?.outlet?.getAddress() + "\n"
        address += context?.resources?.getString(R.string.singapore) + " " + getPostalCode(kioskInfo) + "\n"

        var existTel = false
        if (!kioskInfo?.outlet?.getPhone().isNullOrEmpty()) {
            address += context?.resources?.getString(R.string.tel) + " " + kioskInfo?.outlet?.getPhone()
            existTel = true
        }

        if (!kioskInfo?.outlet?.getFax().isNullOrEmpty()) {
            address += (if (existTel) " / " else "") + context?.resources?.getString(R.string.fax) + " " + kioskInfo?.outlet?.getFax()
        }

        if (!isPrintReceipt) {
            PdfUtils.writeText(address, Element.ALIGN_CENTER, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity.printText(address, printerFontConfig?.setAlignCenter(), printerFontConfig?.setSize1X(),
                    false, false, false)
        }

        var website = context?.resources?.getString(R.string.web_pa)

        if (!isPrintReceipt) {
            PdfUtils.writeText(website, Element.ALIGN_CENTER, PdfUtils.underlineFont1, 0f, 0f)
            PdfUtils.writeText("\n", Element.ALIGN_CENTER, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity.printText(alignRow(website), printerFontConfig?.setAlignCenter(), printerFontConfig?.setSize1X(),
                    false, false, true)
        }

        var receipt = context?.resources?.getString(R.string.reference) + " " + getReceiptNo(receiptId, bookingDetail) + "\n"

        if (!isPrintReceipt) {
            receipt += "\n"
            PdfUtils.writeText(receipt, Element.ALIGN_LEFT, PdfUtils.boldFont1, 0f, 0f)
        } else {
            mainActivity.printText(receipt, printerFontConfig?.setAlignLeft(), printerFontConfig?.setSize1X(),
                    true, false, false)
        }

        var maxLengthReceipt = 14
        var infoReceipt = formatTitle(context?.resources?.getString(R.string.print_date_time), maxLengthReceipt) +
                GeneralUtils.formatToDateTime(getDateTimePayment(payment, bookingDetail)) + "\n"
        infoReceipt += formatTitle(context?.resources?.getString(R.string.print_merchant_id), maxLengthReceipt) +
                (payment?.mid ?: "N/A") + "\n"
        infoReceipt += formatTitle(context?.resources?.getString(R.string.print_terminal_id), maxLengthReceipt) +
                (payment?.tid ?: "N/A") + "\n"

        if (!isPrintReceipt) {
            infoReceipt += formatTitle(context?.resources?.getString(R.string.print_kiosk_id), maxLengthReceipt) +
                    kioskInfo?.name + "\n\n"
        } else {
            infoReceipt += formatTitle(context?.resources?.getString(R.string.print_kiosk_id), maxLengthReceipt) +
                    kioskInfo?.name + "\n\n\n"
        }

        infoReceipt += formatTitle(context?.resources?.getString(R.string.print_payer), maxLengthReceipt) +
                getCustomerName(customer, bookingDetail) + "\n"
        infoReceipt += getLine() + "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(infoReceipt, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity.printText(infoReceipt, printerFontConfig?.setAlignLeft(), printerFontConfig?.setSize1X(),
                    false, false, false)
        }
    }

    fun getPaymentMode(payment: TransactionResponse?, bookingDetail: BookingDetail?): String? {
        return getPaymentModeByType(payment?.paymentType)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getCardNo(payment: TransactionResponse?): String {
        return payment?.panNumber ?: "N/A"
    }

    @ExcludeFromJacocoGeneratedReport
    fun footerReceipt(payment: TransactionResponse?, bookingDetail: BookingDetail?) {
        val mainActivity = (context as MainActivity)

        val line = "\n" + getLine() + "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(line, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity.printText(line, printerFontConfig?.setAlignLeft(), printerFontConfig?.setSize1X(),
                    false, false, false)
        }

        val agreeToPay = alignRow(context?.resources?.getString(R.string.print_i_agree_to_pay))

        if (!isPrintReceipt) {
            PdfUtils.writeText(agreeToPay, Element.ALIGN_CENTER, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity.printText(agreeToPay, printerFontConfig?.setAlignCenter(), printerFontConfig?.setSize1X(),
                    false, false, false)
        }

        val maxLengthCard = 13
        var infoCard = formatTitle(context?.resources?.getString(R.string.print_payment_mode), maxLengthCard) +
                getPaymentMode(payment, bookingDetail) + "\n"
        infoCard += formatTitle(context?.resources?.getString(R.string.print_card_name), maxLengthCard) +
                (payment?.cardLabel ?: "N/A") + "\n"
        infoCard += formatTitle(context?.resources?.getString(R.string.print_card_no), maxLengthCard) +
                getCardNo(payment) + "\n"
        infoCard += formatTitle(context?.resources?.getString(R.string.print_approval_code), maxLengthCard) +
                (payment?.approvalCode ?: "N/A") + "\n"
        infoCard += formatTitle(context?.resources?.getString(R.string.print_batch), maxLengthCard) +
                (payment?.batchNo ?: "N/A") + "\n"
        infoCard += formatTitle(context?.resources?.getString(R.string.print_trace), maxLengthCard) +
                (payment?.transactionAmount ?: "N/A") + "\n"
        infoCard += formatTitle(context?.resources?.getString(R.string.print_stan), maxLengthCard) +
                (payment?.stanNo ?: "N/A") + "\n"
        infoCard += formatTitle(context?.resources?.getString(R.string.ref_num), maxLengthCard) +
                (payment?.rrn ?: "N/A") + "\n"
        infoCard += formatTitle(context?.resources?.getString(R.string.tc), maxLengthCard) +
                (payment?.txnCertificate?.uppercase(Locale.getDefault()) ?: "N/A") + "\n"
        if (Integer.toHexString(FieldData.TxnType.PAYMENT_BY_EZL.toInt()).toString() == payment?.paymentType
                || Integer.toHexString(FieldData.TxnType.PAYMENT_BY_NETS_NFP.toInt()) .toString() == payment?.paymentType) {
            infoCard += formatTitle(context?.resources?.getString(R.string.print_balance), maxLengthCard) +
                    (formatBalance(payment.cardBalanceAmount)) + "\n"
        }
        infoCard + "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(infoCard, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity.printText(infoCard, printerFontConfig?.setAlignLeft(), printerFontConfig?.setSize1X(),
                    false, false, false)
        }

        var refundsAllowed = context?.resources?.getString(R.string.no_refund)

        if (!isPrintReceipt) {
            PdfUtils.writeText(refundsAllowed, Element.ALIGN_CENTER, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity.printText(refundsAllowed!!, printerFontConfig?.setAlignCenter(), printerFontConfig?.setSize1X(),
                    false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun getPromoDiscount(totalCost: TotalCostsResponse?): Float {
        totalCost?.let {
            return totalCost.promoDiscountAmount ?: 0f
        }
        return 0f
    }

    @ExcludeFromJacocoGeneratedReport
    fun totalPromoCodeDiscount(totalCost: TotalCostsResponse?) {
        val totalPromoDiscount = getPromoDiscount(totalCost)
        val promoCode = totalCost?.promoCode
        if (totalPromoDiscount <=0 || promoCode.isNullOrEmpty()) {
            return
        }
        val title = context?.resources?.getString(R.string.print_total_promo, promoCode) ?: ""
        val text = generateTotalContent(title, totalPromoDiscount)
        if (!isPrintReceipt) {
            PdfUtils.writeText(text, Element.ALIGN_RIGHT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            (context as MainActivity).printText(text, PrinterFont.FONT_JUSTIFICATION_LEFT, printerFontConfig?.setSize1X(),
                false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun getTotalDiscount(totalCost: TotalCostsResponse?): Float {
        totalCost?.let {
            var totalDiscount = 0f
            if (totalCost.getTotalDiscountAmount() != null) {
                totalDiscount += totalCost.getTotalDiscountAmount()!!
            }
            if (totalCost.getTotalAdjustmentAmount() != null) {
                totalDiscount += totalCost.getTotalAdjustmentAmount()!!
            }
            return totalDiscount
        }
        return 0f
    }

    @ExcludeFromJacocoGeneratedReport
    fun getTotalPayment(totalCost: TotalCostsResponse?, bookingDetail: BookingDetail?): Float? {
        if (bookingDetail != null)
            return bookingDetail?.totalPayment

        return totalCost?.getTotalPaymentAmount()
    }

    @ExcludeFromJacocoGeneratedReport
    fun totalDiscount(totalCost: TotalCostsResponse?) {
        val mainActivity = (context as MainActivity)
        val totalDiscount = getTotalDiscount(totalCost)
        if (totalDiscount <= 0) {
            return
        }
        val title = context?.resources?.getString(R.string.print_total_discount) ?: ""
        val text = generateTotalContent(title,  totalDiscount)
        if (!isPrintReceipt) {
            PdfUtils.writeText(text, Element.ALIGN_RIGHT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity.printText(text, PrinterFont.FONT_JUSTIFICATION_LEFT, printerFontConfig?.setSize1X(),
                false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun printLine() {
        val mainActivity = (context as MainActivity)

        val line = "-----------   \n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(line, Element.ALIGN_RIGHT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity.printText(line, printerFontConfig?.setAlignRight(), printerFontConfig?.setSize1X(),
                false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun totalReceipt(payment: TransactionResponse?, totalCost: TotalCostsResponse?, bookingDetail: BookingDetail?) {

        printLine()
        totalDiscount(totalCost)
        totalPromoCodeDiscount(totalCost)

        val priceTotal = getTotalPayment(totalCost, bookingDetail) ?: 0F
        val title = context?.resources?.getString(R.string.print_total_gst) ?: ""
        val total = generateTotalContent(title, priceTotal, false) + "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(total, Element.ALIGN_RIGHT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            val mainActivity = (context as MainActivity)
            mainActivity.printText(total, PrinterFont.FONT_JUSTIFICATION_LEFT, printerFontConfig?.setSize1X(),
                    false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun generateTotalContent(title: String, amount: Float, isDiscount: Boolean = true): String {
        var text = title
        val priceFormat = GeneralUtils.formatAmountSymbols("", amount, 2)
        val price = if (isDiscount) {
            String.format("(%s)", priceFormat)
        } else {
            priceFormat
        }
        if (title.length + price.length > lengthDefault - 2) {
            val lst = cutString(title, lengthDefault - (price.length + 6))
            var data = ""
            for (d in 0..lst.size - 1) {
                if (d == 0) {
                    data = lst.get(d)
                    data += getSpace(lengthDefault - (lst.get(d).length + price.length))
                    data += price + "\n"
                } else {
                    data += getSpace(3) + lst.get(d) + "\n"
                }
            }
            text = data
        } else {
            text += getSpace(lengthDefault - (text.length + price.length)) + price + "\n"
        }
        return text.substring(0, text.length - 1)
    }

    @ExcludeFromJacocoGeneratedReport
    fun printTitleClass(cart: CartItem?, index: Int, mainActivity: MainActivity?,
                        totalCost: TotalCostsResponse?, productInfo: ProductInfo?, attendee: Attendee?) {
        var resources = (index + 1).toString() + ". " + HtmlCompat.fromHtml(
            getTitleClassReceipt(cart, productInfo, index) ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
        val classAmount = getBeforeDiscountAmount(totalCost, productInfo, index, attendee) ?: 0F
        val price = GeneralUtils.formatAmountSymbols("", classAmount, 2)
        if (resources.length + price.length > lengthDefault - 2) {
            val lst = cutString(resources, lengthDefault - (price.length + 6))
            var data = ""
            for (d in 0..lst.size - 1) {
                if (d == 0) {
                    data = lst.get(d)
                    data += getSpace(lengthDefault - (lst.get(d).length + price.length))
                    data += price + "\n"
                } else {
                    data += getSpace(3) + lst.get(d) + "\n"
                }
            }
            resources = data
        } else
            resources += getSpace(lengthDefault - (resources.length + price.length)) + price + "\n"


        if (!isPrintReceipt) {
            PdfUtils.writeText(resources, Element.ALIGN_LEFT, PdfUtils.boldFont1, 0f, 0f)
        } else {
            mainActivity?.printText(resources, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                    true, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun printTitleEvent(
        cart: CartItem?,
        index: Int,
        mainActivity: MainActivity?,
        totalCost: TotalCostsResponse?,
        productInfo: ProductInfo?,
        ticketEntity: TicketEntity?
    ) {
        var resources = (index + 1).toString() + ". " + HtmlCompat.fromHtml(
            (getTitleClassReceipt(cart, productInfo, index) ?: "" ) + " - " + (ticketEntity?.ticketTypeName ?: ""), HtmlCompat.FROM_HTML_MODE_LEGACY)
        val classAmount = ticketEntity?.beforeDiscountAmount ?: 0F
        val price = GeneralUtils.formatAmountSymbols("", classAmount, 2)
        if (resources.length + price.length > lengthDefault - 2) {
            val lst = cutString(resources, lengthDefault - (price.length + 6))
            var data = ""
            for (d in 0..lst.size - 1) {
                if (d == 0) {
                    data = lst.get(d)
                    data += getSpace(lengthDefault - (lst.get(d).length + price.length))
                    data += price + "\n"
                } else {
                    data += getSpace(3) + lst.get(d) + "\n"
                }
            }
            resources = data
        } else
            resources += getSpace(lengthDefault - (resources.length + price.length)) + price + "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(resources, Element.ALIGN_LEFT, PdfUtils.boldFont1, 0f, 0f)
        } else {
            mainActivity?.printText(resources, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                true, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun printTitleResource(cart: CartItem?, index: Int, mainActivity: MainActivity?,
                           totalCost: TotalCostsResponse?, productInfo: ProductInfo?, attendee: Attendee?) {
        var resources = (index + 1).toString() + ". " + HtmlCompat.fromHtml(
            getTitleClassReceipt(cart, productInfo, index) ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
        var classAmount = getBeforeDiscountAmount(totalCost, productInfo, index, attendee) ?: 0F
        var price = GeneralUtils.formatAmountSymbols("", classAmount, 2)
        if (resources.length + price!!.length > lengthDefault - 2) {
            var lst = cutString(resources, lengthDefault - (price.length + 6))
            var data = ""
            for (d in 0..lst.size - 1) {
                if (d == 0) {
                    data = lst.get(d)
                    data += getSpace(lengthDefault - (lst.get(d).length + price.length))
                    data += price + "\n"
                } else {
                    data += getSpace(3) + lst.get(d) + "\n"
                }
            }
            resources = data
        } else
            resources += getSpace(lengthDefault - (resources.length + price.length)) + price + "\n"

//        if (resources.endsWith("\n")) {
//            resources = resources.substring(0, resources.length - 1)
//        }

        if (!isPrintReceipt) {
            PdfUtils.writeText(resources, Element.ALIGN_LEFT, PdfUtils.boldFont1, 0f, 0f)
        } else {
            mainActivity?.printText(resources, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                    true, false, false)
        }
    }

    fun getGSTNo(kioskInfo: KioskInfo?, productInfo: ProductInfo?, index: Int, attendee: Attendee?): String? {
        if (productInfo != null) {
            return productInfo.getGSTRegNo() ?: "N/A"
        }

        return attendee?.productInfo?.getGSTRegNo() ?: "N/A"
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getOutletName(kioskInfo: KioskInfo?, productInfo: ProductInfo?, index: Int, attendee: Attendee?): String? {
        if (productInfo != null) {
            return productInfo.getOutletName() ?: "N/A"
        }

        return attendee?.productInfo?.getOutletName() ?: "N/A"
    }

    @ExcludeFromJacocoGeneratedReport
    fun printAmountSuggest(isBeforeDiscountTitle: Boolean?, mainActivity: MainActivity?, messageDiscount: Int?) {
        var suggest = mainActivity?.getString(R.string.before_discount_amount) + "\n"
        if (isBeforeDiscountTitle == null) {
            suggest = mainActivity?.getString(R.string.total_amount) + "\n\n"
        } else if (!isBeforeDiscountTitle) {
            if (messageDiscount != null) {
                suggest = mainActivity?.getString(messageDiscount) + "\n"
            } else {
                suggest = "\n"
            }
        }

        if (!isPrintReceipt) {
            PdfUtils.writeText(suggest, Element.ALIGN_RIGHT, PdfUtils.italicFont1, 0f, 0f)
        } else {
            mainActivity?.printText(suggest, PrinterFont.FONT_JUSTIFICATION_RIGHT, PrinterFont.FONT_SIZE_X1,
                    false, true, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun printGSTRegNo(totalCost: TotalCostsResponse?, mainActivity: MainActivity?,
                      kioskInfo: KioskInfo?, cart: CartItem?, productInfo: ProductInfo?, index: Int, attendee: Attendee?) {
        var gstRegNo = marginLeft(2) + context?.resources?.getString(R.string.gst_reg_no) + " " +
                getGSTNo(kioskInfo, productInfo, index, attendee) + " (" + getOutletName(kioskInfo, productInfo, index, attendee) + ")"
        var regNo = ""
        var lstReg = cutString(gstRegNo, lengthDefault)
        for (d in 0..lstReg.size - 1) {
            if (d == 0) {
                regNo += lstReg.get(d) + "\n"
            } else {
                regNo += marginLeft(10) + lstReg.get(d).trim()
                if (d < lstReg.size - 1) {
                    regNo += "\n"
                }
            }
        }

        if (!isPrintReceipt) {
            PdfUtils.writeText(regNo, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity?.printText(regNo, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                    false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun printGSTRegNoEvent(
        totalCost: TotalCostsResponse?,
        mainActivity: MainActivity?,
        kioskInfo: KioskInfo?,
        cart: CartItem?,
        productInfo: ProductInfo?,
        index: Int,
        ticketEntity: TicketEntity?
    ) {
        val outletName = try {
            (ticketEntity?.listParticipant?.get(0)?.outletName ?: "")
        } catch (e: Exception){
            ""
        }
        var gstRegNo = marginLeft(2) + context?.resources?.getString(R.string.gst_reg_no) + " " +
                getGSTNo(kioskInfo, productInfo, index, null) + " (" + outletName + ")"
        var regNo = ""
        var lstReg = cutString(gstRegNo, lengthDefault)
        for (d in 0..lstReg.size - 1) {
            if (d == 0) {
                regNo += lstReg.get(d) + "\n"
            } else {
                regNo += marginLeft(10) + lstReg.get(d).trim()
                if (d < lstReg.size - 1) {
                    regNo += "\n"
                }
            }
        }

        if (!isPrintReceipt) {
            PdfUtils.writeText(regNo, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity?.printText(regNo, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun printClassFee(mainActivity: MainActivity?, participant: Attendee?, productInfo: ProductInfo?, index: Int, payer: CustomerInfo?) {
        var classFee = marginLeft(2) + context?.resources?.getString(R.string.class_fee) +
                getSpace(1) + getParticipantName(participant, productInfo, index, payer)

        var fee = ""
        var lstFee = cutString(classFee, lengthDefault)
        for (d in 0..lstFee.size - 1) {
            if (d == 0) {
                fee += lstFee.get(d) + "\n"
            } else {
                fee += marginLeft(10) + lstFee.get(d).trim()
                if (d < lstFee.size - 1) {
                    fee += "\n"
                }
            }
        }
        fee += "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(fee, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity?.printText(fee, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                    false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun printIgFee(mainActivity: MainActivity?, participant: Attendee?, productInfo: ProductInfo?, index: Int, payer: CustomerInfo?) {
        var classFee = marginLeft(2) + context?.resources?.getString(R.string.ig_fee) +
                getSpace(1) + getParticipantName(participant, productInfo, index, payer)

        var fee = ""
        var lstFee = cutString(classFee, lengthDefault)
        for (d in 0..lstFee.size - 1) {
            if (d == 0) {
                fee += lstFee.get(d) + "\n"
            } else {
                fee += marginLeft(10) + lstFee.get(d).trim()
                if (d < lstFee.size - 1) {
                    fee += "\n"
                }
            }
        }
        fee += "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(fee, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity?.printText(fee, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun printEventFee(mainActivity: MainActivity?, participant: Attendee?, productInfo: ProductInfo?, index: Int, payer: CustomerInfo?) {
        var classFee = marginLeft(2) + context?.resources?.getString(R.string.event_fee) +
                getSpace(1) + getParticipantName(participant, productInfo, index, payer)

        var fee = ""
        var lstFee = cutString(classFee, lengthDefault)
        for (d in 0..lstFee.size - 1) {
            if (d == 0) {
                fee += lstFee.get(d) + "\n"
            } else {
                fee += marginLeft(10) + lstFee.get(d).trim()
                if (d < lstFee.size - 1) {
                    fee += "\n"
                }
            }
        }
        fee += "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(fee, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity?.printText(fee, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                    false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun printEventFeeEvent(mainActivity: MainActivity?, ticketEntity: TicketEntity?) {
        val fullName = try {
            (ticketEntity?.listParticipant?.get(0)?.participantAfterLoadCart?.fullName ?: "")
        } catch (e: Exception){
            ""
        }
        var classFee = marginLeft(2) + context?.resources?.getString(R.string.event_fee) +
                getSpace(1) + fullName

        var fee = ""
        var lstFee = cutString(classFee, lengthDefault)
        for (d in 0..lstFee.size - 1) {
            if (d == 0) {
                fee += lstFee.get(d) + "\n"
            } else {
                fee += marginLeft(10) + lstFee.get(d).trim()
                if (d < lstFee.size - 1) {
                    fee += "\n"
                }
            }
        }
        fee += "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(fee, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity?.printText(fee, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun getParticipantName(participant: Attendee?, productInfo: ProductInfo?, index: Int, payer: CustomerInfo?): String? {
        if (productInfo != null) {
            return productInfo.getCustomerName()
        }

        return participant?.customerInfo?.mFullName ?: payer?.mFullName
    }

    @ExcludeFromJacocoGeneratedReport
    fun printBookingFee(mainActivity: MainActivity?, participant: Attendee?, productInfo: ProductInfo?, index: Int, payer: CustomerInfo?) {
        var classFee = marginLeft(2) + context?.resources?.getString(R.string.booking_fee) +
                getSpace(1) + getParticipantName(participant, productInfo, index, payer)

        var fee = ""
        var lstFee = cutString(classFee, lengthDefault)
        for (d in 0..lstFee.size - 1) {
            if (d == 0) {
                fee += lstFee.get(d) + "\n"
            } else {
                fee += marginLeft(10) + lstFee.get(d).trim()
                if (d < lstFee.size - 1) {
                    fee += "\n"
                }
            }
        }
        fee += "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(fee, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity?.printText(fee, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                    false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun getDiscount(totalCost: TotalCostsResponse?, productInfo: ProductInfo?, index: Int, attendee: Attendee?): Float? {
        var cost = 0F

        if (productInfo != null) {
            if (productInfo.getDiscountAmount() != null) {
                cost += productInfo.getDiscountAmount()!!
            }
            if (productInfo.adjustmentAmount != null) {
                cost += productInfo.adjustmentAmount!!
            }
            if (productInfo.promoDiscountAmount != null) {
                cost += productInfo.promoDiscountAmount!!
            }
            return cost
        }

        if (attendee?.productInfo?.getDiscountAmount() != null)
            cost += attendee.productInfo?.getDiscountAmount()!!
        if (attendee?.productInfo?.adjustmentAmount != null)
            cost += attendee.productInfo?.adjustmentAmount!!
        if (attendee?.productInfo?.promoDiscountAmount != null) {
            cost += attendee.productInfo?.promoDiscountAmount!!
        }
        return cost
    }

    @ExcludeFromJacocoGeneratedReport
    fun printLessDiscount(totalCost: TotalCostsResponse?, mainActivity: MainActivity?, productInfo: ProductInfo?, index: Int, attendee: Attendee?) {
        var lessDiscount: String = marginLeft(4) + context?.resources?.getString(R.string.less_discount)
        val cost = getDiscount(totalCost, productInfo, index, attendee) ?: 0F
        if (cost <= 0f) {
            return
        }
        val lessDiscountAmount = "(" + GeneralUtils.formatAmountSymbols("", cost, 2) + ")"
        lessDiscount += getSpace(lengthDefault - (lessDiscount.length + lessDiscountAmount.length))
        lessDiscount += lessDiscountAmount + "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(lessDiscount, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity?.printText(lessDiscount, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                    false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun printLessDiscountEvent(
        mainActivity: MainActivity?,
        ticketEntity: TicketEntity?
    ) {
        var lessDiscount: String = marginLeft(4) + context?.resources?.getString(R.string.less_discount)
        val cost = (ticketEntity?.discountAmount ?: 0F) + (ticketEntity?.promoDiscountAmount ?: 0f)
        val lessDiscountAmount = "(" + GeneralUtils.formatAmountSymbols("", cost, 2) + ")"
        lessDiscount += getSpace(lengthDefault - (lessDiscount.length + lessDiscountAmount.length))
        lessDiscount += lessDiscountAmount + "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(lessDiscount, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity?.printText(lessDiscount, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun printDateTimeInfo(carts: ArrayList<CartItem>?, mainActivity: MainActivity?, kioskInfo: KioskInfo?, productInfo: ProductInfo?, index: Int, cartItem: CartItem?) {
        var cartTemp: CartItem? = null

        if (carts != null && carts.size > 0) {
            for (i in 0 until carts.size) {
                if (carts[i].facility != null && !carts[i].facility?.getResourceID().isNullOrEmpty() && carts[i].facility?.getResourceID().equals(productInfo?.productId)) {
                    cartTemp = carts[i]
                    break
                } else if (carts[i].classInfo != null && !carts[i].classInfo?.getClassId().isNullOrEmpty() && carts[i].classInfo?.getClassId().equals(productInfo?.productId)) {
                    cartTemp = carts[i]
                    break
                } else if (carts[i].event != null && !carts[i].event?.eventId.isNullOrEmpty() && carts[i].event?.eventId.equals(productInfo?.productId)) {
                    cartTemp = carts[i]
                    break
                } else if (carts[i].igInfo != null && !carts[i].igInfo?.igId.isNullOrEmpty() && carts[i].igInfo?.igId.equals(productInfo?.productId)) {
                    cartTemp = carts[i]
                    break
                }
            }
        } else if (cartItem != null) {
            cartTemp = cartItem
        }

        if (cartItem?.facility != null) {
            printDateTimeCepasType(mainActivity, kioskInfo, productInfo, index, cartTemp)
        } else {
            printDateTime(mainActivity, kioskInfo, productInfo, index, cartTemp)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun printDateTime(mainActivity: MainActivity?, kioskInfo: KioskInfo?, productInfo: ProductInfo?, index: Int, cartItem: CartItem?) {
        val maxLengthResource = 9
        val marginLeft = 2
        var infoResource = marginLeft(marginLeft) + formatTitle(context?.resources?.getString(R.string.print_date_time), maxLengthResource) +
                getDateTimeReceipt(cartItem, productInfo, index) + getSpace(2)

//        if (cartItem?.event != null) {
//            infoResource += "\n"
//        }

        val venue: String? = getVenue(cartItem, kioskInfo, productInfo, index)
        val timeSessions: String? = getSessionInfoReceipt(cartItem, false)
        infoResource += timeSessions

        if (cartItem?.classInfo != null || cartItem?.event != null || cartItem?.igInfo != null) {
            var dateTime = ""
            val lstDate = cutString(infoResource, lengthDefault)
            for (d in 0..lstDate.size - 1) {
                if (d == 0) {
                    dateTime += marginLeft(marginLeft) + lstDate.get(d).trim()
                } else {
                    dateTime += marginLeft(18) + lstDate.get(d)
                }
            }
            infoResource = dateTime
        }

        infoResource += marginLeft(marginLeft) + formatTitle(context?.resources?.getString(R.string.print_venue), maxLengthResource) +
                venue + "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(infoResource, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity?.printText(infoResource, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                    false, false, false)
        }
    }

    fun getSessionInfoReceipt(cart: CartItem?, isCepasType: Boolean): String? {
        var timeSessions: String? = ""
        if (cart?.classInfo != null) {
            if (cart.classInfo!!.getClassSessions() != null && cart.classInfo!!.getClassSessions()!!.isNotEmpty()) {
                timeSessions = GeneralUtils.formatToTime(cart.classInfo!!.getClassSessions()!!.get(0).getStartTime()) + " - " +
                        GeneralUtils.formatToTime(cart.classInfo!!.getClassSessions()!!.get(0).getEndTime()) + "\n"
            }
        } else if (cart?.facility != null) {
            if (cart.slotList != null) {
                var isFirst = true
                for (slot in cart.slotList!!) {
                    if (isCepasType) {
                        if (isFirst) {
                            timeSessions += slot.mTimeRageName + "\n"
                            isFirst = false
                        } else {
                            timeSessions += marginLeft(10) + slot.mTimeRageName + "\n"
                        }
                    } else {
                        if (isFirst) {
                            isFirst = false
                            timeSessions += "\n"
                        }
                        timeSessions += marginLeft(14) + slot.mTimeRageName + "\n"
                    }
                }
            }
        } else if (cart?.event != null &&
                !cart.event!!.dateFrom.isNullOrEmpty() && !cart.event!!.dateTo.isNullOrEmpty()) {
            timeSessions = GeneralUtils.formatToTime(cart.event!!.dateFrom) + " - " +
                    GeneralUtils.formatToTime(cart.event!!.dateTo) + "\n"
        } else if (cart?.igInfo != null
            && cart.igInfo!!.igSessions != null && cart.igInfo!!.igSessions!!.isNotEmpty()
        ){
            timeSessions = GeneralUtils.formatToTime(cart.igInfo!!.igSessions!![0].getStartTime()) + " - " +
                    GeneralUtils.formatToTime(cart.igInfo!!.igSessions!![0].getEndTime()) + "\n"
        }
        return timeSessions
    }

    fun getDateTimeReceipt(cart: CartItem?, productInfo: ProductInfo?, index: Int): String? {
        var dateTime: String? = ""
        if (cart?.classInfo != null) {
            dateTime = GeneralUtils.formatToDate(cart.classInfo!!.getStartDate())
        } else if (cart?.facility != null) {
            dateTime = GeneralUtils.formatToDate(cart.selectedDate)
        } else if (cart?.event != null) {
            dateTime = GeneralUtils.formatToDate(cart.event!!.dateFrom)
        } else if (cart?.igInfo != null){
            dateTime = GeneralUtils.formatToDate(cart.igInfo!!.startDate)
        }
        return dateTime
    }

    fun getVenue(cart: CartItem?, kioskInfo: KioskInfo?, productInfo: ProductInfo?, index: Int): String? {
        if (productInfo != null) {
            return productInfo.getVenue()
        }

        var venue: String? = ""
        if (cart?.classInfo != null) {
            if (cart.classInfo!!.getClassSessions() != null && cart.classInfo!!.getClassSessions()!!.isNotEmpty()) {
                if (!cart.classInfo!!.getClassSessions()!!.get(0).getExternalVenue().isNullOrEmpty())
                    venue = cart.classInfo!!.getClassSessions()!!.get(0).getExternalVenue()
                else
                    venue = cart.classInfo!!.getClassSessions()!!.get(0).getInternalVenue()
            }
        } else if (cart?.facility != null) {
            venue = cart.facility!!.outletName
        } else if (cart?.event != null) {
            venue = (cart.event!!.venue ?: "")
        } else if (cart?.igInfo != null){
            venue = cart.igInfo!!.outletName
        }

        if (!venue.isNullOrEmpty()) {
            return venue
        } else {
            return kioskInfo?.outlet?.getName()
        }
    }

    fun getTitleClassReceipt(cart: CartItem?, productInfo: ProductInfo?, index: Int): String? {
        if (productInfo != null) {

            if (!productInfo.getProductCode().isNullOrEmpty()) {
                return productInfo.getProductCode() + " - " + productInfo.getProductTitle()
            }

            return productInfo.getProductTitle()
        }

        var title: String? = ""
        if (cart?.classInfo != null) {
            title = cart.classInfo!!.getClassCode() + " - " + cart.classInfo!!.getClassTitle()
        } else if (cart?.facility != null) {
            title = cart.facility!!.getResourcetName()
        } else if (cart?.event != null) {
            title = cart.event!!.eventCode + " - " + cart.event!!.eventTitle
        } else if (cart?.igInfo != null){
            title = cart.igInfo!!.igCode + " - " + cart.igInfo!!.igTitle
        }
        return title
    }

    fun getBeforeDiscountAmount(totalCost: TotalCostsResponse?, productInfo: ProductInfo?, index: Int, attendee: Attendee?): Float? {
        if (productInfo != null) {
            return productInfo.getBeforeDiscountAmount()
        }

        return attendee?.productInfo?.getBeforeDiscountAmount()
    }

    @ExcludeFromJacocoGeneratedReport
    fun printDateTimeCepasType(mainActivity: MainActivity?, kioskInfo: KioskInfo?, productInfo: ProductInfo?, index: Int, cartItem: CartItem?) {
        val maxLengthResource = 5
        val marginLeft = 2

        var infoResource = marginLeft(marginLeft) + formatTitle(context?.resources?.getString(R.string.print_date), maxLengthResource) +
                getDateTimeReceipt(cartItem, productInfo, index) + "\n"
        infoResource += marginLeft(marginLeft) + formatTitle(context?.resources?.getString(R.string.print_time), maxLengthResource)
        val venue: String? = getVenue(cartItem, kioskInfo, productInfo, index)
        var timeSessions: String? = getSessionInfoReceipt(cartItem, true)
        if (timeSessions.isNullOrEmpty()) {
            timeSessions += "\n"
        }
        infoResource += timeSessions
        infoResource += marginLeft(marginLeft) + formatTitle(context?.resources?.getString(R.string.print_venue), maxLengthResource) +
                venue + "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(infoResource, Element.ALIGN_LEFT, PdfUtils.normalFont1, 0f, 0f)
        } else {
            mainActivity?.printText(infoResource, PrinterFont.FONT_JUSTIFICATION_LEFT, PrinterFont.FONT_SIZE_X1,
                    false, false, false)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun bodyBookingDetailReceipt(totalCost: TotalCostsResponse?, kioskInfo: KioskInfo?,
                                 cartList: ArrayList<CartItem>?, bookingDetail: BookingDetail, payer: CustomerInfo?) {
        val products = bookingDetail.products!!
        val mainActivity = (context as MainActivity)

        for (i in 0 until products.size) {
            printTitleClass(null, stt, mainActivity, totalCost, products[i], null)
            printGSTRegNo(totalCost, mainActivity, kioskInfo, null, products[i], stt, null)
            feeType(cartList, products[i], mainActivity, stt, payer)
            printLessDiscount(totalCost, mainActivity, products[i], stt, null)
            printDateTimeInfo(cartList, mainActivity, kioskInfo, products[i], stt, null)
            stt++
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private fun feeType(cartList: ArrayList<CartItem>?, productInfo: ProductInfo?, mainActivity: MainActivity?, index: Int, payer: CustomerInfo?) {
        if (cartList != null && cartList.size > 0) {
            for (i in 0 until cartList.size) {
                if (cartList[i].facility != null && !cartList[i].facility?.getResourceID().isNullOrEmpty() && cartList[i].facility?.getResourceID().equals(productInfo?.productId)) {
                    printBookingFee(mainActivity, null, productInfo, stt, payer)
                    return
                } else if (cartList[i].classInfo != null && !cartList[i].classInfo?.getClassId().isNullOrEmpty() && cartList[i].classInfo?.getClassId().equals(productInfo?.productId)) {
                    printClassFee(mainActivity, null, productInfo, stt, payer)
                    return
                } else if (cartList[i].event != null && !cartList[i].event?.eventId.isNullOrEmpty() && cartList[i].event?.eventId.equals(productInfo?.productId)) {
                    printEventFee(mainActivity, null, productInfo, stt, payer)
                    return
                } else if (cartList[i].igInfo != null && !cartList[i].igInfo?.igId.isNullOrEmpty() && cartList[i].igInfo?.igId.equals(productInfo?.productId)){
                    printIgFee(mainActivity, null, productInfo, stt, payer)
                }
            }
        }
    }

//    fun bodyFacilityReceipt(cartList: ArrayList<CartItem>?, totalCost: TotalCostsResponse?, kioskInfo: KioskInfo?,
//                            customer: CustomerInfo?, participant: CustomerInfo?, bookingDetail: BookingDetail?) {
//        val mainActivity = (context as MainActivity)
//
//        if (cartList != null) {
//            for (i in 0..cartList.size - 1) {
//                val cart = cartList.get(i)
//                printTitleResource(cart, i, mainActivity, totalCost, bookingDetail)
//                printGSTRegNo(totalCost, mainActivity, kioskInfo, cart, bookingDetail, i, null)
//                printBookingFee(mainActivity, cart.attendees as ArrayList<Attendee>?, bookingDetail, i)
//                printLessDiscount(totalCost, mainActivity, bookingDetail, i)
//                printDateTimeCepasType(cart, mainActivity, kioskInfo, bookingDetail, i)
//            }
//        }
//    }

    @ExcludeFromJacocoGeneratedReport
    fun bodyFacilityReceipt(cartItem: CartItem, totalCost: TotalCostsResponse?, kioskInfo: KioskInfo?,
                            payer: CustomerInfo?, bookingDetail: BookingDetail?, index: Int) {
        //follow cart object
        val mainActivity = (context as MainActivity)

        val attendees = cartItem.attendees
        if (attendees != null && attendees.size > 0) {
            for (i in 0 until attendees.size) {
                printTitleResource(cartItem, stt, mainActivity, totalCost, null, attendees[i])
                printGSTRegNo(totalCost, mainActivity, kioskInfo, cartItem, null, index, attendees[i])
                printBookingFee(mainActivity, attendees[i], null, index, payer)
                printLessDiscount(totalCost, mainActivity, null, index, attendees[i])
                printDateTimeInfo(null, mainActivity, kioskInfo, null, index, cartItem)
                stt++
            }
        }

    }

//    fun bodyClassReceipt(cartList: ArrayList<CartItem>?, totalCost: TotalCostsResponse?, kioskInfo: KioskInfo?,
//                         customer: CustomerInfo?, participant: CustomerInfo?, bookingDetail: BookingDetail?) {
//        val mainActivity = (context as MainActivity)
//
//        if (cartList != null) {
//            for (i in 0..cartList.size - 1) {
//                val cart = cartList.get(i)
//                printTitleClass(cart, i, mainActivity, totalCost, bookingDetail, null)
//                printGSTRegNo(totalCost, mainActivity, kioskInfo, cart, bookingDetail, i, null)
//                printClassFee(mainActivity, null, bookingDetail, i)
//                printLessDiscount(totalCost, mainActivity, bookingDetail, i)
//                printDateTime(cart, mainActivity, kioskInfo, bookingDetail, i)
//            }
//        }
//    }

    @ExcludeFromJacocoGeneratedReport
    fun bodyClassReceipt(cartItem: CartItem, totalCost: TotalCostsResponse?, kioskInfo: KioskInfo?,
                         payer: CustomerInfo?, bookingDetail: BookingDetail?, index: Int) {
        val mainActivity = (context as MainActivity)
        val attendees = cartItem.attendees
        if (attendees != null && attendees.size > 0) {
            for (i in 0 until attendees.size) {
                printTitleClass(cartItem, stt, mainActivity, totalCost, null, attendees[i])
                printGSTRegNo(totalCost, mainActivity, kioskInfo, cartItem, null, index, attendees[i])
                printClassFee(mainActivity, attendees[i], null, index, payer)
                printLessDiscount(totalCost, mainActivity, null, index, attendees[i])
                printDateTimeInfo(null, mainActivity, kioskInfo, null, stt, cartItem)
                stt++
            }
        }

    }

    @ExcludeFromJacocoGeneratedReport
    fun bodyIgReceipt(cartItem: CartItem, totalCost: TotalCostsResponse?, kioskInfo: KioskInfo?,
                         payer: CustomerInfo?, bookingDetail: BookingDetail?, index: Int) {
        val mainActivity = (context as MainActivity)
        val attendees = cartItem.attendees
        if (attendees != null && attendees.size > 0) {
            for (i in 0 until attendees.size) {
                printTitleClass(cartItem, stt, mainActivity, totalCost, null, attendees[i])
                printGSTRegNo(totalCost, mainActivity, kioskInfo, cartItem, null, index, attendees[i])
                printIgFee(mainActivity, attendees[i], null, index, payer)
                printLessDiscount(totalCost, mainActivity, null, index, attendees[i])
                printDateTimeInfo(null, mainActivity, kioskInfo, null, stt, cartItem)
                stt++
            }
        }

    }

    @ExcludeFromJacocoGeneratedReport
    fun bodyEventReceipt(cartItem: CartItem, totalCost: TotalCostsResponse?, kioskInfo: KioskInfo?,
                         payer: CustomerInfo?, bookingDetail: BookingDetail?, index: Int) {
        val mainActivity = (context as MainActivity)
        cartItem.event?.listSelectedTicket?.forEach { eventTicket ->
            eventTicket.listTicketParticipantEntity.forEach { ticketEntity ->
                /*ticketEntity.listParticipant.forEach { eventParticipant ->

                }*/
                ticketEntity.ticketTypeName = eventTicket.name
                printTitleEvent(
                    cart = cartItem,
                    index = stt,
                    mainActivity = mainActivity,
                    totalCost = totalCost,
                    productInfo = null,
                    ticketEntity = ticketEntity
                )
                printGSTRegNoEvent(
                    totalCost = totalCost,
                    mainActivity = mainActivity,
                    kioskInfo = kioskInfo,
                    cart = cartItem,
                    productInfo = null,
                    index = -1, //no usages
                    ticketEntity = ticketEntity
                )
                printEventFeeEvent(
                    mainActivity = mainActivity,
                    ticketEntity = ticketEntity
                )
                printLessDiscountEvent(
                    mainActivity = mainActivity,
                    ticketEntity = ticketEntity
                )
                printDateTimeInfo(null, mainActivity, kioskInfo, null, index, cartItem)
                stt++
            }
        }

    }

    @ExcludeFromJacocoGeneratedReport
    fun cutPage() {
        val mainActivity = (context as MainActivity)
        mainActivity.cutPage()
    }

    @ExcludeFromJacocoGeneratedReport
    fun signaturePrint(bitmap: Bitmap?) {
        val mainActivity = (context as MainActivity)

        val signature = context?.resources?.getString(R.string.signature) + "\n"

        if (!isPrintReceipt) {
            PdfUtils.writeText(signature, Element.ALIGN_CENTER, PdfUtils.normalFont1, 0f, 0f)
            PdfUtils.addImage(bitmap)
        } else {
            mainActivity.printText(signature, printerFontConfig?.setAlignCenter(), printerFontConfig?.setSize1X(),
                    false, false, false)
            mainActivity.printImage(bitmap?.copy(bitmap?.config, true))
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun nonSignaturePrint() {
        val mainActivity = (context as MainActivity)

        val noSignature = context?.resources?.getString(R.string.print_no_signature_required) + "\n"

        mainActivity.printText(noSignature, printerFontConfig?.setAlignCenter(), printerFontConfig?.setSize1X(),
                false, false, false)
    }

    private var thread: Thread? = null
    var isPrintReceipt = true

    @ExcludeFromJacocoGeneratedReport
    fun receiptWithSignature(cartList: ArrayList<CartItem>?, receiptId: String?,
                             kioskInfo: KioskInfo?, totalCost: TotalCostsResponse?,
                             payment: TransactionResponse?, customer: CustomerInfo?,
                             bitmap: Bitmap?, parentFragment: Fragment?, bookingDetail: BookingDetail?) {
        val mainActivity = (context as MainActivity)
        val checkResult = mainActivity.checkStatusPrinter()
        printerFontConfig = mainActivity.getPrinterFontConfig()
        lengthDefault = printerFontConfig?.getMaxCharacters() ?: lengthDefault

        LogManager.i("StatusPrinter ${checkResult?.code}")

        if (checkResult?.code == VALID_CODE || checkResult?.code == LOW_PAPER) {
            if (LOW_PAPER == checkResult.code && LOW_PAPER != mainActivity.getPrinterStatus()) {

                mainActivity.setPrinterStatus(LOW_PAPER)
                val deviceInfo = InfoHealthDevice(InfoHealthDevice.PRINTER_NAME, LOW_PAPER)
                mainActivity.healthUpdate(deviceInfo)
            }

            thread = object : Thread() {
                @ExcludeFromJacocoGeneratedReport
                override fun run() {
                    try {
                        generateReceipt(cartList, receiptId, kioskInfo, totalCost, payment, customer, bitmap, parentFragment, bookingDetail)
                        cutPage()

                        view?.navigationRatingView()
                    } catch (e: InterruptedException) {
                        LogManager.i("Print receipt failed")
                    }
                }
            }
            thread?.start()

        } else {
            showErrorPrinter(if (checkResult?.code != null) checkResult.code!! else 0, true)
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun generateReceipt(cartList: ArrayList<CartItem>?, receiptId: String?,
                                 kioskInfo: KioskInfo?, totalCost: TotalCostsResponse?,
                                 payment: TransactionResponse?, customer: CustomerInfo?,
                                 bitmap: Bitmap?, parentFragment: Fragment?, bookingDetail: BookingDetail?) {
        stt = 0

        headerReceipt(kioskInfo, receiptId, payment, customer, bookingDetail)
            //bookingDetail always null
        if (bookingDetail != null && bookingDetail.products != null && bookingDetail.products!!.size > 0) {
            bodyBookingDetailReceipt(totalCost, kioskInfo, cartList, bookingDetail, customer)
        } else {
            if (cartList != null && cartList.size > 0) {
                for (i in 0 until cartList.size) {
                    if (cartList[i].classInfo != null) {
                        bodyClassReceipt(cartList[i], totalCost, kioskInfo, customer, bookingDetail, i)
                    } else if (cartList[i].facility != null) {
                        bodyFacilityReceipt(cartList[i], totalCost, kioskInfo, customer, bookingDetail, i)
                    } else if (cartList[i].event != null) {
                        bodyEventReceipt(cartList[i], totalCost, kioskInfo, customer, bookingDetail, i)
                    } else if (cartList[i].igInfo != null){
                        bodyIgReceipt(cartList[i], totalCost, kioskInfo, customer, bookingDetail, i)
                    }
                }
            }
        }
        totalReceipt(payment, totalCost, bookingDetail)
        if (true == payment?.signatureRequired) {
            signaturePrint(bitmap)
        } else {
            nonSignaturePrint()
        }
        footerReceipt(payment, bookingDetail)
    }

    @ExcludeFromJacocoGeneratedReport
    override fun pdfReceiptResult(): String? {
        PdfUtils.closeDocument()
        return PdfUtils.convertBase64()
    }

    var isErrorPrintStatus = false

    @ExcludeFromJacocoGeneratedReport
    private fun showErrorPrinter(code: Int, isShowAll: Boolean) {
        isErrorPrintStatus = true

        reportHealthDevice(code)

        if (code == ERROR_NO_PAPER) {
            view?.showErrorMessage(R.string.out_of_paper, R.string.e_invoice_will_be_sent)
        } else {
            if (isShowAll) {
                view?.showErrorMessage(R.string.booking_success, R.string.problem_to_printer)
            }
        }
    }

    @ExcludeFromJacocoGeneratedReport
    override fun reportHealthDevice(code: Int) {
        val mainActivity = (context as MainActivity)
        if (code != mainActivity.getPrinterStatus()) {
            mainActivity.setPrinterStatus(code)
            val deviceInfo = InfoHealthDevice(InfoHealthDevice.PRINTER_NAME, code)
            (mainActivity as MainActivity).healthUpdate(deviceInfo)
        }
    }

    private var callBackSendEmail = object : IBaseContract.IBaseInteractorOutput<SendEmailResponse> {
        @ExcludeFromJacocoGeneratedReport
        override fun onSuccess(data: SendEmailResponse?) {
            view?.dismissWaitingPage()
            view?.onSendSuccess()
        }

        @ExcludeFromJacocoGeneratedReport
        override fun onError(data: BaseResponse<SendEmailResponse>) {
            view?.dismissWaitingPage()
            view?.showErrorMessage(data)
        }

    }

    fun getTime(cart: CartItem?): String? {
        var dateTime: String? = ""
        if (cart?.classInfo != null) {
            if (cart.classInfo!!.getClassSessions() != null && cart.classInfo!!.getClassSessions()!!.isNotEmpty()) {
                dateTime = GeneralUtils.formatToTime(cart.classInfo!!.getClassSessions()!!.get(0).getStartTime())
                dateTime += " - " + GeneralUtils.formatToTime(cart.classInfo!!.getClassSessions()!!.get(0).getEndTime())
            }
        } else if (cart?.facility != null) {
            if (cart.slotList != null) {
                for (slot in cart.slotList!!) {
                    dateTime += slot.mTimeRageName + ", "
                }
            }

            if (dateTime!!.length > 1)
                dateTime = dateTime.substring(0, dateTime.length - 2)
        }
        return dateTime
    }

    fun getDate(cart: CartItem?): String? {
        var dateTime: String? = ""
        if (cart?.classInfo != null) {
            dateTime = GeneralUtils.formatToDate(cart.classInfo!!.getStartDate())
            dateTime += " - " + GeneralUtils.formatToDate(cart.classInfo!!.getEndDate())
        } else if (cart?.facility != null) {
            dateTime = GeneralUtils.formatToDate(cart.selectedDate)
        } else if (cart?.event != null) {
            dateTime = GeneralUtils.formatToDate(cart.event!!.dateFrom)
            dateTime += " - " + GeneralUtils.formatToDate(cart.event!!.dateTo)
        } else if (cart?.igInfo != null){
            dateTime = GeneralUtils.formatToDate(cart.igInfo!!.startDate)
            dateTime += " - " + GeneralUtils.formatToDate(cart.igInfo!!.endDate)
        }
        return dateTime
    }

    fun getResourceId(cart: CartItem?): String? {
        var resourceId: String? = ""
        if (cart?.classInfo != null) {
            resourceId = cart.classInfo!!.getClassCode()
        } else if (cart?.facility != null) {
            resourceId = cart.facility!!.getResourceID()
        } else if (cart?.event != null) {
            resourceId = cart.event!!.eventCode
        } else if (cart?.igInfo != null){
            resourceId = cart.igInfo!!.igCode
        }
        return resourceId
    }

    fun getResourceName(cart: CartItem?): String? {
        var resourceName: String? = ""
        if (cart?.classInfo != null) {
            resourceName = cart.classInfo!!.getClassTitle()
        } else if (cart?.facility != null) {
            resourceName = cart.facility!!.getResourcetName()
        } else if (cart?.event != null) {
            resourceName = cart.event!!.eventTitle
        } else if (cart?.igInfo != null){
            resourceName = cart.igInfo!!.igTitle
        }
        return resourceName
    }

    fun getPayAmount(totalCost: TotalCostsResponse?, bookingDetail: BookingDetail?, index: Int): Float? {
        if (bookingDetail?.products != null && index >= 0 && index < bookingDetail.products!!.size) {
            val product = bookingDetail.products!!.get(index)
            return product.getPaymentAmount()
        }

        return totalCost?.getTotalPaymentAmount()
    }

    @ExcludeFromJacocoGeneratedReport
    fun getItems(cartList: ArrayList<CartItem>?, kioskInfo: KioskInfo?,
                 payment: TransactionResponse?, totalCost: TotalCostsResponse?, bookingDetail: BookingDetail?): ArrayList<SendEmailRequest.ItemsSendEmail>? {
        var items: ArrayList<SendEmailRequest.ItemsSendEmail>? = ArrayList()
        if (cartList != null && cartList.size > 0) {
            for (i in 0..cartList.size - 1) {
                var cart = cartList.get(i)
                var item = SendEmailRequest.ItemsSendEmail()
                item.resourceId = getResourceId(cart)
                item.resourceName = getResourceName(cart)
//                item.participantName = getParticipantName(cart.attendees as ArrayList<Attendee>?, bookingDetail, i)
//                item.venue = getVenue(cart, kioskInfo, bookingDetail, i)
                item.time = getTime(cart)
                item.date = getDate(cart)
//                var amount = ((getBeforeDiscountAmount(totalCost, bookingDetail, i)
//                        ?: 0F) * 100).toInt()
//                item.amount = amount
//                var discount = ((getDiscount(totalCost, bookingDetail, i) ?: 0F) * 100).toInt()
//                item.discount = discount
                var amountPaid = ((getPayAmount(totalCost, bookingDetail, i) ?: 0F) * 100).toInt()
                item.amountPaid = amountPaid
//                item.gstNo = getGSTNo(kioskInfo, bookingDetail, i)
                item.locationName = kioskInfo?.outlet?.getFriendlyName() ?: ""

                items?.add(item)
            }
        }
        return items
    }

    @ExcludeFromJacocoGeneratedReport
    fun getLocation(kioskInfo: KioskInfo?): SendEmailRequest.LocationSendEmail? {
        var location = SendEmailRequest.LocationSendEmail()
        location.name = kioskInfo?.outlet?.getFriendlyName() ?: ""
        location.address = kioskInfo?.outlet?.getAddress() ?: ""
        location.url = kioskInfo?.outlet?.officialWebsite
        location.fax = kioskInfo?.outlet?.getFax() ?: ""
        location.tel = kioskInfo?.outlet?.getPhone() ?: ""
        location.postalCode = getPostalCode(kioskInfo) ?: ""
        return location
    }

    fun getTotalFee(totalCost: TotalCostsResponse?, bookingDetail: BookingDetail?): Float? {
        if (bookingDetail != null) {
            return bookingDetail.totalBeforeDiscount
        }

        return totalCost?.getTotalBeforeDiscountAmount()
    }

    fun getTotalDiscount(totalCost: TotalCostsResponse?, bookingDetail: BookingDetail?): Float? {
        if (bookingDetail != null) {
            return bookingDetail.totalDiscount
        }

        return totalCost?.getTotalDiscountAmount()
    }

    fun getTotalAdjustment(totalCost: TotalCostsResponse?, bookingDetail: BookingDetail?): Float? {
        if (bookingDetail != null) {
            return bookingDetail.totalAdjustment
        }

        return totalCost?.getTotalAdjustmentAmount()
    }

    fun getTotalGST(totalCost: TotalCostsResponse?, bookingDetail: BookingDetail?): Float? {
        if (bookingDetail != null) {
            return bookingDetail.totalGST
        }

        return totalCost?.getTotalGST()?.toFloat()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun formatBalance(balanceStr: String?): String {
        if (balanceStr.isNullOrEmpty()) return "N/A"
        try {
            val balance = balanceStr.toDouble()/100
            return "$%.2f".format(balance)
        } catch (e: Exception) {
            LogManager.i("Cannot format card balance")
            return "N/A"
        }
    }

    @ExcludeFromJacocoGeneratedReport
    fun getSendEmailRequest(cartList: ArrayList<CartItem>?, receiptId: String?,
                            kioskInfo: KioskInfo?, totalCost: TotalCostsResponse?,
                            payment: TransactionResponse?, customer: CustomerInfo?,
                            receiverEmail: String?, bookingDetail: BookingDetail?): SendEmailRequest {
        var request = SendEmailRequest()

        var cartInfo: SendEmailRequest.CardInfoSendEmail = SendEmailRequest.CardInfoSendEmail()
        cartInfo.paymentMode = getPaymentMode(payment, bookingDetail)
        cartInfo.cardNo = getCardNo(payment)

        request.kioskId = kioskInfo?.id
        request.receiptId = getReceiptNo(receiptId, bookingDetail)
        request.fullName = getCustomerName(customer, bookingDetail)
        request.receiverEmail = receiverEmail
        var fee = getTotalFee(totalCost, bookingDetail) ?: 0F
        var totalFees = (fee * 100).toInt()
        request.totalFees = totalFees
        var discount = getTotalDiscount(totalCost, bookingDetail) ?: 0F
        var totalDiscounts = (discount * 100).toInt()
        request.totalDiscounts = totalDiscounts
        var adjustments = getTotalAdjustment(totalCost, bookingDetail) ?: 0F
        var totalAdjustments = (adjustments * 100).toInt()
        request.totalAdjustments = totalAdjustments
        var amountPaid = getTotalPayment(totalCost, bookingDetail) ?: 0F
        var totalAmountPaid = (amountPaid * 100).toInt()
        request.totalAmountPaid = totalAmountPaid
        var gst = getTotalGST(totalCost, bookingDetail) ?: 0F
        var totalGST = (gst * 100).toInt()
        request.totalGST = totalGST
        request.cardInfo = cartInfo
        request.location = getLocation(kioskInfo)
        request.receiptDate = GeneralUtils.getCurrentDate()
        request.items = getItems(cartList, kioskInfo, payment, totalCost, bookingDetail)


        return request
    }

    @ExcludeFromJacocoGeneratedReport
    fun sendEmail(cartList: ArrayList<CartItem>?, receiptId: String?,
                  kioskInfo: KioskInfo?, totalCost: TotalCostsResponse?,
                  payment: TransactionResponse?, customer: CustomerInfo?,
                  receiverEmail: String?, bookingDetail: BookingDetail?) {
        view?.showWaitingPage()
        val request = getSendEmailRequest(cartList, receiptId, kioskInfo, totalCost, payment, customer, receiverEmail, bookingDetail)
        val token = MySharedPref(context).eKioskHeader
        interactor?.sendEmail(token, request, callBackSendEmail)
    }
}