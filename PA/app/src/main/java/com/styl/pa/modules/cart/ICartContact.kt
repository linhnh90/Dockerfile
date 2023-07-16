package com.styl.pa.modules.cart

import com.styl.pa.adapters.ParticipantAdapter
import com.styl.pa.adapters.ParticipantCartEventAdapter
import com.styl.pa.adapters.TicketAdapter
import com.styl.pa.entities.cart.Cart
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.checkResidency.CheckResidencyResponse
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.generateToken.BookingResponse
import com.styl.pa.entities.participant.ParticipantResponse
import com.styl.pa.entities.pacesRequest.*
import com.styl.pa.entities.proxy.EmptyRequest
import com.styl.pa.entities.proxy.ProxyRequest
import com.styl.pa.entities.vacancy.VacancyResponse
import com.styl.pa.modules.base.IBaseContract
import com.styl.pa.modules.scanner.zebraScannerService.DcssdkListener

/**
 * Created by Ngatran on 09/14/2018.
 */
interface ICartContact {
    interface IView : IBaseContract.IBaseView {
        fun showErrorMessage(message: String)

        fun getToken(): String?
        fun setPullResult(isResult: Boolean)

        fun dismissScannerDialog()

        fun showErrorMessageTitle(messageResId: Int, title: Int)

        fun displayPayer(cart: Cart?)

        fun getCartId(): String?

        fun getPayer(): CustomerInfo?

        fun updateNoCartItem()

        fun showButtonProceedCheckout(isShown: Boolean)

        fun getLockedCart(): Cart?
        fun showErrorParticipantExist(cartItem: CartItem?, customerInfo: CustomerInfo?)
        fun checkResidencySuccess(eventInfo: EventInfo?, data: CheckResidencyResponse?)
        fun checkEventVacancySuccess(eventInfo: EventInfo?, data: VacancyResponse?)
    }

    interface ICartAdapterView {

        fun loadImage(imageUrl: String?)
        fun setProductName(name: String?)
        fun setInfo(info: String?)
        fun setLLDescInfo(outletName: String?, date: String?, time: String?)
        fun setNoAttendee(noAttendee: String?)
        fun showAttendeePanel(isShown: Boolean)
        fun showEventPanel(isShown: Boolean)
        fun showAddButton(isShown: Boolean)
        fun setTextAddButton(textBtn: String)
        fun setAttendeeAdapter(item: CartItem?)
        fun setParticipantCartEventAdapter(
            item: CartItem?,
            participantAdapterListener: ParticipantAdapter.OnParticipantAdapterListener?,
            ticketAdapterListener: TicketAdapter.OnTicketAdapterListener?,
            listener: ParticipantCartEventAdapter.OnParticipantCartEventListener?
        )
        fun setMinusVisibility(isShown: Boolean)
        fun setPlusVisibility(isShown: Boolean)
        fun showPointerAttendee(isShown: Boolean)
        fun setPointerText(text: String)
        fun setMarginAddAttendeeButton(hasMargin: Boolean)
        fun updateList()
        fun showSkillFuture(isShown: Boolean)
        fun visibleDescription(
            isShowTxtInfo: Boolean,
            isShowLLDesc: Boolean,
            isShowTvOutlet: Boolean = false,
            isShowTvDate: Boolean = false,
            isShowTvTime: Boolean = false
        )
    }

    interface IPresenter : IBaseContract.IBasePresenter {

        fun getScannerConfigEvent(): DcssdkListener.DcssdkConfig

        fun getCartItemCount(): Int
        fun isExitsIndemnity(): Boolean
        fun bindCartViewHolder(position: Int)

        fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
        fun getOldListSize(): Int
        fun getNewListSize(): Int
        fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean

        fun decreaseNoAttendee(position: Int)
        fun increaseNoAttendee(position: Int)

        fun deleteParticipant(token: String, cartItem: CartItem?, position: Int)
        fun updateAttendee(token: String?, customerInfo: CustomerInfo?, selectedProducts: LinkedHashMap<String?, Boolean>)

        fun navigationCheckoutView(isCartUpdate: Boolean, isBookingMyself: Boolean, facilityParticipant: CustomerInfo?)
        fun navigateIndemnityView()

