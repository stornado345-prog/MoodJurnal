package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkPurplePrimary,
    onPrimary = Color(0xFF1E1035),
    primaryContainer = Color(0xFF4A347B),
    onPrimaryContainer = SoftPurpleAccent,
    secondary = DarkPurpleSecondary,
    onSecondary = Color(0xFF141933),
    secondaryContainer = Color(0xFF333856),
    onSecondaryContainer = Color(0xFFC5CAE9),
    tertiary = SoftPurpleAccent,
    background = DarkBackground,
    onBackground = Color(0xFFE6E1E5),
    surface = DarkSurface,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = DarkCardBg,
    onSurfaceVariant = Color(0xFFCAC4D0),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SoftPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = SoftPurpleContainer,
    onPrimaryContainer = Color(0xFF32136C),
    secondary = SoftPurpleSecondary,
    onSecondary = Color(0xFF28114E),
    secondaryContainer = Color(0xFFEDE7F6),
    onSecondaryContainer = Color(0xFF32136C),
    tertiary = SoftPurpleAccent,
    background = SoftPurpleLightBg,
    onBackground = Color(0xFF1C1B1F),
    surface = SoftPurpleCardBg,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF3EDF7),
    onSurfaceVariant = Color(0xFF49454F),
  )

@Composable
fun MoodJournalTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Default to custom brand purple palette for clean consistency
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
