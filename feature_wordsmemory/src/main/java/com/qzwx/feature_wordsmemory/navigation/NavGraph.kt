package com.qzwx.feature_wordsmemory.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.qzwx.feature_wordsmemory.ui.AddNewWordsPage
import com.qzwx.feature_wordsmemory.ui.HomePage
import com.qzwx.feature_wordsmemory.ui.ReviewPage
import com.qzwx.feature_wordsmemory.ui.SettingPage
import com.qzwx.feature_wordsmemory.ui.StatisticPage
import com.qzwx.feature_wordsmemory.ui.VocabularyPage
import com.qzwx.feature_wordsmemory.viewmodel.WordViewModel

@Composable
fun NavGraph(
    navController : NavHostController,
    viewModel : WordViewModel) {
    NavHost(navController = navController, startDestination = "homepage") {
        composable("homepage") { HomePage(viewModel = viewModel, navController = navController) }
        composable("vocabularypage") { VocabularyPage(navController = navController, viewModel) }
        composable("statisticpage") { StatisticPage(viewModel) }
        composable("settingpage") { SettingPage() }
        composable("reviewpage") {
            ReviewPage(navController = navController,
                viewModel = viewModel)
        }
        composable("addnewwordspage") {
            AddNewWordsPage(viewModel = viewModel,
                navController = navController)
        }
    }
}