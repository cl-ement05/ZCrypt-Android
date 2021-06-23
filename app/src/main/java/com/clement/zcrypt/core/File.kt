package com.clement.zcrypt.core

import android.net.Uri
import com.clement.zcrypt.MainActivity
import java.io.*

fun writeFile(
    activity: MainActivity,
    fileUri: Uri,
    data: List<String>
): Boolean {
    val contentResolver = activity.applicationContext.contentResolver

    try {
        contentResolver.openFileDescriptor(fileUri, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { file ->
                for (line in data) {
                    file.write("$line\n".toByteArray())
                }
            }
        }
    } catch (e: FileNotFoundException) {
        return false
    } catch (e: IOException) {
        return false
    }
    return true
}

@Throws(IOException::class)
fun openFile(
    activity: MainActivity,
    fileUri: Uri,
): List<String> {
    val contentResolver = activity.applicationContext.contentResolver

    contentResolver.openInputStream(fileUri).use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            return reader.readLines()
        }
    }
}