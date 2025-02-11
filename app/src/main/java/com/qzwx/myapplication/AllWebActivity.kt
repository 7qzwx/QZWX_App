package com.qzwx.myapplication

import android.content.*
import android.net.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.qzwx.diary.theme.*

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllWebScreen() {
    // 定义一些示例链接项
    val linkItems = listOf(
        LinkItem("https://7qzwx.github.io/WEB_YJY-1.4/", R.drawable.svg_gerenwangye, "七种文学"),
        LinkItem("https://sharedchat.cn/", R.drawable.svg_gpt, "GPT3.5-4o"),
        LinkItem("https://chatglm.cn/main/alltoolsdetail", R.drawable.svg_zhipuqingyan, "智谱清言"),
        LinkItem("https://www.github.com", R.drawable.svg_github, "GitHub"),
        LinkItem("https://yandex.com/", R.drawable.svg_sousuo, "俄罗斯引擎"),
        LinkItem("https://musicjx.com", R.drawable.svg_music, "音乐解析器"),
        LinkItem("https://flac.life", R.drawable.svg_music, "无损生活"),
        LinkItem("https://www.gequbao.com", R.drawable.svg_music, "歌曲宝"),
        LinkItem("https://music.alang.run", R.drawable.svg_music, "听歌房"),
        LinkItem("https://cupfox.app/", R.drawable.svg_chabeihu, "茶杯狐"),
        LinkItem("https://www.p9yy.com/", R.drawable.svg_yinghshi, "P9影视"),
        LinkItem("https://ddys.one/", R.drawable.svg_yinghshi, "低端影视"),
        LinkItem("https://dianyi.ng/", R.drawable.svg_yinghshi, "电影先生"),
        LinkItem("https://keai.cm/", R.drawable.svg_yinghshi, "可爱TV"),
        LinkItem("https://www.haituw.com/", R.drawable.svg_yinghshi, "海兔影视"),
        LinkItem("https://24pindao.tv/", R.drawable.svg_yinghshi, "美剧频道"),
        LinkItem("https://69mj.com/", R.drawable.svg_yinghshi, "69美剧"),
        LinkItem("https://www.netflixgc.com/", R.drawable.svg_yinghshi, "奈飞美剧"),
        LinkItem("https://fitacg.com/", R.drawable.svg_dongman, "菲特动漫"),
        LinkItem("http://www.dmd77.com/", R.drawable.svg_dongman, "动漫岛"),
        LinkItem("http://buding22.com/", R.drawable.svg_dongman, "布丁动漫"),
        LinkItem("https://m.agedm.org/#/", R.drawable.svg_dongman, "AGE动漫"),
        LinkItem("https://m.agedm.org/#/", R.drawable.svg_dongman, "AGE动漫"),
        LinkItem(
            "https://www.logosc.cn/favicon-generator?s=%E9%A1%B5%E9%9D%A2",
            R.drawable.svg_web, "在线制作图标"
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 标题和网格内容
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(linkItems.size) { index ->
                    val item = linkItems[index]
                    LinkItemView(
                        link = item.url,
                        iconResId = item.iconResId,
                        description = item.description
                    )
                }
            }
        }

    }
}

@Composable
fun LinkItemView(link: String, iconResId: Int, description: String) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .clickable {
                // 打开网页链接
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
            }
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = description,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis, // 省略文本
                maxLines = 1 // 最大行数
            )
        }
    }
}
