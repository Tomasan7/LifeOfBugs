package me.tomasan7.lifeofbugs.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*
import me.tomasan7.lifeofbugs.game.bug.Bug
import me.tomasan7.lifeofbugs.game.Game
import me.tomasan7.lifeofbugs.game.GameConfig
import me.tomasan7.lifeofbugs.game.NoBugAtPosException
import me.tomasan7.lifeofbugs.game.Tile
import me.tomasan7.lifeofbugs.serialization.MapSerializer
import java.io.File

class ViewModel
{
    constructor(gameConfig: GameConfig, serializer: MapSerializer)
    {
        this.game = Game(gameConfig)
        this.game.fillRandomBugs()
        this.serializer = serializer
        this.map = game.getMapCopy()
    }

    constructor(serializer: MapSerializer)
    {
        if (mapFile.exists())
            this.game = Game(serializer.deserialize(mapFile.readText()))
        else
        {
            val gameConfig = GameConfig(10, 10)
            this.game = Game(gameConfig)
            game.fillRandomBugs()
        }

        this.serializer = serializer
        this.map = game.getMapCopy()
    }

    private val game: Game
    private val serializer: MapSerializer

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var cycleJob: Job? = null
    private var playingJob: Job? = null

    var map by mutableStateOf(emptyList<List<Tile>>())
        private set
    var cycleDelay by mutableStateOf(100L)
        private set
    var playing by mutableStateOf(false)
        private set

    fun setTickSpeed(speed: Long)
    {
        this.cycleDelay = speed
    }

    fun reset()
    {
        playing = false
        playingJob?.cancel()
        cycleJob?.cancel()
        game.setRandomBugs(game.gameConfig.size)
        update()
    }

    fun getBugs() = map.flatten().filterIsInstance<Tile.BugTile>().map { it.bug }

    fun cycle(onFinish: () -> Unit = {})
    {
        if (cycleJob != null && cycleJob!!.isActive)
            return

        cycleJob = coroutineScope.launch {
            val processedBugs = mutableSetOf<Bug>()
            val positionsToProcess = game.getBugPositions()

            for (bugPos in positionsToProcess)
            {
                try
                {
                    val bug = game.getBug(bugPos)

                    if (bug in processedBugs)
                        continue

                    game.processBug(bugPos)
                    processedBugs.add(bug)
                    update()
                    delay(cycleDelay)
                }
                catch (ignored: NoBugAtPosException)
                {
                }
            }

            val respawnHappened = game.respawnIfNecessary()
            if (respawnHappened)
                update()

            onFinish()
        }
    }

    fun play()
    {
        if (playing)
            return

        playing = true
        playingJob = coroutineScope.launch {
            var finished = true

            while (isActive)
            {
                if (finished)
                {
                    cycle {
                        finished = true
                    }
                    finished = false
                }
                if (cycleDelay > 0)
                    delay(cycleDelay)
                else
                    delay(100)
            }
        }
    }

    fun stopPlaying()
    {
        playing = false
        playingJob?.cancel()
    }

    fun playStop()
    {
        if (playing)
            stopPlaying()
        else
            play()
    }

    fun end()
    {
        cycleJob?.cancel()
        playingJob?.cancel()
        mapFile.writeText(serializer.serialize(game.getMapCopy()))
    }

    fun update()
    {
        map = game.getMapCopy()
    }

    companion object
    {
        private val mapFile = File("map.txt")
    }
}