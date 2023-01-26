package com.ethanmichaelis.clicky.nav

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethanmichaelis.clicky.ClickyStore.Companion.DEBOUNCE_INTERVAL
import com.ethanmichaelis.clicky.R
import com.ethanmichaelis.clicky.ui.theme.ClickyTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun PressButtonPreview() {
    ClickyTheme() {
        ClickyScreen()
    }
}

@Composable
fun ClickyScreen(
    score: Long = 0,
    setScore: (Long) -> Unit = {},
    clickMenu: () -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var debounceJob: Job = remember { Job() }
    var currentScore: Long by remember {
        mutableStateOf(score)
    }
    BackHandler() {
        debounceJob.cancel()
        setScore(currentScore)
        onBackPressed()
    }
    Scaffold(
        topBar = {
            TopAppBar(//TODO
                title = {},
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            debounceJob.cancel()
                            setScore(currentScore)
                            clickMenu()
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_settings_24),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(color = MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            ShowScore(score = currentScore, modifier = Modifier.padding(top = 64.dp))
            PressButton(modifier = Modifier.padding(bottom = 64.dp)) {
                currentScore++
                debounceJob.cancel()
                debounceJob = coroutineScope.launch {
                    delay(DEBOUNCE_INTERVAL)
                    setScore(currentScore)
                }
            }
        }
    }
}

@Composable
fun PressButton(
    modifier: Modifier = Modifier,
    onClick: ()-> Unit = {}
) {
    Icon(
        painter = painterResource(id = R.drawable.plus_button),
        contentDescription = null,
        modifier = modifier.then(
            Modifier
                .size(128.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick)
        )
    )
}

@Composable
fun ShowScore(
    score: Long,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = "$score",
        style = TextStyle(
            fontSize = 32.sp
        )
    )
}