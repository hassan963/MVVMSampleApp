package hassan.mvvmsampleapp.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hassan.mvvmsampleapp.model.Balance
import hassan.mvvmsampleapp.model.ConversionHistory
import hassan.mvvmsampleapp.model.Currency

@Database(entities = [Currency::class, Balance::class, ConversionHistory::class], version = 1)
abstract class ExchangeRateDatabase: RoomDatabase() {
    abstract fun getDatabase() : ExchangeRateDao

    companion object {
        private const val DB_NAME = "exchange_rate_database.db"

        @Volatile
        private var instance: ExchangeRateDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(lock = LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ExchangeRateDatabase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()
    }
}