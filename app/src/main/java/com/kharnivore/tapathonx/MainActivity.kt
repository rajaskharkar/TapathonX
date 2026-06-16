package com.kharnivore.tapathonx

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TapathonTheme {
                TapathonApp()
            }
        }
    }
}

private sealed class TapathonRoute(
    val route: String,
    val title: String,
) {
    object Home : TapathonRoute("home", "Tapathon")
    object SinglePlayer : TapathonRoute("single-player", "1 Player")
    object TwoPlayer : TapathonRoute("two-player", "2 Players")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TapathonApp(
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTitle = when (currentRoute) {
        TapathonRoute.SinglePlayer.route -> TapathonRoute.SinglePlayer.title
        TapathonRoute.TwoPlayer.route -> TapathonRoute.TwoPlayer.title
        else -> TapathonRoute.Home.title
    }
    val isHome = currentRoute == null || currentRoute == TapathonRoute.Home.route
    var isBackLocked by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(currentRoute) {
        isBackLocked = false
    }
    BackHandler(enabled = isBackLocked) {}

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(currentTitle) },
                navigationIcon = {
                    if (!isHome) {
                        IconButton(
                            enabled = !isBackLocked,
                            onClick = { navController.navigateHome() },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back to main screen",
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TapathonRed,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
        },
        containerColor = TapathonBackground,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TapathonRoute.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(TapathonRoute.Home.route) {
                HomeScreen(
                    onSinglePlayerClick = {
                        navController.navigateSingleTopTo(TapathonRoute.SinglePlayer.route)
                    },
                    onTwoPlayerClick = {
                        navController.navigateSingleTopTo(TapathonRoute.TwoPlayer.route)
                    },
                )
            }
            composable(TapathonRoute.SinglePlayer.route) {
                SinglePlayerScreen(
                    onBackLockedChange = { isBackLocked = it },
                )
            }
            composable(TapathonRoute.TwoPlayer.route) {
                TwoPlayerScreen(
                    onBackLockedChange = { isBackLocked = it },
                )
            }
        }
    }
}

private fun NavHostController.navigateSingleTopTo(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavHostController.navigateHome() {
    navigate(TapathonRoute.Home.route) {
        popUpTo(TapathonRoute.Home.route) {
            inclusive = false
        }
        launchSingleTop = true
    }
}

@Composable
private fun HomeScreen(
    onSinglePlayerClick: () -> Unit,
    onTwoPlayerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.tap_candy),
            contentDescription = null,
            modifier = Modifier.size(240.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.height(24.dp))
        ModeButton(text = "1 Player", onClick = onSinglePlayerClick)
        Spacer(Modifier.height(16.dp))
        ModeButton(text = "2 Players", onClick = onTwoPlayerClick)
    }
}

