package com.clement.zcrypt.ui.layouts

import android.content.Context
import android.content.SharedPreferences
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
import androidx.core.content.edit
import com.clement.zcrypt.MainActivity
import com.clement.zcrypt.R
import com.clement.zcrypt.core.OperationStatus
import com.clement.zcrypt.core.mainEncrypt
import com.clement.zcrypt.core.writeFile
import com.clement.zcrypt.ui.components.AppDialog
import com.clement.zcrypt.ui.components.EncryptionAlgorithm
import com.clement.zcrypt.ui.theme.RobotoFont
import com.google.accompanist.insets.navigationBarsWithImePadding

@Composable
fun EncryptLayout(
    activity: MainActivity
) {
    val sharedPrefs: SharedPreferences = activity.getSharedPreferences("zcrypt", Context.MODE_PRIVATE)
    val rememberSender = remember { mutableStateOf(sharedPrefs.getBoolean("enableSenderMemory", true)) }

    val encryptionResult = remember { mutableStateOf<List<String>>(emptyList()) }
    val encryptionStatus = remember { mutableStateOf(0)}

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument()) { documentUri ->
        if (documentUri != null) {
            if (!writeFile(activity, documentUri, encryptionResult.value.subList(0, 5))) {
                encryptionStatus.value = OperationStatus.IO_OR_FILE_ERROR
            } else {
                encryptionStatus.value = OperationStatus.SUCCESS
            }
        } else encryptionStatus.value = OperationStatus.NO_FILE_SELECTED
    }

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

        //checking if sender name is stored on shared prefs
        val senderValue: String =
            activity.getSharedPreferences("zcrypt", Context.MODE_PRIVATE).
            getString("senderMemory", "")!!


        val senderInput = inputBox(
            label = R.string.sender,
            placeholder = R.string.sender_placeholder,
            modifier = Modifier.padding(top = 20.dp),
            icon = R.drawable.ic_send_black,
            iconDescription = R.string.descr_icon_send,
            customValue = senderValue
        )

        val receiverInput = inputBox(
            label = R.string.receiver,
            placeholder = R.string.receiver_placeholder,
            modifier = Modifier.padding(top = 20.dp),
            icon = R.drawable.ic_receiver_black,
            iconDescription = R.string.descr_icon_receiver
        )


        //remember sender row
        Row(Modifier.padding(top = 20.dp)) {
            Text(stringResource(id = R.string.remember_sender), fontFamily = RobotoFont)
            
            Switch(
                modifier = Modifier.padding(start = 15.dp),
                checked = rememberSender.value,
                onCheckedChange = { newState ->
                    rememberSender.value = newState
                    sharedPrefs.edit {
                        putBoolean("enableSenderMemory", newState)
                    }
                }
            )
        }    

        Button(
            onClick = {
                //resetting vars
                encryptionResult.value = emptyList()

                if (messageInput.isNotBlank() && receiverInput.isNotBlank() && senderInput.isNotBlank()) {
                    encryptionResult.value = mainEncrypt(
                        messageInput,
                        receiverInput,
                        senderInput,
                        sharedPrefs.getInt("zcryptLastKey", -1)
                    )

                    if (encryptionResult.value.isNotEmpty()) {
                        launcher.launch("encrypted.txt")
                    } else {
                        encryptionStatus.value = OperationStatus.ZCRYPT_ALGO_ERROR
                    }
                } else {
                    encryptionStatus.value = OperationStatus.BLANK_FIELDS
                }
            },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(stringResource(id = R.string.encrypt))
        }

        //errors dialog
        when(encryptionStatus.value) {
            OperationStatus.BLANK_FIELDS -> AppDialog(
                titleId = R.string.error,
                stringId = R.string.dialog_error_blank,
                variable = encryptionStatus,
                newValue = 0
            )
            OperationStatus.ZCRYPT_ALGO_ERROR -> AppDialog(
                titleId = R.string.error,
                stringId = R.string.dialog_error_zcrypt_algo,
                variable = encryptionStatus,
                newValue = 0,
                formattingValue = "encrypted"
            )
            OperationStatus.NO_FILE_SELECTED -> AppDialog(
                titleId = R.string.error,
                stringId = R.string.dialog_error_no_file_selected,
                variable = encryptionStatus,
                newValue = 0
            )
            OperationStatus.IO_OR_FILE_ERROR -> AppDialog(
                titleId = R.string.error,
                stringId = R.string.dialog_error_file_no_access,
                variable = encryptionStatus,
                newValue = 0
            )
        }


        //encryption success dialog
        if (encryptionStatus.value == OperationStatus.SUCCESS) {
            if (rememberSender.value) {
                //remembering sender input
                sharedPrefs.edit {
                    putString("senderMemory", senderInput)
                }
            } else if (sharedPrefs.getString("senderMemory", null) != null) {
                sharedPrefs.edit {
                    remove("senderMemory")
                }
            }

            //saving last key to shared prefs
            sharedPrefs.edit {
                putInt("zcryptLastKey", encryptionResult.value[5].toInt())
            }

            AppDialog(
                titleId = R.string.success,
                stringId = R.string.dialog_success_finished_encryption,
                variable = encryptionStatus,
                newValue = 0
            )

        }
    }
}

@Composable
fun inputBox(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    @StringRes placeholder: Int,
    @DrawableRes icon: Int,
    @StringRes iconDescription: Int,
    customValue: String = ""
) : String {
    var input by rememberSaveable { mutableStateOf(customValue)}
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
