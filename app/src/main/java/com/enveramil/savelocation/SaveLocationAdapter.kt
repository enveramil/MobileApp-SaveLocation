package com.enveramil.savelocation

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.enveramil.savelocation.databinding.RecyclerRowBinding
import com.enveramil.savelocation.model.Location

class SaveLocationAdapter(var list : List<Location>) : RecyclerView.Adapter<SaveLocationAdapter.SaveLocationHolder>() {

    class SaveLocationHolder(var binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveLocationHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SaveLocationHolder(binding)
    }

    override fun onBindViewHolder(holder: SaveLocationHolder, position: Int) {
        holder.binding.recyclerRow.text = list.get(position).locationName
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,MapsActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}