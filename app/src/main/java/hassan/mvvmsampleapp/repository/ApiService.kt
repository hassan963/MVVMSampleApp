package hassan.mvvmsampleapp.repository

import hassan.mvvmsampleapp.model.CurrencyExchangeRate
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("tasks/api/currency-exchange-rates")
    suspend fun fetchExchangeRates(): Response<CurrencyExchangeRate>
}