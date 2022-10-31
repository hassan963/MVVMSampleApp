package hassan.mvvmsampleapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import hassan.mvvmsampleapp.R
import hassan.mvvmsampleapp.databinding.ActivityMainBinding
import hassan.mvvmsampleapp.model.Currency
import hassan.mvvmsampleapp.ui.fragments.CurrencyListDialogFragment.Companion.TAG_NAME
import hassan.mvvmsampleapp.ui.adapters.BalanceAdapter
import hassan.mvvmsampleapp.ui.fragments.CurrencyListDialogFragment
import hassan.mvvmsampleapp.ui.fragments.MessageDialogFragment
import hassan.mvvmsampleapp.util.roundOffDecimal
import hassan.mvvmsampleapp.viewmodel.CurrencyConverterViewModel
import hassan.mvvmsampleapp.viewmodel.CurrencySelectionViewModel
import java.lang.ref.WeakReference

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CurrencyListDialogFragment.CurrencySelectionListener {
    private var wrThis: WeakReference<MainActivity>? = null

    private val currencyConverterViewModel: CurrencyConverterViewModel by viewModels()
    private val currencySelectionViewModel: CurrencySelectionViewModel by viewModels()

    private val balanceAdapter = BalanceAdapter(emptyList())

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wrThis = WeakReference(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        eventListeners()
        observerLiveData()
    }

    private fun observerLiveData() {
        currencyConverterViewModel.receivedAmountLiveData.observe(this) {
            binding.receiveCurrencyAmountTextview.text = "+ ${roundOffDecimal(it)}"
        }

        currencyConverterViewModel.availableBalanceLiveData.observe(this) {
            balanceAdapter.setData(it)
        }

        currencyConverterViewModel.validationErrorMessageLiveData.observe(this) {
            if (it != -1) {
                val errorMessage = getString(it)
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
            }
        }

        currencyConverterViewModel.isConversionSuccessfulLiveData.observe(this) {
            if (it) {
                val sellAmount =
                    currencyConverterViewModel.sellAmountLiveData.value?.let { amount ->
                        roundOffDecimal(
                            amount
                        )
                    } ?: 0.0

                val receiveAmount =
                    currencyConverterViewModel.receivedAmountLiveData.value?.let { amount ->
                        roundOffDecimal(
                            amount
                        )
                    } ?: 0.0

                val sellCurrency = currencySelectionViewModel.selectedSellCurrency
                val receiveCurrency = currencySelectionViewModel.selectedReceiveCurrency

                val commission =
                    currencyConverterViewModel.commissionAmountLiveData.value?.let { commission ->
                        roundOffDecimal(
                            commission
                        )
                    } ?: 0.0

                var message = String.format(
                    getString(R.string.you_have_converted_successfully_msg),
                    "$sellAmount ${sellCurrency?.currency}",
                    "$receiveAmount ${receiveCurrency?.currency}"
                )

                if (commission > 0.0) {
                    message += String.format(
                        getString(R.string.commission_fee_msg),
                        "$commission ${sellCurrency?.currency}"
                    )
                }

                showDialogFragment(
                    MessageDialogFragment.newInstance(
                        getString(R.string.currency_converted),
                        message
                    ), MessageDialogFragment.TAG_NAME
                )

                currencyConverterViewModel.clearAmountDetails()
                binding.sellCurrencyAmountEdittext.setText("")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        currencySelectionViewModel.isActive = true
        currencySelectionViewModel.fetchCurrencies()
    }

    override fun onPause() {
        super.onPause()
        currencyConverterViewModel.resetConversionState()
        currencySelectionViewModel.isActive = false
        currencySelectionViewModel.fetchCurrencies().cancel()
    }

    private fun setupAdapter() {
        binding.availableBalanceRecyclerview.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = balanceAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun eventListeners() {
        binding.sellCurrencyPicker.setOnClickListener {
            val fragment = CurrencyListDialogFragment.newInstance(this, true)
            showDialogFragment(fragment, TAG_NAME)
        }

        binding.receiveCurrencyPicker.setOnClickListener {
            val fragment = CurrencyListDialogFragment.newInstance(this, false)
            showDialogFragment(fragment, TAG_NAME)
        }

        binding.convertCurrencySubmitButton.setOnClickListener {
            val amount = binding.sellCurrencyAmountEdittext.text.toString()
            if (amount.isNotBlank()) {
                currencyConverterViewModel.saveConvertedAmount(
                    amount.toDouble(),
                    currencyConverterViewModel.receivedAmountLiveData.value,
                    currencySelectionViewModel.selectedSellCurrency,
                    currencySelectionViewModel.selectedReceiveCurrency
                )
            }
        }

        binding.sellCurrencyAmountEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!text.isNullOrBlank()) {
                    currencyConverterViewModel.calculateCurrency(
                        text.toString().toDouble(),
                        currencySelectionViewModel.selectedSellCurrency,
                        currencySelectionViewModel.selectedReceiveCurrency
                    )
                } else {
                    currencyConverterViewModel.clearAmountDetails()
                }
            }

            override fun afterTextChanged(text: Editable?) {

            }

        })
    }

    private fun showDialogFragment(fragment: DialogFragment, tagName: String) {
        if (wrThis?.get() != null && wrThis?.get()?.isFinishing != true) {
            val fm: FragmentManager? = wrThis?.get()?.supportFragmentManager
            val ft: FragmentTransaction? = fm?.beginTransaction()
            val prev: Fragment? = fm?.findFragmentByTag(tagName)
            if (prev != null) ft?.remove(prev)
            ft?.addToBackStack(null)
            if (ft != null) {
                fragment.show(ft, tagName)
            }
        }
    }

    override fun onCurrencySelected(currency: Currency, isModalForSellingCurrency: Boolean) {
        if (isModalForSellingCurrency) {
            currencySelectionViewModel.setSellCurrency(currency)

            binding.sellCurrencyPicker.text = currency.currency

            val amount = binding.sellCurrencyAmountEdittext.text.toString()
            if (amount.isNotBlank()) {
                currencyConverterViewModel.calculateCurrency(
                    amount.toDouble(),
                    currencySelectionViewModel.selectedSellCurrency,
                    currencySelectionViewModel.selectedReceiveCurrency
                )
            }
        } else {
            currencySelectionViewModel.setReceiveCurrency(currency)

            binding.receiveCurrencyPicker.text = currency.currency

            val amount = binding.sellCurrencyAmountEdittext.text.toString()
            if (amount.isNotBlank()) {
                currencyConverterViewModel.calculateCurrency(
                    amount.toDouble(),
                    currencySelectionViewModel.selectedSellCurrency,
                    currencySelectionViewModel.selectedReceiveCurrency
                )
            }
        }
    }
}