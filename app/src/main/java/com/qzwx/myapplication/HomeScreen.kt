package com.qzwx.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val context = LocalContext.current // 获取当前的Context
    LazyColumn(
        modifier = Modifier
            .background(colorResource(id = R.color.zise))
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            ImageCarousel()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "这是一些功能的入口:",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CardList()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "下面是一些可能有用的网址：",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // 添加按钮-进入所有网址页面【下】开始---------------------------
            Button(
                onClick = {
                    val intent = Intent(context, AllWebActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(48.dp) // 按钮高度
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.svg_web), // 按钮图片
                        contentDescription = null,
                        modifier = Modifier.size(24.dp) // 图片大小
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "点我进入",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                }
            }//所有网址进入的页面按钮【上】结束----------------------

        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition", "RememberReturnType")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageCarousel() {
    val images = listOf(
        R.drawable.tp_lunbotu01,
        R.drawable.tp_lunbotu02,
        R.drawable.tp_lunbotu03,
        R.drawable.tp_lunbotu04
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
                        color = Color.White,
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
            activeColor = Color.White,
            inactiveColor = Color.LightGray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        coroutineScope.launch {
            while (true) {
                kotlinx.coroutines.delay(3000)
                val nextPage = (pagerState.currentPage + 1) % images.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }
}

@Composable
fun CardList() {
    val cardItems = listOf(
        Pair(R.drawable.svg_jisuanqi, "计算器"),
        Pair(R.drawable.svg_rijiben, "日记本"),
        Pair(R.drawable.svg_jizhangben, "记账本"),
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
fun CardItem(imageResId: Int, description: String) {
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
                if (description == "计算器") {
                    val intent = Intent(context, com.qzwx.myapplication.JiSuanQi::class.java)
                    context.startActivity(intent)
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
