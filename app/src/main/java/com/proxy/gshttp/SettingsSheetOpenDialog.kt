package com.proxy.gshttp


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.clickable
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.material3.SheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IsSettingsSheetOpen(
    version: String,
    sheetState: SheetState,
    bgColor: Color,
    textColorPrimary: Color,
    textColorSecondary: Color,
    lineColor: Color,
    uriHandler: androidx.compose.ui.platform.UriHandler,
    onDismiss: () -> Unit,
    onThemeClick: () -> Unit,
    onClearHistory: () -> Unit,
    themeSetting: String,
    onLanguageClick: () -> Unit
) {



    val context = LocalContext.current


    val followRedirectsSetting = remember { mutableStateOf(true) }
    val requestTimeout = remember { mutableStateOf(10f) }
    val verifySslSetting = remember { mutableStateOf(true) }
    val ignoreSslSetting = remember { mutableStateOf(false) }
    val userAgentInput = remember { mutableStateOf("GS.HTTP/1.0") }

    LaunchedEffect(key1 = Unit) {
        try {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)


            followRedirectsSetting.value = sharedPref.getBoolean("redirects_setting", true)
            requestTimeout.value = sharedPref.getFloat("timeout_setting", 10f)
            verifySslSetting.value = sharedPref.getBoolean("verify_ssl_setting", true)
            ignoreSslSetting.value = sharedPref.getBoolean("ignore_ssl_setting", false)
            userAgentInput.value =
                sharedPref.getString("user_agent_setting", "GS.HTTP/1.0") ?: "GS.HTTP/1.0"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = bgColor,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(textColorPrimary.copy(alpha = 0.2f))
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.settings_title),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textColorPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

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
                                val sharedPref =
                                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                try {
                                    sharedPref.edit().putBoolean("redirects_setting", newValue)
                                        .apply()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
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
                                val sharedPref =
                                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                try {
                                    sharedPref.edit()
                                        .putFloat("timeout_setting", requestTimeout.value)
                                        .apply()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
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
                                val sharedPref =
                                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                try {
                                    sharedPref.edit().putBoolean("verify_ssl_setting", newValue)
                                        .apply()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
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
                                val sharedPref =
                                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                try {
                                    sharedPref.edit().putBoolean("ignore_ssl_setting", newValue)
                                        .apply()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
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
                                    val sharedPref = context.getSharedPreferences(
                                        "app_prefs",
                                        Context.MODE_PRIVATE
                                    )
                                    try {
                                        sharedPref.edit()
                                            .putString("user_agent_setting", "GS.HTTP/1.0").apply()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            )
                        }

                        TextField(
                            value = userAgentInput.value,
                            onValueChange = { newValue ->
                                userAgentInput.value = newValue
                                val finalAgent = if (newValue.isBlank()) "GS.HTTP/1.0" else newValue
                                val sharedPref =
                                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                try {
                                    sharedPref.edit().putString("user_agent_setting", finalAgent)
                                        .apply()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
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


                            val sharedPref =
                                context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            try {
                                sharedPref.edit().remove("local_search_history").apply()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                            onDismiss()
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






            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = textColorPrimary,
                    contentColor = bgColor
                )
            ) {
                Text(
                    text = stringResource(R.string.btn_done),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}




