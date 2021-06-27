package com.clement.zcrypt.ui.layouts

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.ui.unit.sp
import com.clement.zcrypt.MainActivity
import com.clement.zcrypt.R
import com.clement.zcrypt.core.OperationStatus
import com.clement.zcrypt.core.readFileLines
import com.clement.zcrypt.core.loadZcryptSettings
import com.clement.zcrypt.core.mainDecrypt
import com.clement.zcrypt.ui.components.AppDialog
import com.clement.zcrypt.ui.components.EncryptionAlgorithm
import java.io.IOException

@Composable
fun DecryptLayout(activity: MainActivity) {
    EncryptionAlgorithm(algoId = R.string.zcrypt_algo)

    val decryptionResult = remember { mutableStateOf<List<String>>(emptyList())}
    val decryptionStatus = remember { mutableStateOf(OperationStatus.PENDING) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { documentUri ->
        if (documentUri != null) {
            try {
                val documentLines = readFileLines(activity, documentUri)
                val zcryptSettings = loadZcryptSettings(documentLines)

                if (zcryptSettings == null) {
                    decryptionStatus.value = OperationStatus.INVALID_FILE_FORMAT
                }
                else {
                    decryptionResult.value = mainDecrypt(zcryptSettings[0], zcryptSettings[1], zcryptSettings[2], documentLines)

                    if (decryptionResult.value.isNotEmpty()) {
                        decryptionStatus.value = OperationStatus.SUCCESS
                    } else {
                        decryptionStatus.value = OperationStatus.ZCRYPT_ALGO_ERROR
                    }
                }
            } catch (e: IOException) {
                decryptionStatus.value = OperationStatus.IO_OR_FILE_ERROR
            }
        } else decryptionStatus.value = OperationStatus.NO_FILE_SELECTED
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                launcher.launch(arrayOf("text/plain"))
            },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(stringResource(id = R.string.decrypt))
        }
    }

    //error dialogs
    when (decryptionStatus.value) {
        OperationStatus.IO_OR_FILE_ERROR -> AppDialog(
            titleId = R.string.error,
            stringId = R.string.dialog_error_file_no_access,
            variable = decryptionStatus,
            newValue = 0
        )

        OperationStatus.ZCRYPT_ALGO_ERROR -> AppDialog(
            titleId = R.string.error,
            stringId = R.string.dialog_error_zcrypt_algo,
            variable = decryptionStatus,
            newValue = 0,
            formattingValue = "decrypted"
        )

        OperationStatus.NO_FILE_SELECTED -> AppDialog(
            titleId = R.string.error,
            stringId = R.string.dialog_error_no_file_selected,
            variable = decryptionStatus,
            newValue = 0
        )

        OperationStatus.INVALID_FILE_FORMAT -> AppDialog(
            titleId = R.string.error,
            stringId = R.string.dialog_error_file_invalid_format,
            variable = decryptionStatus,
            newValue = 0
        )
    }


    //result dialog
    if (decryptionResult.value.isNotEmpty() && decryptionStatus.value == OperationStatus.SUCCESS) {
        AlertDialog(
            onDismissRequest = { decryptionStatus.value = 0 },
            text = {
                Column {
                    ResultBox(
                        title = R.string.time,
                        titleIcon = R.drawable.ic_clock_black,
                        iconDescription = R.string.descr_icon_clock,
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(decryptionResult.value[0], fontSize = 18.sp)
                    }

                    ResultBox(
                        title = R.string.message,
                        titleIcon = R.drawable.ic_message_black,
                        iconDescription = R.string.descr_icon_message,
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(decryptionResult.value[3], fontSize = 18.sp)
                    }

                    ResultBox(
                        title = R.string.sender,
                        titleIcon = R.drawable.ic_send_black,
                        iconDescription = R.string.descr_icon_send,
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(decryptionResult.value[1], fontSize = 18.sp)
                    }

                    ResultBox(
                        title = R.string.receiver,
                        titleIcon = R.drawable.ic_receiver_black,
                        iconDescription = R.string.descr_icon_receiver,
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(decryptionResult.value[2], fontSize = 18.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { decryptionStatus.value = 0 }
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
                modifier = Modifier.padding(start = 5.dp),
                fontSize = 18.sp
            )
        }
        SelectionContainer {
            content()
        }
    }
}
