package hassan.mvvmsampleapp.repository

import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun fetchExchangeRates() = apiService.fetchExchangeRates()
}