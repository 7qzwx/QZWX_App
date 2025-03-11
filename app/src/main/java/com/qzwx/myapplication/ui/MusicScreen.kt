package com.qzwx.myapplication.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qzwx.myapplication.AllWebActivity
import com.qzwx.myapplication.JiSuanQi
import com.qzwx.myapplication.R

@Preview
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MusicScreen() {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAFAFA),
                        Color(0xFFF5F5F5)
                    )
                )
            )
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
                Triple(R.drawable.jisuanqiico, "计算器", Color(0xFFFF6B6B)),
                Triple(R.drawable.diaryico, "日记本", Color(0xFF4ECDC4)),
                Triple(R.drawable.accountico, "记账本", Color(0xFFFFBE0B)),
                Triple(com.qzwx.core.R.drawable.qzxt_qdxt, "签到系统", Color(0xFF845EC2)),
                Triple(R.drawable.wordsico, "单词本", Color(0xFF00B4D8)),
                Triple(R.drawable.svg_aixin, "❥(^_-)", Color(0xFFFF85A1)),
                Triple(R.drawable.svg_aixin, "全部网站", Color(0xFF6C757D)),
                Triple(R.drawable.svg_aixin, "Todo", Color(0xFF20C997))
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
                                                val intent =
                                                    Intent(context, JiSuanQi::class.java)
                                                context.startActivity(intent)
                                            }

                                            "签到系统" -> {
                                                val intent = Intent(context,
                                                    com.qzwx.feature_qiandaosystem.QDXTActivity::class.java)
                                                context.startActivity(intent)
                                            }

                                            "单词本"   -> {
                                                val intent = Intent(context,
                                                    com.qzwx.feature_wordsmemory.WordsMemoryActivity::class.java)
                                                context.startActivity(intent)
                                            }

                                            "全部网站" -> {
                                                val intent =
                                                    Intent(context, AllWebActivity::class.java)
                                                context.startActivity(intent)
                                            }

                                            "Todo"     -> {
                                                val intent = Intent(context,
                                                    com.qzwx.feature_todoanddone.TodoAndDoneActivity::class.java)
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

@Composable
fun CardItem(
    imageResId : Int,
    description : String,
    onClick : () -> Unit
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
            androidx.compose.foundation.Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description)
        }
    }
}
