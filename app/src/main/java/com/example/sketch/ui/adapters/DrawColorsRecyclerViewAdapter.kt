package com.example.sketch.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sketch.R
import com.example.sketch.databinding.DrawColorCircleBinding
import com.example.sketch.databinding.SketchVhBinding
import com.example.sketch.ui.models.ChooseColor
import com.example.sketch.ui.models.Sketch

class DrawColorsRecyclerViewAdapter (val context: Context, val setStrokeColorAction:(String)->Unit):
    ListAdapter<ChooseColor, DrawColorsRecyclerViewAdapter.ColorCircleViewHolder>(Diffcallback){

    class ColorCircleViewHolder(private var binding: DrawColorCircleBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(colorData: ChooseColor, context: Context){
            binding.colorIv.setColorFilter(Color.parseColor(colorData.colorString), android.graphics.PorterDuff.Mode.MULTIPLY)
            if (colorData.isSelected){
                binding.colorIv.background = context.resources.getDrawable(R.drawable.mode_enabled)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawColorsRecyclerViewAdapter.ColorCircleViewHolder {
        return DrawColorsRecyclerViewAdapter.ColorCircleViewHolder(
            DrawColorCircleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: DrawColorsRecyclerViewAdapter.ColorCircleViewHolder, position: Int) {
        var colorData = getItem(position)
        holder.bind(colorData, context)
        holder.itemView.setOnClickListener {
            setStrokeColorAction(colorData.colorString)
        }
    }

    companion object{
        private val Diffcallback = object: DiffUtil.ItemCallback<ChooseColor>(){
            override fun areItemsTheSame(oldItem: ChooseColor, newItem: ChooseColor): Boolean {
                return oldItem.colorString == newItem.colorString && oldItem.isSelected == newItem.isSelected
            }

            override fun areContentsTheSame(oldItem: ChooseColor, newItem: ChooseColor): Boolean {
                return oldItem == newItem
            }
        }
    }


}