import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WebsiteDao {
    // 插入一个 Website 实体到数据库
    @Insert
    suspend fun insert(website: Website)

    // 查询所有 Website 实体
    @Query("SELECT * FROM websites")
    suspend fun getAllWebsites(): List<Website>

    // 根据 ID 查询 Website 实体
    @Query("SELECT * FROM websites WHERE id = :id")
    suspend fun getWebsiteById(id: Long): Website?

    // 更新 Website 实体
    @Update
    suspend fun updateWebsite(website: Website)

    // 删除 Website 实体
    @Delete
    suspend fun deleteWebsite(website: Website)
}

