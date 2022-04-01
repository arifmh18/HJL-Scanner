package com.artry.scannerBarcode.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.artry.scannerBarcode.DetailDataDashboard
import com.artry.scannerBarcode.Model.DashboardModel
import com.artry.scannerBarcode.Model.PaketModel
import com.artry.scannerBarcode.databinding.ItemDashboardBinding

class DetailDataListAdapter(val context: Context, val list: MutableList<PaketModel>) : RecyclerView.Adapter<DetailDataListAdapter.ListViewHolder>()  {

    private lateinit var binding : ItemDashboardBinding
    private var mLastClickTime: Long = 0

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemDate = binding.dateItem
        var itemName = binding.itemName
        var itemConten = binding.itemContent
        var itemDelete = binding.btnDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        binding = ItemDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = list[position]
        holder.itemDate.text = data.date
        holder.itemName.text = data.codepaket
        holder.itemDelete.setOnClickListener {
            val intent = Intent("deleteRow")
            intent.putExtra("id", data.id)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}