package hassan.mvvmsampleapp.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hassan.mvvmsampleapp.model.Balance
import hassan.mvvmsampleapp.model.ConversionHistory
import hassan.mvvmsampleapp.model.Currency
import hassan.mvvmsampleapp.repository.ExchangeRateRepository
import hassan.mvvmsampleapp.util.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val repository: ExchangeRateRepository
) : ViewModel() {
    private val availableBalance: MutableLiveData<MutableList<Balance>> = MutableLiveData()
    val availableBalanceLiveData: LiveData<MutableList<Balance>> = availableBalance

    private val sellAmount: MutableLiveData<Double> = MutableLiveData(0.0)
    val sellAmountLiveData: LiveData<Double> = sellAmount

    private val receivedAmount: MutableLiveData<Double> = MutableLiveData(0.0)
    val receivedAmountLiveData: LiveData<Double> = receivedAmount

    private val currencyConversionMessage: MutableLiveData<String> = MutableLiveData("")
    val currencyConversionMessageLiveData: LiveData<String> = currencyConversionMessage

    private val isConversionSuccessful: MutableLiveData<Boolean> = MutableLiveData(false)
    val isConversionSuccessfulLiveData: LiveData<Boolean> = isConversionSuccessful

    private val commissionAmount: MutableLiveData<Double> = MutableLiveData(0.0)
    val commissionAmountLiveData: LiveData<Double> = commissionAmount

    init {
        viewModelScope.launch {
            loadBalance()
        }
    }

    fun calculateCurrency(amount: Double, sellCurrency: Currency?, receiveCurrency: Currency?) {
        if (validateAmount(amount, sellCurrency, receiveCurrency)) {
            sellAmount.value = amount
            try {
                receivedAmount.value = (receiveCurrency?.rate!! / sellCurrency?.rate!!) * amount
            } catch (exception: Exception) {
                receivedAmount.value = 0.0
            }
        }
    }

    fun saveConvertedAmount(sellAmount: Double, receiveAmount: Double?, sellCurrency: Currency?, receiveCurrency: Currency?) {
        if (validateAmount(sellAmount, sellCurrency, receiveCurrency)) {

            viewModelScope.launch {
                val sellBalance: Balance = repository.getBalance(sellCurrency?.currency!!)

                val isDifferentCurrenciesSelected =
                    receiveCurrency?.currency != sellCurrency.currency

                if (!isDifferentCurrenciesSelected) {
                    currencyConversionMessage.value =
                        "Please select different currencies to convert!"
                    return@launch
                }

                if (sellBalance == null || sellBalance.balance <= 0.0) {
                    currencyConversionMessage.value = "Fund not available!"
                    return@launch
                }

                var receiveBalance: Balance? = repository.getBalance(receiveCurrency?.currency!!)

                var newSellAmount = sellBalance.balance - sellAmount

                isConversionFree()
                newSellAmount -= commissionAmount.value ?: 0.0

                if (receiveBalance == null) {
                    receiveBalance = Balance(receiveCurrency.currency, 0.0)
                }

                val newReceiveAmount = receiveBalance.balance + (receiveAmount ?: 0.0)

                repository.saveBalance(Balance(sellBalance.currency, newSellAmount))
                repository.saveBalance(Balance(receiveBalance.currency, newReceiveAmount))

                repository.saveConversionHistory(
                    ConversionHistory(
                        sellCurrency = sellBalance.currency,
                        sellAmount = sellAmount,
                        receiveCurrency = receiveBalance.currency,
                        receiveAmount = newReceiveAmount
                    )
                )

                isConversionSuccessful.value = newReceiveAmount > 0.0

                loadBalance()
            }
        }
    }

    private fun isConversionFree() = viewModelScope.launch {
        var totalConversionCount: Int? = repository.getTotalConversionCount()
        totalConversionCount = totalConversionCount ?: 0

        var totalConversionAmount: Double? = repository.getTotalConversionAmount(CURRENCY_EUR)
        totalConversionAmount = totalConversionAmount ?: 0.0

        val shouldAddCommission = totalConversionCount >= NUMBER_OF_FREE_CONVERSION_LIMIT ||
                totalConversionAmount >= TOTAL_AMOUNT_OF_FREE_CONVERSION_FOR_EUR_LIMIT ||
                (totalConversionCount > 0.0 && totalConversionCount % 10 == 0)

        commissionAmount.value = if (shouldAddCommission) {
            COMMISSION_AFTER_FREE_CONVERSION_LIMIT
        } else {
            0.0
        }
    }

    private fun loadBalance() {
        viewModelScope.launch {
            val currentBalances = repository.getAllCurrenciesBalance()

            if (currentBalances.isEmpty()) {
                val balances = mutableListOf<Balance>()
                balances.add(Balance(CURRENCY_EUR, CURRENCY_EUR_BALANCE))
                balances.add(Balance(CURRENCY_USD, CURRENCY_USD_BALANCE))

                repository.saveAllCurrenciesBalance(balances)

                availableBalance.value = balances
            } else {
                availableBalance.value = currentBalances
            }
        }
    }

    private fun validateAmount(amount: Double, sellCurrency: Currency?, receiveCurrency: Currency?): Boolean {
        val isCurrenciesSelected =
            receiveCurrency?.currency != null && sellCurrency?.currency != null

        if (!isCurrenciesSelected) {
            return false
        }

        if (amount <= 0.0) {
            currencyConversionMessage.value = "Please enter a valid amount!"
            return false
        }
        return true
    }

    fun clearAmountDetails() {
        sellAmount.value = 0.0
        receivedAmount.value = 0.0
    }

    fun resetConversionState() {
        isConversionSuccessful.value = false
    }
}