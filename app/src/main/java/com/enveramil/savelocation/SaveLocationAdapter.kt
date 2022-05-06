package com.enveramil.savelocation

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.enveramil.savelocation.databinding.RecyclerRowBinding
import com.enveramil.savelocation.model.Location

class SaveLocationAdapter(var list : List<Location>) : RecyclerView.Adapter<SaveLocationAdapter.SaveLocationHolder>() {
    private lateinit var animation : Animation
    var lastPosition = -1

    class SaveLocationHolder(var binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveLocationHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SaveLocationHolder(binding)
    }

    override fun onBindViewHolder(holder: SaveLocationHolder, position: Int) {
        holder.binding.recyclerRow.text = list.get(position).locationName
        holder.binding.cardView.setOnClickListener {
            val intent = Intent(holder.itemView.context,MapsActivity::class.java)
            intent.putExtra("selectedPlace",list.get(position))
            intent.putExtra("info","old")
            holder.itemView.context.startActivity(intent)
        }

        if (holder.adapterPosition > lastPosition){
            animation = AnimationUtils.loadAnimation(holder.itemView.context,R.anim.slide_in_row)
            holder.itemView.startAnimation(animation)
            lastPosition = holder.adapterPosition

        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
}