package com.example.prodman



import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
//import com.example.inventory2.databinding.ItemListItemBinding
import com.example.prodman.data.ProdVersion
import com.example.prodman.data.ProdVersionBatch
import com.example.product.databinding.BatchListItemBinding
import com.example.product.databinding.FragmentProductDetailBinding
import com.example.product.databinding.ProductListProductBinding
import com.example.product.databinding.ProdversionListItemBinding

//import com.example.inventory2.databinding.ItemListItemBinding

/**
 * [ListAdapter] implementation for the recyclerview.
 */

class BatchListAdapter(private val onItemClicked: (ProdVersionBatch) -> Unit) :
    ListAdapter<ProdVersionBatch, BatchListAdapter.BatchViewHolder>(DiffCallback) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchViewHolder {
        return BatchViewHolder(
            //FragmentProductDetailBinding.inflate(
            BatchListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
            // ProdversionListProductBinding.

        )
    }

    override fun onBindViewHolder(holder: BatchViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class BatchViewHolder(private var binding: BatchListItemBinding) :
    //class ProdVersionViewHolder(private var binding: FragmentProductDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(prodVersionBatch: ProdVersionBatch) {
            var finDate:String? = prodVersionBatch.finishDate?:""
            if (finDate!!.length==0) {
                binding.batchDate.text =
                    "Batch " + prodVersionBatch.batch + " started " + prodVersionBatch.startDate +'('+prodVersionBatch.progress.toString()+"%)"

            }
            else {
                binding.batchDate.text =
                    "Batch " + prodVersionBatch.batch + " started " + prodVersionBatch.startDate + ", finished " + prodVersionBatch.finishDate + '('+prodVersionBatch.progress.toString()+"%)"
                if (prodVersionBatch.finishDate !=null)
                    binding.batchDate.setTypeface(binding.batchDate.getTypeface(), Typeface.BOLD)


            }
           //binding.ProdVersionPackaging.text =
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ProdVersionBatch>() {
            override fun areItemsTheSame(oldItem: ProdVersionBatch, newItem: ProdVersionBatch): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ProdVersionBatch, newItem: ProdVersionBatch): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
