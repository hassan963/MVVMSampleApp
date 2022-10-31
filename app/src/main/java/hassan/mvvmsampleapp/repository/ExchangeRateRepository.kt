package hassan.mvvmsampleapp.repository

import dagger.hilt.android.scopes.ActivityRetainedScoped
import hassan.mvvmsampleapp.model.*
import hassan.mvvmsampleapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class ExchangeRateRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val db: ExchangeRateDatabase
) : BaseApiResponse() {

    suspend fun fetchCurrencies(): kotlinx.coroutines.flow.Flow<NetworkResult<CurrencyExchangeRate>> {
        return flow {
            emit(safeApiCall { remoteDataSource.fetchExchangeRates() })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun saveCurrencies(currencies: MutableList<Currency>) {
        db.getDatabase().saveCurrencies(currencies)
    }

    suspend fun getAllCurrencies() =
        db.getDatabase().getCurrencies()

    suspend fun getAllCurrenciesBalance() =
        db.getDatabase().getAllCurrenciesBalance()

    suspend fun getBalance(currency: String) =
        db.getDatabase().getBalance(currency)

    suspend fun saveAllCurrenciesBalance(balances: MutableList<Balance>) =
        db.getDatabase().saveAllCurrenciesBalance(balances)

    suspend fun saveBalance(balance: Balance) =
        db.getDatabase().saveBalance(balance)

    suspend fun saveConversionHistory(conversionHistory: ConversionHistory) =
        db.getDatabase().saveConversionHistory(conversionHistory)

    suspend fun getTotalConversionCount() =
        db.getDatabase().getTotalConversionCount()

    suspend fun getTotalConversionAmount(currency: String) =
        db.getDatabase().getTotalConversionAmount(currency)
}