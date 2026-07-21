import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.theolaforgeeval.data.local.dao.CategoryDao
import com.example.theolaforgeeval.data.local.dao.RecurringTransactionDao
import com.example.theolaforgeeval.data.local.dao.TransactionDao
import com.example.theolaforgeeval.data.local.database.Converters
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.model.RecurringTransactionEntity
import com.example.theolaforgeeval.model.TransactionActionEntity

@Database(
    entities = [
        CategoryEntity::class,
        TransactionActionEntity::class,
        RecurringTransactionEntity::class
               ],
    version = 11
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao() : TransactionDao
    abstract fun recurringTransactionDao(): RecurringTransactionDao
}