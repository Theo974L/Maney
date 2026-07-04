import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.theolaforgeeval.data.local.dao.CategoryDao
import com.example.theolaforgeeval.data.local.dao.TransactionDao
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.model.TransactionActionEntity

@Database(
    entities = [
        CategoryEntity::class,
        TransactionActionEntity::class
               ],
    version = 8
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao() : TransactionDao
}