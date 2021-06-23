package com.clement.zcrypt.core

import java.lang.Integer.toBinaryString
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue
import kotlin.random.Random

data class Zcrypt(var limitLow: Int = -1,
                  var limitHigh: Int = -1,
                  var keyNum: Int = -1) {

    fun createKey(lastKey: Int): Int {
        this.limitLow = Random.nextInt(10, 31)
        this.limitHigh = Random.nextInt(165, 255)
        this.keyNum = Random.nextInt(this.limitHigh - 65, this.limitHigh - this.limitLow)

        while (true) {
            if ((this.keyNum - lastKey).absoluteValue > 5) break
            else this.keyNum = Random.nextInt(this.limitHigh - 65, this.limitHigh - this.limitLow)
        }
        return this.keyNum
    }

    fun encryptDate(): String {
        var finalTimeEncrypted = ""
        val current = LocalDateTime.now()
        for (value in current.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))) {
            val toEncrypt = Character.getNumericValue(value)
            if (toEncrypt != -1) finalTimeEncrypted += (toEncrypt + this.keyNum).toString()
        }
        return finalTimeEncrypted
    }

    private fun mainEncrypt(
        toEncrypt: Int
    ): String {
        val encrypted: Int
        if (this.keyNum % 2 == 0) {
            encrypted = if (toEncrypt - this.keyNum >= this.limitLow) {
                toEncrypt - this.keyNum
            } else {
                val cut: Int = toEncrypt - this.limitLow
                this.limitHigh - this.keyNum + cut
            }
        } else {
            encrypted = if (toEncrypt + this.keyNum <= this.limitHigh) {
                toEncrypt + this.keyNum
            } else {
                val cut: Int = this.limitHigh - toEncrypt
                this.limitLow + this.keyNum - cut
            }
        }

        if (encrypted.toString().length == 2) {
            val tensDigit: Int = Character.getNumericValue(encrypted.toString()[0])
            val unitsDigit: Int = Character.getNumericValue(encrypted.toString()[1])

            return "00000000" +
                    convertToEightBitBinary(tensDigit) +
                    convertToEightBitBinary(unitsDigit)
        }
        else {
            val hundredsDigit: Int = Character.getNumericValue(encrypted.toString()[0])
            val tensDigit: Int = Character.getNumericValue(encrypted.toString()[1])
            val unitsDigit: Int = Character.getNumericValue(encrypted.toString()[2])

            return convertToEightBitBinary(hundredsDigit) +
                    convertToEightBitBinary(tensDigit) +
                    convertToEightBitBinary(unitsDigit)
        }
    }

    fun encryptString(
        content: String
    ): String {
        var finalMessageBinary = ""
        for (letter in content) {
            finalMessageBinary += mainEncrypt(letter.toInt())
        }
        return finalMessageBinary
    }

    fun decryptTime(
        inputDate: String
    ): String {
        val chSize = 3
        val date: String = inputDate.substring(0, chSize * 8)
        val time: String = inputDate.substring(chSize * 8)

        //decrypting date
        var dateDecr = ""
        for (number in date.indices step 3) {
            dateDecr += ((date[number].toString() + date[number + 1].toString() + date[number + 2].toString()).toInt() - this.keyNum).toString()
        }

        var dayDecrypted: String = dateDecr.substring(0, 2)
        var monthDecrypted: String = dateDecr.substring(2, 4)
        val yearDecrypted: String = dateDecr.substring(4)
        if (dayDecrypted.length == 1) dayDecrypted = "0$dayDecrypted"
        if (monthDecrypted.length == 1) monthDecrypted = "0$monthDecrypted"


        //same for time
        var timeDecr = ""
        for (number in time.indices step 3) {
            timeDecr += ((time[number].toString() + time[number + 1].toString() + time[number + 2].toString()).toInt() - this.keyNum).toString()
        }

        var hourDecrypted: String = timeDecr.substring(0, 2)
        var minDecrypted: String = timeDecr.substring(2, 4)
        var secDecrypted: String = timeDecr.substring(4)
        if (hourDecrypted.length == 1) hourDecrypted = "0$hourDecrypted"
        if (minDecrypted.length == 1) minDecrypted = "0$minDecrypted"
        if (secDecrypted.length == 1) secDecrypted = "0$secDecrypted"

        return "$dayDecrypted.$monthDecrypted.$yearDecrypted.$hourDecrypted.$minDecrypted.$secDecrypted"

    }

    private fun mainDecrypt(
        asciiBits: String
    ): Int {
        val encryptedAscii: String = (asciiBits.substring(0, 8).toInt(2)).toString() +
                (asciiBits.substring(8, 16).toInt(2)).toString() +
                (asciiBits.substring(16).toInt(2)).toString()
        val toDecrypt: Int = encryptedAscii.toInt()

        val decryptedAscii: Int
        if (this.keyNum % 2 == 0) {
            decryptedAscii = if (toDecrypt + this.keyNum <= this.limitHigh) {
                toDecrypt + this.keyNum
            } else {
                val cut: Int = this.limitHigh - toDecrypt
                this.limitLow + this.keyNum - cut
            }
        } else {
            decryptedAscii = if (toDecrypt - this.keyNum >= this.limitLow) {
                toDecrypt - this.keyNum
            } else {
                val cut: Int = toDecrypt - this.limitLow
                this.limitHigh - this.keyNum + cut
            }
        }

        return decryptedAscii

    }

    fun decryptString(
        content: String
    ): String {
        var decrypted = ""
        for (i in content.indices step 24) {
            decrypted += mainDecrypt(content.substring(i, i + 24)).toChar()
        }
        return decrypted
    }

    companion object {
        fun convertToEightBitBinary(
            number: Int
        ): String {
            val binary = toBinaryString(number)
            return binary.padStart(8, '0')
        }
    }

}
