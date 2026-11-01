package com.v2ray.ang.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.v2ray.ang.R
import com.v2ray.ang.dto.GroupMapItem
import com.v2ray.ang.util.Utils
import java.text.DateFormat
import java.util.Date
import java.util.Locale

/**
 * FireVPN: простой главный экран — «Пригласи друзей» с фениксом, карточка подписки (срок, трафик,
 * сервер, кабинет) и большая кнопка «Подключить». Полный список профилей v2rayNG — по кнопке «Все профили».
 */
private val Ember = Color(0xFF120305)
private val Card = Color(0xFF1A0608)
private val Tile = Color(0xFF220A0C)
private val Flame = Color(0xFFFF6A1F)
private val FireRed = Color(0xFFE8203C)
private val Gold = Color(0xFFFFB02E)
private val TextMain = Color(0xFFFFF3EC)
private val TextDim = Color(0xFFC9A59A)
private const val INVITE_URL = "https://t.me/VpnFire57_bot?start=invite"
private const val CABINET_URL = "https://fire-vpn.online/my/"

/** Рамка с огненным свечением, как на макете. */
private fun Modifier.fireGlow(shape: Shape, glow: Dp = 14.dp, strong: Boolean = false): Modifier = this
    .shadow(glow, shape, ambientColor = Flame, spotColor = Flame)
    .border(
        BorderStroke(if (strong) 2.dp else 1.5.dp, Brush.verticalGradient(listOf(Gold, Flame, FireRed))),
        shape
    )

@Composable
fun FireVpnHome(
    contentPadding: PaddingValues,
    mainViewModel: MainViewModel,
    groups: List<GroupMapItem>,
    selectedGuid: String?,
    isRunning: Boolean,
    statusText: String,
    onAction: (MainAction) -> Unit,
    onShowAllProfiles: () -> Unit,
) {
    // подписка FireVPN — группа с данными подписки, иначе выбранная/первая
    val group = groups.firstOrNull { it.usageInfo != null } ?: groups.first()
    val servers = mainViewModel.serverGroupState(group.id).collectAsStateWithLifecycle().value.servers
    val current = servers.firstOrNull { it.guid == selectedGuid }
    var showPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ember)
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InviteCard()
        SubscriptionCard(
            group = group,
            serverName = current?.profile?.remarks,
            onRefresh = { onAction(MainAction.UpdateSubscriptions) },
            onSwap = { showPicker = true },
        )
        ConnectButton(isRunning = isRunning, onClick = { onAction(MainAction.ToggleService) })
        Text(
            text = statusText,
            color = TextDim,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isRunning) { onAction(MainAction.TestCurrentServer) }
        )
        Text(
            text = stringResource(R.string.firevpn_all_profiles),
            color = TextDim,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowAllProfiles() }
                .padding(vertical = 6.dp)
        )
        Spacer(Modifier.navigationBarsPadding())
    }

    if (showPicker) {
        ServerPicker(
            servers = servers.map { it.guid to it.profile.remarks },
            selectedGuid = selectedGuid,
            onPick = { guid ->
                showPicker = false
                onAction(MainAction.SelectServer(guid))
            },
            onDismiss = { showPicker = false }
        )
    }
}

@Composable
private fun InviteCard() {
    val context = LocalContext.current
    val shape = RoundedCornerShape(28.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fireGlow(shape)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF0E0204), Color(0xFF1C0507)))),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(22.dp))
        Text(
            text = stringResource(R.string.firevpn_invite_title),
            color = TextMain,
            fontSize = 34.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.firevpn_invite_sub_start))
                withStyle(SpanStyle(color = Gold, fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.firevpn_invite_sub_bonus))
                }
                append(stringResource(R.string.firevpn_invite_sub_end))
            },
            color = TextMain,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Image(
            painter = painterResource(R.drawable.firevpn_phoenix),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            // птица пониже, чтобы «Подключить» помещалась на экран, как на макете
            modifier = Modifier.fillMaxWidth().height(210.dp)
        )
        GlowButton(
            text = stringResource(R.string.firevpn_invite_button),
            filled = false,
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 18.dp),
            onClick = { Utils.openUri(context, INVITE_URL) }
        )
    }
}

