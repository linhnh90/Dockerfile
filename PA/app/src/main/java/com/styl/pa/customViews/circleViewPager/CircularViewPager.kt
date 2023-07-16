package com.styl.pa.customViews.circleViewPager


import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.View
import com.styl.pa.utils.LogManager


/**
 * Created by Ngatran on 03/22/2019.
 */
class CircularViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {
    private val mListener: CircularViewPagerListener

    companion object {
        private val DELAY = 2000
        private val TAG = CircularViewPager::class.java.simpleName
    }

    init {
        mListener = CircularViewPagerListener()
        overScrollMode = View.OVER_SCROLL_NEVER
        offscreenPageLimit = 1
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        addOnPageChangeListener(mListener)
    }


    inner class CircularViewPagerListener : OnPageChangeListener {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            LogManager.d(TAG, "onPageScrolled")
        }

        override fun onPageSelected(position: Int) {
            postDelayed({ loopCurrentItem(position) }, DELAY.toLong())
        }

        override fun onPageScrollStateChanged(state: Int) {
            LogManager.d(TAG, "onPageScrollStateChanged")
        }

        private fun loopCurrentItem(position: Int) {
            val pageCount = (adapter?.count ?: 0)
            if (position == pageCount - 1) {
                setCurrentItem(0, false)
            } else if (position == 0) {
                setCurrentItem(pageCount - 1, false)
            } else if (position > 0 && position < (pageCount - 1)) {
                setCurrentItem(position + 1, false)
            }
        }

    }
}