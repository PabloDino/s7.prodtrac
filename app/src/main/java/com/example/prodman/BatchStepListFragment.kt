package com.example.prodman


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prodman.data.ProdBatchStep
import com.example.prodman.data.ProdBatchStepDao
import com.example.prodman.data.ProdVersionBatch

import com.example.prodman.model.ProdViewModel
import com.example.prodman.model.ProdViewModelFactory
import com.example.product.databinding.StepListFragmentBinding
import kotlinx.coroutines.launch


class BatchStepListFragment : Fragment() {
    private val navigationArgs: BatchStepListFragmentArgs by navArgs()
    lateinit var prodBatch: ProdVersionBatch
    //lateinit var batch: ProdVersionBatch
    private var prodId:Int=-1
    private var version:Int=-1
    private var batch:Int=-1
    private var prodName:String =""
    private var startDate:String =""
    private lateinit var recyclerView: RecyclerView

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


    private var _binding: StepListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StepListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prodId = viewModel.prodId//navigationArgs.prodId
        batch = navigationArgs.batchId
        version = viewModel.version
        prodName = viewModel.prodName//vnavigationArgs.prodName
         //version = navigationArgs.version
        // Retrieve the item details using the itemId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when the data actually changes.
        viewModel.fragmentName="BatchStepListFragment"

        viewModel.retrieveBatch(prodId, version, batch).observe(this.viewLifecycleOwner) { selectedBatch ->
            prodBatch = selectedBatch
            //bindProduct(product)
            bindBatch(prodBatch)

        }





        val adapter = BatchStepListAdapter ({
            if (viewModel.modelRole.equals("Reader"))
                Toast.makeText(context, "Your role is reader so you cannot manage step details", Toast.LENGTH_LONG).show()

            else {
                val action =
                    BatchStepListFragmentDirections.actionBatchStepListFragmentToStepDetailFragment(
                        it.stepId
                    )

                this.findNavController().navigate(action)
            }


        },  viewModel, requireContext())
        /*
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.
        versionModel.versionsOfProduct.observe(this.viewLifecycleOwner) { versions ->
            versions.let {
                adapter.submitList(it)
            }
        }

       */
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //val adapter = BatchStepListAdapter({})
        // by passing in the stop name, filtered results are returned,
        // and tapping rows won't trigger navigation
        recyclerView.adapter = adapter
        lifecycle.coroutineScope.launch {
            viewModel.batchSteps(prodId,   batch, viewModel.version).collect() {

                adapter.submitList(it)
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
     fun onSupportNavigateUp(): Boolean {
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container) // Replace with your fragment container ID
        val fragmentTransaction = fragmentManager.beginTransaction()
   override
        if (currentFragment != null) {
            // Check if the fragment is in the back stack
            if (fragmentManager.backStackEntryCount > 0) {
                // Pop the back stack to remove the top fragment
                fragmentManager.popBackStack()
            } else {
                // If not in the back stack, simply remove it
                fragmentTransaction.remove(currentFragment).commit()
            }
            return true
        }
   
        return super.onSupportNavigateUp()
    }
   */
//    Make sure to replace R.id.fragment_container with the ID of your fragment container view in your layout. Also, ensure that you have added fragments to the back stack using addToBackStack() when you originally added them to the fragment transaction if you want them to be popped from the back stack.

    /**
     * Binds views with the passed in item data.
     */
    private fun bindBatch( prodBatch: ProdVersionBatch)
    {
        binding.apply {

            prodName.text = viewModel.prodName
            batch.text = "Batch "+prodBatch.batch.toString()+ " started "+prodBatch.startDate
            val imagePath = viewModel.downloadPath+"/"+viewModel.picture
            prodImage.setImageURI(Uri.parse(imagePath))

            //productPackaging.text = product.packaging
            //productLabel.text = product.labeling

        }
        viewModel.batch=prodBatch.batch
        viewModel.startDate=prodBatch.startDate
    }



}
