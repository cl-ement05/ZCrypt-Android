package com.clement.zcrypt

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import com.clement.zcrypt.ui.components.ZCryptAppBar
import com.clement.zcrypt.ui.layouts.DecryptLayout
import com.clement.zcrypt.ui.layouts.EncryptLayout
import com.clement.zcrypt.ui.layouts.HintsLayout
import com.clement.zcrypt.ui.layouts.tabLayout
import com.clement.zcrypt.ui.theme.RobotoFont
import com.clement.zcrypt.ui.theme.ZCryptTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainMenuView()
        }
    }

    @Composable
    fun MainMenuView() {
        ZCryptTheme {
            // A surface container using the 'background' color from the theme
            Surface(color = MaterialTheme.colors.background) {
                Column(Modifier.fillMaxWidth()) {
                    ZCryptAppBar(
                        title = {
                            Text(
                                stringResource(id = R.string.app_name),
                                fontFamily = RobotoFont
                            )
                        }
                    )

                    val sharedPrefs: SharedPreferences = this@MainActivity.getSharedPreferences("zcrypt", Context.MODE_PRIVATE)
                    if (!sharedPrefs.contains("firstLaunch")) {
                        sharedPrefs.edit {
                            putBoolean("firstLaunch", true)
                        }
                        HintsLayout(this@MainActivity)
                    } else LayoutsManager()
                }
            }
        }
    }

    @Composable
    fun LayoutsManager() {
        val selectedTab: Int = tabLayout()
        if (selectedTab == 0) EncryptLayout(this@MainActivity)
        else DecryptLayout(this@MainActivity)
    }

}

