package com.qzwx.qzwxapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.*
import com.dotlottie.dlplayer.*
import com.lottiefiles.dotlottie.core.compose.ui.*
import com.lottiefiles.dotlottie.core.util.*
import kotlinx.coroutines.*

@Composable
fun SplashPage(navController : NavController) {
    LaunchedEffect(UInt) {
        delay(2000)
        navController.navigate("HomePage")
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
