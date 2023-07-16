package com.styl.pa.modules.printer

/**
 * Created by NgaTran on 9/30/2020.
 */
interface IPrinterFontConfig {
    fun getMaxCharacters(): Int
    fun getPrinterPageWidth(): Int
    fun setAlignCenter(): Int
    fun setAlignLeft(): Int
    fun setAlignRight(): Int
    fun setSize1X(): Int
    fun setSize2X(): Int
    fun setSize3X(): Int
    fun setSize4X(): Int
    fun setSize5X(): Int
    fun setSize6X(): Int
    fun setSize7X(): Int
    fun setSize8X(): Int
    fun setDefaultFont(): Int
    fun setCustomFont1(): Int
}