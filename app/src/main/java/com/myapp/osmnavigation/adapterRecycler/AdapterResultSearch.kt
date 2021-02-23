package com.myapp.osmnavigation.adapterRecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myapp.osmnavigation.R
import com.myapp.osmnavigation.databinding.ListItemSearchBinding
import com.myapp.osmnavigation.util.InfoPlacesList

class AdapterResultSearch(
    private val listResult: ArrayList<InfoPlacesList>,
    private val callbackTouch: ListenerCallbackTouch
) : RecyclerView.Adapter<ResultSearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultSearchViewHolder {
        val dataBinding: ListItemSearchBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_search,
            parent,
            false
        )
        return ResultSearchViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ResultSearchViewHolder, position: Int) {
        holder.dataBinding.also {
            it.viewResults = listResult[position]
            it.callback = callbackTouch
        }
    }

    override fun getItemCount() = listResult.size

    fun setData(newResult: ArrayList<InfoPlacesList>) {
        val diffCallback = DetailDiffResult(listResult, newResult)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listResult.clear()
        listResult.addAll(newResult)
        diffResult.dispatchUpdatesTo(this)
    }
}

class ResultSearchViewHolder(var dataBinding: ListItemSearchBinding) :
    RecyclerView.ViewHolder(dataBinding.root)

class ListenerCallbackTouch(val block: (InfoPlacesList) -> Unit) {
    fun onClick(result: InfoPlacesList) = block(result)

}

class DetailDiffResult(
    private val oldList: List<InfoPlacesList>,
    private val newList: List<InfoPlacesList>
) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].name == newList[newItemPosition].name

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}