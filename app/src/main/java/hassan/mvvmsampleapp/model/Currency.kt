package hassan.mvvmsampleapp.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class Currency(
    @PrimaryKey
    val currency: String,
    val rate: Double,
) {
    @Ignore
    var isSelected: Boolean = false
}