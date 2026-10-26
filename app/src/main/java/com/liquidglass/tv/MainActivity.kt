package com.liquidglass.tv

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.painter.BitmapPainter
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import com.liquidglass.tv.ui.theme.*

data class AppEntry(
    val label: String,
    val packageName: String,
    val icon: Drawable
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiquidGlassHome()
        }
    }
}

@Composable
fun LiquidGlassHome() {
    val context = LocalContext.current
    val apps = remember { loadTvApps(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Декоративные "стеклянные" пятна света на фоне — часть эффекта Liquid Glass
        GlassBlob(offsetX = 80.dp, offsetY = 40.dp, size = 260.dp, color = NebulaTeal)
        GlassBlob(offsetX = 900.dp, offsetY = 300.dp, size = 320.dp, color = NebulaPurple)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 56.dp, top = 40.dp, end = 56.dp, bottom = 40.dp)
        ) {
            Text(
                text = "Liquid Glass TV",
                color = TextPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(apps) { app ->
                    GlassAppCard(app = app) {
                        val launchIntent =
                            context.packageManager.getLeanbackLaunchIntentForPackage(app.packageName)
                                ?: context.packageManager.getLaunchIntentForPackage(app.packageName)
                        launchIntent?.let { context.startActivity(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun GlassBlob(offsetX: androidx.compose.ui.unit.Dp, offsetY: androidx.compose.ui.unit.Dp, size: androidx.compose.ui.unit.Dp, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(size)
            .blur(120.dp) // создаёт мягкое свечение — ключевой элемент "стеклянного" стиля
            .clip(RoundedCornerShape(50))
            .background(color)
    )
}

@Composable
fun GlassAppCard(app: AppEntry, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .width(180.dp)
            .onFocusChanged { focused = it.isFocused }
            .clip(RoundedCornerShape(24.dp))
            .background(if (focused) GlassSurfaceFocused else GlassSurface)
            .border(
                width = if (focused) 2.dp else 1.dp,
                color = if (focused) GlassHighlight else GlassBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            val bitmap = remember(app.packageName) { drawableToBitmap(app.icon) }
            Image(
                painter = BitmapPainter(bitmap.asImageBitmap()),
                contentDescription = app.label
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = app.label,
            color = TextPrimary,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}

// Получаем список приложений, у которых есть "баннер"/запуск для TV
fun loadTvApps(context: android.content.Context): List<AppEntry> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
    }
    val resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
    return resolveInfos.map {
        AppEntry(
            label = it.loadLabel(pm).toString(),
            packageName = it.activityInfo.packageName,
            icon = it.loadIcon(pm)
        )
    }.sortedBy { it.label }
}

fun drawableToBitmap(drawable: Drawable): Bitmap {
    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