@Composable
private fun SinglePlayerScreen(
    onBackLockedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedSeconds by rememberSaveable { mutableStateOf(TimerOptions.first()) }
    var highScore by rememberSaveable { mutableStateOf(context.getHighScore(selectedSeconds)) }
    var tapCount by rememberSaveable { mutableStateOf(0) }
    var secondsLeft by rememberSaveable { mutableStateOf(selectedSeconds) }
    var gameState by rememberSaveable { mutableStateOf(GameState.Ready) }

    LaunchedEffect(gameState) {
        onBackLockedChange(gameState.locksBackNavigation)
    }
    DisposableEffect(Unit) {
        onDispose { onBackLockedChange(false) }
    }

    LaunchedEffect(selectedSeconds) {
        highScore = context.getHighScore(selectedSeconds)
        if (gameState == GameState.Ready || gameState == GameState.Finished) {
            secondsLeft = selectedSeconds
        }
    }

    LaunchedEffect(gameState, selectedSeconds) {
        if (gameState != GameState.Playing) return@LaunchedEffect
        secondsLeft = selectedSeconds
        while (secondsLeft > 0) {
            delay(1_000L)
            secondsLeft--
        }
        gameState = GameState.Finished
        if (tapCount > highScore) {
            highScore = tapCount
            context.saveHighScore(selectedSeconds, tapCount)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TapathonRed)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "High score: $highScore",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            TimerMenu(
                selectedSeconds = selectedSeconds,
                enabled = gameState != GameState.Playing,
                onSecondsSelected = { selectedSeconds = it },
                modifier = Modifier.width(92.dp),
            )
        }
        TapZone(
            text = singlePlayerZoneText(gameState, secondsLeft, tapCount),
            onTap = {
                if (gameState == GameState.Playing) tapCount++
            },
            modifier = Modifier.weight(1f),
        )
        ModeButton(
            text = when (gameState) {
                GameState.Ready -> "Start"
                GameState.Countdown -> "Get Ready..."
                GameState.Playing -> "Go Go Go!"
                GameState.Finished -> "Play Again"
            },
            enabled = gameState != GameState.Playing,
            onClick = {
                tapCount = 0
                secondsLeft = selectedSeconds
                gameState = GameState.Playing
            },
        )
    }
}

@Composable
private fun TwoPlayerScreen(
    onBackLockedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedSeconds by rememberSaveable { mutableStateOf(TimerOptions.first()) }
    var topTapCount by rememberSaveable { mutableStateOf(0) }
    var bottomTapCount by rememberSaveable { mutableStateOf(0) }
    var secondsLeft by rememberSaveable { mutableStateOf(selectedSeconds) }
    var countdownLeft by rememberSaveable { mutableStateOf(3) }
    var gameState by rememberSaveable { mutableStateOf(GameState.Ready) }

    LaunchedEffect(gameState) {
        onBackLockedChange(gameState.locksBackNavigation)
    }
    DisposableEffect(Unit) {
        onDispose { onBackLockedChange(false) }
    }

    LaunchedEffect(selectedSeconds) {
        if (gameState == GameState.Ready || gameState == GameState.Finished) {
            secondsLeft = selectedSeconds
        }
    }

    LaunchedEffect(gameState, selectedSeconds) {
        when (gameState) {
            GameState.Countdown -> {
                countdownLeft = 3
                while (countdownLeft > 0) {
                    delay(1_000L)
                    countdownLeft--
                }
                secondsLeft = selectedSeconds
                gameState = GameState.Playing
            }
            GameState.Playing -> {
                secondsLeft = selectedSeconds
                while (secondsLeft > 0) {
                    delay(1_000L)
                    secondsLeft--
                }
                gameState = GameState.Finished
            }
            GameState.Ready,
            GameState.Finished -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(TapathonRed),
        )
        TapZone(
            text = twoPlayerZoneText(
                gameState = gameState,
                secondsLeft = secondsLeft,
                countdownLeft = countdownLeft,
                tapCount = topTapCount,
                result = playerResult(topTapCount, bottomTapCount),
            ),
            onTap = {
                if (gameState == GameState.Playing) topTapCount++
            },
            modifier = Modifier
                .weight(1f)
                .rotate(180f),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(TapathonRed),
        )
        TapZone(
            text = twoPlayerZoneText(
                gameState = gameState,
                secondsLeft = secondsLeft,
                countdownLeft = countdownLeft,
                tapCount = bottomTapCount,
                result = playerResult(bottomTapCount, topTapCount),
            ),
            onTap = {
                if (gameState == GameState.Playing) bottomTapCount++
            },
            modifier = Modifier.weight(1f),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TapathonRed)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ModeButton(
                text = when (gameState) {
                    GameState.Ready -> "Start"
                    GameState.Countdown -> "Get Ready..."
                    GameState.Playing -> "Go Go Go!"
                    GameState.Finished -> "Play Again"
                },
                enabled = gameState == GameState.Ready || gameState == GameState.Finished,
                onClick = {
                    topTapCount = 0
                    bottomTapCount = 0
                    gameState = GameState.Countdown
                },
                modifier = Modifier.weight(1f),
            )
            TimerMenu(
                selectedSeconds = selectedSeconds,
                enabled = gameState == GameState.Ready || gameState == GameState.Finished,
                onSecondsSelected = { selectedSeconds = it },
                modifier = Modifier.width(96.dp),
            )
        }
    }
}

