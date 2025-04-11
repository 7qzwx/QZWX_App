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
import androidx.compose.ui.unit.*
import androidx.navigation.*
import com.google.accompanist.pager.*
import com.qzwx.qzwxapp.*
import com.qzwx.qzwxapp.R
import com.qzwx.qzwxapp.navigation.*
import kotlinx.coroutines.*

@Composable
fun HomePage(navController : NavController) {
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
        , bottomBar = { QZWXBottomNavigation(navController=navController) }
    ) { PaddingValues ->
    val context = LocalContext.current // 获取当前的Context
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(PaddingValues)
    ) {
        item {
            ImageCarousel()
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Text(
                text = "这是一些功能的入口:",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray),
            )
        }
        item { CardList() }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Text(
                text = "下面是一些可能用到的网址：",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
        }
        // 添加按钮-进入所有网址页面【下】开始---------------------------
        item {
            Button(
                onClick = {
                    val intent = Intent(context, AllWebActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        start = 36.dp,
                        end = 36.dp
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_svg_web), // 按钮图片
                        contentDescription = null,
                        modifier = Modifier.size(24.dp) // 图片大小
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "点我进入",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                }
            }
        } //所有网址进入的页面按钮【上】结束----------------------
        item { Spacer(modifier = Modifier.width(16.dp)) }
    }
}
}
@SuppressLint("CoroutineCreationDuringComposition", "RememberReturnType")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageCarousel() {
    val images = listOf(
        R.drawable.shenli,
        R.drawable.tp1,
        R.drawable.tp2,
        R.drawable.tp3
    )
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val customFontFamily = remember {
        FontFamily(
            Font(R.font.qzwx_shouxie)
        )
    }

    Column {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "七\n种\n文\n学",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = customFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        shadow = Shadow(
                            color = Color.Black,
                            blurRadius = 4f
                        ),
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = MaterialTheme.colorScheme.onBackground,
            inactiveColor = Color.LightGray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        coroutineScope.launch {
            while (true) {
                delay(3000)
                val nextPage = (pagerState.currentPage + 1) % images.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }
}

@Composable
fun CardList() {
    val cardItems = listOf(
        Pair(R.drawable.jisuanqiico, "计算器"),
        Pair(R.drawable.diaryico, "日记本"),
        Pair(R.drawable.accountico, "记账本"),
        Pair(com.qzwx.core.R.drawable.qzxt_qdxt, "签到系统"),
        Pair(R.drawable.wordsico, "单词本"),
        Pair(R.drawable.svg_aixin, "❥(^_-)")
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        items(cardItems.size) { index ->
            CardItem(
                imageResId = cardItems[index].first,
                description = cardItems[index].second
            )
            Spacer(modifier = Modifier.width(12.dp)) // 增加卡片之间的间距
        }
    }
}

@Composable
fun CardItem(imageResId : Int, description : String) {
    val context = LocalContext.current // 获取当前的Context
    val customFontFamily = remember {
        FontFamily(
            Font(R.font.qzwx_kaiti)
        )
    }

    Card(
        modifier = Modifier
            .width(140.dp) // 调整卡片宽度
            .padding(8.dp) // 增加外边距
            .shadow(8.dp, shape = RoundedCornerShape(12.dp)) // 调整阴影效果
            .clickable {
                when (description) {
                    "计算器"   -> {
                        val intent = Intent(context, JiSuanQi::class.java)
                        context.startActivity(intent)
                    }

                    "日记本"   -> {

                    }

                    "签到系统" -> {
                        val intent =
                            Intent(context, com.qzwx.feature_qiandaosystem.QDXTActivity::class.java)
                        context.startActivity(intent)
                    }

                    "单词本"   -> {
                        val intent =
                            Intent(context,
                                com.qzwx.feature_wordsmemory.WordsMemoryActivity::class.java)
                        context.startActivity(intent)
                    }

                    "记账本"   -> {
                        val intent =
                            Intent(context,
                                com.qzwx.feature_accountbook.AccountBookActivity::class.java)
                        context.startActivity(intent)
                    }
                    // 你可以在这里处理其他卡片的点击事件
                }
            }, // 添加点击事件处理
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // 保持长宽比为1:1
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEEEEEE),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 将图片大小调整得更小，以便文字可以显示
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp) // 调整图片大小
                    .clip(RoundedCornerShape(12.dp)) // 调整圆角
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = customFontFamily, // 使用自定义字体
                    fontSize = 12.sp, // 调整字体大小
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(4.dp) // 增加文字的内边距
                    .fillMaxWidth() // 确保文字占据整个宽度
            )
        }
    }
}

