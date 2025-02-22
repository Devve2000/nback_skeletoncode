package mobappdev.example.nback_cimpl.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.GameApplication
import mobappdev.example.nback_cimpl.NBackHelper
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository

/**
 * This is the GameViewModel.
 *
 * It is good practice to first make an interface, which acts as the blueprint
 * for your implementation. With this interface we can create fake versions
 * of the viewmodel, which we can use to test other parts of our app that depend on the VM.
 *
 * Our viewmodel itself has functions to start a game, to specify a gametype,
 * and to check if we are having a match
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */


interface GameViewModel {
    val gameState: StateFlow<GameState>
    val score: StateFlow<Int>
    val highscore: StateFlow<Int>
    val currentIndex: StateFlow<Int>
    val nBack: Int
    val size: Int
    val combinations: Int
    val eventInterval: Long

    fun setGameType(gameType: GameType)
    fun startGame()

    fun checkMatch()
}

class GameVM(
    private val userPreferencesRepository: UserPreferencesRepository
): GameViewModel, ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    override val gameState: StateFlow<GameState>
        get() = _gameState.asStateFlow()

    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int>
        get() = _score

    private val _highscore = MutableStateFlow(0)
    override val highscore: StateFlow<Int>
        get() = _highscore

    private val _currentIndex = MutableStateFlow(0)
    override val currentIndex: StateFlow<Int>
        get() = _currentIndex


    // nBack is currently hardcoded
    override val nBack: Int = 2
    override val size: Int = 10
    override val combinations: Int = 9


    private var job: Job? = null  // coroutine job for the game event
    override val eventInterval: Long = 2500L  // 2500 ms (2.5s)

    private val nBackHelper = NBackHelper()  // Helper that generate the event array
    private var events = emptyArray<Int>()  // Array with all events

    private var eventPoints = IntArray(0)


    override fun setGameType(gameType: GameType) {
        // update the gametype in the gamestate
        _gameState.value = _gameState.value.copy(gameType = gameType)
    }

    override fun startGame() {
        job?.cancel()  // Cancel any existing game loop
        _score.value = 0
        _currentIndex.value = 0
        _gameState.value = _gameState.value.copy(eventValue = -1)

        // Get the events from our C-model (returns IntArray, so we need to convert to Array<Int>)
        events = nBackHelper.generateNBackString(size, combinations, 30, nBack).toList().toTypedArray()  // Todo Higher Grade: currently the size etc. are hardcoded, make these based on user input
        Log.d("GameVM", "The following sequence was generated: ${events.contentToString()}")

        eventPoints = IntArray(events.size) {0}

        job = viewModelScope.launch {
            when (gameState.value.gameType) {
                GameType.Audio -> runAudioGame(events)
                GameType.AudioVisual -> runAudioVisualGame()
                GameType.Visual -> runVisualGame(events)
            }

            if(score.value > highscore.value){
                updateHighScore(score.value)
            }
        }
    }

    private suspend fun updateHighScore(score: Int){
        userPreferencesRepository.saveHighScore(score)
    }

    override fun checkMatch(){
        //Not allowed to update point for same event
        if(eventPoints[currentIndex.value] != 0)
            return

        val currentValue = events[currentIndex.value]
        val startIndex = (currentIndex.value - 2).coerceAtLeast(0) //Not allowed to go outside array. Change -2 to the nBack

        //Check the event array if the position is matching
        for (i in startIndex until currentIndex.value) {
            if(events[i] == currentValue){
                eventPoints[currentIndex.value] = 1
                _score.value = _score.value + 1
                return
            }
        }
        eventPoints[currentIndex.value] = -1
        _score.value = _score.value - 1
    }
    private suspend fun runAudioGame(events: Array<Int>) {
        delay(600L) //Just to give the screen some time to initialize
        for (value in events) {
            _gameState.value = _gameState.value.copy(eventValue = 64 + value) //Put 64 make the eventValue ASCII
            delay(eventInterval)
            _gameState.value = _gameState.value.copy(eventValue = 0) //Makes sure the next element is picked up
            delay(500L)
            _currentIndex.value += 1
        }
        _gameState.value = _gameState.value.copy(eventValue = -2) //'-2' tells the view to pop back
    }

    private suspend fun runVisualGame(events: Array<Int>){
        delay(600L) //Just to give the screen some time to initialize
        for (value in events) {
            _gameState.value = _gameState.value.copy(eventValue = value)
            delay(eventInterval)
            _gameState.value = _gameState.value.copy(eventValue = -1)
            delay(500L)
            _currentIndex.value += 1
        }
        _gameState.value = _gameState.value.copy(eventValue = -2) //'-2' tells the view to pop back
    }

    private fun runAudioVisualGame(){
        // Todo: Make work for Higher grade
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GameApplication)
                GameVM(application.userPreferencesRespository)
            }
        }
    }

    init {
        // Code that runs during creation of the vm
        viewModelScope.launch {
            userPreferencesRepository.highscore.collect {
                _highscore.value = it
            }
        }
    }
}

private fun mapNumberToLetter(events: Array<Int>):Array<Char> {
    val result = Array(events.size){'A'}
    for((index, value) in events.withIndex()) {
        // Map numbers 1-9 to letters A-I
         result[index] = when (value) {
            1 -> 'A'
            2 -> 'B'
            3 -> 'C'
            4 -> 'D'
            5 -> 'E'
            6 -> 'F'
            7 -> 'G'
            8 -> 'H'
            9 -> 'I'
            else -> ' '
        }
    }
    return result
}

// Class with the different game types
enum class GameType{
    Audio,
    Visual,
    AudioVisual
}

data class GameState(
    // You can use this state to push values from the VM to your UI.
    val gameType: GameType = GameType.Visual,  // Type of the game
    val eventValue: Int = -1  // The value of the array string
)

/*
class FakeVM: GameViewModel{
    override val gameState: StateFlow<GameState>
        get() = MutableStateFlow(GameState()).asStateFlow()
    override val score: StateFlow<Int>
        get() = MutableStateFlow(2).asStateFlow()
    override val highscore: StateFlow<Int>
        get() = MutableStateFlow(42).asStateFlow()
    override val nBack: Int
        get() = 2

    override fun setGameType(gameType: GameType) {
    }

    override fun startGame() {
    }

    override fun checkMatch() {
    }
}*/