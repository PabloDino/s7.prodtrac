package com.example.prodman

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prodman.data.Hazard
import com.example.prodman.data.ProdBatchStep
import com.example.prodman.model.ProdViewModel
import com.example.prodman.model.ProdViewModelFactory
import com.example.product.databinding.StepDetailFragmentBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


/**
 * [ItemDetailFragment] displays the details of the selected item.
 */
class StepDetailFragment : Fragment() {
    private val navigationArgs: StepDetailFragmentArgs by navArgs()
    lateinit var step: ProdBatchStep
    lateinit var editText: TextInputEditText
    //lateinit var version: ProdVersion
    private var stepId:Int=-1
    private var prodName:String =""
    private var batchDate:String =""
    private var batchId:Int=-1
    private var prodId:Int=-1
    private var version:Int=-1
    private lateinit var hazardRecycler: RecyclerView
    lateinit var imm:InputMethodManager

    private var _checkBy:String=""
    private var _reading:Int=0
    private var _verdict:String=""
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


    private var _binding: StepDetailFragmentBinding? = null
    val binding get() = _binding!!
    lateinit var hazList:List<Hazard>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StepDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stepId = navigationArgs.id
        prodName = viewModel.prodName//navigationArgs.prodName
        prodId = viewModel.prodId//navigationArgs.prodId
        version = viewModel.version//navigationArgs.batchId
        batchId = viewModel.batch//navigationArgs.batchId
        batchDate = viewModel.startDate// navigationArgs.batchDate

        editText = binding.reading
        // Retrieve the item details using the itemId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when he data actually changes.
        viewModel.fragmentName="StepDetailFragment"
        viewModel.stepId= stepId
        //imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        //editText.setShowSoftInputOnFocus(false)

        /*
        val mesViewModel:MeasureViewModel by activityViewModels {
            MeasureViewModelFactory(
                (activity?.application as ProductApplication).database.measureDao()
            )
        }

         */

        hazardRecycler = binding.recyclerHazards
        hazardRecycler.layoutManager = LinearLayoutManager(requireContext())
        //val measureList = mesViewModel.allMeasures()
        viewModel.retrieveStep(prodId, version, batchId, stepId).observe(this.viewLifecycleOwner) { selectedStep ->
            step = selectedStep
            bind(step,prodId, prodName, batchId, batchDate)

           val adapter = HazardListAdapter(viewModel)
            hazardRecycler.adapter = adapter
            lifecycle.coroutineScope.launch {
                viewModel.stepHazards(prodId, version, batchId, stepId).collect() {
                    adapter.submitList(it)
                }
            }
        }














    }

    private fun <T> List(): List<T> {
      return emptyList()
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
    private fun bind(step:ProdBatchStep,pid:Int, pName:String, pbatch:Int, pDate:String) {
        binding.apply {
            stepName.text = step.stepName
            stepId.text=step.stepCode+": "
            prodName.text = pid.toString() +". " +pName
            batch.text = "Batch "+pbatch.toString()+ " started "+pDate
            if (step.checkby.isNullOrEmpty())
                checkedBy.setText(viewModel.modelUsername)
            else
                 checkedBy.setText(step.checkby)

            val imagePath = viewModel.downloadPath+"/"+viewModel.picture
            prodImage.setImageURI(Uri.parse(imagePath))

            //if(step.reading!=nullmmeasureName)
            //newHazard.setText(step.reading?.toString())
           // verdict.setText(step.verdict).
           if (step.reading!=null)
                reading.setText(step.reading.toString())
            detail.setText(step.detail)
            when (step.checkedRisk){
                "high"->{optHigh.isChecked=true}
                "medium"->{optMed.isChecked=true}
                "low"->{optLow.isChecked=true}
            }



            saveHazOnce.setOnClickListener {
                Toast.makeText(context ,"Adding hazard to batch", Toast.LENGTH_SHORT).show()
                GlobalScope.launch {
                    viewModel.stepLock=viewModel.modelUsername
                    viewModel.newHazard(prodId, version, viewModel.batch, viewModel.stepId,
                        newHazard.text.toString(),0,newHazard.text.toString(),newHazard.text.toString() ,
                        -1)
                    val adapter = HazardListAdapter(viewModel)
                    adapter.submitList(emptyList())
                    viewModel.stepHazards(prodId, version, viewModel.batch, viewModel.stepId,).collect() {
                        adapter.submitList(it)
                    }
                }
            }

            saveHazStep.setOnClickListener {
                Toast.makeText(context ,"Adding hazard to step", Toast.LENGTH_SHORT).show()
                GlobalScope.launch {
                    viewModel.stepLock=viewModel.modelUsername
                    viewModel.newStepHazard(prodId, version, viewModel.batch, viewModel.stepId,newHazard.text.toString(),1 ,newHazard.text.toString(),newHazard.text.toString(),newHazard.text.toString(),-1)
                    val adapter = HazardListAdapter(viewModel)
                    adapter.submitList(emptyList())
                    viewModel.stepHazards(prodId, version, viewModel.batch, viewModel.stepId).collect() {
                        adapter.submitList(it)
                    }
                }
            }

            saveAction.setOnClickListener {
                var localreading =0;
                if (reading.text!!.isNotEmpty())
                    localreading = Integer.parseInt(reading.text.toString())

                var pbsRisk=""
                if (binding.optLow.isChecked)
                    pbsRisk="low"
                if (binding.optMed.isChecked)
                    pbsRisk="medium"
                if (binding.optHigh.isChecked)
                    pbsRisk="high"


               if (viewModel.checkIfStepHandled(Integer.parseInt(step.id.toString()), viewModel.prodId,viewModel.version, viewModel.batch,
                    viewModel.stepId, localreading, detail.text.toString(),
                    checkedBy.text.toString() ,"", pbsRisk)) {
                   viewModel.saveStep(
                       Integer.parseInt(step.id.toString()),
                       viewModel.prodId,
                       viewModel.version,
                       viewModel.batch,
                       viewModel.stepId,
                       localreading,
                       detail.text.toString(),
                       checkedBy.text.toString(),
                       "",
                       pbsRisk
                   )

                   updateStep(
                       step,
                       checkedBy.text.toString(),
                       localreading,
                       prodId,
                       pbatch,
                       pName,
                       pDate,
                       detail.text.toString(),
                       "",
                       pbsRisk
                   )



               }
                else
                   Toast.makeText(context ,"Unhandled hazards", Toast.LENGTH_LONG).show()

            }

        }
    }




    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.checkedBy.text.toString()
        )
    }

    private fun updateStep(step: ProdBatchStep, checkBy:String, reading:Int, prodId:Int, pbatch:Int,
                           pName:String, pDate:String, detail:String, risk:String, checkedRisk:String) {
    if (isEntryValid()) {
        viewModel.updateStep(step.id,step.prodId,step.version, step.batchId,
            step.stepId,step.stepName, SimpleDateFormat("yyyy-MM-dd").toString(),checkBy,
            reading, detail, risk, checkedRisk)
        val action = StepDetailFragmentDirections.actionStepDetailFragmentToBatchStepListFragment(step.batchId)
        findNavController().navigate(action)
    }
        else{
        Toast.makeText(context, "Please state who checked the step ",Toast.LENGTH_LONG).show()

    }
}



}
