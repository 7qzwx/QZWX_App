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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qzwx.diary.theme.QZWXTheme

// 数据类用于存储链接、图标和描述信息
data class LinkItem(val url: String, val iconResId: Int, val description: String)

class AllWebActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QZWXTheme {
                AllWebScreen()
            }
        }
    }
}

@Composable
fun AllWebScreen() {
    // 定义一些示例链接项
    val linkItems = listOf(
        LinkItem("https://7qzwx.github.io/WEB_YJY-1.4/", R.drawable.svg_gerenwangye, "七种文学网页"),
        LinkItem("https://sharedchat.cn/", R.drawable.svg_gpt, "国内免费使用GPT3.5-4o"),
        LinkItem("https://chatglm.cn/main/alltoolsdetail", R.drawable.svg_zhipuqingyan, "智谱清言（国内AI）"),
        LinkItem("https://www.github.com", R.drawable.svg_github, "GitHub"),
        LinkItem("https://yandex.com/",R.drawable.svg_sousuo,"俄罗斯所搜引擎"),
        LinkItem("https://musicjx.com",R.drawable.svg_music, "音乐下载，在线播放①"),
        LinkItem("https://flac.life", R.drawable.svg_music, "音乐下载，在线播放②"),
        LinkItem("https://www.gequbao.com", R.drawable.svg_music, "音乐下载，在线播放③"),
        LinkItem("https://music.itzo.cn", R.drawable.svg_music, "音乐下载，在线播放④"),
        LinkItem("https://music.alang.run", R.drawable.svg_music, "音乐房间，可一起听音乐"),
        LinkItem("https://cupfox.app/", R.drawable.svg_chabeihu, "茶杯狐【看电影】"),
        LinkItem("https://www.p9yy.com/", R.drawable.svg_yinghshi, "P9影视【看电影✔】"),
        LinkItem("https://ddys.one/", R.drawable.svg_yinghshi, "低端影视【看电影】"),
        LinkItem("https://dianyi.ng/", R.drawable.svg_yinghshi, "电影先生【看电影√】"),
        LinkItem("https://keai.cm/", R.drawable.svg_yinghshi, "可爱TV【看电影】"),
        LinkItem("https://www.haituw.com/", R.drawable.svg_yinghshi, "海兔影视【看电影】"),
        LinkItem("https://24pindao.tv/", R.drawable.svg_yinghshi, "美剧频道"),
        LinkItem("https://69mj.com/", R.drawable.svg_yinghshi, "69美剧"),
        LinkItem("https://www.netflixgc.com/", R.drawable.svg_yinghshi, "奈飞美剧"),
        LinkItem("https://fitacg.com/", R.drawable.svg_dongman, "菲特动漫"),
        LinkItem("http://www.dmd77.com/", R.drawable.svg_dongman, "动漫岛"),
        LinkItem("http://buding22.com/", R.drawable.svg_dongman, "布丁动漫"),
        LinkItem("https://m.agedm.org/#/", R.drawable.svg_dongman, "AGE动漫"),
        LinkItem("https://www.fre321.com/anime/s", R.drawable.svg_dongman, "动漫搜索")


    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF310952)) // 使用柔和的背景颜色
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "下面是一些可能有用的网站：",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFFCAEAE), // 更深的颜色提高可读性
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

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
            .shadow(4.dp, RoundedCornerShape(8.dp)) // 添加阴影效果
            .background(Color.White, shape = RoundedCornerShape(8.dp))
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
                modifier = Modifier.size(32.dp) // 增加图标大小
            )
            Spacer(modifier = Modifier.width(16.dp)) // 增加间距
            Column {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF333333), // 更深的颜色提高可读性
                        fontWeight = FontWeight.Bold // 加粗字体
                    )
                )
                Text(
                    text = link,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF1E88E5) // 使用更鲜艳的蓝色
                    )
                )
            }
        }
    }
}

// 添加预览功能
@Preview(showBackground = true)
@Composable
fun AllWebScreenPreview() {
    QZWXTheme {
        AllWebScreen()
    }
}
