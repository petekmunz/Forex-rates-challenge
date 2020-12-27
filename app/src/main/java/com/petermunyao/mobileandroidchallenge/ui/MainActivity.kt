package com.petermunyao.mobileandroidchallenge.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.infinum.dbinspector.DbInspector
import com.petermunyao.mobileandroidchallenge.R
import com.petermunyao.mobileandroidchallenge.databinding.ActivityMainBinding
import com.petermunyao.mobileandroidchallenge.viewmodels.MainViewModel
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.math.round

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding!!.lifecycleOwner = this
        supportActionBar?.title = getString(R.string.activity_title)
        setTransparentDrawable(binding!!.txtUSDCode)
        binding!!.etxtUsdNum.addTextChangedListener(usdWatcher)
        //binding!!.etxtCurrencyNum.addTextChangedListener(otherCurrencyWatcher)

        if (Prefs.contains(getString(R.string.preferred_currency_code))) {
            binding!!.txtCurrencyCode.text =
                Prefs.getString(
                    getString(R.string.preferred_currency_code),
                    getString(R.string.default_currency_code)
                )
            binding!!.lytCurrencyNum.helperText =
                Prefs.getString(
                    getString(R.string.preferred_currency_name),
                    getString(R.string.default_currency_name)
                )
        }

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
            } else {
                viewModel.getRemoteExchangeRates()
            }
        })

        binding!!.txtCurrencyCode.setOnClickListener {
            MaterialAlertDialogBuilder(this@MainActivity)
                .setTitle(resources.getString(R.string.dialog_title))
                .setNeutralButton(resources.getString(R.string.dialog_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.dialog_ok)) { dialog, _ ->
                    if (viewModel.getExchangeRate(binding!!.txtCurrencyCode.text as String) != null) {
                        val otherCurrencyResult =
                            viewModel.getExchangeRate(binding!!.txtCurrencyCode.text as String)!! * (binding!!.etxtUsdNum.text.toString()
                                .toFloat())
                        binding!!.currencyInput =
                            (round(otherCurrencyResult * 1000) / 1000).toString()
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

        binding!!.txtInspector.setOnClickListener {
            DbInspector.show()
        }
    }

    private val usdWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s != null) {
                if (s.isNotEmpty()) {
                    val usdFormatted = s.toString().toFloat()
                    if (viewModel.getExchangeRate(binding!!.txtCurrencyCode.text as String) != null) {
                        val otherCurrencyResult =
                            viewModel.getExchangeRate(binding!!.txtCurrencyCode.text as String)!! * usdFormatted
                        binding!!.currencyInput =
                            (round(otherCurrencyResult * 1000) / 1000).toString()
                    }
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    private val otherCurrencyWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s != null) {
                if (s.isNotEmpty()) {
                    val currencyFormatted = s.toString().toFloat()
                    if (viewModel.getExchangeRate(binding!!.txtCurrencyCode.text as String) != null) {
                        val usdResult =
                            currencyFormatted / viewModel.getExchangeRate(binding!!.txtCurrencyCode.text as String)!!
                        binding!!.usdInput = (round(usdResult * 10000) / 1000).toString()
                    }
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

    private fun setTransparentDrawable(textview: TextView) {
        val tranparentDrawable = ColorDrawable(Color.TRANSPARENT)
        textview.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            tranparentDrawable,
            null
        )
    }

}