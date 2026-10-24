package com.proxy.gshttp


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.clickable
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    version: String,
    bgColor: Color,
    textColorPrimary: Color,
    textColorSecondary: Color,
    lineColor: Color,
    uriHandler: androidx.compose.ui.platform.UriHandler,
    onBackClick: () -> Unit,
    onThemeClick: () -> Unit,
    onClearHistory: () -> Unit,
    themeSetting: String,
    onLanguageClick: () -> Unit
) {

    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val saveSetting: (String, Any) -> Unit = { key, value ->
        try {
            val editor = sharedPref.edit()
            when (value) {
                is Boolean -> editor.putBoolean(key, value)
                is Float -> editor.putFloat(key, value)
                is String -> editor.putString(key, value)
            }
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val followRedirectsSetting = remember { mutableStateOf(sharedPref.getBoolean("redirects_setting", true)) }
    val requestTimeout = remember { mutableStateOf(sharedPref.getFloat("timeout_setting", 10f)) }
    val verifySslSetting = remember { mutableStateOf(sharedPref.getBoolean("verify_ssl_setting", true)) }
    val ignoreSslSetting = remember { mutableStateOf(sharedPref.getBoolean("ignore_ssl_setting", false)) }
    val userAgentInput = remember { mutableStateOf(sharedPref.getString("user_agent_setting", "GS.HTTP/1.0") ?: "GS.HTTP/1.0") }


    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textColorPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = textColorPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {




                item {
                    Text(
                        text = stringResource(R.string.network_section),
                        fontSize = 12.sp,
                        color = textColorSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.auto_redirect),
                                color = textColorPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = stringResource(R.string.auto_redirect_sub),
                                color = textColorSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = followRedirectsSetting.value, onCheckedChange = { newValue ->
                                followRedirectsSetting.value = newValue
                                saveSetting("redirects_setting", newValue)
                            }, colors = SwitchDefaults.colors(
                                checkedThumbColor = bgColor,
                                checkedTrackColor = textColorPrimary,
                                uncheckedThumbColor = textColorSecondary,
                                uncheckedTrackColor = lineColor
                            )
                        )
                    }
                }



                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "${stringResource(R.string.timeout_label)}: ${requestTimeout.value.toInt()}c",
                            color = textColorPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Slider(
                            value = requestTimeout.value, onValueChange = { newValue ->
                                requestTimeout.value = newValue
                            }, onValueChangeFinished = {
                                saveSetting("timeout_setting", requestTimeout.value)
                            }, valueRange = 1f..30f, steps = 28
                        )
                    }
                }
                item { HorizontalDivider(color = lineColor, thickness = 0.5.dp) }
                item {
                    Text(
                        text = stringResource(R.string.ssl_section),
                        fontSize = 12.sp,
                        color = textColorSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.verify_ssl),
                                color = textColorPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = stringResource(R.string.verify_ssl_sub),
                                color = textColorSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = verifySslSetting.value, onCheckedChange = { newValue ->
                                verifySslSetting.value = newValue
                                verifySslSetting.value = newValue
                                saveSetting("verify_ssl_setting", newValue)
                            }, colors = SwitchDefaults.colors(
                                checkedThumbColor = bgColor,
                                checkedTrackColor = textColorPrimary,
                                uncheckedThumbColor = textColorSecondary,
                                uncheckedTrackColor = lineColor
                            )
                        )
                    }
                }


                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.ignore_ssl),
                                color = textColorPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = stringResource(R.string.ignore_ssl_sub),
                                color = textColorSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = ignoreSslSetting.value, onCheckedChange = { newValue ->
                                ignoreSslSetting.value = newValue
                                ignoreSslSetting.value = newValue
                                saveSetting("ignore_ssl_setting", newValue)
                            }, colors = SwitchDefaults.colors(
                                checkedThumbColor = bgColor,
                                checkedTrackColor = textColorPrimary,
                                uncheckedThumbColor = textColorSecondary,
                                uncheckedTrackColor = lineColor
                            )
                        )
                    }
                }




                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.user_agent),
                                color = textColorPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = stringResource(R.string.default_user),
                                color = textColorPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.clickable {
                                    userAgentInput.value = "GS.HTTP/1.0"
                                    saveSetting("user_agent_setting", "GS.HTTP/1.0")
                                }
                            )
                        }

                        TextField(
                            value = userAgentInput.value,
                            onValueChange = { newValue ->
                                userAgentInput.value = newValue
                                val finalAgent = if (newValue.isBlank()) "GS.HTTP/1.0" else newValue
                                saveSetting("user_agent_setting", finalAgent)

                            },
                            placeholder = {
                                Text(
                                    text = "GS.HTTP/1.0",
                                    color = textColorSecondary.copy(alpha = 0.7f)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = bgColor,
                                unfocusedContainerColor = bgColor,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = textColorPrimary,
                                unfocusedTextColor = textColorPrimary
                            )
                        )
                    }
                }



                item { HorizontalDivider(color = lineColor, thickness = 0.5.dp) }

                item {
                    Button(
                        onClick = onThemeClick,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = lineColor,
                            contentColor = textColorPrimary
                        )
                    ) {
                        val themeText = when (themeSetting) {
                            "dark" -> stringResource(R.string.theme_dark)
                            "light" -> stringResource(R.string.theme_light)
                            else -> stringResource(R.string.theme_system)
                        }
                        Text(
                            text = "${stringResource(R.string.theme_btn)}:  $themeText",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                item {
                    Button(
                        onClick = onLanguageClick,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = lineColor,
                            contentColor = textColorPrimary
                        )
                    ) {
                        Text(
                            text = "${stringResource(R.string.lang_btn)}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {

                            onClearHistory()
                            try {
                                sharedPref.edit().remove("local_search_history").apply()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                            onBackClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(

                            containerColor = lineColor,
                            contentColor = textColorPrimary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.clear_history),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}











