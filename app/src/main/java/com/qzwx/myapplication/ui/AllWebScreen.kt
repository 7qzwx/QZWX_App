package com.qzwx.myapplication.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qzwx.myapplication.R

val linkItems = listOf(
    LinkItem("https://7qzwx.github.io/WEB_YJY-1.4/", R.drawable.svg_gerenwangye, "七种文学"),
    LinkItem("https://www.github.com", R.drawable.svg_github, "GitHub"),
    LinkItem("https://yandex.com/", R.drawable.svg_sousuo, "俄罗斯引擎"),
    LinkItem("https://www.mq59.com/", R.drawable.svg_rijiben, "搜书网站"),
    LinkItem("https://heck.ai/", R.drawable.svg_gpt, "免费gpt"),
    LinkItem("https://bz.zzzmh.cn/index", com.qzwx.core.R.drawable.app_bzwz, "壁纸网站"),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllWebScreen() {
//    val allWebsites by viewModel.allWebsites.collectAsState()
    var showAddWebsiteDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "下面是一些可能有用的网站：",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
//                items(allWebsites.size) { index ->
//                    val website = allWebsites[index]
//                    LinkItemView(
//                        link = website.url,
//                        iconResId = website.iconResId,
//                        description = website.name
//                    )
//                }
            }
        }

        FloatingActionButton(
            onClick = { showAddWebsiteDialog = true },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "添加网站")
        }

        if (showAddWebsiteDialog) {
            AddWebsiteDialog(
                onAdd = { name, url ->
//                    val newWebsite = WebsiteEntity(
//                        url = url,
//                        iconResId = R.drawable.svg_web, // 默认图标
//                        name = name
//                    )
//                    viewModel.insertWebsite(newWebsite)
                    showAddWebsiteDialog = false
                },
                onClose = { showAddWebsiteDialog = false }
            )
        }
    }
}

@Composable
fun AddWebsiteDialog(onAdd : (String, String) -> Unit, onClose : () -> Unit) {
    var websiteName by remember { mutableStateOf("") }
    var websiteUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("添加新网站") },
        text = {
            Column {
                TextField(
                    value = websiteName,
                    onValueChange = { websiteName = it },
                    label = { Text("网站名称") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = websiteUrl,
                    onValueChange = { websiteUrl = it },
                    label = { Text("网站链接") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (websiteName.isNotBlank() && websiteUrl.isNotBlank()) {
                    onAdd(websiteName, websiteUrl)
                }
            }) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text("取消")
            }
        }
    )
}

@Composable
fun LinkItemView(link : String, iconResId : Int, description : String) {
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

// 数据类用于存储链接、图标和描述信息
data class LinkItem(val url : String, val iconResId : Int, val description : String)