package com.myapp.osmnavigation.adapterRecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myapp.osmnavigation.R
import com.myapp.osmnavigation.databinding.ListItemPoisRoadBinding
import com.myapp.osmnavigation.databinding.ListItemSelectPlacesBinding
import org.osmdroid.bonuspack.location.POI

class AdapterSelectPOIPlaces (
    private val listResult: ArrayList<POI>,
    private val callbackTouch: ListenerCallbackPOISelect
) : RecyclerView.Adapter<ResultSelectPOIViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultSelectPOIViewHolder {
        val dataBinding: ListItemSelectPlacesBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_select_places,
            parent,
            false
        )
        return ResultSelectPOIViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ResultSelectPOIViewHolder, position: Int) {
        holder.dataBinding.also {
            it.viewItemSelect = listResult[position]
            it.callViewSelect = callbackTouch
        }
    }

    override fun getItemCount() = listResult.size

    fun setData(newResult: ArrayList<POI>) {
        val diffCallback = POISelectDiffResult(listResult, newResult)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listResult.clear()
        listResult.addAll(newResult)
        diffResult.dispatchUpdatesTo(this)
    }
}

class ResultSelectPOIViewHolder(var dataBinding: ListItemSelectPlacesBinding) :
    RecyclerView.ViewHolder(dataBinding.root)

class ListenerCallbackPOISelect(val block: (POI) -> Unit) {
    fun onClick(result: POI) = block(result)

}

class POISelectDiffResult(
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
