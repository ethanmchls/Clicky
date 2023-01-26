package com.ethanmichaelis.clicky.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethanmichaelis.clicky.ui.theme.ClickyTheme

@Composable
fun MenuScreen (
    logoutClick: () -> Unit = {},
    leaderboardClick: () -> Unit = {},
    resetScoreClick: () -> Unit = {},
    updateUsernameClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(top = 16.dp),
            text = "CLICKY",
            style = TextStyle(
                fontSize = 64.sp,
                fontFamily = FontFamily.SansSerif,
            ),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        TextButton(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = resetScoreClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                backgroundColor = Color.LightGray
            )
        ) {
            Text(text = "Reset score")
        }
        TextButton(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = leaderboardClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                backgroundColor = Color.LightGray
            )
        ) {
            Text(text = "Leaderboard")
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = logoutClick,
        ) {
            Text(text = "Logout")
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = updateUsernameClick,
        ) {
            Text(text = "Change Username")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    ClickyTheme {
        MenuScreen()
    }
}