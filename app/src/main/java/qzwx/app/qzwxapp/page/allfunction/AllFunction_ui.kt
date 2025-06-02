package qzwx.app.qzwxapp.page.allfunction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlin.random.Random
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import qzwx.app.qzwxapp.R

// 展示模式枚举
enum class DisplayMode {
    LIST, GRID, STAGGERED
}

// 应用项数据类
data class AppItem(
    val title: String?="",
    val color: Color,
    val textColor: Color,
    val description: String?="",
    val onClick: () -> Unit,
    val backgroundImage: Int? = null
)

/**
 * 功能中心顶部栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FunctionTopBar(
    currentDisplayMode: DisplayMode,
    onModeChange: (DisplayMode) -> Unit,
    colorScheme: ColorScheme
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "功能中心",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    )
                )
                Text(
                    text = "探索应用的所有功能",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onModeChange(DisplayMode.LIST) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (currentDisplayMode == DisplayMode.LIST)
                        colorScheme.primaryContainer else Color.Transparent,
                    contentColor = if (currentDisplayMode == DisplayMode.LIST)
                        colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ViewList,
                    contentDescription = "列表视图"
                )
            }
            
            IconButton(
                onClick = { onModeChange(DisplayMode.GRID) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (currentDisplayMode == DisplayMode.GRID)
                        colorScheme.primaryContainer else Color.Transparent,
                    contentColor = if (currentDisplayMode == DisplayMode.GRID)
                        colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = "网格视图"
                )
            }
            
            IconButton(
                onClick = { onModeChange(DisplayMode.STAGGERED) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (currentDisplayMode == DisplayMode.STAGGERED)
                        colorScheme.primaryContainer else Color.Transparent,
                    contentColor = if (currentDisplayMode == DisplayMode.STAGGERED)
                        colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = "瀑布流视图"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.surface,
            titleContentColor = colorScheme.onSurface
        )
    )
}

/**
 * 功能内容区域，根据不同显示模式显示不同的内容布局
 */
@Composable
fun FunctionContent(
    currentDisplayMode: DisplayMode,
    appItems: List<AppItem>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = currentDisplayMode == DisplayMode.LIST,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(appItems) { item ->
                    ListAppCard(item)
                }
            }
        }
        
        AnimatedVisibility(
            visible = currentDisplayMode == DisplayMode.GRID,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(appItems) { item ->
                    GridAppCard(item)
                }
            }
        }
        
        AnimatedVisibility(
            visible = currentDisplayMode == DisplayMode.STAGGERED,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(appItems) { item ->
                    StaggeredAppCard(item)
                }
            }
        }
    }
}

/**
 * 网格视图中的应用卡片
 */
@Composable
fun GridAppCard(app: AppItem) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { 
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { app.onClick() }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (app.backgroundImage == null) app.color else Color.Transparent
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (app.backgroundImage != null) {
                Image(
                    painter = painterResource(id = app.backgroundImage),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                app.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = app.textColor
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                app.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = app.textColor.copy(alpha = 0.8f)
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * 列表视图中的应用卡片
 */
@Composable
fun ListAppCard(app: AppItem) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { 
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { app.onClick() }
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (app.backgroundImage == null) app.color else Color.Transparent
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (app.backgroundImage != null) {
                Image(
                    painter = painterResource(id = app.backgroundImage),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                app.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = app.textColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                app.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = app.textColor.copy(alpha = 0.8f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * 瀑布流视图中的应用卡片
 */
@Composable
fun StaggeredAppCard(app: AppItem) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { 
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { app.onClick() }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (app.backgroundImage == null) app.color else Color.Transparent
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (app.backgroundImage != null) {
                Image(
                    painter = painterResource(id = app.backgroundImage),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                app.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = app.textColor
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                app.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = app.textColor.copy(alpha = 0.8f)
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
