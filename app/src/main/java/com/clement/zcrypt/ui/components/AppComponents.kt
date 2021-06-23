package com.clement.zcrypt.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Preview
@Composable
fun AppBarPreview() {
    ZCryptTheme(darkTheme = true) {
        ZCryptAppBar(title = { Text("ZCrypt")})
    }
}