package com.styl.pa.modules.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.FragmentManager
import com.styl.pa.R
import com.styl.pa.enums.TagName
import com.styl.pa.modules.base.BaseFragment
import com.styl.pa.modules.main.view.MainActivity
import com.styl.pa.modules.setting.terminalSetting.view.TerminalSettingsFragment
import com.styl.pa.modules.setting.txnLog.view.TransactionLogFragment
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * Created by NgaTran on 10/18/2020.
 */
class SettingsFragment : BaseFragment(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        (activity as? MainActivity)?.enableHeader(false)
        rg_options?.check(R.id.rb_terminal_setting)
        rg_options?.setOnCheckedChangeListener(settingsRadioGroupListener)

        val fragment = TerminalSettingsFragment()
        navigateFragment(fragment)

        btn_exit?.setOnClickListener(this)
        btn_close_setting?.setOnClickListener(this)
    }

    private val settingsRadioGroupListener = object : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            when (p1) {
                R.id.rb_terminal_setting -> {
                    val fragment = TerminalSettingsFragment()
                    navigateFragment(fragment)
                }

                R.id.rb_receipt -> {
                    val fragment = TransactionLogFragment()
                    navigateFragment(fragment)
                }
            }
        }
    }

    private fun navigateFragment(fragment: BaseFragment) {
        val ft = childFragmentManager.beginTransaction()
        ft?.replace(R.id.fl_setting_container, fragment, TagName.TerminalSettingsFragment.value)
        ft?.commitAllowingStateLoss()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_exit -> {
                (activity as? MainActivity)?.stopLockTask()
                (activity as? MainActivity)?.finish()
            }

            R.id.btn_close_setting -> {
                fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }

        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        (activity as? MainActivity)?.enableHeader(true)
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}