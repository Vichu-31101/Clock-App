package com.example.clockapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clockapp.R
import com.example.clockapp.classes.StopwatchClass
import kotlinx.android.synthetic.main.sw_list_layout.view.*

class swAdapter(private var list: MutableList<StopwatchClass>)
    : RecyclerView.Adapter<SwViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.sw_list_layout, parent, false)
        return SwViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: SwViewHolder, position: Int) {
        val sw = list[position]
        holder.view.flag.text = sw.flag
        holder.view.time.text = sw.time
        holder.view.number.text = (list.size).toString()
    }

    override fun getItemCount(): Int = list.size

    fun addData(sw: StopwatchClass){
        list.add(sw)
        notifyItemInserted(list.size-1)

    }

    fun deleteAll(){
        list.clear()
        notifyDataSetChanged()
    }
}


class SwViewHolder(val view: View) :
    RecyclerView.ViewHolder(view)