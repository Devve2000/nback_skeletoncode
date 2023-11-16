import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

@Composable
fun VisualGameScreen(
    vm: GameViewModel,
    navController: NavController
){
    val gameState by vm.gameState.collectAsState()
    val currentScore by vm.score.collectAsState()

    LaunchedEffect(vm) {
        vm.startGame()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(3f))

        Text(
            text = "Current score: $currentScore",
            fontSize = 40.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(5f))
        repeat(3) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { column ->
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(
                            if(gameState.eventValue == row * 3 + column + 1){
                                Color.Yellow
                            } else {
                                MaterialTheme.colorScheme.primary
                                   },
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .weight(1f)
                            .aspectRatio(1f)

                    ) {
                        Text(
                            text = "${row * 3 + column + 1}",
                            fontSize = 40.sp
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { vm.checkMatch() },
            modifier = Modifier
                .width(250.dp)
                .height(75.dp)

        ){
            Text(text = "Position Match", fontSize = 25.sp, color = Color.Black)
        }
    }
}