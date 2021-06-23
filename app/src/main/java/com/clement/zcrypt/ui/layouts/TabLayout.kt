package com.clement.zcrypt.ui.layouts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.clement.zcrypt.R
import com.clement.zcrypt.ui.theme.ZCryptTheme

@Composable
fun tabLayout(): Int {
    var selectedTab by remember {
        mutableStateOf(0)
    }
    TabRow(
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = selectedTab,
        backgroundColor = MaterialTheme.colors.primary,
        tabs = {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(stringResource(R.string.encrypt)) },
                icon = {
                    Image(
                        painterResource(id = R.drawable.ic_padlock_black),
                        stringResource(id = R.string.descr_icon_padlock)
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(stringResource(R.string.decrypt)) },
                icon = {
                    Image(
                        painterResource(id = R.drawable.ic_padunlock_black),
                        stringResource(id = R.string.descr_icon_padunlock)
                    )
                }
            )
        }
    )
    return selectedTab
}

@Preview
@Composable
fun TabLayoutPreview() {
    ZCryptTheme {
        tabLayout()
    }
}