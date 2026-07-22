package com.jitz.adhoni_launcher.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.jitz.adhoni_launcher.viewmodel.AppModel
import com.jitz.adhoni_launcher.viewmodel.SafeAppsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    viewModel: SafeAppsViewModel,
    onBackToLauncher: () -> Unit
) {
    val appList by viewModel.appList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parent Dashboard - Safe Apps") },
                navigationIcon = {
                    IconButton(onClick = onBackToLauncher) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = appList,
                key = { app: AppModel -> app.packageName }
            ) { app ->
                AppToggleRow(
                    app = app,
                    onToggle = { isChecked ->
                        viewModel.toggleAppPermission(app.packageName, isChecked)
                    }
                )
            }
        }
    }
}

@Composable
fun AppToggleRow(
    app: AppModel,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (app.isAllowed)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = rememberDrawablePainter(app.icon),
                    contentDescription = app.label,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = app.label,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Switch(
                checked = app.isAllowed,
                onCheckedChange = onToggle
            )
        }
    }
}