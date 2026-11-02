package com.v2ray.ang.extension

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.v2ray.ang.ui.compose.AppSnackbarManager
import com.v2ray.ang.ui.compose.ToastType

private val mainHandler by lazy { Handler(Looper.getMainLooper()) }
private const val SNACKBAR_RETRY_MS = 700L

private fun Context.dispatchMessage(
    message: CharSequence,
    type: ToastType,
    long: Boolean = false
) {
    val handledBySnackbar = AppSnackbarManager.show(
        message = message,
        type = type,
        long = long
    )
    if (!handledBySnackbar) {
        val appContext = applicationContext
        val duration = if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        // FireVPN: экран мог быть на паузе на долю секунды (например, системный запрос VPN) —
        // ждём его и показываем огненную плашку; не вернулся — обычное сообщение Android
        mainHandler.postDelayed({
            if (!AppSnackbarManager.show(message = message, type = type, long = long)) {
                Toast.makeText(appContext, message, duration).show()
            }
        }, SNACKBAR_RETRY_MS)
    }
}

/**
 * Shows a toast message with the given resource ID.
 *
 * @param message The resource ID of the message to show.
 * @param long Whether to display the message for a longer duration.
 */
fun Context.toast(message: Int, long: Boolean = false) {
    dispatchMessage(getString(message), ToastType.NORMAL, long)
}

/**
 * Shows a toast message with the given text.
 *
 * @param message The text of the message to show.
 * @param long Whether to display the message for a longer duration.
 */
fun Context.toast(message: CharSequence, long: Boolean = false) {
    dispatchMessage(message, ToastType.NORMAL, long)
}

/**
 * Shows a success toast message with the given resource ID.
 *
 * @param message The resource ID of the message to show.
 * @param long Whether to display the message for a longer duration.
 */
fun Context.toastSuccess(message: Int, long: Boolean = false) {
    dispatchMessage(getString(message), ToastType.SUCCESS, long)
}

/**
 * Shows a success toast message with the given text.
 *
 * @param message The text of the message to show.
 * @param long Whether to display the message for a longer duration.
 */
fun Context.toastSuccess(message: CharSequence, long: Boolean = false) {
    dispatchMessage(message, ToastType.SUCCESS, long)
}

/**
 * Shows an error toast message with the given resource ID.
 *
 * @param message The resource ID of the message to show.
 * @param long Whether to display the message for a longer duration.
 */
fun Context.toastError(message: Int, long: Boolean = false) {
    dispatchMessage(getString(message), ToastType.ERROR, long)
}

/**
 * Shows an error toast message with the given text.
 *
 * @param message The text of the message to show.
 * @param long Whether to display the message for a longer duration.
 */
fun Context.toastError(message: CharSequence, long: Boolean = false) {
    dispatchMessage(message, ToastType.ERROR, long)
}

/**
 * Shows an info toast message with the given resource ID.
 *
 * @param message The resource ID of the message to show.
 * @param long Whether to display the message for a longer duration.
 */
fun Context.toastInfo(message: Int, long: Boolean = false) {
    dispatchMessage(getString(message), ToastType.INFO, long)
}

/**
 * Shows an info toast message with the given text.
 *
 * @param message The text of the message to show.
 * @param long Whether to display the message for a longer duration.
 */
fun Context.toastInfo(message: CharSequence, long: Boolean = false) {
    dispatchMessage(message, ToastType.INFO, long)
}
