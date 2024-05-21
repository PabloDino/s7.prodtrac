

/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.prodman

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prodman.data.Measure
//import com.example.inventory2.databinding.ItemListItemBinding
import com.example.prodman.data.Product
import com.example.product.databinding.MeasureListItemBinding
import com.example.product.databinding.ProductListProductBinding
//import com.example.inventory2.databinding.ItemListItemBinding

/**
 * [ListAdapter] implementation for the recyclerview.
 */

class MeasureListAdapter(private val context: Context,
                         private val itemList: List<Measure>) :
    ListAdapter<Measure,MeasureListAdapter.MeasureViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasureViewHolder {
        return MeasureViewHolder(
            MeasureListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: MeasureViewHolder, position: Int) {
        val current = getItem(position)
/*
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }

 */
        showToast("binding measure")



        holder.bind(current)
    }

    class MeasureViewHolder(private var binding: MeasureListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val measureName = binding.measureName

        init {
            binding.measureName.text = "init"
            binding.measureName.setOnCheckedChangeListener { buttonView, isChecked ->
                 if (isChecked) {
                    // CheckBox is checked
                     binding.measureName.text = "checked"
                   } else {
                    // CheckBox is unchecked
                     binding.measureName.text = "unchecked"           }
            }
        }

        fun bind(measure: Measure) {
            binding.measureName.text = measure.name+":"+measure.description
            //binding.checkBox.text =
            //binding.productPackaging.text =
            // binding.productLabel.text = product.packaging+" with "+product.labeling




        }

    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Measure>() {
            override fun areItemsTheSame(oldItem: Measure, newItem: Measure): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Measure, newItem: Measure): Boolean {
                return oldItem.description  == newItem.description
            }
        }
    }
}
