package com.clement.zcrypt.ui.layouts

import android.content.Context
import android.content.SharedPreferences
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
import androidx.core.content.edit
import com.clement.zcrypt.MainActivity
import com.clement.zcrypt.R
import com.clement.zcrypt.core.startEncryption
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
    val encryptionEnd = remember { mutableStateOf(false)}
    val documentUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument()) {
        documentUri.value = it
    }
    val encryptionResult = remember { mutableStateOf<List<String>>(emptyList()) }
    val encryptionSuccess = remember { mutableStateOf(0)}
    /*
    0 is used for encryption not finished
    1 is used for fail due to blank text fields
    2 is used for fail due to error in zcrypt algo (usually because of special char)
    3 is used for fail when writing file
     */
    val rememberSender = remember { mutableStateOf(sharedPrefs.getBoolean("enableSenderMemory", true)) }


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
                encryptionSuccess.value = 0
                encryptionEnd.value = true

                if (messageInput.isNotBlank() && receiverInput.isNotBlank() && senderInput.isNotBlank()) {
                    encryptionResult.value = startEncryption(
                        messageInput,
                        receiverInput,
                        senderInput,
                        sharedPrefs.getInt("zcryptLastKey", -1)
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

        //when receiving a value from the launcher for create document
        documentUri.value?.let {
            if (!writeFile(activity, it, encryptionResult.value.subList(0, 5))) {
                encryptionSuccess.value = 3
            } else {
                encryptionSuccess.value = -1
            }
        }


        //errors dialog
        if (encryptionSuccess.value != 0 && encryptionSuccess.value != -1) {
            when(encryptionSuccess.value) {
                1 -> AppDialog(R.string.dialog_error_blank) {
                    encryptionSuccess.value = 0
                }
                2 -> AppDialog(R.string.dialog_error_invalid) {
                    encryptionSuccess.value = 0
                }
                3 -> AppDialog(R.string.dialog_error_file) {
                    encryptionSuccess.value = 0
                }
            }
        }


        //encryption end and success dialog
        if (encryptionSuccess.value == -1) {
            if(encryptionEnd.value) {
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

                AppDialog(R.string.dialog_success_finished_encryption) {
                    encryptionEnd.value = false
                }

            }
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
