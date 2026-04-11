package com.bajrangi.todayinhistory.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bajrangi.todayinhistory.presentation.navigation.HistoryNavGraph
import com.bajrangi.todayinhistory.presentation.theme.TodayInHistoryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            TodayInHistoryTheme {
                HistoryNavGraph()
            }
        }
    }
}
