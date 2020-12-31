package com.petermunyao.mobileandroidchallenge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.petermunyao.mobileandroidchallenge.databinding.CurrenyItemBinding
import com.petermunyao.mobileandroidchallenge.model.ExchangeRateInfo

class ExchangeRatesAdapter :
    ListAdapter<ExchangeRateInfo, ExchangeRatesAdapter.ExchangeRateViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CurrenyItemBinding.inflate(
            inflater,
            parent,
            false
        )
        return ExchangeRateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExchangeRateViewHolder, position: Int) {
        holder.bindDetails(getItem(position))
    }

    inner class ExchangeRateViewHolder(private val binding: CurrenyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindDetails(info: ExchangeRateInfo?) {
            binding.otherCurrency = info?.otherCurrency
            binding.referenceCurrency = info?.referenceCurrency
            binding.otherToRefRate = info?.otherCurrencyTORefRate
            binding.refToOtherRate = info?.refCurrencyToOtherRate
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<ExchangeRateInfo>() {
            override fun areItemsTheSame(
                oldItem: ExchangeRateInfo,
                newItem: ExchangeRateInfo
            ): Boolean {
                return oldItem.referenceCurrency == newItem.referenceCurrency
            }

            override fun areContentsTheSame(
                oldItem: ExchangeRateInfo,
                newItem: ExchangeRateInfo
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}