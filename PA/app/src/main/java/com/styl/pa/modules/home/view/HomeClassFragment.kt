package com.styl.pa.modules.home.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.styl.pa.BuildConfig
import com.styl.pa.R
import com.styl.pa.adapters.ProductAdapter
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.event.EventInfo
import com.styl.pa.entities.recommendatetions.RecommendationItem
import com.styl.pa.enums.SearchType
import com.styl.pa.enums.TagName
import com.styl.pa.interfaces.AddToCartEvent
import com.styl.pa.interfaces.OnClickRecyclerViewItem
import com.styl.pa.modules.main.view.MainActivity
import kotlinx.android.synthetic.main.fragment_home_class.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeClassFragment : Fragment() {
    companion object {
        private val ARG_INFO_LIST = BuildConfig.APPLICATION_ID + ".args.ARG_INFO_LIST"

        fun newInstance(infoList: ArrayList<RecommendationItem>): HomeClassFragment {
            val f = HomeClassFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_INFO_LIST, infoList)
            f.arguments = args
            return f
        }
    }

    private var infoList = ArrayList<RecommendationItem>()
    private lateinit var getView: View
    private lateinit var adapter: ProductAdapter

    private var onClickRecyclerViewItem: OnClickRecyclerViewItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            infoList = arguments!!.getParcelableArrayList(ARG_INFO_LIST)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_home_class, container, false)

        init()

        return getView
    }

    private var addToCart = object : AddToCartEvent.AddToCart {
        override fun addItem(view: View, position: Int, isQuickBook: Boolean) {
            if (position >= 0 && position < infoList.size) {
                if (SearchType.COURSES.toString().equals(infoList[position].typeItem)) {
//                    (activity as MainActivity).checkAddToCart(infoList[position].infoItem as ClassInfo, null, SearchType.COURSES, null, null)
                    val cartItem = CartItem(UUID.randomUUID().toString(), infoList[position].infoItem as? ClassInfo,
                            null, null, null, null, null, null)
                    if (isQuickBook) {
                        (activity as? MainActivity)?.doQuickBook(arrayOf(cartItem).toList())
                    } else {
                        (activity as? MainActivity)?.addToCart(arrayOf(cartItem).toList())
                    }
                } else if (SearchType.EVENTS.toString().equals(infoList[position].typeItem)) {
//                    (activity as MainActivity).checkAddToCart(infoList[position].infoItem as EventInfo, SearchType.EVENTS, null, null)
                    val cartItem = CartItem(UUID.randomUUID().toString(), null, null,
                            infoList[position].infoItem as? EventInfo, null, null, null, null)
                    (activity as? MainActivity)?.addToCart(arrayOf(cartItem).toList())
                }
            }
        }

    }

    private fun init() {
        if (fragmentManager != null && fragmentManager?.findFragmentByTag(TagName.HomeFragment.value) != null && fragmentManager?.findFragmentByTag(TagName.HomeFragment.value) is HomeFragment) {
            onClickRecyclerViewItem = (fragmentManager?.findFragmentByTag(TagName.HomeFragment.value) as HomeFragment).getOnClickRecyclerViewItem()
        }

        getView.rcv_class_info_list.layoutManager = GridLayoutManager(activity, 4)

        adapter = ProductAdapter(infoList, activity)
        adapter.setOnClickRecyclerViewItem(onClickRecyclerViewItem)
        adapter.setAddToCart(addToCart)
        getView.rcv_class_info_list.isNestedScrollingEnabled = false
        getView.rcv_class_info_list.adapter = adapter
    }

}
