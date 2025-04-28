package com.qzwx.qzwxapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import kotlinx.coroutines.delay

@Composable
fun SplashPage(navController : NavController) {
    LaunchedEffect(UInt) {
        delay(2000)
        navController.navigate("MainScreen") {
            popUpTo("SplashPage") { inclusive = true }
        }
    }
    Splash()
}

@Composable
fun Splash() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DotLottieAnimation(
            source = DotLottieSource.Asset("splash.lottie"),
            autoplay = true,    //自动播放
            loop = false,   //循环
            speed = 2f,   //速度
            useFrameInterpolation = false,
            playMode = Mode.FORWARD, //            modifier = Modifier.background(Color.LightGray)
        )
    }
}
