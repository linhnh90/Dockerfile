package com.styl.pa.entities.classes

/**
 * Created by Ngatran on 09/20/2018.
 */
class MultiSelectionObject {
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

    constructor(string: String){
        this.value = string
    }

    constructor(string: String, isCheck:Boolean){
        this.value = string
        this.isCheck = isCheck
    }
}