        fun canProceed(): Boolean
        fun isTicketEventValid(): Boolean
        fun isCartChange(cart: Cart?): Boolean

        fun navigateCustomerVerificationView(
            requestCode: Int,
            isPayer: Boolean,
            cartItemPosition: Int = 0,
            eventTicketPosition: Int? = null,
            ticketEntityPosition: Int? = null,
            participantPosition: Int? = null
        )

        fun removeItemFromCart(token: String, cartId: String?, position: Int)

        fun navigateAddParticipantViewForFacility(requestCode: Int)

        fun checkResidency(
            token: String,
            eventInfo: EventInfo?
        )

        fun checkEventVacancy(
            token: String,
            eventInfo: EventInfo?,
            isResident: Boolean
        )

        fun navigateSelectTicketType(
            requestCode: Int,
            eventCode: String?,
            listEventTicket: ArrayList<EventTicket>?,
            selectedNumber: Int?
        )

        fun navigateAddParticipantEvent(
            requestCode: Int,
            eventInfo: EventInfo?,
            cartItemPosition: Int,
            eventTicketPosition: Int,
            ticketEntityPosition: Int,
            participantPosition: Int
        )

    }

    interface IInteractor : IBaseContract.IBaseInteractor {

        fun removeItemFromCart(token: String,
                               userId: String,
                               request: ProxyRequest<ProductRequest>,
                               output: IBaseContract.IBaseInteractorOutput<BookingResponse>)

        fun removeEventItemFromCart(token: String,
                               userId: String,
                               request: ProxyRequest<ProductRequest>,
                               output: IBaseContract.IBaseInteractorOutput<BookingResponse>)
        fun removeItemIgFromCart(token: String,
                               userId: String,
                               request: ProxyRequest<ProductRequest>,
                               output: IBaseContract.IBaseInteractorOutput<BookingResponse>)
        fun addParticipant(token: String,
                           userId: String,
                           request: ProxyRequest<ParticipantRequest<ParticipantItem>>,
                           output: IBaseContract.IBaseInteractorOutput<ParticipantResponse>)
        fun deleteParticipant(token: String,
                              userId: String,
                              request: ProxyRequest<ParticipantRequest<ParticipantUserId>>,
                              output: IBaseContract.IBaseInteractorOutput<ParticipantResponse>? = null)

        fun deleteParticipantIg(token: String,
                              userId: String,
                              request: ProxyRequest<ParticipantRequest<ParticipantUserId>>,
                              output: IBaseContract.IBaseInteractorOutput<ParticipantResponse>? = null)

        fun checkResidency(
            token: String,
            userId: String,
            request: ProxyRequest<EmptyRequest>,
            output: IBaseContract.IBaseInteractorOutput<CheckResidencyResponse>
        )

        fun checkEventVacancy(
            token: String,
            eventId: String,
            userId: String,
            isMember: Boolean,
            isResident: Boolean,
            callback: IBaseContract.IBaseInteractorOutput<VacancyResponse>
        )
    }

    interface IInteractorOutput

    interface IDeleteReservationOutput {

        fun onDeleteSuccess()

        fun onDeleteFailed()
    }

    interface IRouter : IBaseContract.IBaseRouter {
        fun navigationCheckoutView(isCartUpdate: Boolean, isBookingMyself: Boolean, facilityParticipant: CustomerInfo?)
        fun navigateIndemnityView()

        fun navigateCustomerVerificationView(
            requestCode: Int,
            customerList: ArrayList<CustomerInfo>,
            isPayer: Boolean,
            eventTicketPosition: Int? = null,
            ticketEntityPosition: Int? = null,
            participantPosition: Int? = null,
            cartItemPosition: Int? = -1
        )

        fun navigateHomeView()

        fun navigateAddParticipantViewForFacility(requestCode: Int)

        fun navigateSelectTicketTypeView(
            requestCode: Int,
            eventCode: String?,
            listEventTicket: ArrayList<EventTicket>?,
            selectedNumber: Int? = null
        )

        fun navigateAddParticipantEvent(
            requestCode: Int,
            eventInfo: EventInfo?,
            cartItemPosition: Int,
            eventTicketPosition: Int,
            ticketEntityPosition: Int,
            participantPosition: Int
        )
    }
}