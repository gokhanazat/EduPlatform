package com.eduplatform.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun EduTheme(
    dark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colors = if (Build.VERSION.SDK_INT >= 31) {
        if (dark) dynamicDarkColorScheme(context)
        else dynamicLightColorScheme(context)
    } else {
        if (dark) {
            darkColorScheme(
                primary = Primary,
                secondary = Secondary,
                tertiary = Tertiary,
                surface = DarkBg,
                background = DarkBg
            )
        } else {
            lightColorScheme(
                primary = Primary,
                secondary = Secondary,
                tertiary = Tertiary,
                background = Background
            )
        }
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
