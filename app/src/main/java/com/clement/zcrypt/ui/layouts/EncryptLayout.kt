package com.clement.zcrypt.ui.layouts

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clement.zcrypt.MainActivity
import com.clement.zcrypt.R
import com.clement.zcrypt.core.startEncryption
import com.clement.zcrypt.core.writeFile
import com.clement.zcrypt.ui.components.EncryptionAlgorithm
import com.google.accompanist.insets.navigationBarsWithImePadding

@Composable
fun EncryptLayout(
    activity: MainActivity
) {
    EncryptionAlgorithm(algoId = R.string.zcrypt_algo)

    Column(
        Modifier
            .fillMaxSize()
            .navigationBarsWithImePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val messageInput = inputBox(
            label = R.string.message,
            placeholder = R.string.message_placeholder,
            modifier = Modifier.padding(top = 20.dp),
            icon = R.drawable.ic_message_black,
            iconDescription = R.string.descr_icon_message
        )

        val senderInput = inputBox(
            label = R.string.sender,
            placeholder = R.string.sender_placeholder,
            modifier = Modifier.padding(top = 20.dp),
            icon = R.drawable.ic_send_black,
            iconDescription = R.string.descr_icon_send
        )

        val receiverInput = inputBox(
            label = R.string.receiver,
            placeholder = R.string.receiver_placeholder,
            modifier = Modifier.padding(top = 20.dp),
            icon = R.drawable.ic_receiver_black,
            iconDescription = R.string.descr_icon_receiver
        )


        val documentUri = remember { mutableStateOf<Uri?>(null) }
        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument()) {
            documentUri.value = it
        }
        val encryptionResult = remember { mutableStateOf<List<String>>(emptyList()) }
        val encryptionSuccess = remember { mutableStateOf(0)}
        /*
        -1 is used when encryption and file write were successful ie the whole encryption process has ended
        0 is used for successful encryption
        1 is used for fail due to blank text fields
        2 is used for fail due to error in zcrypt algo (usually because of special char)
        3 is used for fail when writing file
         */

        Button(
            onClick = {
                if (messageInput.isNotBlank() && receiverInput.isNotBlank() && senderInput.isNotBlank()) {
                    encryptionResult.value = startEncryption(
                        messageInput,
                        receiverInput,
                        senderInput
                    )

                    if (encryptionResult.value.isNotEmpty()) {
                        launcher.launch("encrypted.txt")
                    } else {
                        encryptionSuccess.value = 2
                    }
                } else {
                    encryptionSuccess.value = 1
                }
            },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(stringResource(id = R.string.encrypt))
        }

        documentUri.value?.let {
            if (!writeFile(activity, it, encryptionResult.value)) {
                encryptionSuccess.value = 3
            } else {
                encryptionSuccess.value = -1
            }
        }


        if (encryptionSuccess.value != 0) {
            AlertDialog(
                onDismissRequest = { encryptionSuccess.value = 0 },
                text = {
                    when(encryptionSuccess.value) {
                        -1 -> Text(stringResource(id = R.string.dialog_success_finished_encryption))
                        1 -> Text(stringResource(id = R.string.dialog_error_blank))
                        2 -> Text(stringResource(id = R.string.dialog_error_invalid))
                        3 -> Text(stringResource(id = R.string.dialog_error_file))
                    }
                },
                confirmButton = {
                    TextButton(onClick = { encryptionSuccess.value = 0 }) {
                        Text(stringResource(id = R.string.ok))
                    }
                }
            )
        }

    }

}

@Composable
fun inputBox(
    @StringRes label: Int,
    @StringRes placeholder: Int,
    @DrawableRes icon: Int,
    @StringRes iconDescription: Int,
    modifier: Modifier = Modifier
) : String {
    var input by rememberSaveable { mutableStateOf("")}
    OutlinedTextField(
        modifier = modifier,
        value = input,
        onValueChange = { input = it },
        label = {
            Text(stringResource(id = label))
        },
        placeholder = {
            Text(stringResource(id = placeholder))
        },
        trailingIcon = {
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = iconDescription)
            )
        }
    )
    return input
}