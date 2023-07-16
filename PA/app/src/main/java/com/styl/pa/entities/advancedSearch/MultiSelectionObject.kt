package com.styl.pa.entities.advancedSearch

import com.styl.pa.entities.courseCategory.CourseCategory
import com.styl.pa.entities.generateToken.Outlet

/**
 * Created by Ngatran on 09/20/2018.
 */
open class MultiSelectionObject {
    private var value: String = ""
    var Value: String
        get() {
            return value
        }
        set(value) {
            this.value = value
        }

    private var isCheck: Boolean = false
    var IsCheck: Boolean
        get() {
            return isCheck
        }
        set(value) {
            this.isCheck = value
        }

    constructor()

    constructor(string: String) {
        this.value = string
    }

    constructor(string: String, isCheck: Boolean) {
        this.value = string
        this.isCheck = isCheck
    }

    constructor(courseCategory: CourseCategory) {
        this.value = if (!courseCategory.getCategoryName().isNullOrEmpty()) courseCategory.getCategoryName()!! else ""
        this.isCheck = false
    }

    constructor(outlet: Outlet) {
        this.value = if (!outlet.getFriendlyName().isNullOrEmpty()) outlet.getFriendlyName()!! else ""
        this.isCheck = false
    }

}