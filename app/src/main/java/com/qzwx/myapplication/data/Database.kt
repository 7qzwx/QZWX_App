import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context

// 定义 Room 数据库
@Database(entities = [Website::class], version = 1, exportSchema = false) // 指定支持的实体类型和数据库版本
abstract class AppDatabase : RoomDatabase() {

    // 获取 WebsiteDao 实例的方法
    abstract fun websiteDao(): WebsiteDao

    // 单例模式实现，确保只有一个实例存在
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 获取数据库实例的方法
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // 使用应用上下文以避免内存泄漏
                    AppDatabase::class.java, // 数据库类
                    "app_database" // 数据库文件名
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}