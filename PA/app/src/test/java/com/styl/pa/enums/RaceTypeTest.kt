package com.styl.pa.enums

import org.junit.Test

class RaceTypeTest {

    @Test
    fun getRaceType() {
        var raceType = RaceType.getRaceType("CHINESE")
        assert(raceType == RaceType.CHINESE.type.toString())
        raceType = RaceType.getRaceType("MALAY")
        assert(raceType == RaceType.MALAY.type.toString())
        raceType = RaceType.getRaceType("INDIAN")
        assert(raceType == RaceType.INDIAN.type.toString())
        raceType = RaceType.getRaceType("")
        assert(raceType == RaceType.OTHERS.type.toString())
        raceType = RaceType.getRaceType(null)
        assert(raceType == RaceType.OTHERS.type.toString())
    }
}