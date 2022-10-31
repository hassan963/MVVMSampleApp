package hassan.mvvmsampleapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import hassan.mvvmsampleapp.model.Currency
import hassan.mvvmsampleapp.repository.ExchangeRateRepository
import hassan.mvvmsampleapp.viewmodel.CurrencyConverterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CurrencyConverterViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: ExchangeRateRepository
    private lateinit var viewModel: CurrencyConverterViewModel


    @Before
    fun setup() {
        viewModel = CurrencyConverterViewModel(repository)
    }

    @Test
    fun isValidAmount() {
        mainCoroutineRule.dispatcher.runBlockingTest {
            val sellCurrency = Currency("EUR", 1.0)
            val receiveCurrency = Currency("USD", 1.129031)

            Mockito.`when`(viewModel.calculateCurrency(100.00, sellCurrency, receiveCurrency)).thenReturn(
                assert((viewModel.receivedAmountLiveData.value ?: 0.0) > 0.0)
            )
        }
    }

    @Test
    fun isConversionFree() {
        mainCoroutineRule.dispatcher.runBlockingTest {
            Mockito.`when`(repository.getTotalConversionCount()).thenReturn(
                6
            )
            viewModel.applyConversionCommission(250.0)
            assertThat((viewModel.commissionAmountLiveData.value ?: 0.0) > 0.0).isTrue()
        }
    }
}