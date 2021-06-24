package com.clement.zcrypt.ui.layouts

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clement.zcrypt.MainActivity
import com.clement.zcrypt.R
import com.clement.zcrypt.core.openFile
import com.clement.zcrypt.core.startDecryption
import com.clement.zcrypt.ui.components.EncryptionAlgorithm
import java.io.IOException

@Composable
fun DecryptLayout(activity: MainActivity) {
    EncryptionAlgorithm(algoId = R.string.zcrypt_algo)

    val decryptionStarted = remember { mutableStateOf(false)}
    val documentUri = remember { mutableStateOf<Uri?>(null)}
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        documentUri.value = it
    }

    val decryptionResult = remember { mutableStateOf<List<String>>(emptyList())}
    val decryptionSuccess = remember { mutableStateOf(0) }

    /*
    0 when everything is fine
    1 when IO Exception is thrown while trying to read from file
    2 when exception during the *real* decryption or when parsing file lines
     */

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                launcher.launch(arrayOf("text/plain"))
                decryptionStarted.value = true
            },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(stringResource(id = R.string.decrypt))
        }
    }

    documentUri.value?.let {
        try {
            val documentLines = openFile(activity, documentUri.value!!)
            decryptionResult.value = startDecryption(documentLines)
            if (decryptionResult.value.isEmpty()) {
                decryptionSuccess.value = 2
            }
        } catch (e: IOException) {
            decryptionSuccess.value = 1
        }
    }

    if (decryptionSuccess.value != 0) {
        AlertDialog(
            onDismissRequest = { decryptionSuccess.value = 0 },
            text = {
                when(decryptionSuccess.value) {
                    1 -> Text(stringResource(id = R.string.dialog_error_access_file))
                    2 -> Text(stringResource(id = R.string.dialog_error_decrypting_general))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { decryptionSuccess.value = 0 }
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        )
    }

    if (decryptionResult.value.isNotEmpty() && decryptionStarted.value) {
        AlertDialog(
            onDismissRequest = { decryptionStarted.value = false },
            text = {
                Column {
                    ResultBox(
                        title = R.string.time,
                        titleIcon = R.drawable.ic_clock_black,
                        iconDescription = R.string.descr_icon_clock,
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(decryptionResult.value[0])
                    }

                    ResultBox(
                        title = R.string.message,
                        titleIcon = R.drawable.ic_message_black,
                        iconDescription = R.string.descr_icon_message,
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(decryptionResult.value[3])
                    }

                    ResultBox(
                        title = R.string.sender,
                        titleIcon = R.drawable.ic_send_black,
                        iconDescription = R.string.descr_icon_send,
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(decryptionResult.value[1])
                    }

                    ResultBox(
                        title = R.string.receiver,
                        titleIcon = R.drawable.ic_receiver_black,
                        iconDescription = R.string.descr_icon_receiver,
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(decryptionResult.value[2])
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { decryptionStarted.value = false }
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        )
    }
}

@Composable
fun ResultBox(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @DrawableRes titleIcon: Int,
    @StringRes iconDescription: Int,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        //title row including icon and title
        Row {
            Image(
                painter = painterResource(id = titleIcon),
                contentDescription = stringResource(id = iconDescription)
            )
            Text(
                text = stringResource(title),
                modifier = Modifier.padding(start = 5.dp)
            )
        }

        content()
    }
}
