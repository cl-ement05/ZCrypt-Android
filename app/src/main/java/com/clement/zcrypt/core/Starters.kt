package com.clement.zcrypt.core

import java.lang.Exception
import java.lang.NumberFormatException
import kotlin.random.Random

fun startEncryption(
    messageInput: String,
    receiverInput: String,
    senderInput: String
): List<String> {
    val zcrypt = Zcrypt()
    var lastKey: Int = zcrypt.createKey(Random.nextInt(100, 190))

    try {
        return with(zcrypt) {
            listOf(
                encryptDate(),
                encryptString(senderInput),
                encryptString(receiverInput),
                "${Zcrypt.convertToEightBitBinary(zcrypt.keyNum)}b${zcrypt.limitLow}${zcrypt.limitHigh}",
                encryptString(messageInput)
            )
         }
    } catch (e: Exception) {
        return emptyList()
    }
}

fun startDecryption(
    lines: List<String>
): List<String> {
    try {
        val key = Integer.parseInt(lines[3].substring(0, 8), 2)
        val limitLow = Integer.parseInt(lines[3].substring(8, 10))
        val limitHigh = Integer.parseInt(lines[3].substring(10))

        val zcrypt = Zcrypt(limitLow, limitHigh, key)

        try {
            return listOf(
                zcrypt.decryptTime(lines[0]),
                zcrypt.decryptString(lines[1]),
                zcrypt.decryptString(lines[2]),
                zcrypt.decryptString(lines[4])
            )
        } catch (e: Exception) {
            return emptyList()
        }

    } catch (e: Exception) {
        emptyList<String>()
    }


}