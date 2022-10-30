package hassan.mvvmsampleapp.model

data class CurrencyExchangeRate (
    val base: String?= null,
    val data: String? = null,
    var rates: Map<String, Double>? = null
)