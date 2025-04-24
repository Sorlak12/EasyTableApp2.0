package com.example.easytableapp.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun EasyTableAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(primary = Color(0xFF6200EE)),
        typography = MaterialTheme.typography.copy(bodyLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)),
        content = content
    )
}