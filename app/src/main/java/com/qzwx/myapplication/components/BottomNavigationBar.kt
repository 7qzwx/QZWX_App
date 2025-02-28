import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

// 底部导航栏项的数据类
data class BottomNavItem(
    val label : String,
    val iconResId : Int,
    val route : String
)

// 底部导航栏的 Composable 函数
@Composable
fun CustomBottomNavigationBar(
    items : List<BottomNavItem>,
    navController : NavHostController
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Box(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(Color(0xFF310968))
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(56.dp)
                        .animateContentSize() // 添加动画效果
                        .width(if (selected) 70.dp else 56.dp)
                        .clickable(
                            indication = null, // 禁用默认的涟漪效果
                            interactionSource = null, // 可选：禁用交互源
                        ) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                ) {
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(Color.Magenta)
                        )
                    }

                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                        tint = if (selected) Color.White else Color.Gray
                    )
                }
            }
        }
    }
}
