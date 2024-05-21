package com.example.prodman



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
//import com.example.inventory2.databinding.ItemListItemBinding
import com.example.prodman.data.ProdVersion
import com.example.product.databinding.FragmentProductDetailBinding
import com.example.product.databinding.ProductListProductBinding
import com.example.product.databinding.ProdversionListItemBinding

//import com.example.inventory2.databinding.ItemListItemBinding

/**
 * [ListAdapter] implementation for the recyclerview.
 */

class ProdVersionListAdapter(private val onItemClicked: (ProdVersion) -> Unit) :
    ListAdapter<ProdVersion, ProdVersionListAdapter.ProdVersionViewHolder>(DiffCallback) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdVersionViewHolder {
        return ProdVersionViewHolder(
            //FragmentProductDetailBinding.inflate(
            ProdversionListItemBinding.inflate(
                    LayoutInflater.from(
                    parent.context
                )
            )
       // ProdversionListProductBinding.

        )
    }

    override fun onBindViewHolder(holder: ProdVersionViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class ProdVersionViewHolder(private var binding: ProdversionListItemBinding) :
    //class ProdVersionViewHolder(private var binding: FragmentProductDetailBinding) :
    RecyclerView.ViewHolder(binding.root) {

        fun bind(prodVersion: ProdVersion) {

            binding.versionName.text = "Version " +prodVersion.version.toString() +" of prod#"+ prodVersion.prodId.toString()+":"+prodVersion.comments
            //binding.ProdVersionPackaging.text =
         }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ProdVersion>() {
            override fun areItemsTheSame(oldItem: ProdVersion, newItem: ProdVersion): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ProdVersion, newItem: ProdVersion): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
