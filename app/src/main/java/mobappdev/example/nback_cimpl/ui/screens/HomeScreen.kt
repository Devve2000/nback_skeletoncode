package mobappdev.example.nback_cimpl.ui.screens

import android.content.res.Configuration
import android.graphics.fonts.FontStyle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.R
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType

/**
 * This is the Home screen composable
 *
 * Currently this screen shows the saved highscore
 * It also contains a button which can be used to show that the C-integration works
 * Furthermore it contains two buttons that you can use to start a game
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */

@Composable
fun HomeScreen(
    vm: GameViewModel,
    navController: NavController
) {
    val highscore by vm.highscore.collectAsState()  // Highscore is its own StateFlow
    val gameState by vm.gameState.collectAsState()
    val scope = rememberCoroutineScope()
    val orientation = LocalConfiguration.current.orientation

    Scaffold(
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(32.dp),
                text = "High-Score = $highscore",
                style = MaterialTheme.typography.headlineLarge
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Text(
                        text =
                        "Current game options:" +
                                "\nSize: ${vm.size}" +
                                "\nCombinations: ${vm.combinations}" +
                                "\nnBack: ${vm.nBack}" +
                                "\nEvent interval: ${vm.eventInterval}",
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //If the button is pressed the selected game starts
                    Button(
                        onClick = {
                            navController.navigate("visualgamescreen")
                            scope.launch {
                                vm.startGame()
                            }
                        }
                    ) {
                        Text(
                            text = "Start game",
                            fontSize = 32.sp,
                        )
                    }

                    if(orientation == Configuration.ORIENTATION_PORTRAIT){
                        Text(
                            text =
                            "Current game options:" +
                                    "\nSize: ${vm.size}" +
                                    "\nCombinations: ${vm.combinations}" +
                                    "\nnBack: ${vm.nBack}" +
                                    "\nEvent interval: ${vm.eventInterval}",
                            fontSize = 24.sp,
                            lineHeight = 24.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight()
                            .wrapContentHeight(Alignment.Bottom),
                        text = "Select game type".uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                    )
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                            vm.setGameType(GameType.Audio)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.sound_on),
                        contentDescription = "Sound",
                        tint = (if (gameState.gameType == GameType.Audio) Color.Red else Color.Unspecified),
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f)
                    )
                }
                Button(
                    onClick = {
                            vm.setGameType(GameType.Visual)
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.visual),
                        contentDescription = "Visual",
                        tint = (if (gameState.gameType == GameType.Visual) Color.Red else Color.Unspecified),
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f)
                    )
                }
            }
        }
    }
}

/*
@Preview
@Composable
fun HomeScreenPreview() {
    // Since I am injecting a VM into my homescreen that depends on Application context, the preview doesn't work.
    Surface(){
        HomeScreen(FakeVM())
    }
}
*/