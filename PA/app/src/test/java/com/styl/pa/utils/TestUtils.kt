package com.styl.pa.utils

import com.styl.pa.entities.cart.Attendee
import com.styl.pa.entities.cart.Cart
import com.styl.pa.entities.cart.CartItem
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.customer.CustomerInfo
import org.mockito.kotlin.spy
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

object TestUtils {

    fun readJsonFile(filename: String): String {
        return readResource(filename)
    }

    fun readResource(filename: String): String {
        val inputStream = javaClass.classLoader?.getResourceAsStream(filename)
        assert(inputStream != null)
        return inputStream?.use {
            String(it.readBytes())
        } ?: ""
    }

    fun getResourceAsStream(filename: String): InputStream? {
        return javaClass.classLoader?.getResourceAsStream(filename)
    }

    fun mockCart(customerInfo: CustomerInfo?, hasAttendee: Boolean): Cart {
        val course1: ClassInfo = spy()
        course1.setClassId("62dbfee4-787d-eb11-81b2-00155dad210e")
        course1.sku = "TestCourse1"
        course1.setIndemnityRequired(true)
        val cartItem1 = CartItem(
                UUID.randomUUID().toString(), course1, null,
                null, null, null, null, null
        )
        val course2: ClassInfo = spy()
        course2.setClassId("72dbfee4-787d-eb11-81b2-00155dad210e")
        course2.sku = "TestCourse2"
        course2.setIndemnityRequired(false)
        val cartItem2 = CartItem(
                UUID.randomUUID().toString(), course2, null,
                null, null, null, null, null
        )
        val course3: ClassInfo = spy()
        course3.setClassId("82dbfee4-787d-eb11-81b2-00155dad210e")
        course3.sku = "TestCourse3"
        course3.setIndemnityRequired(true)
        val cartItem3 = CartItem(
                UUID.randomUUID().toString(), course3, null,
                null, null, null, null, null
        )
        val cart = Cart(arrayListOf(cartItem1, cartItem2, cartItem3), customerInfo, null)
        if (hasAttendee) {
            cart.items!![0].attendees = arrayListOf(Attendee(null, customerInfo, null))
            cart.items!![2].attendees = arrayListOf(Attendee(null, customerInfo, null))
        }
        return cart
    }

}