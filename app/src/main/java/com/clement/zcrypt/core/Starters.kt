package com.clement.zcrypt.core

import java.lang.Exception
import kotlin.random.Random

fun startEncryption(
    messageInput: String,
    receiverInput: String,
    senderInput: String,
    savedLastKey: Int
): List<String> {
    val zcrypt = Zcrypt()
    var lastKey = 0
    lastKey = if (savedLastKey != -1) {
        zcrypt.createKey(savedLastKey)
    } else {
        zcrypt.createKey(Random.nextInt(100, 190))
    }

    try {
        return with(zcrypt) {
            listOf(
                encryptDate(),
                encryptString(senderInput),
                encryptString(receiverInput),
                "${Zcrypt.convertToEightBitBinary(zcrypt.keyNum)}b${zcrypt.limitLow}${zcrypt.limitHigh}",
                encryptString(messageInput),
                lastKey.toString()
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
        val limitLow = Integer.parseInt(lines[3].substring(9, 11))
        val limitHigh = Integer.parseInt(lines[3].substring(11))

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

    return emptyList()

}