package hassan.mvvmsampleapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConversionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sellCurrency: String,
    val receiveCurrency: String,
    val sellAmount: Double,
    val receiveAmount: Double
)