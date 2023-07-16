package com.styl.pa.enums

import org.junit.Test

class GenderTest {
    @Test
    fun gender_test() {
        var gender = Gender.getGender("Male")
        assert(gender == Gender.MALE.value.toString())
        gender = Gender.getGender("Female")
        assert(gender == Gender.FEMALE.value.toString())
        gender = Gender.getGender("")
        assert(gender == Gender.OTHERS.value.toString())
        gender = Gender.getGender(null)
        assert(gender == Gender.OTHERS.value.toString())
    }
}