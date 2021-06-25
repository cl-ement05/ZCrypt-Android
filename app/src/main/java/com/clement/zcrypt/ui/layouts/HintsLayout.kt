package com.clement.zcrypt.ui.layouts

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clement.zcrypt.MainActivity
import com.clement.zcrypt.R
import com.clement.zcrypt.ui.components.TextBlock
import com.clement.zcrypt.ui.theme.RobotoFont

@Composable
fun HintsLayout(activity: MainActivity) {
    val hintShowing = remember { mutableStateOf("first") }
    val launchMain = remember { mutableStateOf(false) }

    if (!launchMain.value) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (hintShowing.value) {
                "first" -> {
                    val zcryptPresentationTexts = listOf<@StringRes Int>(
                        R.string.welcomes_zcrypt_description,
                        R.string.welcomes_zcrypt_description_2,
                        R.string.welcomes_zcrypt_history,
                        R.string.welcomes_zcrypt_history_2,
                    )
                    HintLayout(
                        listOfHintsIds = zcryptPresentationTexts,
                        buttonText = R.string.lets_see_how_it_works,
                        variable = hintShowing,
                        newVariableValue = "encrypt"
                    )
                }
                "encrypt" -> {
                    val encryptMessageExplanation = listOf<@StringRes Int>(
                        R.string.welcomes_explanation_base,
                        R.string.welcomes_basic_steps_encrypt,
                        R.string.welcomes_step_1_encrypt,
                        R.string.welcomes_step_2_encrypt,
                        R.string.welcomes_step_3_encrypt
                    )

                    HintLayout(
                        listOfHintsIds = encryptMessageExplanation,
                        buttonText = R.string.lets_see_how_decrypt_works,
                        variable = hintShowing,
                        newVariableValue = "decrypt"
                    )
                }
                "decrypt" -> {
                    val decryptMessageExplanation = listOf<@StringRes Int>(
                        R.string.welcomes_basic_steps_decrypt,
                        R.string.welcomes_step_1_decrypt,
                        R.string.welcomes_step_2_decrypt,
                        R.string.welcomes_step_3_decrypt,
                        R.string.welcomes_enjoy
                    )

                    HintLayout(
                        listOfHintsIds = decryptMessageExplanation,
                        buttonText = R.string.start,
                        variable = launchMain,
                        newVariableValue = true
                    )
                }
            }
        }
    } else activity.LayoutsManager()


}


@Composable
fun <T> HintLayout(
    listOfHintsIds: List<Int>,
    @StringRes buttonText: Int,
    variable: MutableState<T>,
    newVariableValue: T
) {
    Text(stringResource(id = R.string.welcome_to_zcrypt), fontSize = 30.sp, fontFamily = RobotoFont)

    TextBlock(stringIds = listOfHintsIds)
    Button(
        modifier = Modifier.padding(top = 60.dp, start = 15.dp, end = 15.dp),
        onClick = {
            variable.value = newVariableValue
        }
    ) {
        Text(stringResource(id = buttonText))
    }

}