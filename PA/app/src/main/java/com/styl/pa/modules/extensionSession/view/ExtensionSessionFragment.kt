package com.styl.pa.modules.extensionSession.view


import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.styl.pa.R
import com.styl.pa.modules.base.FullScreenDialogFragment
import kotlinx.android.synthetic.main.fragment_extension_session.view.*

class ExtensionSessionFragment : FullScreenDialogFragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_end_session -> {
                dismiss()
            }
            R.id.btn_extend_session -> {
                dismiss()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.fragment_extension_session, null)

        view.btn_end_session.setOnClickListener(this)
        view.btn_extend_session.setOnClickListener(this)

        val dialog = AlertDialog.Builder(activity)
                .setView(view)
                .create()

        if (dialog.getWindow() != null) {
            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        return dialog
    }

}
