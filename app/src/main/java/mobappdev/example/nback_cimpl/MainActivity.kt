package mobappdev.example.nback_cimpl

import VisualGameScreen
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mobappdev.example.nback_cimpl.ui.screens.HomeScreen
import mobappdev.example.nback_cimpl.ui.theme.NBack_CImplTheme
import mobappdev.example.nback_cimpl.ui.viewmodels.GameVM
import java.util.Locale

/**
 * This is the MainActivity of the application
 *
 * Your navigation between the two (or more) screens should be handled here
 * For this application you need at least a homescreen (a start is already made for you)
 * and a gamescreen (you will have to make yourself, but you can use the same viewmodel)
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */


class MainActivity : ComponentActivity() {
    private var textToSpeech: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            NBack_CImplTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Instantiate the viewmodel
                    val gameViewModel: GameVM = viewModel(
                        factory = GameVM.Factory
                    )


                    // Initialize TextToSpeech
                    textToSpeech = TextToSpeech(this) {
                        if (it == TextToSpeech.SUCCESS) {
                            // Language selection, here using English
                            val languageResult = textToSpeech?.setLanguage(Locale.ENGLISH)
                            if (languageResult == TextToSpeech.LANG_MISSING_DATA ||
                                languageResult == TextToSpeech.LANG_NOT_SUPPORTED
                            ) {
                                // Handle language data missing or not supported
                                textToSpeech = null
                            }
                        }
                    }

                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "homescreen"
                    ){
                        composable("homescreen"){
                            HomeScreen(vm = gameViewModel, navController = navController)
                        }
                        composable("visualgamescreen"){
                            VisualGameScreen(vm = gameViewModel, navController = navController, textToSpeech = textToSpeech)
                        }
                    }
                }
            }
        }
    }
}