package com.petermunyao.mobileandroidchallenge.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.petermunyao.mobileandroidchallenge.adapter.ExchangeRatesAdapter
import com.petermunyao.mobileandroidchallenge.databinding.FragmentRatesDetailedBinding
import com.petermunyao.mobileandroidchallenge.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentRatesDetailed : Fragment() {

    private var binding: FragmentRatesDetailedBinding? = null
    private var adapter: ExchangeRatesAdapter? = null
    private val viewModel: MainViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRatesDetailedBinding.inflate(inflater, container, false)
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.recyclerView?.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding!!.recyclerView.setHasFixedSize(true)
        val selectedCurrency = arguments?.getString("currencySelected")
        if (selectedCurrency != null) {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "$selectedCurrency Exchange Rates"
            val exchangeRates = viewModel.getExchangeRatesForSelectedCurrency(selectedCurrency)
            if (adapter == null) {
                adapter = ExchangeRatesAdapter()
            }
            binding?.recyclerView?.adapter = adapter
            adapter!!.submitList(exchangeRates)
        } else {
            Toast.makeText(requireContext(), "No currency selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.recyclerView?.adapter = null
        binding = null
    }
}