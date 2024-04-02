package xyz.teamgravity.pulltorefreshdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.teamgravity.pulltorefreshdemo.ui.theme.PullToRefreshDemoTheme
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {

    private val items: List<String> by lazy {
        buildList {
            repeat(100) { index ->
                add(getString(R.string.your_item, index))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PullToRefreshDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val scope = rememberCoroutineScope()
                    var refreshing by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        PullToRefresh(
                            refreshing = refreshing,
                            onRefresh = {
                                scope.launch {
                                    refreshing = true
                                    delay(3.seconds)
                                    refreshing = false
                                }
                            }
                        ) {
                            LazyColumn(
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = items,
                                    key = { it }
                                ) { item ->
                                    Text(
                                        text = item,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    refreshing = true
                                },
                                modifier = Modifier.align(Alignment.BottomCenter)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.refresh)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PullToRefresh(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(
            key1 = Unit,
            block = {
                onRefresh()
            }
        )
    }

    LaunchedEffect(
        key1 = refreshing,
        block = {
            if (refreshing) pullToRefreshState.startRefresh() else pullToRefreshState.endRefresh()
        }
    )

    Box(
        modifier = Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        content()
    }
}