package hassan.mvvmsampleapp.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import hassan.mvvmsampleapp.model.Balance

class BalanceDiffCallback(private val oldList: List<Balance>, private val newList: List<Balance>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].currency == newList[newItemPosition].currency && oldList[oldItemPosition].balance == newList[newItemPosition].balance
    }
}