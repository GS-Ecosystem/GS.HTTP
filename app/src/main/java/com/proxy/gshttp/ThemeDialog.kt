package com.proxy.gshttp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
@Composable
fun ThemeSelectionDialog(
    themeSetting: String,
    textColorPrimary: Color,
    textColorSecondary: Color,
    containerColor: Color,
    onThemeSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(R.string.theme_btn),
                color = textColorPrimary
            )
        },
        text = {
            Column {

                val themes = listOf(
                    "system" to stringResource(R.string.theme_system),
                    "light" to stringResource(R.string.theme_light),
                    "dark" to stringResource(R.string.theme_dark)
                )

                themes.forEach { (themeCode, themeName) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(themeCode) }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = themeSetting == themeCode,
                            onClick = { onThemeSelected(themeCode) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = textColorPrimary,
                                unselectedColor = textColorSecondary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = themeName,
                            color = textColorPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = textColorPrimary
                )
            }
        },
        containerColor = containerColor,
        shape = RoundedCornerShape(28.dp)
    )
}
