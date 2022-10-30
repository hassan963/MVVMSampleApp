package hassan.mvvmsampleapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Balance(
    @PrimaryKey
    val currency: String,
    val balance: Double
)