@Composable
private fun ModeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TapathonRed,
            contentColor = Color.White,
            disabledContainerColor = TapathonRed.copy(alpha = 0.72f),
            disabledContentColor = Color.White,
        ),
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimerMenu(
    selectedSeconds: Int,
    enabled: Boolean,
    onSecondsSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) expanded = !expanded
        },
        modifier = modifier,
    ) {
        Surface(
            color = Color.White,
            contentColor = TapathonRed,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .height(44.dp)
                .clickable(enabled = enabled) { expanded = true },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${selectedSeconds}s",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            TimerOptions.forEach { seconds ->
                DropdownMenuItem(
                    text = { Text(seconds.toString()) },
                    onClick = {
                        onSecondsSelected(seconds)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun TapZone(
    text: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = Color.White,
        contentColor = Color.Black,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap,
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Normal,
                lineHeight = 34.sp,
            )
        }
    }
}

@Composable
private fun TapathonTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = TapathonRed,
            onPrimary = Color.White,
            background = TapathonBackground,
            surface = Color.White,
        ),
        content = content,
    )
}

private fun singlePlayerZoneText(
    gameState: GameState,
    secondsLeft: Int,
    tapCount: Int,
): String = when (gameState) {
    GameState.Ready -> "Tap Zone"
    GameState.Countdown,
    GameState.Playing -> "Tap now!\nTime left: $secondsLeft\nTap count: $tapCount"
    GameState.Finished -> "Time Up!\nTapathon score: $tapCount"
}

private fun twoPlayerZoneText(
    gameState: GameState,
    secondsLeft: Int,
    countdownLeft: Int,
    tapCount: Int,
    result: PlayerResult,
): String = when (gameState) {
    GameState.Ready -> "Tap Zone"
    GameState.Countdown -> "Get Ready...\n\n$countdownLeft"
    GameState.Playing -> "Tap now!\nTime left: $secondsLeft\nTap count: $tapCount"
    GameState.Finished -> when (result) {
        PlayerResult.Win -> "You Won!\nTap count: $tapCount"
        PlayerResult.Loss -> "You Lost!\nTap count: $tapCount"
        PlayerResult.Draw -> "It's a Draw!\nTap count: $tapCount"
    }
}

private fun playerResult(playerCount: Int, opponentCount: Int): PlayerResult = when {
    playerCount > opponentCount -> PlayerResult.Win
    playerCount < opponentCount -> PlayerResult.Loss
    else -> PlayerResult.Draw
}

private fun Context.getHighScore(timerValue: Int): Int {
    return getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
        .getInt("HighScore_$timerValue", 0)
}

private fun Context.saveHighScore(timerValue: Int, highScore: Int) {
    getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
        .edit()
        .putInt("HighScore_$timerValue", highScore)
        .apply()
}

private enum class GameState {
    Ready,
    Countdown,
    Playing,
    Finished,
}

private val GameState.locksBackNavigation: Boolean
    get() = this == GameState.Countdown || this == GameState.Playing

private enum class PlayerResult {
    Win,
    Loss,
    Draw,
}

private val TimerOptions = listOf(5, 10, 15, 20, 30)
private const val PreferencesName = "Tapathon"
private val TapathonRed = Color(0xFFD21404)
private val TapathonBackground = Color(0xFFFAFAFA)

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TapathonTheme {
        HomeScreen(
            onSinglePlayerClick = {},
            onTwoPlayerClick = {},
        )
    }
}
