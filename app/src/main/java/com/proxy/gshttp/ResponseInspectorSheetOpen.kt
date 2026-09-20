package com.proxy.gshttp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponseInspectorSheet(
    version: String,
    sheetState: SheetState,
    bgColor: Color,
    textColorPrimary: Color,
    textColorSecondary: Color,
    lineColor: Color,
    lastValidUrl: String,
    responseBodyText: String,
    responseHeadersText: String,
    responseCookiesText: String,
    currentLanguage: String,
    onDismiss: () -> Unit
) {


    val context = LocalContext.current


    var activeSearchTab by remember { mutableStateOf("Body") }
    var searchQuery by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = bgColor,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(textColorPrimary.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.inspector_title),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textColorPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    try {

                        val browserIntent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(lastValidUrl)
                        )
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = bgColor.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(1.dp, lineColor, RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = stringResource(R.string.btn_open_browser_emoji),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(lineColor.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Body", "Headers", "Cookies").forEach { tab ->
                    val isSelected = activeSearchTab == tab
                    TextButton(
                        onClick = { activeSearchTab = tab },
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) bgColor else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                    ) {
                        Text(
                            text = tab,
                            color = textColorPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))


            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = {
                    Text(stringResource(R.string.search_log_placeholder))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = bgColor,
                    unfocusedContainerColor = bgColor,
                    focusedTextColor = textColorPrimary,
                    unfocusedTextColor = textColorPrimary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            val currentTextData = when (activeSearchTab) {
                "Body" -> responseBodyText
                "Headers" -> responseHeadersText
                "Cookies" -> responseCookiesText
                else -> ""
            }


            val filteredText: String = if (searchQuery.isEmpty()) {
                currentTextData
            } else {
                try {
                    if (currentTextData.length > 500_000) {

                        "ERROR_TOO_BIG"
                    } else {
                        currentTextData.lines()
                            .filter { it.contains(searchQuery, ignoreCase = true) }
                            .joinToString("\n")
                    }
                } catch (e: Exception) {
                    "ERROR_SEARCH_FAILED"
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(bgColor, RoundedCornerShape(16.dp))
                    .border(1.dp, lineColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(

                            text = if (filteredText == "ERROR_TOO_BIG") {
                                androidx.compose.ui.text.AnnotatedString(stringResource(R.string.search_too_big))
                            } else if (filteredText == "ERROR_SEARCH_FAILED") {
                                androidx.compose.ui.text.AnnotatedString(stringResource(R.string.search_error))
                            } else if (filteredText.trim().isEmpty()) {
                                if (searchQuery.isNotEmpty()) {
                                    androidx.compose.ui.text.AnnotatedString(stringResource(R.string.not_found))
                                } else if (activeSearchTab == "Cookies") {
                                    androidx.compose.ui.text.AnnotatedString(stringResource(R.string.cookies_empty))
                                } else if (activeSearchTab == "Body") {
                                    androidx.compose.ui.text.AnnotatedString(stringResource(R.string.body_empty))
                                } else {
                                    androidx.compose.ui.text.AnnotatedString(stringResource(R.string.no_data))
                                }
                            } else {

                                androidx.compose.ui.text.AnnotatedString(filteredText)
                            },
                            color = textColorPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}









