package com.clement.zcrypt.core

import java.lang.Exception
import java.lang.NumberFormatException
import kotlin.random.Random

fun mainEncrypt(
    messageInput: String,
    receiverInput: String,
    senderInput: String,
    savedLastKey: Int
): List<String> {
    val zcrypt = Zcrypt()
    val lastKey = if (savedLastKey != -1) {
        zcrypt.createKey(savedLastKey)
    } else {
        zcrypt.createKey(Random.nextInt(100, 190))
    }

    return try {
        with(zcrypt) {
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
        emptyList()
    }
}

fun loadZcryptSettings(
    lines: List<String>
): List<Int>? {
    return try {
        val key = Integer.parseInt(lines[3].substring(0, 8), 2)
        val limitLow = Integer.parseInt(lines[3].substring(9, 11))
        val limitHigh = Integer.parseInt(lines[3].substring(11))

        listOf(limitLow, limitHigh, key)

    } catch (e: NumberFormatException) {
        null
    }

}

fun mainDecrypt(
    limitLow: Int,
    limitHigh: Int,
    key: Int,
    lines: List<String>
): List<String> {
    return try {
        val zcrypt = Zcrypt(limitLow, limitHigh, key)

        listOf(
            zcrypt.decryptTime(lines[0]),
            zcrypt.decryptString(lines[1]),
            zcrypt.decryptString(lines[2]),
            zcrypt.decryptString(lines[4])
        )
    } catch (e: Exception) {
        emptyList()
    }
}