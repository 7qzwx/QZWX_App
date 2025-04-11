package com.qzwx.qzwxapp.ui

import android.annotation.*
import android.content.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.qzwx.qzwxapp.*
import com.qzwx.qzwxapp.R
import com.qzwx.qzwxapp.navigation.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MusicPage(navController : NavController) {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        bottomBar = { AnimatedNavigationBarExample(navController = navController) }) { PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background
                )
                .padding(PaddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "全部应用集合",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color(0xFF2C3E50),
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
                val cardItems = listOf(
                    Triple(
                        R.drawable.jisuanqiico,
                        "计算器",
                        Color(0xFFF8B195)
                    ),    // 温暖珊瑚色
                    Triple(
                        R.drawable.diaryico,
                        "日记本",
                        Color(0xFFF67280)
                    ),       // 柔和玫瑰色
                    Triple(
                        R.drawable.accountico,
                        "记账本",
                        Color(0xFFFBC490)
                    ),     // 温暖杏色
                    Triple(
                        com.qzwx.core.R.drawable.qzxt_qdxt,
                        "签到系统",
                        Color(0xFFE8D6CF)
                    ), // 柔和米色
                    Triple(
                        R.drawable.wordsico,
                        "单词本",
                        Color(0xFFD6A2E8)
                    ),      // 淡雅紫色
                    Triple(
                        R.drawable.svg_aixin,
                        "❥(^_-)",
                        Color(0xFFBEAEE2)
                    ),     // 柔和薰衣草色
                    Triple(
                        R.drawable.svg_aixin,
                        "全部网站",
                        Color(0xFFCFBAF0)
                    ),   // 淡紫色
                    Triple(
                        R.drawable.svg_aixin,
                        "Todo",
                        Color(0xFFC3B1E1)
                    )        // 柔和丁香色
                )

                items((cardItems.indices step 2).toList()) { i ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (j in 0 until 2) {
                            if (i + j < cardItems.size) {
                                var isPressed by remember { mutableStateOf(false) }

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .graphicsLayer {
                                            scaleX = if (isPressed) 0.98f else 1f
                                            scaleY = if (isPressed) 0.98f else 1f
                                        }
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable {
                                            isPressed = true
                                            when (cardItems[i + j].second) {
                                                "计算器"   -> {
                                                    val intent = Intent(
                                                        context,
                                                        JiSuanQi::class.java
                                                    )
                                                    context.startActivity(intent)
                                                }

                                                "签到系统" -> {
                                                    val intent = Intent(
                                                        context,
                                                        com.qzwx.feature_qiandaosystem.QDXTActivity::class.java
                                                    )
                                                    context.startActivity(intent)
                                                }

                                                "单词本"   -> {
                                                    val intent = Intent(
                                                        context,
                                                        com.qzwx.feature_wordsmemory.WordsMemoryActivity::class.java
                                                    )
                                                    context.startActivity(intent)
                                                }

                                                "日记本"   -> {
                                                    val intent = Intent(
                                                        context,
                                                        com.qzwx.feature_diary.DiaryActivity::class.java
                                                    )
                                                    context.startActivity(intent)
                                                }

                                                "记账本"   -> {
                                                    val intent = Intent(
                                                        context,
                                                        com.qzwx.feature_accountbook.AccountBookActivity::class.java
                                                    )
                                                    context.startActivity(intent)
                                                }

                                                "全部网站" -> {
                                                    val intent = Intent(
                                                        context,
                                                        AllWebActivity::class.java
                                                    )
                                                    context.startActivity(intent)
                                                }

                                                "Todo"     -> {
                                                    val intent = Intent(
                                                        context,
                                                        com.qzwx.feature_todoanddone.TodoAndDoneActivity::class.java
                                                    )
                                                    context.startActivity(intent)
                                                }
                                            }
                                            isPressed = false
                                        },
                                     shape = RoundedCornerShape(20.dp),
                                     colors = CardDefaults.cardColors(
                                         containerColor = cardItems[i + j].third.copy(alpha = 0.08f)
                                     ),
                                     elevation = CardDefaults.cardElevation(
                                         defaultElevation = 0.dp,
                                         pressedElevation = 0.dp
                                     )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = cardItems[i + j].first),
                                            contentDescription = null,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = cardItems[i + j].second,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = FontFamily(Font(R.font.qzwx_kaiti)),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = cardItems[i + j].third.copy(alpha = 0.85f)
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun CardItem(
    imageResId : Int,description : String,onClick : ()->Unit
) {
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description)
        }
    }
}
