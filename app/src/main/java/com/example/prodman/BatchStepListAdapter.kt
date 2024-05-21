package com.example.prodman


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
//import com.example.inventory2.databinding.ItemListItemBinding
import com.example.prodman.data.ProdVersion
import com.example.prodman.data.ProdBatchStep
import com.example.prodman.model.ProdViewModel
import com.example.product.databinding.BatchListItemBinding
import com.example.product.databinding.FragmentProductDetailBinding
import com.example.product.databinding.ProductListProductBinding
import com.example.product.databinding.ProdversionListItemBinding
import com.example.product.databinding.StepListItemBinding

//import com.example.inventory2.databinding.ItemListItemBinding

/**
 * [ListAdapter] implementation for the recyclerview.
 */

class BatchStepListAdapter(private val onItemClicked: (ProdBatchStep) -> Unit,
                  val viewModel: ProdViewModel, val context: Context) :
    ListAdapter<ProdBatchStep, BatchStepListAdapter.BatchStepViewHolder>(DiffCallback) {

    val vm = viewModel
    val cn = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchStepViewHolder {
        return BatchStepViewHolder(
            //FragmentProductDetailBinding.inflate(
           StepListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: BatchStepViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current, vm, cn)
    }

    class BatchStepViewHolder(private var binding: StepListItemBinding) :
    //class ProdVersionViewHolder(private var binding: FragmentProductDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(batchStep: ProdBatchStep, viewModel: ProdViewModel,
                 context: Context) {
            val prodId = batchStep.prodId
            val version = batchStep.version
            val batch = batchStep.batchId
            val step = batchStep.stepId

            binding.stepName.text = batchStep.stepCode.toString() +"." +batchStep.stepName.toString()
            binding.stepName.setTypeface(binding.stepName.getTypeface(), Typeface.BOLD)

            if (!(batchStep.checkby.isNullOrEmpty())) {
                binding.stepChecked.visibility = View.VISIBLE
            }
            else
                binding.stepChecked.visibility = View.INVISIBLE
            when (batchStep.risk) {
                "safe" -> {
                    binding.stepName.setTextColor(Color.parseColor("#1b663e"))
                }

                "care" -> {
                    binding.stepName.setTextColor(Color.parseColor("#ff8c00"))
                }

                "danger" -> {
                    binding.stepName.setTextColor(Color.RED)
                }

                "checked" -> {
                    binding.stepName.setTextColor(Color.BLUE)
                }

            }
            if (!(batchStep.checkby.isNullOrEmpty())) {
                if (viewModel.modelRole=="Reader")
                    binding.undoStep.visibility=View.GONE
                else {
                    binding.undoStep.visibility = View.VISIBLE
                    binding.undoStep.tooltipText = "Undo " + binding.stepName.text.toString()


                    binding.undoStep.setOnClickListener {
                        showPasswordInputDialog(context,viewModel, binding.stepName.text.toString(),
                            prodId, version, batch, step )
                    }

                }
            }
            else
                binding.undoStep.visibility = View.INVISIBLE




        }

        private fun showPasswordInputDialog(context: Context,viewModel: ProdViewModel, stepName:String,
                                            prodId:Int, version:Int, batch:Int, step:Int
        ){
            val builder = AlertDialog.Builder(context)
            builder.setTitle("(WORK IN PROGRESS) Enter Password to undo "+stepName)

            // Set up the input
            val input = EditText(context)
            builder.setView(input)

            // Set up the buttons
            builder.setPositiveButton("OK") { dialog, which ->
                val password = input.text.toString()
                /*
                if (viewModel.checkPass(password)) {
                    // Handle the password entered here
                    // For example, you can validate the password or perform any other action
                    val stepdata = viewModel.currentBatchStep(prodId, version, batch, step)
                    viewModel.saveStep(
                        step, viewModel.prodId, viewModel.version, viewModel.batch,
                        viewModel.stepId, stepdata.reading!!, stepdata.detail!!,
                        "", "", stepdata.risk.toString()
                    )
                }

                 */

             }


            builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

            builder.show()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ProdBatchStep>() {
            override fun areItemsTheSame(oldItem: ProdBatchStep, newItem: ProdBatchStep): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ProdBatchStep, newItem: ProdBatchStep): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
