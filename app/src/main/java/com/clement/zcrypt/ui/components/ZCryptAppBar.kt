package com.clement.zcrypt.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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


@Preview
@Composable
fun AppBarPreview() {
    ZCryptTheme(darkTheme = true) {
        ZCryptAppBar(title = { Text("ZCrypt")})
    }
}