
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

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
//import com.example.inventory2.databinding.ItemListItemBinding
import com.example.prodman.data.Product
import com.example.prodman.model.ProdViewModel
import com.example.product.databinding.ProductListProductBinding

//import com.example.inventory2.databinding.ItemListItemBinding

/**
 * [ListAdapter] implementation for the recyclerview.
 */

class ProductListAdapter( val viewModel:ProdViewModel,private val onItemClicked: (Product) -> Unit) :
    ListAdapter<Product, ProductListAdapter.ProductViewHolder>(DiffCallback) {



    var  products: List<Product> = emptyList()
        //get() = emptyList()
        set(value) {
            field = value
            // For an extra challenge, update this to use the paging library.

            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ProductListProductBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val current = getItem(position)

        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current, viewModel)
    }

    class ProductViewHolder(private var binding: ProductListProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, viewModel: ProdViewModel) {
            binding.productName.text = product.id.toString() +"."+product.prodName
            //binding.productPackaging.text =
            binding.productLabel.text = product.packaging+" with "+product.labeling
            val prodImage = binding.prodImage

            // Replace with your image file path in the Downloads directory
            //val imagePath = "/storage/emulated/0/Download/prodMan/"+product.picture
            val imagePath = viewModel.downloadPath+"/"+product.picture

            // Method 1: Using setImageURI() with the image file URI
            prodImage.setImageURI(Uri.parse(imagePath))

        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.prodName == newItem.prodName
            }
        }
    }
}
