package com.clement.zcrypt.ui.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.clement.zcrypt.ui.theme.ZCryptTheme

@Composable
fun DecryptLayout() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

    }
}

@Preview
@Composable
fun DecryptLayoutPreview() {
    ZCryptTheme {
        DecryptLayout()
    }
}