package com.petermunyao.mobileandroidchallenge.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.infinum.dbinspector.DbInspector
import com.petermunyao.mobileandroidchallenge.R
import com.petermunyao.mobileandroidchallenge.databinding.ActivityMainBinding
import com.petermunyao.mobileandroidchallenge.viewmodels.MainViewModel
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding!!.lifecycleOwner = this
        supportActionBar?.title = getString(R.string.activity_title)
        binding!!.etxtUsdNum.addTextChangedListener(usdWatcher)
        setPreferredCurrencyToViews()

        //Get supported currencies from local db, if not present fetch from remote
        viewModel.getLocalCurrencies().observe(this, {
            if (it != null) {
                viewModel.setCurrenciesToListInMemory(it.currencies)
            } else {
                viewModel.getRemoteCurrencies()
            }
        })

        //Get currency exchange rates from local db, if not present fetch from remote
        viewModel.getLocalExchangeRates().observe(this, {
            if (it != null) {
                viewModel.setCurrentRatesToMemory(it.currencyRates)
                viewModel.lastRefreshTime = it.timeStamp
            } else {
                viewModel.getRemoteExchangeRates()
            }
        })

        //Observe error cases when making network calls
        viewModel.errorLiveData.observe(this, {
            if (it != null) {
                showSnackbar(it)
            }
        })

        //Show supported currencies in a confirmation dialog
        binding!!.txtCurrencyCode.setOnClickListener {
            MaterialAlertDialogBuilder(this@MainActivity)
                .setTitle(resources.getString(R.string.dialog_title))
                .setNeutralButton(resources.getString(R.string.dialog_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.dialog_ok)) { dialog, _ ->
                    if (binding!!.etxtUsdNum.text.toString().isNotEmpty()) {
                        binding!!.currencyInput = viewModel.calculateOtherCurrencyValue(
                            binding!!.etxtUsdNum.text.toString(),
                            binding!!.txtCurrencyCode.text as String
                        )

                    } else {
                        binding!!.currencyInput = ""
                    }
                    dialog.dismiss()
                }
                .setSingleChoiceItems(viewModel.currenciesList.toTypedArray(), 1) { _, which ->
                    val currencyFull = viewModel.currenciesList[which]
                    val currencyCode = currencyFull.substring(currencyFull.length - 3)
                    val currencyName = currencyFull.substring(0, currencyFull.length - 3)
                    setPreferredCurrencyToPrefs(currencyCode, currencyName)
                    binding!!.txtCurrencyCode.text = currencyCode
                    binding!!.lytCurrencyNum.helperText = currencyName
                }
                .show()
        }

        binding!!.btnRefresh.setOnClickListener {
            if (viewModel.lastRefreshTime == null) {
                viewModel.getRemoteExchangeRates()
            } else {
                if (viewModel.refreshExchangeRates(viewModel.lastRefreshTime!!)) {
                    showSnackbar("Latest exchange rates are being fetched")
                } else {
                    showSnackbar("Refresh calls are limited to 30 minute intervals")
                }
            }
        }

        binding!!.txtInspector.setOnClickListener {
            DbInspector.show()
        }
    }

    private val usdWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s != null) {
                if (s.isNotEmpty()) {
                    binding!!.currencyInput = viewModel.calculateOtherCurrencyValue(
                        s.toString(),
                        binding!!.txtCurrencyCode.text as String
                    )
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    private fun setPreferredCurrencyToPrefs(currencyCode: String, currencyName: String) {
        Prefs.putString(getString(R.string.preferred_currency_code), currencyCode)
        Prefs.putString(getString(R.string.preferred_currency_name), currencyName)
    }

    private fun setPreferredCurrencyToViews() {
        binding?.txtCurrencyCode?.text =
            Prefs.getString(
                getString(R.string.preferred_currency_code),
                getString(R.string.default_currency_code)
            )
        binding?.lytCurrencyNum?.helperText =
            Prefs.getString(
                getString(R.string.preferred_currency_name),
                getString(R.string.default_currency_name)
            )
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding?.root!!, message, Snackbar.LENGTH_SHORT).show()
    }
}