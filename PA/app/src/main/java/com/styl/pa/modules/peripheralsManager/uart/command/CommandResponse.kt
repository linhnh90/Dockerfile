package com.styl.pa.modules.peripheralsManager.uart.command

import com.styl.pa.utils.LogManager


class CommandResponse : BaseCommand {

    constructor(response: ByteArray) {
        try {
            var position = 0
            this.stx = response[position]
            position++

            this.lenght = ByteArray(2)
            System.arraycopy(response, position, this.lenght!!, 0, lenght!!.size) // lenght{hight, low}
            position = position + lenght!!.size

            this.command = response[position]
            position++

            this.serialNumber = response[position]
            position++

            val lenghtData = (this.lenght!![0].toInt() shl 8) + (this.lenght!![1].toInt() and 0xff) - 4
            this.data = ByteArray(lenghtData)
            System.arraycopy(response, position, this.data!!, 0, lenghtData)
            position = position + data!!.size

            this.fcc = ByteArray(2)
            System.arraycopy(response, position, this.fcc!!, 0, 2)

        } catch (e: IndexOutOfBoundsException) {
            LogManager.i("Error CommandResponse: IndexOutOfBounds")
        } catch (e: NegativeArraySizeException) {
            LogManager.i("Error CommandResponse: NegativeArraySize")
        }
    }

}