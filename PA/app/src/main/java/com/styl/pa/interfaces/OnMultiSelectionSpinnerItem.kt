package com.styl.pa.interfaces

import com.styl.pa.entities.advancedSearch.MultiSelectionObject
import com.styl.pa.entities.courseCategory.CourseCategory
import com.styl.pa.entities.generateToken.Outlet
import java.util.*

/**
 * Created by Ngatran on 09/19/2018.
 */
interface OnMultiSelectionSpinnerItem {
    interface OnMultiSelectionStringSpinnerItem {
        fun onClickMulti(arrayList: ArrayList<MultiSelectionObject>, string: String, indexList: ArrayList<Int>)
    }

    interface OnMultiSelectionOutletSpinnerItem {
        fun onClickMulti(arrayList: ArrayList<Outlet>, string: String, indexList: ArrayList<Int>)
    }

    interface OnMultiSelectionCategorySpinnerItem {
        fun onClickMulti(arrayList: ArrayList<CourseCategory>, string: String, indexList: ArrayList<Int>)
    }
}