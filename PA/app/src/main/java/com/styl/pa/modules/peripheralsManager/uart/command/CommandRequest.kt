package com.styl.pa.modules.peripheralsManager.uart.command

class CommandRequest : BaseCommand {

    constructor(sn: Byte) {
        this.command = ACK
        this.serialNumber = sn
        this.lenght = ByteArray(2)
        this.data = ByteArray(0)
        this.lenght!![0] = 0x00
        this.lenght!![1] = 0x04
    }

    constructor(cmd: Byte, data: ByteArray) {
        this.command = cmd
        this.data = ByteArray(data.size)
        this.data = data
    }

    constructor(cmd: Byte, sn: Byte, data: ByteArray) {
        this.command = cmd
        this.serialNumber = sn
        this.data = ByteArray(data.size)
        this.data = data
    }

    @ExperimentalUnsignedTypes
    fun build(): ByteArray {
        val result = ByteArray(data!!.size + 1 + 2 + 1 + 1 + 2)
        var position = 0

        result[position] = stx
        position++

        this.lenght = calculatorLengthOrFcc(4 + data!!.size)
        System.arraycopy(lenght!!, 0, result, position, lenght!!.size)
        position += lenght!!.size

        result[position] = command
        position++

        result[position] = serialNumber
        position++

        System.arraycopy(data!!, 0, result, position, data!!.size)
        position += data!!.size

        val valueFcc =
            (data!!.toUByteArray()).sumOf { it.toUInt() } +
                    (lenght!!.toUByteArray()).sumOf { it.toUInt() } +
                    command.toUByte().toUInt() + serialNumber.toUByte().toUInt()
        fcc = calculatorLengthOrFcc(valueFcc.toInt())
        System.arraycopy(fcc!!, 0, result, position, fcc!!.size)
        return result
    }

    @ExperimentalUnsignedTypes
    fun calculatorLengthOrFcc(len: Int): ByteArray {
        val result = ByteArray(2)
        result[0] = (len shr 8).toByte()
        result[1] = len.and(0x00FF).toByte()
        return result
    }

}