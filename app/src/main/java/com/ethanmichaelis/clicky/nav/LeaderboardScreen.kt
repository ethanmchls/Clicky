package com.ethanmichaelis.clicky.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.LinkedList

@Composable
fun LeaderboardScreen(
    backButtonClick: () -> Unit = {},
    getRank: Int = 999,
    getTopTen: List<Pair<String, Long>> = LinkedList(),
//    getUsername: String = "",
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .background(color = MaterialTheme.colors.primary),
                title = {
                    Text(
                        modifier = Modifier,
                        text = "Leaderboard",
                    )
                },
                navigationIcon = {
                    IconButton(onClick = backButtonClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.LightGray),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            var position = 0
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(it)
                        .background(color = Color.LightGray)
                        .height(64.dp),
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 18.dp)
                            .align(Alignment.CenterVertically),
                        text = "Your position: $getRank",
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontFamily = FontFamily.SansSerif,
                        )
                    )
                }
            }
            items(getTopTen) {
                position++
                PlayerCard(playerInfo = it, position = position)
            }
        }
    }
}

@Composable
fun PlayerCard(
    playerInfo: Pair<String, Long>,
    position: Int,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
//            .background(color = Color.LightGray)
            .height(42.dp),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.secondary,
        elevation = 5.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .align(Alignment.CenterVertically),
                text = "$position   ${playerInfo.second} - ${playerInfo.first}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 28.sp,
                    fontFamily = FontFamily.SansSerif,
                ),
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun LeaderboardScreenPreview() {
    LeaderboardScreen()
}