package com.proxy.gshttp


import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    textColorPrimary: Color,
    textColorSecondary: Color,
    containerColor: Color,
    onLanguageSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(R.string.lang_title),
                color = textColorPrimary
            )
        },
        text = {
            Column {

                val languages = listOf("ru" to "Русский", "en" to "English")


                languages.forEach { (langCode, langName) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(langCode) }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = currentLanguage == langCode,
                            onClick = { onLanguageSelected(langCode) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = textColorPrimary,
                                unselectedColor = textColorSecondary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = langName,
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
