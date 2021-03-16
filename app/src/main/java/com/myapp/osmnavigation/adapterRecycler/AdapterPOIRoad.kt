package com.myapp.osmnavigation.adapterRecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myapp.osmnavigation.R
import com.myapp.osmnavigation.databinding.ListItemPoisRoadBinding
import org.osmdroid.bonuspack.location.POI

class AdapterPOIRoad(
    private val listResult: ArrayList<POI>,
    private val callbackTouch: ListenerCallbackPOI
) : RecyclerView.Adapter<ResultPOIViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultPOIViewHolder {
        val dataBinding: ListItemPoisRoadBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_pois_road,
            parent,
            false
        )
        return ResultPOIViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ResultPOIViewHolder, position: Int) {
        holder.dataBinding.also {
            it.viewPOIRoad = listResult[position]
            it.callbackPOI = callbackTouch
        }
    }

    override fun getItemCount() = listResult.size

    fun setData(newResult: ArrayList<POI>) {
        val diffCallback = POIDiffResult(listResult, newResult)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listResult.clear()
        listResult.addAll(newResult)
        diffResult.dispatchUpdatesTo(this)
    }
}

class ResultPOIViewHolder(var dataBinding: ListItemPoisRoadBinding) :
    RecyclerView.ViewHolder(dataBinding.root)

class ListenerCallbackPOI(val block: (POI) -> Unit) {
    fun onClick(result: POI) = block(result)

}

class POIDiffResult(
    private val oldList: List<POI>,
    private val newList: List<POI>
) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].mId == newList[newItemPosition].mId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}