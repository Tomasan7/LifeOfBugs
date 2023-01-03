package me.tomasan7.lifeofbugs.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import me.tomasan7.lifeofbugs.game.bug.Bug
import me.tomasan7.lifeofbugs.game.Tile
import me.tomasan7.lifeofbugs.serialization.BasicMapSerializer
import me.tomasan7.lifeofbugs.util.ceil
import me.tomasan7.lifeofbugs.util.floor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Game(applicationScope: ApplicationScope)
{
    val viewModel = remember { ViewModel(BasicMapSerializer) }

    Window(
        onCloseRequest = { viewModel.end(); applicationScope.exitApplication() },
        title = "Life of Bugs",
        icon = painterResource("bug.png"),
        state = WindowState(size = DpSize(550.dp, 900.dp)),
        onKeyEvent = { keyEvent ->
            if (keyEvent.key == Key.Spacebar && keyEvent.type == KeyEventType.KeyDown)
                viewModel.cycle()
            true
        }) {
        Column {
            LazyColumn {
                items(viewModel.map) { row ->
                    LazyRow {
                        items(row) { tile ->
                            Tile(tile)
                        }
                    }
                }
            }

            Spacer(Modifier.height(50.dp))

            Scoreboard(viewModel.getBugs())

            Button(onClick = { viewModel.cycle() }) {
                Text("Cycle")
            }

            Button(onClick = { viewModel.reset() }) {
                Text("Restart")
            }

            Button(onClick = { viewModel.playStop() }) {
                if (viewModel.playing)
                    Text("Stop")
                else
                    Text("Play")
            }

            Slider(
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    activeTrackColor = Color.Blue,
                    inactiveTrackColor = Color.Blue
                ),
                value = viewModel.cycleDelay.toFloat(),
                onValueChange = { viewModel.setTickSpeed(it.toLong()) },
                valueRange = 0f..1000f,
                steps = 100
            )

            Text("Cycle delay: ${viewModel.cycleDelay}ms")
        }
    }
}

@Composable
fun Scoreboard(bugs: List<Bug?>)
{
    val bugsSortedColumns by derivedStateOf {
        val bugsNonNullSorted = bugs.filterNotNull().sortedBy { it.name }.sortedByDescending { it.score }.take(10)

        val half = bugsNonNullSorted.size / 2f
        val firstHalfSize = half.ceil()
        val secondHalfSize = half.floor()

        val firstHalf = bugsNonNullSorted.subList(0, firstHalfSize)
        val secondHalf = bugsNonNullSorted.subList(firstHalfSize, firstHalfSize + secondHalfSize)

        listOf(firstHalf, secondHalf)
    }

    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        var place = 1
        bugsSortedColumns.forEach { bugsSortedColumn ->
            Column {
                bugsSortedColumn.forEach { bug ->
                    Text(
                        "$place. ${bug.name} (${bug.score})",
                        color = bug.color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)

                    place++
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Bug(bug: Bug, onClick: () -> Unit = {}, onLongClick: () -> Unit = {}, modifier: Modifier = Modifier)
{
    key(bug.id)
    {
        TooltipArea(
            delayMillis = 0,
            tooltip = {
                Surface(elevation = 5.dp, shape = RoundedCornerShape(4.dp), color = bug.color) {
                    Box(Modifier.padding(2.dp)) {
                        Text(bug.name + ": " + bug.brain.getBrainName(), color = textColor(bug.color))
                    }
                }
            }
        )
        {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.onClick(onClick = onClick, onLongClick = onLongClick)
            ) {
                val animatedRotation by smoothRotation(bug.orientation.degrees)

                Image(
                    painter = painterResource("bug.png"),
                    contentDescription = "me.tomasan7.lifeofbugs.bug.Bug",
                    colorFilter = ColorFilter.tint(color = bug.color),
                    modifier = modifier.rotate(animatedRotation)
                )

                Text(bug.score.toString(), color = textColor(bug.color))
            }
        }
    }
}

@Composable
fun Tile(tile: Tile, onClick: () -> Unit = {})
{
    when (tile)
    {
        is Tile.BugTile         -> Bug(tile.bug, modifier = Modifier.size(40.dp), onClick = onClick)
        is Tile.Wall, Tile.Void -> Box(modifier = Modifier.size(40.dp).background(Color.Black))
        is Tile.Space           -> Box(modifier = Modifier.size(40.dp))
    }
}

@Composable
fun smoothRotation(rotation: Int): State<Float>
{
    /* https://stackoverflow.com/a/68259116 */

    val (lastRotation, setLastRotation) = remember { mutableStateOf(0) } // this keeps last rotation
    var newRotation = lastRotation // newRotation will be updated in proper way
    val modLast =
        if (lastRotation > 0) lastRotation % 360 else 360 - (-lastRotation % 360) // last rotation converted to range [-359; 359]

    if (modLast != rotation) // if modLast isn't equal rotation retrieved as function argument it means that newRotation has to be updated
    {
        val backward =
            if (rotation > modLast) modLast + 360 - rotation else modLast - rotation // distance in degrees between modLast and rotation going backward
        val forward =
            if (rotation > modLast) rotation - modLast else 360 - modLast + rotation // distance in degrees between modLast and rotation going forward

        // update newRotation so it will change rotation in the shortest way
        newRotation = if (backward < forward)
        {
            // backward rotation is shorter
            lastRotation - backward
        }
        else
        {
            // forward rotation is shorter (or they are equal)
            lastRotation + forward
        }

        setLastRotation(newRotation)
    }

    return animateFloatAsState(
        targetValue = -newRotation.toFloat(),
        animationSpec = tween(
            durationMillis = 100,
            easing = LinearEasing
        )
    )
}

@Composable
fun textColor(backgroundColor: Color): Color
{
    val red = backgroundColor.red
    val green = backgroundColor.green
    val blue = backgroundColor.blue

    val luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue

    return if (luminance > 0.5) Color.Black else Color.White
}

class Ref(var value: Int)

// Note the inline function below which ensures that this function is essentially
// copied at the call site to ensure that its logging only recompositions from the
// original call site.
@Composable
inline fun LogCompositions(msg: String)
{
    val ref = remember { Ref(0) }
    SideEffect { ref.value++ }
    println("Compositions: $msg ${ref.value}")
}