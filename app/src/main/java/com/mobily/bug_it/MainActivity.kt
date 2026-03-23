package com.mobily.bug_it

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobily.bug_it.feature_bug.data.model.BugReportPayload
import com.mobily.bug_it.feature_bug.presentation.screen.BugDetailScreen
import com.mobily.bug_it.feature_bug.presentation.screen.BugListScreen
import com.mobily.bug_it.feature_bug.presentation.screen.BugScreenContainer
import com.mobily.bug_it.feature_bug.presentation.viewmodel.BugListViewModel
import com.mobily.bug_it.feature_bug.presentation.viewmodel.BugViewModel

class MainActivity : ComponentActivity() {

    private val bugViewModel: BugViewModel by viewModels()
    private var shouldNavigateToAddBug = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        setContent {
            BugApp(bugViewModel, shouldNavigateToAddBug.value) {
                shouldNavigateToAddBug.value = false
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
    var selectedBug by remember { mutableStateOf<BugReportPayload?>(null) }
    
    // Handle conditional navigation when intent is received
    LaunchedEffect(navigateToCreate) {
        if (navigateToCreate) {
            navController.navigate("add_bug") {
                // Pop up to the start destination to ensure a clean back stack
                popUpTo(navController.graph.startDestinationId)
            }
            onNavigated()
        }
    }

    MaterialTheme {
        NavHost(navController = navController, startDestination = "bug_list") {
            composable("bug_list") {
                val listViewModel: BugListViewModel = viewModel()
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