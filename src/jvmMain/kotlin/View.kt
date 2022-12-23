import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlin.math.ceil

@Composable
fun Game()
{
    val viewModel = remember { ViewModel(GameConfig(10, 10), SimpleBrain()) }
    val gameConfig = viewModel.gameConfig

    var bugDialogViewed by remember { mutableStateOf(false) }
    var dialogBug by remember { mutableStateOf<Bug?>(null) }

    Column {
        repeat(gameConfig.height) { y ->
            Row {
                repeat(gameConfig.width) { x ->
                    val bug = viewModel.bugs[y * gameConfig.width + x]
                    if (bug != null)
                        Bug(
                            bug = bug,
                            onClick = { viewModel.updateBug(bug, bug.rotateLeft()) },
                            onLongClick = { viewModel.moveBugAndEat(bug, Move.FORWARD) },
                            modifier = Modifier.size(50.dp)
                        )
                    else
                        Box(modifier = Modifier.size(50.dp))
                }
            }
        }

        Spacer(Modifier.height(50.dp))

        Scoreboard(viewModel.bugs)

        Button(onClick = { viewModel.cycle()}) {
            Text("Cycle")
        }
    }

    if (bugDialogViewed && dialogBug != null)
        Dialog(onCloseRequest = { bugDialogViewed = false }) {
            Text(dialogBug!!.name)
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
    key(bug.id) {
        TooltipArea(
            delayMillis = 0,
            tooltip = {
                Surface(elevation = 5.dp, shape = RoundedCornerShape(4.dp), color = bug.color) {
                    Box(Modifier.padding(2.dp)) {
                        Text(bug.name, color = textColor(bug.color))
                    }
                }
            }
        )
        {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.onClick(onClick = onClick, onLongClick = onLongClick)
            ) {
                val animatedRotation by animateFloatAsState(bug.orientation.degrees.toFloat())

                Image(
                    painter = painterResource("bug.png"),
                    contentDescription = "Bug",
                    colorFilter = ColorFilter.tint(color = bug.color),
                    modifier = modifier.rotate(animatedRotation)
                )

                Text(bug.score.toString(), color = textColor(bug.color))
            }
        }
    }
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