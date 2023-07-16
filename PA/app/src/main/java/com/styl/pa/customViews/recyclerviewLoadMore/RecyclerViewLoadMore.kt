package com.styl.pa.customViews.recyclerviewLoadMore

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet

class RecyclerViewLoadMore : RecyclerView {
    internal var linearLayoutManager: LinearLayoutManager? = null
    internal var onLoadMoreListener: OnLoadMoreListener? = null
    internal var isLoading: Boolean = false
    internal var visibleThreshold = 5

    companion object {
        private val TAG = RecyclerViewLoadMore::class.java.simpleName
    }

    constructor (context: Context) : super(context) {

    }

    constructor (context: Context, attrs: AttributeSet?) : super(context, attrs) {

    }

    constructor (context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {

    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        if (linearLayoutManager == null) {
            linearLayoutManager = this.layoutManager as LinearLayoutManager
        }
        val totalItemCount = linearLayoutManager!!.itemCount
        val lastVisibleItem = linearLayoutManager!!.findLastVisibleItemPosition()
        if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
            if (onLoadMoreListener != null) {
                onLoadMoreListener!!.onLoadMore()
            }
            isLoading = true
        }
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener
    }

    fun setLoaded() {
        isLoading = false
    }
}