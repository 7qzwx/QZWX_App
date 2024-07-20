package com.qzwx.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.qzwx.myapplication.ui.theme.MyApplicationTheme

// 数据类用于存储链接、图标和描述信息
data class LinkItem(val url: String, val iconResId: Int, val description: String)

class AllWebActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                AllWebScreen()
            }
        }
    }
}

@Composable
fun AllWebScreen() {
    // Define some sample link items
    val linkItems = listOf(
        LinkItem("https://www.baidu.com", R.drawable.svg_web, "百度"),
        LinkItem("https://www.google.com", R.drawable.svg_web, "Google"),
        LinkItem("https://www.github.com", R.drawable.svg_web, "GitHub"),
        LinkItem("https://www.bilibili.com", R.drawable.svg_web, "Bilibili")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Gray)
    ) {
        Text(
            text = "下面是一些有用的网站：",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(linkItems) { item ->
                LinkItemView(link = item.url, iconResId = item.iconResId, description = item.description)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun LinkItemView(link: String, iconResId: Int, description: String) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(link)
                }
                context.startActivity(Intent.createChooser(intent, "选择浏览器"))
            }
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black
                    )
                )
                Text(
                    text = link,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Blue
                    )
                )
            }
        }
    }
}
