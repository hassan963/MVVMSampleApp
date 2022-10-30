package hassan.mvvmsampleapp.model

import hassan.mvvmsampleapp.util.*


enum class DefaultCurrency (val currency: String, val balance: Double) {
    EUR(CURRENCY_EUR, CURRENCY_EUR_BALANCE),
    USD(CURRENCY_USD, CURRENCY_USD_BALANCE)
}