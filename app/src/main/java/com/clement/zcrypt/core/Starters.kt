package com.clement.zcrypt.core

import java.lang.Exception
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