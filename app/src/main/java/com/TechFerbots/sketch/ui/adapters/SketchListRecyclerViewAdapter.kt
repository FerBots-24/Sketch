package com.TechFerbots.sketch.ui.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sketch.databinding.SketchVhBinding
import com.TechFerbots.sketch.ui.models.Sketch
import java.lang.Exception

class SketchListRecyclerViewAdapter (val toSketchAction:(sketch: Sketch)->Unit):
    ListAdapter<Sketch, SketchListRecyclerViewAdapter.SketchViewHolder>(Diffcallback){

    class SketchViewHolder(private var binding: SketchVhBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(sketch: Sketch){
            binding.createdAtTv.text = sketch.createdAt
            binding.modifiedAtTv.text = sketch.modifiedAt
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SketchViewHolder {
        return SketchViewHolder(
            SketchVhBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SketchViewHolder, position: Int) {
        var sketchData = getItem(position)
        holder.bind(sketchData)
        holder.itemView.setOnClickListener {
            toSketchAction(sketchData)
        }
    }

    companion object{
        private val Diffcallback = object: DiffUtil.ItemCallback<Sketch>(){
            override fun areItemsTheSame(oldItem: Sketch, newItem: Sketch): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Sketch, newItem: Sketch): Boolean {
                return oldItem == newItem
            }
        }
    }


}