package com.qzwx.feature_accountbook.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qzwx.feature_accountbook.page.calendarpage.CalendarPage
import com.qzwx.feature_accountbook.page.chartpage.ChartPage
import com.qzwx.feature_accountbook.page.homepage.HomePage
import com.qzwx.feature_accountbook.page.mypage.MyPage
import com.qzwx.feature_accountbook.page.walletpage.WalletPage

@Composable
fun AppNavHost(navController : NavController) {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        NavHost(
            navController = navController,
            startDestination = "HomePage",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("HomePage") { HomePage() }
            composable("CalendarPage") { CalendarPage() }
            composable("ChartPage") { ChartPage() }
            composable("MyPage") { MyPage() }
            composable("WalletPage") { WalletPage() }
        }
        // 底部导航栏
        ColorButtonNavBar(
            navController = navController,
            modifier = Modifier
        )
    }
}
