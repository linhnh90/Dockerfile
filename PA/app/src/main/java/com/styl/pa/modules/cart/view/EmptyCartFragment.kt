package com.styl.pa.modules.cart.view

import android.os.Bundle
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.styl.pa.R
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.main.view.MainActivity
import kotlinx.android.synthetic.main.fragment_empty_cart.view.*

/**
 * Created by trangpham on 10/10/2018
 */
class EmptyCartFragment : BaseFragment(), View.OnClickListener {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val v = inflater.inflate(R.layout.fragment_empty_cart, null, false)

        v.btn_return.setOnClickListener(this)

        return v
    }

    override fun onResume() {
        super.onResume()

        setTitle(getString(R.string.cart_title))
        (activity as? MainActivity)?.setBackgroundLine(R.drawable.bg_gradient_orange)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_return -> fragmentManager?.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
        }
    }
}