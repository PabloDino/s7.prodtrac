

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

//import com.example.inventory2.databinding.ItemListItemBinding

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prodman.data.Hazard
import com.example.prodman.model.ProdViewModel
import com.example.product.databinding.HazardListItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notifyAll


//import com.template.androidtemplate.R

//import com.example.inventory2.databinding.ItemListItemBinding

/**
 * [ListAdapter] implementation for the recyclerview.
 */

//class HazardListAdapter( private val dataList: List<List<Measure>>) :
class HazardListAdapter( val viewModel: ProdViewModel) :
    ListAdapter<Hazard, HazardListAdapter.HazardViewHolder>(DiffCallback) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HazardViewHolder {
        return HazardViewHolder(viewModel,
            HazardListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: HazardViewHolder, position: Int) {
        val current = getItem(position)

    // holder.
        holder.bind(viewModel, current)


       // val childLayoutManager = LinearLayoutManager( holder.itemView.context,   RecyclerView.HORIZONTAL, false)

        //childLayoutManager.initialPrefetchItemCount = 4
/*

        val mesLayoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        val mesAdapter = MeasureListAdapter(context, current)
        holder.measureRecyclerView.apply {
            layoutManager = mesLayoutManager
            adapter = mesAdapter
        }

      */


    }

    class HazardViewHolder(viewModel: ProdViewModel,
                           private var binding: HazardListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var currrank= 0
        //val measureRecyclerView: RecyclerView =  binding.recyclerMeasures

        fun bind(viewModel: ProdViewModel,hazard: Hazard) {
            if (hazard.rank == 0) {


                binding.hazardName.text =  hazard.description

                when (hazard.handled) {
                    0 -> {
                        binding.hazardName.setTextColor(Color.RED)
                    }

                    1-> {
                        binding.hazardName.setTextColor(Color.BLACK)
                    }

                }

                binding.measureName.visibility = View.GONE// INVISIBLE
                binding.measureName.text= hazard.description
                binding.hazardName.visibility=View.VISIBLE
                binding.plusBlue.visibility=View.VISIBLE
                binding.redX.visibility=View.INVISIBLE
                binding.roundX.visibility=View.VISIBLE
                binding.hazardName.setTypeface(binding.hazardName.getTypeface(), Typeface.BOLD)
                binding.plainTextInput.visibility = View.GONE
            }
            else
            {
                if (hazard.measureName!!.trim().length==0){
                    binding.measureName.visibility = View.GONE
                    binding.hazardName.visibility = View.GONE
                    binding.plusBlue.visibility = View.GONE
                    binding.redX.visibility = View.VISIBLE
                    binding.roundX.visibility = View.GONE
                     //binding.plainTextInput.setBackgroundColor(Col
                    //binding.productPackaging.text =
                    // binding.productLabel.text = product.packagin
                    binding.plainTextInput.visibility = View.VISIBLE
                }
                else {
                    when (hazard.handled)
                    {0->{binding.measureName.isChecked=false}
                        1->{binding.measureName.isChecked=true}
                    }

                    binding.measureName.visibility = View.VISIBLE
                    binding.hazardName.visibility = View.GONE
                    binding.plusBlue.visibility = View.GONE
                    binding.redX.visibility = View.VISIBLE
                    binding.roundX.visibility = View.GONE
                    binding.measureName.text = hazard.rank.toString() + ":" + hazard.measureName
                    //binding.plainTextInput.setBackgroundColor(Color.parseColor("#ff0000"))
                    //binding.productPackaging.text =
                    // binding.productLabel.text = product.packaging+" with "+product.labeling
                    binding.plainTextInput.visibility = View.GONE
                }
            }

            binding.plusBlue.setOnClickListener {
                Toast.makeText(this.itemView.context!! ,"Adding Measure", Toast.LENGTH_SHORT).show()
                GlobalScope.launch {
                    viewModel.stepLock=viewModel.modelUsername
                    viewModel.newMeasure(hazard.id, hazard.hazardid, hazard.prodId!!, hazard.version, hazard.batch!!,hazard.stepId!!)
                    val adapter = HazardListAdapter(viewModel)
                    adapter.submitList(emptyList())
                    viewModel.stepHazards(hazard.prodId, hazard.version, hazard.batch, hazard.stepId).collect() {
                            adapter.submitList(it)
                        }
                    }
                }

            binding.plainTextInput.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    Toast.makeText(this.itemView.context!! ,"Saving Measure", Toast.LENGTH_SHORT).show()
                    GlobalScope.launch {

                        viewModel.setMeasure(hazard.id, binding.plainTextInput.text.toString())
                    }
                    return@OnKeyListener true
                }
                false
            })
                //viewModel.
               //this@HazardViewHolder.notifyAll()


            binding.redX.setOnClickListener {
                Toast.makeText(this.itemView.context!! ,"Removing measure "+hazard.hazardid.toString()+":"+hazard.measureName , Toast.LENGTH_SHORT).show()
                GlobalScope.launch {

                    viewModel.clearMeasure(hazard.id, hazard.measureName.toString())
                }
            }
            binding.roundX.setOnClickListener {

                GlobalScope.launch {

                    currrank = viewModel.getNextRankForHazard(hazard.hazardid)

                    if (currrank == 0) {

                        viewModel.clearMeasure(hazard.id, "")

                    }
                }


                    /*
                    withContext(Dispatchers.Main) {
                        if (currrank == 0) {
                            Toast.makeText(

                                "Removing hazard " + hazard.hazardid.toString() + ":" + hazard.description,
                                Toast.LENGTH_SHORT
                            ).show()


                        } else
                            Toast.makeText(
                                this.itemView.context!!,
                                "Please remove all measures before removing hazard " + hazard.hazardid.toString() + ":" + hazard.description,
                                Toast.LENGTH_SHORT
                            ).show()
                    }

                     */

            }
            /*
            binding.measureName.setOnClickListener {
                if (binding.measureName.isChecked) {
                    viewModel.handleMeasure(hazard.measureId!!, hazard.hazardid)
                } else {
                    viewModel.clearMeasure(hazard.measureId!!, hazard.hazardid)
                }
            }

             */

            binding.measureName.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // CheckBox is checked
                    viewModel.handleMeasure(hazard.measureId!!, hazard.hazardid)
                } else {
                    // CheckBox is unchecked
                    viewModel.clearMeasure(hazard.measureId!!, hazard.hazardid)
                }
            }


          //  binding.
           // binding.measureName.setText(hazard.measureName)

        }


    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Hazard>() {
            override fun areItemsTheSame(oldItem: Hazard, newItem: Hazard): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Hazard, newItem: Hazard): Boolean {
                return oldItem.description  == newItem.description
            }
        }
    }
}
