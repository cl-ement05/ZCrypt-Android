package com.clement.zcrypt.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clement.zcrypt.R
import com.clement.zcrypt.ui.theme.RobotoFont
import com.clement.zcrypt.ui.theme.ZCryptTheme

@Composable
fun ZCryptAppBar(
    modifier: Modifier = Modifier,
    title: @Composable RowScope.() -> Unit
) {
    TopAppBar(
        title = {
            Row { title() }
        },
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White
    )
}

@Composable
fun EncryptionAlgorithm(
    @StringRes algoId: Int
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_security_black),
            contentDescription = stringResource(id = R.string.descr_icon_security)
        )
        Text(
            text = stringResource(id = R.string.encryption_algo)
                    + " : "
                    + stringResource(id = algoId),
            fontFamily = RobotoFont,
            modifier = Modifier.padding(start = 3.dp),
            color = Color.White
        )
    }
}

@Composable
fun TextBlock(
    stringIds: List<Int>
) {
    stringIds.forEach { element ->
        Text(
            modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp),
            text = stringResource(id = element),
            fontFamily = RobotoFont,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AppDialog(
    @StringRes stringId: Int,
    dismissAction: () -> Unit
) {
    AlertDialog(
        onDismissRequest = dismissAction,
        text = {
            Text(stringResource(id = stringId))
        },
        confirmButton = {
            TextButton(
                onClick = dismissAction
            ) {
                Text(stringResource(id = R.string.ok))
            }
        }
    )
}

@Preview
@Composable
fun AppBarPreview() {
    ZCryptTheme(darkTheme = true) {
        ZCryptAppBar(title = { Text("ZCrypt")})
    }
}