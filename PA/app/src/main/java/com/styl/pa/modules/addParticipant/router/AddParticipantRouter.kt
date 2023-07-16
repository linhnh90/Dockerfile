package com.styl.pa.modules.addParticipant.router

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.DialogFragment
import com.styl.pa.entities.customer.CustomerInfo
import com.styl.pa.modules.addParticipant.AddParticipantContract
import com.styl.pa.modules.addParticipant.presenter.AddParticipantPresenter

class AddParticipantRouter(var fragment: DialogFragment?): AddParticipantContract.Router {
    override fun navigateToCartPage(requestCode: Int, participantInfo: CustomerInfo?) {
        val intent = Intent()
        intent.putExtra(AddParticipantPresenter.ARG_PARTICIPANT_INFO, participantInfo)
        fragment?.targetFragment?.onActivityResult(requestCode, Activity.RESULT_OK, intent)
        fragment?.dismiss()
    }
}