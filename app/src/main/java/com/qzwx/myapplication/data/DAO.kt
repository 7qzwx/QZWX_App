import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// 定义一个数据访问对象 (DAO) 用于与 Website 实体交互
@Dao
interface WebsiteDao {

    // 插入一个 Website 实体到数据库
    @Insert
    suspend fun insert(website: Website) // 悬挂函数，用于异步插入网站信息

    // 查询所有 Website 实体
    @Query("SELECT * FROM websites")
    suspend fun getAllWebsites(): List<Website> // 悬挂函数，用于异步获取所有网站信息
}