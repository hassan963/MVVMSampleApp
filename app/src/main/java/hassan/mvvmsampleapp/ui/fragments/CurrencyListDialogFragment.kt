package hassan.mvvmsampleapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import hassan.mvvmsampleapp.databinding.DialogCurrencySelectionBinding
import hassan.mvvmsampleapp.model.Currency
import hassan.mvvmsampleapp.ui.adapters.CurrencyListAdapter
import hassan.mvvmsampleapp.viewmodel.CurrencySelectionViewModel

@AndroidEntryPoint
class CurrencyListDialogFragment(private val listener: CurrencySelectionListener) :
    BottomSheetDialogFragment() {
    private var binding: DialogCurrencySelectionBinding? = null
    private lateinit var viewModel: CurrencySelectionViewModel
    private var isModalForSellingCurrency: Boolean = true

    interface CurrencySelectionListener {
        fun onCurrencySelected(currency: Currency, isModalForSellingCurrency: Boolean)
    }

    private val currencyListAdapter = CurrencyListAdapter(emptyList()) { selectedCurrency ->
        listener.onCurrencySelected(selectedCurrency, isModalForSellingCurrency)
        dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.isModalForSellingCurrency = arguments?.getBoolean(KEY_MODAL_TYPE, false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCurrencySelectionBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { viewModel = ViewModelProvider(it)[CurrencySelectionViewModel::class.java] }

        setupAdapter()
        observerLiveData()
    }

    private fun setupAdapter() {
        binding?.currencyOptionsRecyclerview?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = currencyListAdapter
        }
    }

    private fun observerLiveData() {
        viewModel.currencyRatesLiveData.observe(this) { list ->
            if (isModalForSellingCurrency) {
                list.forEach {
                    it.isSelected = it.currency == viewModel.selectedSellCurrency?.currency
                }
            } else {
                list.forEach {
                    it.isSelected = it.currency == viewModel.selectedReceiveCurrency?.currency
                }
            }

            list.sortByDescending { it.isSelected }
            currencyListAdapter.setData(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val TAG_NAME = "CurrencyListDialogFragment"
        const val KEY_MODAL_TYPE = "keyModalType"

        fun newInstance(
            listener: CurrencySelectionListener,
            isModalForSellingCurrency: Boolean
        ): CurrencyListDialogFragment {
            val dialog = CurrencyListDialogFragment(listener)
            val bundle = Bundle()
            bundle.putBoolean(KEY_MODAL_TYPE, isModalForSellingCurrency)
            dialog.arguments = bundle
            return dialog
        }
    }
}