@Composable
private fun SubscriptionCard(
    group: GroupMapItem,
    serverName: String?,
    onRefresh: () -> Unit,
    onSwap: () -> Unit,
) {
    val context = LocalContext.current
    val info = group.usageInfo
    val now = System.currentTimeMillis() / 1000
    val active = info != null && (info.expire == 0L || info.expire > now)
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fireGlow(shape, glow = 10.dp)
            .clip(shape)
            .background(Card)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = info?.title?.takeIf { it.isNotBlank() } ?: group.remarks.ifBlank { "FireVPN" },
                color = TextMain,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (!serverName.isNullOrBlank()) {
                Spacer(Modifier.width(10.dp))
                Text(
                    text = serverName,
                    color = Flame,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(Modifier.weight(1f))
            }
            IconButton(onClick = onRefresh) {
                Icon(painterResource(R.drawable.ic_refresh_24dp), contentDescription = null, tint = Flame)
            }
            IconButton(onClick = onSwap) {
                Icon(painterResource(R.drawable.ic_firevpn_swap_24dp), contentDescription = null, tint = Flame)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoTile(
                label = stringResource(if (active) R.string.firevpn_sub_active else R.string.firevpn_sub_inactive),
                value = when {
                    info == null -> "—"
                    info.expire == 0L -> stringResource(R.string.firevpn_sub_forever)
                    else -> stringResource(
                        R.string.firevpn_sub_until,
                        DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(Date(info.expire * 1000))
                    )
                },
                modifier = Modifier.weight(1f)
            )
            InfoTile(
                label = stringResource(R.string.firevpn_sub_traffic),
                value = if (info == null) "—" else
                    "${bytes(info.upload + info.download)} / ${if (info.total > 0) bytes(info.total) else "∞"}",
                modifier = Modifier.weight(1f)
            )
        }
        GlowButton(
            text = stringResource(R.string.firevpn_sub_cabinet),
            filled = false,
            onClick = { Utils.openUri(context, info?.webPageUrl ?: CABINET_URL) }
        )
    }
}

@Composable
private fun InfoTile(label: String, value: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(Tile)
            .border(1.dp, Color(0x33FF6A1F), shape)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(label, color = Flame, fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = TextMain, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1,
            overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun GlowButton(text: String, filled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .fireGlow(shape, glow = 8.dp)
            .clip(shape)
            .background(
                if (filled) Brush.horizontalGradient(listOf(Flame, FireRed))
                else Brush.verticalGradient(listOf(Color(0xFF2A0B0D), Color(0xFF1A0608)))
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = TextMain, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ConnectButton(isRunning: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(26.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .fireGlow(shape, glow = if (isRunning) 24.dp else 14.dp, strong = true)
            .clip(shape)
            .background(
                if (isRunning) Brush.horizontalGradient(listOf(Flame, FireRed))
                else Brush.verticalGradient(listOf(Color(0xFF2A0B0D), Color(0xFF160406)))
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(if (isRunning) R.string.firevpn_disconnect else R.string.firevpn_connect),
            color = TextMain,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServerPicker(
    servers: List<Pair<String, String>>,
    selectedGuid: String?,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Card) {
        Text(
            text = stringResource(R.string.firevpn_pick_server),
            color = TextMain,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        LazyColumn(modifier = Modifier.padding(bottom = 24.dp)) {
            items(servers) { (guid, name) ->
                val selected = guid == selectedGuid
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPick(guid) }
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(name, color = if (selected) Flame else TextMain, fontSize = 17.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f))
                    if (selected) Box(Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Flame))
                }
            }
        }
    }
}

private fun bytes(n: Long): String {
    val units = listOf("B", "KB", "MB", "GB", "TB")
    var v = n.toDouble()
    var i = 0
    while (v >= 1024 && i < units.lastIndex) { v /= 1024; i++ }
    return if (i == 0) "$n B" else String.format(Locale.getDefault(), "%.1f %s", v, units[i])
}
