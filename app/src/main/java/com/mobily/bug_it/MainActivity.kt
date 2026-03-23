package com.mobily.bug_it

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobily.bug_it.feature_bug.domain.model.BugReport
import com.mobily.bug_it.feature_bug.presentation.screen.BugDetailScreen
import com.mobily.bug_it.feature_bug.presentation.screen.BugListScreen
import com.mobily.bug_it.feature_bug.presentation.screen.BugScreenContainer
import com.mobily.bug_it.feature_bug.presentation.viewmodel.BugListViewModel
import com.mobily.bug_it.feature_bug.presentation.viewmodel.BugViewModel
import com.mobily.bug_it.ui.theme.BugITTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val bugViewModel: BugViewModel by viewModels()
    private var shouldNavigateToAddBug = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        setContent {
            BugITTheme {
                BugApp(bugViewModel, shouldNavigateToAddBug.value) {
                    shouldNavigateToAddBug.value = false
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        val hasImages = when (intent.action) {
            Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("image/") == true) {
                    (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { uri ->
                        bugViewModel.addImages(listOf(uri))
                        true
                    } ?: false
                } else false
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                if (intent.type?.startsWith("image/") == true) {
                    val uris = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
                    }
                    uris?.let { 
                        bugViewModel.addImages(it)
                        true
                    } ?: false
                } else false
            }
            else -> false
        }

        if (hasImages) {
            shouldNavigateToAddBug.value = true
        }
    }
}

@Composable
fun BugApp(
    bugViewModel: BugViewModel,
    navigateToCreate: Boolean,
    onNavigated: () -> Unit
) {
    val navController = rememberNavController()
    var selectedBug by remember { mutableStateOf<BugReport?>(null) }
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen { showSplash = false }
    } else {
        // Handle conditional navigation when intent is received
        LaunchedEffect(navigateToCreate) {
            if (navigateToCreate) {
                navController.navigate("add_bug") {
                    popUpTo(navController.graph.startDestinationId)
                }
                onNavigated()
            }
        }

        NavHost(navController = navController, startDestination = "bug_list") {
            composable("bug_list") {
                val listViewModel: BugListViewModel = hiltViewModel()
                val state by listViewModel.state.collectAsState()
                
                BugListScreen(
                    state = state,
                    onAddBug = { navController.navigate("add_bug") },
                    onRefresh = { listViewModel.loadBugs() },
                    onBugClick = { bug ->
                        selectedBug = bug
                        navController.navigate("bug_detail")
                    }
                )
            }
            composable("add_bug") {
                BugScreenContainer(
                    viewModel = bugViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("bug_detail") {
                selectedBug?.let { bug ->
                    BugDetailScreen(
                        bug = bug,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Bug-IT",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Icon(
                imageVector = Icons.Default.BugReport,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Report bugs instantly",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}
