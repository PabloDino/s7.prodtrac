
package com.example.prodman


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prodman.data.ProdVersion
import com.example.prodman.data.ProdVersionBatch
import com.example.prodman.data.Product
import com.example.prodman.model.ProdViewModel
import com.example.prodman.model.ProdViewModelFactory
import com.example.product.databinding.BatchListFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext


class BatchListFragment : Fragment() {
    private val navigationArgs: BatchListFragmentArgs by navArgs()
    lateinit var product: Product
    var prodVersion: ProdVersion? =null
    private var prodId:Int=-1
    private var version:Int=-1
    private var prodName:String =""
    private var comments:String =""
    private lateinit var recyclerView: RecyclerView
    private lateinit var  fab:FloatingActionButton
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

    private var _binding: BatchListFragmentBinding? = null
    private val binding get() = _binding!!




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BatchListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prodId = viewModel.prodId//vnavigationArgs.prodId
        prodName = viewModel.prodName//navigationArgs.prdName
        version = navigationArgs.version
        viewModel.version = version
        viewModel.fragmentName="BatchListFragment"

        //comments =  viewModel.comments//navigationArgs.versionComments


        // Retrieve the item details using the itemId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when the data actually changes.
        //Log.d("versionchk", "dtlFrag:"+prodId.toString()+":"+prodName)

        fab = binding.floatingActionButton

        fab.setOnClickListener({
          lifecycleScope.launch {
              viewModel.newBatch(prodId, version)
          }

        })

        viewModel.retrieveVersion(prodId, version).observe(this.viewLifecycleOwner) { selectedVersion ->
            prodVersion = selectedVersion
            //bindProduct(product)
            if (prodVersion!=null)
                  bindVersion(prodId, prodName, prodVersion!!)

        }



     //if (prodVersion!=null) {
         val adapter = BatchListAdapter {

             val action =
                 BatchListFragmentDirections.actionBatchListFragmentToBatchStepListFragment(it.batch)
             this.findNavController().navigate(action)


         }
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
         //val adapter = BatchListAdapter({})
         // by passing in the stop name, filtered results are returned,
         // and tapping rows won't trigger navigation
         recyclerView.adapter = adapter
         lifecycle.coroutineScope.launch {
/*
            viewModel.retrieveVersion(version, prodId).observe(requireParentFragment().viewLifecycleOwner!!)  { selectedVersion ->
                prodVersion = selectedVersion
                //bindProduct(product)
                bindVersion(prodId, prodName, prodVersion)

            }
*/
             viewModel.versionBatches(prodId, version).collect() {

                 adapter.submitList(it)
             }
        // }

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
    private fun bindVersion(pid:Int, pName:String, prodVersion: ProdVersion) {
        binding.apply {
            prodName.text=pid.toString()+":"+pName
            versionName.text = prodVersion.version.toString() +":"+prodVersion.comments
            val imagePath = viewModel.downloadPath+"/"+viewModel.picture
            prodImage.setImageURI(Uri.parse(imagePath))

        }
        viewModel.version = prodVersion.version
        viewModel.comments = prodVersion.comments


    }



}
