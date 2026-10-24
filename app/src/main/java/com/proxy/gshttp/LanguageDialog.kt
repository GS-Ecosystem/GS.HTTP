package com.proxy.gshttp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    textColorPrimary: Color,
    textColorSecondary: Color,
    containerColor: Color,
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = containerColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.lang_title),
                        color = textColorPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = textColorPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = containerColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val languages = listOf("ru" to "Русский", "en" to "English")

            languages.forEach { (langCode, langName) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelected(langCode) }
                        .padding(vertical = 12.dp)
                ) {
                    RadioButton(
                        selected = currentLanguage == langCode,
                        onClick = { onLanguageSelected(langCode) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = textColorPrimary,
                            unselectedColor = textColorSecondary
                        )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = langName,
                        color = textColorPrimary
                    )
                }
            }
        }
    }
}
