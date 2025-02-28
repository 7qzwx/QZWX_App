package com.qzwx.feature_qiandaosystem.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.qzwx.core.room.room_qiandaosystem.CheckInRepository
import com.qzwx.feature_qiandaosystem.ui.CalendarScreen
import com.qzwx.feature_qiandaosystem.ui.CheckInScreen
import com.qzwx.feature_qiandaosystem.ui.HistoryScreen
import com.qzwx.feature_qiandaosystem.viewmodel.CheckInViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NavGraph(checkInRepository : CheckInRepository) {
    val navController = rememberNavController() // 创建 NavHostController
    val checkInViewModel = CheckInViewModel(checkInRepository) // 创建 ViewModel
    NavHost(navController = navController, startDestination = "check_in") {
        composable("check_in") { CheckInScreen(navController = navController, checkInRepository) }
        composable("calendar") {
            CalendarScreen(viewModel = checkInViewModel)
        }
        composable("history/{checkInName}", arguments = listOf(
            navArgument("checkInName") {
                type = NavType.StringType
                nullable = false // 不允许为空
                defaultValue = "" // 默认值，可以根据需要调整
            }
        )) { backStackEntry ->
            val checkInName = backStackEntry.arguments?.getString("checkInName") ?: ""
            HistoryScreen(checkInName = checkInName, checkInRepository = checkInRepository)
        }
    }
}

