package hassan.mvvmsampleapp.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import hassan.mvvmsampleapp.model.Currency

class CurrencyDiffCallback (private val oldList: List<Currency>, private val newList: List<Currency>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].currency == newList[newItemPosition].currency
    }
}