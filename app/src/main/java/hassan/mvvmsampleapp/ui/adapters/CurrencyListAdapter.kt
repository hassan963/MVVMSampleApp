package hassan.mvvmsampleapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hassan.mvvmsampleapp.R
import hassan.mvvmsampleapp.base.BaseRecyclerViewAdapter
import hassan.mvvmsampleapp.databinding.ItemCurrencyOptionBinding
import hassan.mvvmsampleapp.model.Currency

class CurrencyListAdapter(listOfCurrencies: List<Currency>, private val onSelectedCurrency: (Currency) -> Unit) :
    BaseRecyclerViewAdapter<Currency, CurrencyListAdapter.CurrencyItemViewHolder>(listOfCurrencies.toMutableList()) {

    inner class CurrencyItemViewHolder(private val binding: ItemCurrencyOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(currency: Currency, position: Int) {
            binding.currencyNameCheckbox.setOnCheckedChangeListener(null)

            binding.currencyNameCheckbox.text = currency.currency
            binding.currencyNameCheckbox.isChecked = currency.isSelected


            binding.currencyNameCheckbox.setOnClickListener {
                currency.isSelected = !currency.isSelected
                notifyItemChanged(position)
                onSelectedCurrency(currency)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyItemViewHolder {
        val binding: ItemCurrencyOptionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_currency_option,
            parent,
            false
        )
        return CurrencyItemViewHolder(binding);
    }

    override fun onBindViewHolder(holder: CurrencyItemViewHolder, position: Int) {
        holder.bindData(mList[position], position)
    }

    override fun setData(list: List<Currency>, selectedItem: Currency) {
        val diffCallback = CurrencyDiffCallback(this.mList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        mList.clear()
        list.forEach { it.isSelected = it.currency == selectedItem.currency }
        mList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}