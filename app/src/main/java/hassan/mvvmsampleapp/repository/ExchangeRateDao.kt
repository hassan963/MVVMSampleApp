package hassan.mvvmsampleapp.repository

import androidx.room.*
import hassan.mvvmsampleapp.model.Balance
import hassan.mvvmsampleapp.model.ConversionHistory
import hassan.mvvmsampleapp.model.Currency

@Dao
interface ExchangeRateDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveCurrencies(currencies: MutableList<Currency>)

  @Query("select * FROM Currency WHERE currency = :currency")
  suspend fun getCurrency(currency: String): Currency

  @Query("select * FROM Currency")
  suspend fun getCurrencies(): MutableList<Currency>

  @Query("select * FROM Balance ORDER BY balance DESC")
  suspend fun getAllCurrenciesBalance(): MutableList<Balance>

  @Query("select * FROM Balance WHERE currency = :currency")
  suspend fun getBalance(currency: String): Balance

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveAllCurrenciesBalance(balance: MutableList<Balance>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveBalance(balance: Balance)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveConversionHistory(conversionHistory: ConversionHistory)

  @Query("select COUNT(id) FROM ConversionHistory")
  suspend fun getTotalConversionCount(): Int

  @Query("select SUM(sellAmount) as total FROM ConversionHistory WHERE sellCurrency = :sellCurrency")
  suspend fun getTotalConversionAmount(sellCurrency: String): Double
}