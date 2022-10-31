package hassan.mvvmsampleapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import hassan.mvvmsampleapp.model.ConversionHistory
import hassan.mvvmsampleapp.model.Currency
import hassan.mvvmsampleapp.repository.ExchangeRateDao
import hassan.mvvmsampleapp.repository.ExchangeRateDatabase
import hassan.mvvmsampleapp.util.CURRENCY_EUR
import hassan.mvvmsampleapp.util.CURRENCY_USD
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LocalDBTest {
    private lateinit var database: ExchangeRateDatabase
    private lateinit var dao: ExchangeRateDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ExchangeRateDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.getDatabase()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        database.close()
    }

    @Test
    fun writeReadCurrency() = runBlocking {
        val list = mutableListOf<Currency>()
        val euro = Currency(CURRENCY_EUR, 100.00)
        list.add(euro)
        dao.saveCurrencies(list)

        val existingCurrency = dao.getCurrencies()
        assertThat(existingCurrency.firstOrNull { it.currency == euro.currency } != null).isTrue()
    }

    @Test
    fun getSpecificCurrencyTotalConversionAmount() = runBlocking {
        val conversion = ConversionHistory(
            sellCurrency = CURRENCY_EUR,
            receiveCurrency = CURRENCY_USD,
            sellAmount = 100.00,
            receiveAmount = 99.65
        )
        dao.saveConversionHistory(conversion)

        val history = dao.getTotalConversionAmount(CURRENCY_EUR)

        assertThat(history == 100.00).isTrue()
    }
}