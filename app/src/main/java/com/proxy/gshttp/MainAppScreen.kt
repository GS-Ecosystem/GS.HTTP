package com.proxy.gshttp

import android.widget.Toast
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import kotlin.text.trim
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.outlined.Info
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.core.content.edit
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.mutableFloatStateOf




interface MonochromeColors {
    val bgPrimary: Color
    val bgSecondary: Color
    val bgElevated: Color
    val textPrimary: Color
    val textSecondary: Color
    val accent: Color
    val line: Color
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    themeSetting: String,
    onThemeChange: (String) -> Unit
) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val saveSetting: (String, Any) -> Unit = { key, value ->
        try {

            sharedPref.edit {
                when (value) {
                    is Boolean -> putBoolean(key, value)
                    is Float -> putFloat(key, value)
                    is String -> putString(key, value)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    val scope = rememberCoroutineScope()
    var isInfoDialogVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var isLanguageDialogOpen by remember { mutableStateOf(false) }
    val systemLang = java.util.Locale.getDefault().language
    val initialLang = if (systemLang == "ru") "ru" else "en"
    var currentLanguage by remember { mutableStateOf(initialLang) }

    LaunchedEffect(Unit) {
        try {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            currentLanguage = sharedPref.getString("app_lang", initialLang) ?: initialLang
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    var currentView by remember { mutableStateOf("welcome") }
    var isSettingsScreenOpen by remember { mutableStateOf(false) }

    var requestTimeout by remember { mutableFloatStateOf(10f) }

    var isThemeDialogOpen by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf("") }
    var resText by remember { mutableStateOf("") }
    var resTextColor by remember { mutableStateOf(Color.Unspecified) }
    var safeText by remember { mutableStateOf("") }
    var safeTextColor by remember { mutableStateOf(Color.Unspecified) }
    var isLoading by remember { mutableStateOf(false) }
    var requestBodyInput by remember { mutableStateOf("") }
    var requestHeadersInput by remember { mutableStateOf("") }


    var searchHistory by remember { mutableStateOf(setOf<String>()) }



    LaunchedEffect(key1 = Unit) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        try {
            val savedHistory = sharedPref.getStringSet("local_search_history", emptySet())
            searchHistory = savedHistory?.take(10)?.toSet() ?: emptySet()
        } catch (_: Exception) {
            searchHistory = emptySet()
            try {
                sharedPref.edit { remove("local_search_history") }
            } catch (_: Exception) {
            }
        }
    }


    var responseBodyText by remember { mutableStateOf("") }
    var responseHeadersText by remember { mutableStateOf("") }
    var responseCookiesText by remember { mutableStateOf("") }
    var lastValidUrl by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }


    val infoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var isHistorySheetOpen by remember { mutableStateOf(false) }
    var isResponseInspectorSheetOpen by remember { mutableStateOf(false) }

    var isScanPressed by remember { mutableStateOf(false) }


    var userAgentInput by remember { mutableStateOf("GS.HTTP/1.0") }
    var requestTimeoutText by remember { mutableStateOf("10") }
    var followRedirectsSetting by remember { mutableStateOf(true) }
    var selectedMethod by remember { mutableStateOf("GET") }

    val scanScale by animateFloatAsState(
        targetValue = if (isScanPressed) 0.9f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(
            stiffness = 450f,
            dampingRatio = 0.75f
        ),
        label = "scan_scale"
    )

    LaunchedEffect(Unit) {
        try {
            trackEvent(context, scope, "app_open")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(Unit) {
        try {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            userAgentInput =
                sharedPref.getString("user_agent_setting", "GS.HTTP/1.0") ?: "GS.HTTP/1.0"
            requestTimeoutText = sharedPref.getString("timeout_setting", "10") ?: "10"
            followRedirectsSetting = sharedPref.getBoolean("redirects_setting", true)
            selectedMethod = sharedPref.getString("method_setting", "GET") ?: "GET"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val isDark = when (themeSetting) {
        "dark" -> true
        "light" -> false
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val palette = if (isDark) {
        object : MonochromeColors {
            override val bgPrimary = Color(0xFF000000)
            override val bgSecondary = Color(0xFF111111)
            override val bgElevated = Color(0xFF1E1E1E)
            override val textPrimary = Color(0xFFFFFFFF)
            override val textSecondary = Color(0xFF888888)
            override val accent = Color(0xFFFFFFFF)
            override val line = Color(0xFF333333)
        }
    } else {
        object : MonochromeColors {
            override val bgPrimary = Color(0xFFFFFFFF)
            override val bgSecondary = Color(0xFFF4F4F4)
            override val bgElevated = Color(0xFFE5E5E5)
            override val textPrimary = Color(0xFF000000)
            override val textSecondary = Color(0xFF777777)
            override val accent = Color(0xFF000000)
            override val line = Color(0xFFDDDDDD)
        }
    }

    resTextColor = palette.textPrimary
    safeTextColor = palette.textSecondary

    val switchView: (String) -> Unit = { target ->
        scope.launch {
            try {
                if (target == "main") trackEvent(context, scope, "view_scan_screen")
                currentView = target
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val runScan: () -> Unit = {
        searchQuery = ""
        val url = urlInput.trim()
        val hasSpaces = url.contains(" ")
        val isInvalidProtocol = (url.startsWith("https:/") && !url.startsWith("https://")) ||
                (url.startsWith("http:/") && !url.startsWith("http://"))

        if (url.isEmpty() || hasSpaces || isInvalidProtocol) {
            resText = context.getString(R.string.status_error)
            safeText = context.getString(R.string.status_invalid)
            isLoading = false
        } else {
            try {
                trackEvent(context, scope, "run_scan_action")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isLoading = true
            resText = ""
            safeText = ""
            responseBodyText = ""
            responseHeadersText = ""
            responseCookiesText = ""
            lastValidUrl = ""

            val updatedHistory = (setOf(url) + searchHistory).take(10).toSet()
            searchHistory = updatedHistory

            scope.launch(Dispatchers.IO) {
                try {
                    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    sharedPref.edit {
                        remove("local_search_history")
                        putStringSet("local_search_history", updatedHistory)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            scope.launch(Dispatchers.IO) {
                val fullUrl = if (url.startsWith("http")) url else "https://$url"
                val currentAgent = if (userAgentInput.isBlank()) "GS.HTTP/1.0" else userAgentInput
                val timeoutLong = requestTimeout.toLong().coerceAtLeast(1L)

                try {
                    val client = OkHttpClient.Builder()
                        .connectTimeout(timeoutLong, TimeUnit.SECONDS)
                        .readTimeout(timeoutLong, TimeUnit.SECONDS)
                        .followRedirects(followRedirectsSetting)
                        .build()

                    val requestBuilder = Request.Builder()
                        .url(fullUrl)
                        .header("User-Agent", currentAgent)


                    if (requestHeadersInput.isNotBlank()) {
                        requestHeadersInput.lines().forEach { line ->
                            val parts = line.split(":", limit = 2)
                            if (parts.size == 2) {
                                val key = parts[0].trim()
                                val value = parts[1].trim()
                                if (key.isNotEmpty()) {
                                    requestBuilder.addHeader(key, value)
                                }
                            }
                        }
                    }


                    val mediaType = "application/json; charset=utf-8".toMediaType()
                    val bodyBytes = requestBodyInput.toByteArray(Charsets.UTF_8)
                    val requestBody = bodyBytes.toRequestBody(mediaType)



                    when (selectedMethod) {
                        "GET" -> requestBuilder.get()
                        "HEAD" -> requestBuilder.head()


                        "POST" -> requestBuilder.post(requestBody)
                        "PUT" -> requestBuilder.put(requestBody)
                        "DELETE" -> requestBuilder.delete(requestBody)
                        "PATCH" -> requestBuilder.patch(requestBody)


                        "OPTIONS" -> requestBuilder.method("OPTIONS", null)

                        "TRACE" -> {
                            requestBuilder.removeHeader("Content-Type")
                            requestBuilder.method("TRACE", null)
                        }

                        "CONNECT" -> {
                            val emptyConnectBody = ByteArray(0).toRequestBody(null)
                            requestBuilder.method("CONNECT", emptyConnectBody)
                        }
                    }



                    val startTime = System.currentTimeMillis()

                    client.newCall(requestBuilder.build()).execute().use { response ->
                        val duration = System.currentTimeMillis() - startTime

                        val code = response.code
                        val protocol = response.protocol
                        val isHttps = response.request.url.isHttps


                        val body = response.body?.string() ?: ""


                        val bodySizeInBytes = body.toByteArray(Charsets.UTF_8).size
                        val sizeText = if (bodySizeInBytes >= 1024) {
                            String.format("%.2f KB", bodySizeInBytes / 1024.0)
                        } else {
                            "$bodySizeInBytes Bytes"
                        }


                        val protocolLine = "${protocol.toString().uppercase()} $code ${response.message}"
                        val headers = protocolLine + "\n" + response.headers.joinToString("\n") { "${it.first}: ${it.second}" }

                        val cookiesList = response.headers("Set-Cookie")
                        val cookies = if (cookiesList.isNotEmpty()) {
                            cookiesList.joinToString("\n")
                        } else {
                            context.getString(R.string.cookies_empty)
                        }

                        withContext(Dispatchers.Main) {

                            resText = "HTTP $code (${duration}ms, $sizeText)"

                            safeText = if (isHttps) {
                                context.getString(R.string.status_ssl)
                            } else {
                                context.getString(R.string.status_http)
                            }

                            responseBodyText = body
                            responseHeadersText = headers
                            responseCookiesText = cookies
                            lastValidUrl = fullUrl
                        }
                    }

                } catch (_: IllegalArgumentException) {
                    withContext(Dispatchers.Main) {
                        resText = context.getString(R.string.status_error)
                        safeText = context.getString(R.string.status_invalid)
                    }
                } catch (_: java.io.IOException) {
                    withContext(Dispatchers.Main) {
                        resText = context.getString(R.string.status_error)
                        safeText = context.getString(R.string.status_no_server)
                    }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) {
                        resText = context.getString(R.string.status_error)
                        safeText = context.getString(R.string.status_error)
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading = false
                    }
                }
            }
        }
    }


    var dragAmountSum by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.bgPrimary)
            .pointerInput(currentView) {
                detectHorizontalDragGestures(
                    onDragStart = { dragAmountSum = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        dragAmountSum += dragAmount
                    },
                    onDragEnd = {
                        val threshold = 50f
                        if (dragAmountSum > threshold) {
                            if (currentView == "main") {
                                switchView("welcome")
                            }
                        } else if (dragAmountSum < -threshold) {
                            if (currentView == "welcome") {
                                switchView("main")
                            }
                        }
                    }
                )
            }
    ) {
        val context = LocalContext.current
        var lastBackPressTime by remember { mutableStateOf(0L) }


        val exitSwipeText = stringResource(id = R.string.exit_swipe)


        BackHandler(enabled = true) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastBackPressTime < 2000) {
                (context as? android.app.Activity)?.finish()
            } else {

                Toast.makeText(
                    context,
                    exitSwipeText,
                    Toast.LENGTH_SHORT
                ).show()

                lastBackPressTime = currentTime
            }
        }


        if (currentView == "welcome") {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 8.dp, end = 24.dp)
                ) {
                    IconButton(
                        onClick = { isInfoDialogVisible = true },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Показать меню",
                            tint = palette.textSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(320.dp)
                        .align(Alignment.Center)
                ) {


                    Spacer(modifier = Modifier.weight(0.3f))




                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(palette.textPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_gs_ht),
                            contentDescription = "Аватар",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }



                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        } else if (currentView == "main") {

            val methodsList = listOf(
                "GET",
                "POST",
                "HEAD",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS",
                "TRACE",
                "CONNECT"
            )
            val pagerState = rememberPagerState(
                pageCount = { methodsList.size }
            )



            LaunchedEffect(pagerState.currentPage) {
                selectedMethod = methodsList[pagerState.currentPage]
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { isHistorySheetOpen = true }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = palette.textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = { isSettingsScreenOpen = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = palette.textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .width(320.dp)
                        .align(Alignment.TopCenter)
                        .padding(top = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = Color.Transparent,
                        contentColor = palette.textPrimary,
                        edgePadding = 0.dp,
                        indicator = { tabPositions ->
                            if (pagerState.currentPage < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                    color = palette.textPrimary,
                                    height = 3.dp
                                )

                            }
                        },
                        divider = {}
                    ) {
                        methodsList.forEachIndexed { index, method ->
                            val isSelected = pagerState.currentPage == index
                            val scope = rememberCoroutineScope()
                            Tab(
                                selected = isSelected,
                                onClick = {

                                    scope.launch { pagerState.animateScrollToPage(index) }
                                },
                                text = {
                                    Text(
                                        text = method,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) palette.textPrimary else palette.textSecondary
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalPager(
                        state = pagerState,
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) { page ->
                        val currentMethodForPage = methodsList[page]

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 0.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {


                            TextField(
                                value = urlInput,
                                onValueChange = { urlInput = it },
                                placeholder = {
                                    Text(
                                        stringResource(R.string.placeholder_url),
                                        color = palette.textSecondary.copy(alpha = 0.7f)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = palette.textPrimary,
                                    unfocusedIndicatorColor = palette.line,
                                    focusedTextColor = palette.textPrimary,
                                    unfocusedTextColor = palette.textPrimary
                                )
                            )



                            Spacer(modifier = Modifier.height(16.dp))


                            if (currentMethodForPage != "CONNECT") {
                                TextField(
                                    value = requestHeadersInput,
                                    onValueChange = { requestHeadersInput = it },
                                    placeholder = {
                                        Text(
                                            "Headers (Key: Value на новой строке)",
                                            color = palette.textSecondary.copy(alpha = 0.7f)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = palette.textPrimary,
                                        unfocusedIndicatorColor = palette.line,
                                        focusedTextColor = palette.textPrimary,
                                        unfocusedTextColor = palette.textPrimary
                                    )
                                )
                            }


                            val methodsWithBody = listOf("POST", "PUT", "PATCH", "DELETE")
                            if (currentMethodForPage in methodsWithBody) {
                                Spacer(modifier = Modifier.height(16.dp))
                                TextField(
                                    value = requestBodyInput,
                                    onValueChange = { requestBodyInput = it },
                                    placeholder = {
                                        Text(
                                            "Request Body (JSON / Text)",
                                            color = palette.textSecondary.copy(alpha = 0.7f)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 5,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = palette.textPrimary,
                                        unfocusedIndicatorColor = palette.line,
                                        focusedTextColor = palette.textPrimary,
                                        unfocusedTextColor = palette.textPrimary
                                    )
                                )
                            }


                            if (!isLoading && resText.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = resText,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = resTextColor,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = safeText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = safeTextColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }


                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    if (!isLoading && lastValidUrl.isNotEmpty()) {
                        Button(
                            onClick = {
                                try {
                                    val browserIntent =
                                        Intent(Intent.ACTION_VIEW, lastValidUrl.toUri())
                                    context.startActivity(browserIntent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.bgElevated,
                                contentColor = palette.textPrimary
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.width(320.dp).height(56.dp)
                                .border(1.dp, palette.line, RoundedCornerShape(16.dp))
                        ) {
                            Text(
                                stringResource(R.string.btn_open_browser),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Button(
                            onClick = { isResponseInspectorSheetOpen = true },
                            modifier = Modifier.width(320.dp).height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.bgSecondary,
                                contentColor = palette.textPrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                stringResource(R.string.btn_search_data),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (isLoading) {
                        Box(
                            modifier = Modifier.height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = palette.textPrimary,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    } else {
                        AnimatedButton(
                            text = stringResource(R.string.btn_scan),
                            textColor = palette.bgPrimary,
                            bgColor = palette.textPrimary,
                            scale = scanScale,
                            onPressDown = { isScanPressed = true },
                            onPressUp = { isScanPressed = false }
                        ) { runScan() }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(32.dp))
                    .background(
                        color = palette.bgSecondary,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(1.dp, palette.line, RoundedCornerShape(32.dp))
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isHomeSelected = currentView == "welcome"
                Button(
                    onClick = { switchView("welcome") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHomeSelected) palette.bgElevated else Color.Transparent,
                        contentColor = if (isHomeSelected) palette.textPrimary else palette.textSecondary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.nav_home),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                val isScannerSelected = currentView == "main"
                Button(
                    onClick = { switchView("main") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isScannerSelected) palette.bgElevated else Color.Transparent,
                        contentColor = if (isScannerSelected) palette.textPrimary else palette.textSecondary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.nav_scanner),

                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        val uriHandler = LocalUriHandler.current

        if (isInfoDialogVisible) {
            InfoBottomSheetDialog(
                version = "1.0.7",
                sheetState = infoSheetState,
                bgColor = palette.bgSecondary,
                textColorPrimary = palette.textPrimary,
                textColorSecondary = palette.textSecondary,
                lineColor = palette.line,
                uriHandler = uriHandler,
                onDismiss = {
                    isInfoDialogVisible = false
                }
            )
        }



        AnimatedVisibility(
            visible = isSettingsScreenOpen,

            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + slideInHorizontally(
                animationSpec = tween(durationMillis = 300),
                initialOffsetX = { fullWidth -> fullWidth }
            ),

            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutHorizontally(
                animationSpec = tween(durationMillis = 300),
                targetOffsetX = { fullWidth -> fullWidth }
            )
        ) {

            BackHandler(enabled = isSettingsScreenOpen) {
                isSettingsScreenOpen = false
            }

            SettingsScreen(
                version = "1.0.7",
                bgColor = palette.bgSecondary,
                textColorPrimary = palette.textPrimary,
                textColorSecondary = palette.textSecondary,
                lineColor = palette.line,
                uriHandler = LocalUriHandler.current,
                themeSetting = themeSetting,
                onBackClick = { isSettingsScreenOpen = false },
                onThemeClick = { isThemeDialogOpen = true },
                onLanguageClick = { isLanguageDialogOpen = true },
                onClearHistory = { searchHistory = emptySet() }
            )
        }


        if (isHistorySheetOpen) {
            HistorySheetOpenDialog(
                version = "1.0.7",
                historySheetState = historySheetState,
                searchHistory = searchHistory.toList(),
                bgColor = palette.bgSecondary,
                textColorPrimary = palette.textPrimary,
                textColorSecondary = palette.textSecondary,
                lineColor = palette.line,
                onUrlSelected = { selectedUrl ->
                    urlInput = selectedUrl
                },
                onClearHistory = {
                    searchHistory = emptySet()
                    urlInput = ""
                    resText = ""
                    safeText = ""
                    searchHistory = emptySet()
                },
                onDismiss = { isHistorySheetOpen = false }
            )
        }


        if (isResponseInspectorSheetOpen) {
            ResponseInspectorSheet(
                version = "1.0.7",
                sheetState = sheetState,
                bgColor = palette.bgSecondary,
                textColorPrimary = palette.textPrimary,
                textColorSecondary = palette.textSecondary,
                lineColor = palette.line,
                lastValidUrl = lastValidUrl,
                responseBodyText = responseBodyText,
                responseHeadersText = responseHeadersText,
                responseCookiesText = responseCookiesText,
                currentLanguage = currentLanguage,
                onDismiss = { isResponseInspectorSheetOpen = false }
            )
        }


        fun handleThemeChange(newTheme: String) {
            try {
                onThemeChange(newTheme)
                saveSetting("theme_setting", newTheme)
                isThemeDialogOpen = false
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Ошибка при смене темы",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }




        AnimatedVisibility(
            visible = isThemeDialogOpen,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + slideInHorizontally(
                animationSpec = tween(durationMillis = 300),
                initialOffsetX = { fullWidth -> fullWidth }
            ),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutHorizontally(
                animationSpec = tween(durationMillis = 300),
                targetOffsetX = { fullWidth -> fullWidth }
            )
        ) {

            BackHandler(enabled = isThemeDialogOpen) {
                isThemeDialogOpen = false
            }

            ThemeSelectionScreen(
                themeSetting = themeSetting,
                textColorPrimary = palette.textPrimary,
                textColorSecondary = palette.textSecondary,
                containerColor = palette.bgSecondary,
                onThemeSelected = { themeCode ->
                    handleThemeChange(themeCode)
                },
                onBackClick = { isThemeDialogOpen = false }
            )
        }




        fun saveLanguage(lang: String, context: Context) {
            try {

                context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit {
                    putString("app_lang", lang)
                }


                currentLanguage = lang
                isLanguageDialogOpen = false


                if (context is android.app.Activity) {
                    context.intent?.let { intent ->
                        context.finish()
                        context.startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Ошибка смены языка",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }



        AnimatedVisibility(
            visible = isLanguageDialogOpen,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + slideInHorizontally(
                animationSpec = tween(durationMillis = 300),
                initialOffsetX = { fullWidth -> fullWidth }
            ),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutHorizontally(
                animationSpec = tween(durationMillis = 300),
                targetOffsetX = { fullWidth -> fullWidth }
            )
        ) {

            BackHandler(enabled = isLanguageDialogOpen) {
                isLanguageDialogOpen = false
            }

            LanguageSelectionScreen(
                textColorPrimary = palette.textPrimary,
                textColorSecondary = palette.textSecondary,
                containerColor = palette.bgSecondary,
                currentLanguage = currentLanguage,
                onLanguageSelected = { selectedLang ->
                    saveLanguage(selectedLang, context)
                },
                onBackClick = { isLanguageDialogOpen = false }
            )
        }
    }
}






