//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.qzwx.myapplication.room.AppDatabase
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//// CoreActivity.kt
//class MainActivity : AppCompatActivity() {
//    private lateinit var websiteViewModel: WebsiteViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // 获取数据库实例
//        val database = AppDatabase.getInstance(this)
//
//        // 初始化 ViewModel
//        val factory = WebsiteViewModelFactory(database.websiteDao())
//        websiteViewModel = ViewModelProvider(this, factory).get(WebsiteViewModel::class.java)
//
//        // 使用 ViewModel
//        websiteViewModel.getAllWebsites().observe(this) { websites ->
//            // 更新 UI
//        }
//    }
//}
//
//// WebsiteViewModel.kt
//class WebsiteViewModel(private val websiteDao: WebsiteDao) : ViewModel() {
//    // 获取所有网站
//    fun getAllWebsites(): LiveData<List<Website>> {
//        return websiteDao.getAllWebsites()
//    }
//
//    // 插入网站
//    fun insertWebsite(url: String, iconResId: Int, name: String) {
//        viewModelScope.launch {
//            val website = Website(url = url, iconResId = iconResId, name = name)
//            websiteDao.insert(website)
//        }
//    }
//}
//
//class WebsiteViewModelFactory(private val websiteDao: WebsiteDao) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(WebsiteViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return WebsiteViewModel(websiteDao) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}