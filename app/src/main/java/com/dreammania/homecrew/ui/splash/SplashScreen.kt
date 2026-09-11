package com.dreammania.homecrew.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.dreammania.homecrew.R
import com.dreammania.homecrew.ui.theme.HomecrewTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(key1 = true) {
        delay(4000) // Wait for 4 seconds
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(1000) // Wait for fade out to complete
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha.value),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.dreammania),
            contentDescription = "Dreammania Logo"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    HomecrewTheme {
        SplashScreen(onTimeout = {})
    }
}
