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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prodman.data.ProdVersion
import com.example.prodman.data.Product
import com.example.prodman.model.ProdViewModel
import com.example.prodman.model.ProdViewModelFactory
import com.example.product.databinding.ActivityMainBinding
import com.example.product.databinding.FragmentProductDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * [ItemDetailFragment] displays the details of the selected item.
 */
class ProductDetailFragment : Fragment() {
    private val navigationArgs: ProductDetailFragmentArgs by navArgs()
    lateinit var product: Product
    private var prodId:Int=-1
    private lateinit var recyclerView: RecyclerView
    lateinit var actBinding: ActivityMainBinding

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


    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // actBinding = ActivityMainBinding.inflate(inflater, container, false)
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prodId = navigationArgs.id
        viewModel.fragmentName="ProductDetailFragment"

        //prodName = navigationArgs.productName
        // Retrieve the item details using the itemId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when the data actually changes.
         //Log.d("versionchk", "dtlFrag:"+prodId.toString()+":"+prodName)

        viewModel.retrieveProduct(prodId).observe(this.viewLifecycleOwner) { selectedProduct ->
            product = selectedProduct
            viewModel.prodId=prodId
            viewModel.prodName=product.prodName
            viewModel.labelling= product.labeling
            viewModel.packaging = product.packaging
            viewModel.picture = product.picture!!
            bindProduct(product)
        }





        val adapter = ProdVersionListAdapter {
           // actBinding.versionNo.text = it.version.toString()
            val action =
                ProductDetailFragmentDirections.actionProductDetailFragmentToBatchListFragment (it.version)
            this.findNavController().navigate(action)

        }



        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())



        recyclerView.adapter = adapter
        lifecycle.coroutineScope.launch {
            viewModel.productVersions (prodId).collect() {

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
 private fun bindProduct(product: Product) {
     binding.apply {

         prodName.text = product.prodName
         productPackaging.text = product.packaging
         productLabel.text = product.labeling
         val imagePath = viewModel.downloadPath+"/"+product.picture

         // Method 1: Using setImageURI() with the image file URI
         prodImage.setImageURI(Uri.parse(imagePath))
     }
 }



}
