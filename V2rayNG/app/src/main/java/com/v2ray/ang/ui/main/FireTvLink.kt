package com.v2ray.ang.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.v2ray.ang.R
import com.v2ray.ang.util.QRCodeDecoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * FireVPN на телевизоре: подписка по QR-коду. Сайт выдаёт короткий код и секретный токен, телевизор показывает
 * QR на t.me/бот?start=tv_<код>; человек подтверждает в боте (или вводит код в кабинете на сайте), а телевизор
 * раз в несколько секунд спрашивает сайт по токену и получает адрес подписки. Бот: my-vpn/bot/webshop.py.
 */
object FireTvApi {
    private const val API = "https://fire-vpn.online/api/tv/"
    private val json = "application/json".toMediaType()
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    data class Code(val code: String, val token: String, val url: String, val expiresIn: Int)

    private fun post(path: String, body: JSONObject): JSONObject {
        val request = Request.Builder().url(API + path).post(body.toString().toRequestBody(json)).build()
        client.newCall(request).execute().use { r ->
            val text = r.body?.string().orEmpty()
            if (!r.isSuccessful) throw IllegalStateException("HTTP ${r.code}")
            return JSONObject(text)
        }
    }

    suspend fun newCode(): Code = withContext(Dispatchers.IO) {
        val o = post("new", JSONObject())
        Code(o.getString("code"), o.getString("token"), o.getString("url"), o.optInt("expires_in", 600))
    }

    /** Адрес подписки, "" — ещё ждём, null — код истёк. */
    suspend fun poll(token: String): String? = withContext(Dispatchers.IO) {
        val o = post("poll", JSONObject().put("token", token))
        when (o.optString("status")) {
            "ok" -> o.getString("sub_url")
            "wait" -> ""
            else -> null
        }
    }
}

private val Ember = Color(0xFF120305)
private val TextMain = Color(0xFFFFF3EC)
private val TextDim = Color(0xFFC9A59A)
private val Flame = Color(0xFFFF6A1F)
private val Gold = Color(0xFFFFB02E)

/** Панель «QR-код + код»: сама берёт код, ждёт подтверждения и отдаёт адрес подписки в onSubscription. */
@Composable
fun TvLinkPanel(onSubscription: (String) -> Unit, modifier: Modifier = Modifier, onClose: (() -> Unit)? = null) {
    var code by remember { mutableStateOf<FireTvApi.Code?>(null) }
    var failed by remember { mutableStateOf(false) }
    var attempt by remember { mutableIntStateOf(0) }
    val retryFocus = remember { FocusRequester() }

    LaunchedEffect(attempt) {
        failed = false
        code = null
        var errors = 0
        while (true) {
            val c = runCatching { FireTvApi.newCode() }.getOrNull()
            if (c == null) { failed = true; return@LaunchedEffect }
            code = c
            val until = System.currentTimeMillis() + (c.expiresIn - 15) * 1000L
            while (System.currentTimeMillis() < until) {
                delay(3000)
                val sub = runCatching { FireTvApi.poll(c.token) }
                    .onFailure { errors++ }.onSuccess { errors = 0 }.getOrDefault("")
                if (errors >= 10) { failed = true; return@LaunchedEffect }
                if (sub == null) break               // код истёк — берём новый
                if (sub.isNotEmpty()) { onSubscription(sub); return@LaunchedEffect }
            }
        }
    }

    Row(
        modifier = modifier.padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(36.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (failed) {
            Image(painterResource(R.drawable.firevpn_flame), contentDescription = null, modifier = Modifier.size(200.dp))
        } else Box(
            modifier = Modifier.size(260.dp).clip(RoundedCornerShape(18.dp)).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            val c = code
            val qr = remember(c?.url) { c?.let { QRCodeDecoder.createQRCode(it.url, 600) } }
            if (qr != null) Image(qr.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize().padding(12.dp))
            else CircularProgressIndicator(color = Flame)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(stringResource(R.string.firevpn_tv_title), color = TextMain, fontSize = 30.sp, fontWeight = FontWeight.Black,
                lineHeight = 34.sp)
            if (failed) {
                Text(stringResource(R.string.firevpn_tv_error), color = TextDim, fontSize = 18.sp)
                Button(
                    onClick = { attempt++ },
                    colors = ButtonDefaults.buttonColors(containerColor = Flame),
                    modifier = Modifier.focusRequester(retryFocus).tvFocus(RoundedCornerShape(24.dp))
                ) { Text(stringResource(R.string.firevpn_tv_retry), fontSize = 18.sp) }
                LaunchedEffect(Unit) { runCatching { retryFocus.requestFocus() } }
            } else {
                Text(stringResource(R.string.firevpn_tv_scan), color = TextDim, fontSize = 18.sp, lineHeight = 24.sp)
                Text(stringResource(R.string.firevpn_tv_or_code), color = TextDim, fontSize = 16.sp)
                Text(code?.code?.chunked(3)?.joinToString(" ") ?: "··· ···", color = Gold, fontSize = 44.sp,
                    fontWeight = FontWeight.Black, letterSpacing = 6.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = Flame, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.firevpn_tv_wait), color = TextDim, fontSize = 15.sp)
                }
            }
            if (onClose != null) {
                Spacer(Modifier.height(4.dp))
                val closeFocus = remember { FocusRequester() }
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A0B0D)),
                    modifier = Modifier.focusRequester(closeFocus).tvFocus(RoundedCornerShape(24.dp))
                ) { Text(stringResource(R.string.firevpn_tv_close), color = TextMain) }
                if (!failed) LaunchedEffect(Unit) { runCatching { closeFocus.requestFocus() } }
            }
        }
    }
}

/** Окно поверх главного экрана (кнопка «Добавить подписку» на телевизоре). */
@Composable
fun TvLinkDialog(onSubscription: (String) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(28.dp)).background(Ember),
            contentAlignment = Alignment.Center
        ) {
            TvLinkPanel(onSubscription = onSubscription, onClose = onDismiss, modifier = Modifier.fillMaxWidth())
        }
    }
}
