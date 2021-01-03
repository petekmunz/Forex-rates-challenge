package com.petermunyao.mobileandroidchallenge.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.petermunyao.mobileandroidchallenge.R
import com.petermunyao.mobileandroidchallenge.databinding.FragmentMainBinding
import com.petermunyao.mobileandroidchallenge.viewmodels.MainViewModel
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentMain : Fragment() {

    private var binding: FragmentMainBinding? = null
    private val viewModel: MainViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.etxtPrimaryAmount?.addTextChangedListener(primaryCurrencyWatcher)
        setPreferredCurrencyToViews()
        //Get supported currencies from local db, if not present fetch from remote
        viewModel.getLocalCurrencies().observe(viewLifecycleOwner, {
            if (it != null) {
                viewModel.setCurrenciesToListInMemory(it.currencies)
            } else {
                viewModel.getRemoteCurrencies()
            }
        })

        //Get currency exchange rates from local db, if not present fetch from remote
        viewModel.getLocalExchangeRates().observe(viewLifecycleOwner, {
            if (it != null) {
                viewModel.setCurrentRatesToMemory(it.currencyRates)
                viewModel.lastRefreshTime = it.timeStamp
            } else {
                viewModel.getRemoteExchangeRates()
            }
        })

        //Observe error cases when making network calls
        viewModel.errorLiveData.observe(viewLifecycleOwner, {
            if (it != null) {
                showSnackbar(it)
            }
        })

        //Show supported currencies in a confirmation dialog
        binding?.txtPrimaryCode?.setOnClickListener {
            if (viewModel.isCurrenciesListInitialized()) {
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(getString(R.string.dialog_title))
                    .setNeutralButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(getString(R.string.dialog_ok)) { dialog, _ ->
                        if (binding?.etxtPrimaryAmount?.text.toString()
                                .isNotEmpty() && binding?.etxtPrimaryAmount?.toString() != "."
                        ) {
                            binding?.currencySecondaryInput =
                                viewModel.calculateAmountFromRate(
                                    binding?.txtPrimaryCode?.text as String,
                                    binding?.txtSecondaryCode?.text as String,
                                    binding?.etxtPrimaryAmount?.text.toString()
                                )

                        } else {
                            binding?.currencySecondaryInput = ""
                        }
                        dialog.dismiss()
                    }
                    .setSingleChoiceItems(viewModel.currenciesList, 1) { _, which ->
                        val currencyFull = viewModel.currenciesList[which]
                        val currencyCode = currencyFull.substring(0, 3)
                        val currencyName = currencyFull.substring(4)
                        setPreferredPrimaryCurrencyToPrefs(currencyCode, currencyName)
                        binding?.currencyPrimary = currencyCode
                        binding?.lytCurrencyPrimary?.helperText = currencyName
                    }.show()
            } else {
                showSnackbar("Currencies have not been retrieved, turn on internet connection & restart app")
            }
        }
        binding?.txtSecondaryCode?.setOnClickListener {
            if (viewModel.isCurrenciesListInitialized()) {
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(getString(R.string.dialog_title))
                    .setNeutralButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(getString(R.string.dialog_ok)) { dialog, _ ->
                        if (binding?.etxtPrimaryAmount?.text.toString()
                                .isNotEmpty() && binding?.etxtPrimaryAmount?.toString() != "."
                        ) {
                            binding?.currencySecondaryInput =
                                viewModel.calculateAmountFromRate(
                                    binding?.txtPrimaryCode?.text as String,
                                    binding?.txtSecondaryCode?.text as String,
                                    binding?.etxtPrimaryAmount?.text.toString()
                                )

                        } else {
                            binding?.currencySecondaryInput = ""
                        }
                        dialog.dismiss()
                    }
                    .setSingleChoiceItems(viewModel.currenciesList, 1) { _, which ->
                        val currencyFull = viewModel.currenciesList[which]
                        val currencyCode = currencyFull.substring(0, 3)
                        val currencyName = currencyFull.substring(4)
                        setPreferredSecondaryCurrencyToPrefs(currencyCode, currencyName)
                        binding?.currencySecondary = currencyCode
                        binding?.lytCurrencySecondary?.helperText = currencyName
                    }
                    .show()
            } else {
                showSnackbar("Currencies have not been retrieved, turn on internet connection & restart app")
            }
        }

        binding?.btnRefresh?.setOnClickListener {
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

        binding?.btnRatesSecondary?.setOnClickListener {
            val bundle = bundleOf("currencySelected" to binding?.txtSecondaryCode?.text)
            it.findNavController().navigate(R.id.action_show_detailed_rates, bundle)
        }

        binding?.btnRatesPrimary?.setOnClickListener {
            val bundle = bundleOf("currencySelected" to binding?.txtPrimaryCode?.text)
            it.findNavController().navigate(R.id.action_show_detailed_rates, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private val primaryCurrencyWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s != null) {
                if (s.isNotEmpty() && s.toString() != ".") {
                    binding?.currencySecondaryInput = viewModel.calculateAmountFromRate(
                        binding?.txtPrimaryCode?.text as String,
                        binding?.txtSecondaryCode?.text as String,
                        s.toString()
                    )
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    private fun setPreferredSecondaryCurrencyToPrefs(currencyCode: String, currencyName: String) {
        Prefs.putString(getString(R.string.preferred_secondary_code), currencyCode)
        Prefs.putString(getString(R.string.preferred_secondary_name), currencyName)
    }

    private fun setPreferredPrimaryCurrencyToPrefs(currencyCode: String, currencyName: String) {
        Prefs.putString(getString(R.string.preferred_primary_code), currencyCode)
        Prefs.putString(getString(R.string.preferred_primary_name), currencyName)
    }

    private fun setPreferredCurrencyToViews() {
        binding?.currencyPrimary =
            Prefs.getString(
                getString(R.string.preferred_primary_code),
                getString(R.string.primary_currency_code)
            )
        binding?.currencySecondary =
            Prefs.getString(
                getString(R.string.preferred_secondary_code),
                getString(R.string.secondary_currency_code)
            )
        binding?.lytCurrencySecondary?.helperText =
            Prefs.getString(
                getString(R.string.preferred_secondary_name),
                getString(R.string.secondary_currency_name)
            )
        binding?.lytCurrencyPrimary?.helperText =
            Prefs.getString(
                getString(R.string.preferred_primary_name),
                getString(R.string.primary_currency_name)
            )
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding?.root!!, message, Snackbar.LENGTH_SHORT).show()
    }
}