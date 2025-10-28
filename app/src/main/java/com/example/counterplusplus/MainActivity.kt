package com.example.counterplusplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class CounterViewModel : ViewModel() {

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    private val _isAutoIncrementing = MutableStateFlow(false)
    val isAutoIncrementing: StateFlow<Boolean> = _isAutoIncrementing.asStateFlow()

    private val _autoIncrementInterval = MutableStateFlow(1000L)
    val autoIncrementInterval: StateFlow<Long> = _autoIncrementInterval.asStateFlow()

    private var autoIncrementJob: Job? = null


    fun increment() {
        _count.value++
    }

    fun decrement() {
        _count.value--
    }

    fun reset() {
        _count.value = 0
    }


    fun toggleAutoIncrement() {
        _isAutoIncrementing.value = !_isAutoIncrementing.value
        startOrStopAutoIncrement()
    }

    fun setAutoIncrementInterval(intervalMillis: Long) {
        _autoIncrementInterval.value = intervalMillis
        // If auto already running, restart it with new value
        if (_isAutoIncrementing.value) {
            startOrStopAutoIncrement()
        }
    }

    private fun startOrStopAutoIncrement() {
        autoIncrementJob?.cancel() // any existing jobs will be canceled
        if (_isAutoIncrementing.value) {
            // If auto-increment is on, start a new job
            autoIncrementJob = viewModelScope.launch {
                while (true) {
                    delay(_autoIncrementInterval.value)
                    _count.value++
                }
            }
        }
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CounterAppNavigation()
                }
            }
        }
    }
}

@Composable
fun CounterAppNavigation() {
    val navController = rememberNavController()
    val viewModel: CounterViewModel = viewModel()

    NavHost(navController = navController, startDestination = "counter") {
        composable("counter") {
            CounterScreen(
                viewModel = viewModel,
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(viewModel: CounterViewModel, onNavigateToSettings: () -> Unit) {
    val count by viewModel.count.collectAsState()
    val isAuto by viewModel.isAutoIncrementing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Counter++") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // current count
            Text(
                text = "$count",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            //  auto-mode status
            Text(
                text = "Auto mode: ${if (isAuto) "ON" else "OFF"}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { viewModel.increment() }) { Text("+1") }
                Button(onClick = { viewModel.decrement() }) { Text("-1") }
                Button(onClick = { viewModel.reset() }) { Text("Reset") }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Auto-mode toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Auto-Increment")
                Switch(
                    checked = isAuto,
                    onCheckedChange = { viewModel.toggleAutoIncrement() }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: CounterViewModel, onNavigateBack: () -> Unit) {
    val intervalMillis by viewModel.autoIncrementInterval.collectAsState()
    val intervalSeconds = (intervalMillis / 1000L).toFloat()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Auto-Increment Interval",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${intervalSeconds.toInt()} seconds",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = intervalSeconds,
                onValueChange = { newValue ->
                    // Update ViewModel with  new value in ms
                    viewModel.setAutoIncrementInterval(newValue.toLong() * 1000)
                },
                valueRange = 1f..10f,
                steps = 8
            )

            Text(
                text = "Adjust auto-increment interval delay (1-10s).",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
