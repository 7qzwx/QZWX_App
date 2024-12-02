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
    var showDialog by remember { mutableStateOf(false) } // 控制对话框的显示
    var websiteName by remember { mutableStateOf("") } // 网站名称输入
    var websiteUrl by remember { mutableStateOf("") } // 网站网址输入
    var selectedIconIndex by remember { mutableStateOf(-1) } // -1 表示没有选中任何图标
    var errorMessage by remember { mutableStateOf("") } // 错误信息

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
        // 悬浮添加按钮
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd) // 显式使用 Modifier
                .padding(16.dp)
                .size(56.dp),
            shape = RoundedCornerShape(28.dp) // 圆形按钮
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "添加网站",
                tint = Color.White
            )
        }
        // 对话框
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("添加网站") },
                text = {
                    Column {
                        // 四个图标按钮
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val iconResIds = listOf(
                                R.drawable.svg_music1,
                                R.drawable.svg_movie,
                                R.drawable.svg_dongman,
                                R.drawable.svg_web
                            )

                            iconResIds.forEachIndexed { index, iconResId ->
                                IconButton(
                                    onClick = {
                                        selectedIconIndex = index // 记录选中的图标索引
                                    },
                                    modifier = Modifier.background(Color.Transparent) // 设置背景为透明
                                ) {
                                    Icon(
                                        painter = painterResource(id = iconResId),
                                        contentDescription = null,
                                        modifier = Modifier.size(25.dp),
                                        tint = if (selectedIconIndex == index) Color(0xFFFF8F00) else Color.Black // 根据是否选中改变图标颜色
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = websiteName,
                            onValueChange = { websiteName = it },
                            label = { Text("网站名称") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    BorderStroke(1.dp, Color.Transparent),
                                    shape = RoundedCornerShape(0.dp)
                                ), // 添加透明边框，避免背景颜色影响
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent, // 设置背景色为透明
                                focusedIndicatorColor = Color(0xFFEF6C00),
                                focusedLabelColor = Color(0xFF49ACB8), // 设置聚焦时标签颜色
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center) // 居中对齐
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = websiteUrl,
                            onValueChange = { websiteUrl = it },
                            label = { Text("网址") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    BorderStroke(1.dp, Color.Transparent),
                                    shape = RoundedCornerShape(0.dp)
                                ), // 添加透明边框，避免背景颜色影响
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent, // 设置背景色为透明
                                focusedLabelColor = Color(0xFF49ACB8), // 设置聚焦时标签颜色
                                focusedIndicatorColor = Color(0xFF6200EE),
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Left) // 居中对齐
                        )
                        // 显示错误信息
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color.Red, // 错误信息颜色
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (websiteName.isBlank() || websiteUrl.isBlank() || selectedIconIndex == -1) {
                            errorMessage = "请填写完整信息！(图标，名称，网址)"
                        } else {
                            // 在这里添加你的逻辑
                            // 例如：将新网站添加到 linkItems 列表
//                            val newItem = LinkItem(websiteUrl, iconResIds[selectedIconIndex], websiteName)
                            // 在这里可以执行添加逻辑，比如更新状态或将新网站保存到列表中
                            showDialog = false
                        }
                    }) {
                        Text("添加")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("取消")
                    }
                }
            )
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
