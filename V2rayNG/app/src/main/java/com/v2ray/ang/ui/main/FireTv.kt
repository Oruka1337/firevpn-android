package com.v2ray.ang.ui.main

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/** FireVPN на телевизоре: управление пультом (стрелки и «ОК»), без касаний и буфера обмена. */
object FireTv {
    fun isTv(context: Context): Boolean {
        val ui = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
        return ui?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION ||
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }
}

@Composable
fun rememberIsTv(): Boolean {
    val context = LocalContext.current
    return remember { FireTv.isTv(context) }
}

private val FocusRing = Color(0xFFFFE08A)

/**
 * Выбранный пультом элемент — светлая рамка и чуть крупнее. Ставить в начало цепочки, до clickable:
 * пальцем на телефоне фокус не появляется, так что там ничего не меняется.
 */
fun Modifier.tvFocus(shape: Shape): Modifier = composed {
    var focused by remember { mutableStateOf(false) }
    this
        .onFocusChanged { focused = it.hasFocus }
        .then(if (focused) Modifier.scale(1.04f).border(3.dp, FocusRing, shape) else Modifier)
}
