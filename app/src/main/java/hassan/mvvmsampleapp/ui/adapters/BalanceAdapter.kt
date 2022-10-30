package hassan.mvvmsampleapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hassan.mvvmsampleapp.R
import hassan.mvvmsampleapp.base.BaseRecyclerViewAdapter
import hassan.mvvmsampleapp.databinding.ItemCurrencyBalanceBinding
import hassan.mvvmsampleapp.model.Balance
import hassan.mvvmsampleapp.util.roundOffDecimal

class BalanceAdapter(listOfBalance: List<Balance>) :
    BaseRecyclerViewAdapter<Balance, BalanceAdapter.BalanceItemViewHolder>(listOfBalance.toMutableList()) {

    class BalanceItemViewHolder(private val binding: ItemCurrencyBalanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(balance: Balance) {
            val amount = "${roundOffDecimal(balance.balance)} ${balance.currency}"
            binding.balanceTextview.text = amount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceItemViewHolder {
        val binding: ItemCurrencyBalanceBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_currency_balance,
            parent,
            false
        );
        return BalanceItemViewHolder(binding);
    }

    override fun onBindViewHolder(holder: BalanceItemViewHolder, position: Int) {
        holder.bindData(mList[position])
    }

    override fun setData(list: List<Balance>) {
        val diffCallback = BalanceDiffCallback(this.mList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        mList.clear()
        mList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}