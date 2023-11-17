import android.content.res.Configuration
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mobappdev.example.nback_cimpl.ui.viewmodels.GameState
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

@Composable
fun VisualGameScreen(
    vm: GameViewModel,
    navController: NavController,
    textToSpeech: TextToSpeech?
){
    val gameState by vm.gameState.collectAsState()
    val currentScore by vm.score.collectAsState()
    val orientation = LocalConfiguration.current.orientation

    LaunchedEffect(vm) {
        vm.startGame()
    }

    if (gameState.gameType == GameType.Audio) {
        LaunchedEffect(gameState.eventValue) {
            val asciiText =
                gameState.eventValue.toChar().toString() // Convert the eventValue to ASCII
            speak(asciiText, textToSpeech)
        }
    }



    //HERE IS PORTRAIT MODE
if(orientation == Configuration.ORIENTATION_PORTRAIT) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        GenerateTopText(currentScore = currentScore, fontSize = 50,
            Modifier
                .padding(8.dp)
        )

        if (gameState.gameType == GameType.Visual) {
            GenerateMatrix(
                gameState = gameState,
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .aspectRatio(1f)
                    .fillMaxWidth(1f)
            )
        }

        GeneratePositionMatchButton(vm,
            Modifier
                .padding(8.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
        )

    }
}
else



 //   HERE IS LANDSCAPE MODE
{
    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        GenerateTopText(currentScore = currentScore, fontSize = 30,
            Modifier
                .padding(8.dp)
        )

        if (gameState.gameType == GameType.Visual) {
            GenerateMatrix(
                gameState = gameState,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            )
        }


        if (gameState.gameType == GameType.Audio) {
            LaunchedEffect(gameState.eventValue) {
                val asciiText =
                    gameState.eventValue.toChar().toString() // Convert the eventValue to ASCII
                speak(asciiText, textToSpeech)
            }
        }

        GeneratePositionMatchButton(vm,
            Modifier
                .padding(8.dp)
                .fillMaxHeight(0.5f)
                .clip(RoundedCornerShape(64.dp))
        )
}
}
}

@Composable
private fun GenerateTopText(currentScore: Int, fontSize: Int, modifier: Modifier){
    Text(
        text = "Current score: $currentScore",
        fontSize = fontSize.sp,
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
private fun GenerateMatrix(gameState: GameState, modifier: Modifier) {

        // Creation of the matrix.
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = modifier
                .padding(8.dp)
        ) {
            items(9) { index ->
                Box(
                    modifier = modifier
                        .padding(8.dp)
                        //.aspectRatio(1f)
                        .background(
                            shape = CircleShape,
                            color = if (gameState.eventValue == index + 1) {
                                Color.Yellow
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                )
            }
        }
    }

@Composable
private fun GeneratePositionMatchButton(vm: GameViewModel, modifier: Modifier){
    Button(
        onClick = { vm.checkMatch() },
        modifier = modifier

    ){
        Text(
            text = "Position Match",
            fontSize = 30.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp

        )
    }
}

// Function to speak the provided text using TextToSpeech
private fun speak(text: String?, textToSpeech: TextToSpeech?) {
    textToSpeech?.let {
        if (text != null && text.isNotBlank()) {
            // Speak the text
            it.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
}