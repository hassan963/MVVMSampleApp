package hassan.mvvmsampleapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hassan.mvvmsampleapp.model.Currency
import hassan.mvvmsampleapp.repository.ExchangeRateRepository
import hassan.mvvmsampleapp.util.NetworkResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencySelectionViewModel @Inject constructor(private val repository: ExchangeRateRepository) :
    ViewModel() {
    private val currencyRates: MutableLiveData<MutableList<Currency>> = MutableLiveData()
    val currencyRatesLiveData: LiveData<MutableList<Currency>> = currencyRates

    var selectedSellCurrency: Currency? = null
    var selectedReceiveCurrency: Currency? = null

    var isActive: Boolean = true

    init {
        viewModelScope.launch {
            val cachedCurrencyRates = repository.getAllCurrencies()
            if (cachedCurrencyRates.isNotEmpty()) {
                currencyRates.value = cachedCurrencyRates
            }
        }
    }

    fun fetchCurrencies() = viewModelScope.launch {
        while (isActive) {
            repository.fetchCurrencies().collect { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        val listOfCurrencies = mutableListOf<Currency>()

                        response.data?.rates?.forEach { data ->
                            listOfCurrencies.add(Currency(currency = data.key, rate = data.value))
                        }

                        repository.saveCurrencies(listOfCurrencies)

                        currencyRates.value = listOfCurrencies
                    }

                    is NetworkResult.Error -> {
                        isActive = false
                    }

                    is NetworkResult.Loading -> {

                    }
                }
                delay(5000)
            }
        }
    }

    fun setSellCurrency(currency: Currency) {
        selectedSellCurrency = currency
    }

    fun setReceiveCurrency(currency: Currency) {
        selectedReceiveCurrency = currency
    }
}