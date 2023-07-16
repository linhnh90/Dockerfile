package com.styl.pa.modules.customerverification

import android.content.Context
import android.text.Spanned
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener

interface CustomerVerificationContract {

    interface View : IBaseContract.IBaseView {
        fun getContext(): Context?
        fun showInvalidId()
        fun showCoursePanel(isShown: Boolean, customerId: String?)

        fun showMessageRescan(messageId: Int)
        fun showMessageRescan(title: Int, message: String, isShowTitle: Boolean)

        fun setupScannerConfigEvent(event: DcssdkListener.DcssdkConfig)
        fun pullTrigger(isPull: Boolean)
        fun updateNRICCode(nricCode: String)
        fun resetScanner()
        fun closeScanner()

        fun updatePayerInfoToMain(info: CustomerInfo?)
        fun isClickScanQRCode()
        fun clearNric()
        fun isPayer(): Boolean
        fun isCartUpdate(): Boolean
    }

    interface CourseAdapterView {

        fun setNoneOption()
        fun setCourseName(name: Spanned?)
        fun setShowOption(isShown: Boolean)
        fun setChecked(isChecked: Boolean)
        fun setEnabled(isEnabled: Boolean)
        fun updateList()
    }

    interface Presenter : IBaseContract.IBasePresenter {

        fun verifyCustomer(token: String?, idNo: String?)

        fun setSelected(position: Int, isSelected: Boolean)

        fun getItemCount(): Int
        fun bindViewHolder(position: Int)

        fun doContinue()

        fun setupScannerConfigEvent()

        fun isPayer(): Boolean

        fun setAvailableItems(customerId: String?)

        fun setParamsForEvent(
            eventTicketPosition: Int?,
            ticketEntityPosition: Int?,
            participantPosition: Int?,
            cartItemPosition: Int?
        )
    }

    interface Interactor : IBaseContract.IBaseInteractor {

        fun verifyCustomer(token: String?, request: ProxyRequest<EmptyRequest>)
    }

    interface CustomerVerificationOutput : IBaseContract.IBaseInteractorOutput<CustomerInfo>

    interface Router : IBaseContract.IBaseRouter {

        fun navigateCartPage(
            requestCode: Int,
            customerInfo: CustomerInfo?,
            selectedProducts: Array<String?>,
            isCartUpdate: Boolean = false,
            cartItemPosition: Int? = -1
        )
        fun navigateIndemnityView(
            requestCode: Int,
            customerInfo: CustomerInfo?,
            selectedProducts: Array<String?>,
            eventTicketPosition: Int? = null,
            ticketEntityPosition: Int? = null,
            participantPosition: Int? = null,
            cartItemPosition: Int?
        )
    }
}
