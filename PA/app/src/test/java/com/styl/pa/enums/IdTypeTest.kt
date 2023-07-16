package com.styl.pa.enums

import org.junit.Test

class IdTypeTest {
    @Test
    fun idType_test() {
        var idType = IdType.getIdType("NRIC")
        assert(idType == IdType.NRIC.type.toString())
        idType = IdType.getIdType("FIN")
        assert(idType == IdType.FIN.type.toString())
        idType = IdType.getIdType("")
        assert(idType == IdType.PASSPORT.type.toString())
        idType = IdType.getIdType(null)
        assert(idType == IdType.PASSPORT.type.toString())
    }
}