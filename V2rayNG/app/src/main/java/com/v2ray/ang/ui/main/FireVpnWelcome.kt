package com.v2ray.ang.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.R
import com.v2ray.ang.util.Utils

/** FireVPN: первый запуск — серверов ещё нет, объясняем, где взять ссылку-подписку. */
private const val FIREVPN_BOT_URL = "https://t.me/VpnFire57_bot"
private const val FIREVPN_CABINET_URL = "https://fire-vpn.online/my/"

@Composable
fun FireVpnWelcome(contentPadding: PaddingValues, onAction: (MainAction) -> Unit, onTvSubscription: (String) -> Unit) {
    val context = LocalContext.current
    if (rememberIsTv()) {
        // телевизор: вставить ссылку нечем — сразу QR-код, подписку подтверждают с телефона
        Box(
            modifier = Modifier.fillMaxSize().padding(contentPadding).padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) { TvLinkPanel(onSubscription = onTvSubscription, modifier = Modifier.fillMaxWidth()) }
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.firevpn_flame),
            contentDescription = null,
            modifier = Modifier.size(96.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.firevpn_welcome_text),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onAction(MainAction.ImportClipboard) },
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text(stringResource(R.string.firevpn_welcome_paste), fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = { onAction(MainAction.ImportQRcode) },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) { Text(stringResource(R.string.firevpn_welcome_scan)) }
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.firevpn_welcome_no_sub),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = { Utils.openUri(context, FIREVPN_BOT_URL) }) {
            Text(stringResource(R.string.firevpn_welcome_bot))
        }
        TextButton(onClick = { Utils.openUri(context, FIREVPN_CABINET_URL) }) {
            Text(stringResource(R.string.firevpn_welcome_site))
        }
    }
}
