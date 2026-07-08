package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.IptvDashboard
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      // Force darkTheme = true for the ultimate high-end IPTV cinema experience
      MyApplicationTheme(darkTheme = true) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          Scaffold(
            modifier = Modifier
              .fillMaxSize()
              .safeDrawingPadding()
          ) { innerPadding ->
            IptvDashboard(modifier = Modifier.fillMaxSize())
          }
        }
      }
    }
  }
}
