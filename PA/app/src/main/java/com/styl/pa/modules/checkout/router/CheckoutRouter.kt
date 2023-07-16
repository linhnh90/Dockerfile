package com.styl.pa.modules.checkout.router

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import com.styl.pa.R
import com.styl.pa.entities.cart.Cart
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.promocode.PromoCode
import com.styl.pa.entities.reservation.BookingDetail
import com.styl.pa.entities.reservation.TotalCostsResponse
import com.styl.pa.entities.wirecard.TransactionResponse
import com.styl.pa.enums.TagName
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.cart.view.CartFragment
import com.styl.pa.modules.checkout.ICheckoutContact
import com.styl.pa.modules.checkout.view.CheckoutFragment.Companion
import com.styl.pa.modules.checkoutVerify.view.CheckoutVerificationFragment
import com.styl.pa.modules.declaration.view.DeclarationFragment
import com.styl.pa.modules.email.view.EmailAdditionalFragment
import com.styl.pa.modules.extensionSession.view.ExtensionSessionFragment
import com.styl.pa.modules.payment.view.PaymentFragment
import com.styl.pa.modules.paymentSuccessful.view.PaymentSuccessfulFragment
import com.styl.pa.modules.promo.view.PromoCodeFragment
import com.styl.pa.modules.rating.view.RatingFragment
import com.styl.pa.modules.signature.view.SignatureFragment

/**
 * Created by Ngatran on 09/14/2018.
 */
class CheckoutRouter(var view: BaseFragment?) : ICheckoutContact.IRouter {

    override fun navigationPaymentSuccessfulView(
        transactionResponse: TransactionResponse?,
        receiptId: String?,
        totalCost: TotalCostsResponse?,
        bitmap: Bitmap?,
        bookingDetail: BookingDetail?,
        productType: String?
    ) {
        val f = PaymentSuccessfulFragment.newInstance(transactionResponse, receiptId, totalCost, bitmap, bookingDetail, productType)
        val ft = view?.childFragmentManager?.beginTransaction()
        ft?.add(R.id.container_checkout, f, TagName.PaymentSuccessfulFragment.value)
        ft?.addToBackStack(TagName.PaymentSuccessfulFragment.value)
        ft?.commit()

        if (view?.childFragmentManager?.findFragmentById(R.id.container_checkout) is CheckoutVerificationFragment) {
            ft?.hide(view?.childFragmentManager?.findFragmentById(R.id.container_checkout)!!)
        }
    }

    override fun navigationSignature() {
        val f = SignatureFragment()
        val ft = view?.childFragmentManager?.beginTransaction()
        ft?.add(R.id.container_signature, f, TagName.SignatureFragment.value)
        ft?.addToBackStack(TagName.SignatureFragment.value)
        ft?.commit()
    }

    override fun navigationCheckoutVerificationView(cart: Cart?) {
        val f = CheckoutVerificationFragment()
        val ft = view?.childFragmentManager?.beginTransaction()
        ft?.add(R.id.container_checkout, f, TagName.CheckoutVerificationFragment.value)
        ft?.commit()
    }

    override fun navigationView() {
        val f = ExtensionSessionFragment()
        if (view?.fragmentManager != null) {
            f.show(view?.fragmentManager!!, ExtensionSessionFragment::class.java.simpleName)
        }
    }

    override fun navigationRatingView(payer: CustomerInfo?) {
        val f = RatingFragment.newInstance(payer)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.RatingFragment.value)
        ft?.addToBackStack(TagName.RatingFragment.value)
        ft?.commit()
        if (view != null) {
            ft?.hide(view!!)
        }
    }

    override fun backCartView(isDelete: Boolean) {
        val intent = Intent()
        intent.putExtra(CartFragment.ARG_IS_LOAD, isDelete)
        view?.targetFragment?.onActivityResult(CartFragment.LOAD_CART_CODE, Activity.RESULT_OK, intent)
        view?.fragmentManager?.popBackStack()
    }

    override fun navigationPaymentView(amout: Float) {
        val f = PaymentFragment.newInstance(amout)
        f.setTargetFragment(view, Companion.REQUEST_PAYMENT)
        val ft = view?.fragmentManager?.beginTransaction()
        ft?.add(R.id.container, f, TagName.PaymentFragment.value)
        ft?.addToBackStack(TagName.PaymentFragment.value)
        ft?.commit()
    }

    override fun navigationPromoCodeView(promoCodeList: ArrayList<PromoCode>) {
        val f = PromoCodeFragment.newInstance(promoCodeList)
        f.setTargetFragment(view, Companion.SELECT_PROMO_CODE_REQUEST_CODE)
        if (view?.fragmentManager != null) {
            f.show(view?.fragmentManager!!, PromoCodeFragment::class.java.simpleName)
        }
    }

    override fun navigateToEmailAdditional() {
        val f = EmailAdditionalFragment.newInstance()
        f.setTargetFragment(view, Companion.EMAIL_REQUEST_CODE)
        if (view?.fragmentManager != null) {
            f.show(view?.fragmentManager!!, EmailAdditionalFragment::class.java.simpleName)
        }
    }

    override fun navigateToDeclaration(requestCode: Int, quickBookProductType: String?) {
        val fragment = DeclarationFragment.newInstance(quickBookProductType)
        fragment.setTargetFragment(view, requestCode)
        if (view?.fragmentManager != null){
            fragment.show(view?.fragmentManager!!, DeclarationFragment::class.java.simpleName)
        }
    }

}