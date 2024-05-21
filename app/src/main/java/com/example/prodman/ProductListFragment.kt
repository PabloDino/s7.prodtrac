

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

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prodman.data.Product
import com.example.prodman.ProductListAdapter
import com.example.prodman.model.ProdViewModel
import com.example.prodman.model.ProdViewModelFactory
import com.example.product.databinding.ActivityMainBinding
import com.example.product.databinding.ProductListFragmentBinding
import com.example.product.databinding.ProductListProductBinding


//import com.example.inventory2.databinding.ItemListFragmentBinding

/**
 * Main fragment displaying details for all items in the database.
 */
class ProductListFragment : Fragment(){
    private val viewModel: ProdViewModel by activityViewModels {
        ProdViewModelFactory(
            (activity?.application as ProductApplication).database.productDao(),
            (activity?.application as ProductApplication).database.prodVersionDao(),
            (activity?.application as ProductApplication).database.prodVersionBatchDao(),
            (activity?.application as ProductApplication).database.prodBatchStepDao(),
            (activity?.application as ProductApplication).database.hazardDao(),
            (activity?.application as ProductApplication).database.measureDao(),
            (activity?.application as ProductApplication).database.userDao(),
            activity?.application as ProductApplication

        )
    }


    private var _binding: ProductListFragmentBinding? = null
    private val binding get() = _binding!!
    //private var actProdId:TextView?=requireActivity().findViewById<TextView>(R.id.text1) as TextView;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProductListFragmentBinding.inflate(inflater, container, false)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fragmentName="ProductListFragment"
        //val adapter = LocalProdAdapter(ProdClick {})

        val adapter = ProductListAdapter(viewModel) {
            //viewModel.prodName= it.prodName
            //viewModel.prodId = it.id
            val action =
                ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(it.id)
            this.findNavController().navigate(action)


        }



        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
       // binding.recyclerView.visibility=View.GONE
        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.

        viewModel.allProducts.observe(this.viewLifecycleOwner) { products ->
            products.let {
                adapter.submitList(it)
            }
        }

       /*
        viewModel.prodlist.observe(viewLifecycleOwner, Observer<List<Product>> { products ->
            products?.apply {
                val liveDataProdList: MutableLiveData<List<Product>> = MutableLiveData()
                liveDataProdList.value = products
               viewModel.prodlist= liveDataProdList
              //  adapter.submitList(liveDataProdList.value)
            }
        })
       */




    }

    class ProdClick(val block: (Product) -> Unit) {
        /**
         * Called when a video is clicked
         *
         * @param video the video that was clicked
         */
        fun onClick(product: Product) = block(product)
    }



    class LocalProdAdapter(val callback: ProdClick) : RecyclerView.Adapter<LocalProdViewHolder>()

    {

        /**
         * The videos that our Adapter will show
         */
        var products: List<Product> = emptyList()
            set(value) {
                field = value
                // For an extra challenge, update this to use the paging library.

                // Notify any registered observers that the data set has changed. This will cause every
                // element in our RecyclerView to be invalidated.
                notifyDataSetChanged()
            }

        /**
         * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
         * an item.
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalProdViewHolder {
            return LocalProdViewHolder(
                ProductListProductBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )
        }

        override fun getItemCount() = products.size

        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
         * position.
         */

        override fun onBindViewHolder(holder: LocalProdViewHolder, position: Int) {
            val current = products[position]
            //holder.itemView.setOnClickListener {
             //   onItemClicked(current)
           // }
            holder.bind(current)
        }

        /*
        override fun onBindViewHolder(holder: LocalProdViewHolder, position: Int) {
            holder.viewDataBinding.also {
                it.video = videos[position]
                it.videoCallback = callback
            }
        }

         */

    }

    /**
     * ViewHolder for DevByte items. All work is done by data binding.
     */

    class LocalProdViewHolder(private var binding: ProductListProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.productName.text = product.id.toString() +"."+product.prodName
            //binding.productPackaging.text =
            binding.productLabel.text = product.packaging+" with "+product.labeling

        }
    }
}
