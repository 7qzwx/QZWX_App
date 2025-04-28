package com.qzwx.feature_accountbook.page.homepage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun HomePage_TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, start = 12.dp, end = 12.dp)
            .wrapContentHeight(), // 让高度自适应内容
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {},
            shape = RoundedCornerShape(2.dp),
            modifier = Modifier
                .padding(start = 2.dp)
                .height(40.dp) // 设定按钮高度
        ) {
            Icon(
                Icons.Filled.Book,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text("个人账本", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.weight(1f)) // 占位符，将 actions 推到右侧
        IconButton(
            onClick = {},
            modifier = Modifier.size(40.dp)
        ) {
            Icon(Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp))
        }
        IconButton(
            onClick = {},
            modifier = Modifier.size(40.dp)
        ) {
            Icon(Icons.Filled.MoreVert,
                contentDescription = null,
                modifier = Modifier.size(20.dp))
        }
    }
}


@Composable
fun HomePage_Card() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFfdcbf1), Color(0xFFe6dee9)), // 渐变色
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 0f) // 控制渐变方向（水平渐变）
                    )
                )
        ) {
            ConstraintLayout {
                val (text1, text2, text3, expenditure) = createRefs()  // 创建引用名称
                // 第一个 Column：剩余预算
                Column(
                    modifier = Modifier.constrainAs(text1) {
                        top.linkTo(parent.top, margin = 5.dp)
                        start.linkTo(parent.start, margin = 12.dp)
                    }
                ) {
                    Text("剩余预算", fontSize = 12.sp)
                    Text("600", fontSize = 18.sp)
                }
                // 第二个 Column：本月结余
                Column(
                    modifier = Modifier.constrainAs(text2) {
                        top.linkTo(parent.top, margin = 5.dp)
                        start.linkTo(text1.end, margin = 26.dp)  // 水平间距
                    }
                ) {
                    Text("本月结余", fontSize = 12.sp)
                    Text("1200", fontSize = 18.sp)
                }
                // 第三个 Column：本月收入
                Column(
                    modifier = Modifier.constrainAs(text3) {
                        top.linkTo(parent.top, margin = 5.dp)
                        start.linkTo(text2.end, margin = 26.dp)  // 水平间距
                    }
                ) {
                    Text("本月收入", fontSize = 12.sp)
                    Text("500", fontSize = 18.sp)
                } // 第四个 Column：本月支出
                Column(
                    modifier = Modifier.constrainAs(expenditure) {
                        top.linkTo(text1.bottom, margin = 5.dp)
                        start.linkTo(parent.start, margin = 16.dp)  // 水平间距
                        bottom.linkTo(parent.bottom, margin = 5.dp)
                    }
                ) {
                    Text("本月支出", fontSize = 12.sp)
                    Text("1000", fontSize = 30.sp)
                }
            }
        }
    }
}

@Composable
fun HomePage_BudgetCard(modifier : Modifier = Modifier) {
    val totalBudget = 1000f  // 总预算
    val spent = 388.6f       // 已支出
    val remaining = totalBudget - spent
    val dailyRemaining = remaining / 15  // 假设一个月剩余 15 天
    val progress = remaining / totalBudget // 计算进度条比例

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp),
        shape = RoundedCornerShape(2.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFECD2), Color(0xFFFCB69F)),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 0f)
                    )
                )
                .padding(12.dp)
        ) {
            // 第一行：X月预算 & 已支出
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("3月预算", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White,)
                Text("已支出: ¥$spent", fontSize = 12.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))
            // 第二行：进度条
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFF4CAF50), // 进度条颜色
                backgroundColor = Color.White.copy(alpha = 0.5f) // 进度条背景
            )

            Spacer(modifier = Modifier.height(8.dp))
            // 第三行：预算使用情况 & 剩余日预算
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$remaining/$totalBudget", fontSize = 12.sp, color = Color.White)
                Text("剩余日预算: ${"%.2f".format(dailyRemaining)}",
                    fontSize = 12.sp,
                    color = Color.White)
            }
        }
    }
}
