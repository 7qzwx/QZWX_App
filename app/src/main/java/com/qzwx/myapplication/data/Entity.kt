import androidx.room.Entity
import androidx.room.PrimaryKey

// 定义一个实体类 Website，用于表示一个网站的信息
@Entity(tableName = "websites") // 指定此实体对应的数据库表名为 "websites"
data class Website(
    @PrimaryKey(autoGenerate = true) // 将 id 定义为主键，并且自动递增
    val id: Long = 0, // 网站的唯一标识符

    val url: String, // 网站的 URL 地址

    val iconResId: Int, // 网站图标的资源 ID

    val name: String // 网站的名称
)