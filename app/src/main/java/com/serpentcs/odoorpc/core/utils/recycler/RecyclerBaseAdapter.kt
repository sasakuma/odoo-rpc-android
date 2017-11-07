package com.serpentcs.odoorpc.core.utils.recycler

import android.databinding.DataBindingUtil
import android.support.annotation.DrawableRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.serpentcs.odoorpc.R
import com.serpentcs.odoorpc.core.utils.recycler.entities.*
import com.serpentcs.odoorpc.databinding.ItemViewRecyclerEmptyBinding
import com.serpentcs.odoorpc.databinding.ItemViewRecyclerErrorBinding
import com.serpentcs.odoorpc.databinding.ItemViewRecyclerLessBinding
import com.serpentcs.odoorpc.databinding.ItemViewRecyclerMoreBinding


abstract class RecyclerBaseAdapter(
        protected val items: ArrayList<Any>,
        protected var recyclerView: RecyclerView
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    companion object {
        @JvmField
        val TAG = "RecyclerBaseAdapter"

        // The Empty view type
        @JvmField
        protected val VIEW_TYPE_EMPTY = -1

        // The Error view type
        @JvmField
        protected val VIEW_TYPE_ERROR = -2

        // The Less view type
        @JvmField
        protected val VIEW_TYPE_LESS = -3

        // The More view type
        @JvmField
        protected val VIEW_TYPE_MORE = -4

        @JvmField
        val ACTION_RV_HIDE_EMPTY = -5

        @JvmField
        val ACTION_RV_HIDE_ERROR = -6

        @JvmField
        val ACTION_RV_HIDE_LESS = -7

        @JvmField
        val ACTION_RV_HIDE_MORE = -8

        @JvmField
        val ACTION_RV_SHOW_EMPTY = -9

        @JvmField
        val ACTION_RV_SHOW_ERROR = -10

        @JvmField
        val ACTION_RV_SHOW_LESS = -11

        @JvmField
        val ACTION_RV_SHOW_MORE = -12

        @JvmField
        val ACTION_RV_HIDE_REFRESH = -13

        @JvmField
        val ACTION_RV_SHOW_REFRESH = -14

        @JvmField
        val ACTION_RV_ADD_MORE_LISTENER = -15

        @JvmField
        val ACTION_RV_REMOVE_MORE_LISTENER = -16

        private val DefaultVisibleThreshold = 2
    }

    protected val searchItems: ArrayList<Any> = ArrayList(items)
    private var isLoading: Boolean = false
    private var moreVisibleThreshold: Int = 0
    private var lessVisibleThreshold: Int = 0

    private var pvtMoreListener: (() -> Unit)? = null
    private var pvtLessListener: (() -> Unit)? = null
    private var pvtRetryListener: (() -> Unit)? = null

    init {
        setupScrollListener(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val inflater = LayoutInflater.from(parent!!.context)
        when (viewType) {
            VIEW_TYPE_EMPTY -> {
                val binding = DataBindingUtil.inflate<ItemViewRecyclerEmptyBinding>(
                        inflater,
                        R.layout.item_view_recycler_empty,
                        parent,
                        false
                )
                return EmptyViewHolder(binding)
            }
            VIEW_TYPE_ERROR -> {
                val binding = DataBindingUtil.inflate<ItemViewRecyclerErrorBinding>(
                        inflater,
                        R.layout.item_view_recycler_error,
                        parent,
                        false
                )
                return ErrorViewHolder(binding)
            }
            VIEW_TYPE_LESS -> {
                val binding = DataBindingUtil.inflate<ItemViewRecyclerLessBinding>(
                        inflater,
                        R.layout.item_view_recycler_less,
                        parent,
                        false
                )
                return LessViewHolder(binding)
            }
            VIEW_TYPE_MORE -> {
                val binding = DataBindingUtil.inflate<ItemViewRecyclerMoreBinding>(
                        inflater,
                        R.layout.item_view_recycler_more,
                        parent,
                        false
                )
                return MoreViewHolder(binding)
            }
        }
        return null
    }

    override fun onBindViewHolder(baseHolder: RecyclerView.ViewHolder?, basePosition: Int) {
        if (baseHolder != null) {
            val position = baseHolder.adapterPosition
            when (getItemViewType(position)) {
                VIEW_TYPE_EMPTY -> {
                    val holder = baseHolder as EmptyViewHolder
                    val item = items[position] as EmptyItem
                    holder.binding.tvMessage.text = item.message
                    val drawableResId = item.drawableResId
                    if (drawableResId > 0) {
                        holder.binding.ivIcon.setImageResource(drawableResId)
                    }
                }
                VIEW_TYPE_ERROR -> {
                    val holder = baseHolder as ErrorViewHolder
                    val item = items[position] as ErrorItem
                    holder.binding.tvCause.text = item.message
                    holder.binding.bnRetry.setOnClickListener {
                        hideError()
                        pvtRetryListener?.invoke()
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is EmptyItem -> {
                VIEW_TYPE_EMPTY
            }
            is ErrorItem -> {
                VIEW_TYPE_ERROR
            }
            is Int -> {
                item
            }
            else -> {
                super.getItemViewType(position)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                filterResults.count = searchItems.size
                filterResults.values = searchItems
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                @Suppress("UNCHECKED_CAST")
                addAll(results!!.values!! as ArrayList<Any>)
            }
        }
    }

    fun setupScrollListener(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        val layoutManager = recyclerView.layoutManager
        if (layoutManager != null && layoutManager is LinearLayoutManager) {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (pvtMoreListener != null || pvtLessListener != null) {
                        val totalItemCount = layoutManager.itemCount
                        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                        if (!isLoading) {
                            if (totalItemCount <= lastVisibleItem + moreVisibleThreshold) {
                                if (pvtMoreListener != null) {
                                    synchronized(this) {
                                        isLoading = true
                                        recyclerView!!.post {
                                            showMore()
                                            pvtMoreListener!!.invoke()
                                        }
                                    }
                                }
                            } else if (0 >= firstVisibleItem - lessVisibleThreshold) {
                                if (pvtLessListener != null) {
                                    synchronized(this) {
                                        isLoading = true
                                        recyclerView!!.post {
                                            showLess()
                                            pvtLessListener!!.invoke()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    fun clear() {
        val start = 0
        val count = itemCount
        items.clear()
        notifyItemRangeRemoved(start, count)
    }

    fun addAll(items: ArrayList<Any>) {
        val start = itemCount
        val count = items.size
        this.items.addAll(items)
        notifyItemRangeInserted(start, count)
    }

    fun showEmpty(
            message: String = recyclerView.context.getString(R.string.recycler_empty_title),
            @DrawableRes
            drawableResId: Int = R.drawable.ic_format_list_bulleted_black_24dp
    ) {
        clear()
        items += EmptyItem(message, drawableResId)
        notifyItemInserted(0)
    }

    fun hideEmpty() = clear()

    fun showError(message: String) {
        clear()
        items += ErrorItem(message)
        notifyItemInserted(0)
    }

    fun hideError() = clear()

    fun showLess() {
        val position = 0
        val start = position + 1
        val count = itemCount - position
        items.add(position, VIEW_TYPE_LESS)
        notifyItemInserted(position)
        notifyItemRangeChanged(start, count)
    }

    fun hideLess() {
        if (itemCount > 0) {
            val item = items[0]
            if (item is Int && item == VIEW_TYPE_LESS) {
                val position = 0
                @Suppress("UnnecessaryVariable")
                val start = position
                val count = itemCount - 1 - position
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(start, count)
            }
        }
    }

    fun showMore() {
        val position = itemCount
        items += VIEW_TYPE_MORE
        notifyItemInserted(position)
    }

    fun hideMore() {
        if (itemCount > 0) {
            val position = itemCount - 1
            val item = items[position]
            if (item is Int && item == VIEW_TYPE_MORE) {
                items.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    fun updateSearchItems() {
        searchItems.clear()
        searchItems.addAll(items)
    }

    fun moreListener(
            visibleThreshold: Int = DefaultVisibleThreshold,
            moreListener: () -> Unit
    ) {
        moreVisibleThreshold = visibleThreshold
        pvtMoreListener = moreListener
    }

    fun hasMoreListener(): Boolean = pvtMoreListener != null

    fun removeMoreListener() {
        pvtMoreListener = null
    }

    fun finishedlessLoading() {
        isLoading = false
    }

    fun lessListener(
            visibleThreshold: Int = DefaultVisibleThreshold,
            lessListener: () -> Unit
    ) {
        lessVisibleThreshold = visibleThreshold
        pvtLessListener = lessListener
    }

    fun hasLessListener(): Boolean = pvtLessListener != null

    fun removeLessListener() {
        pvtLessListener = null
    }

    fun finishedMoreLoading() {
        isLoading = false
    }

    fun retryListener(retryListener: () -> Unit) {
        pvtRetryListener = retryListener
    }

    fun hasRetryListener(): Boolean = pvtRetryListener != null

    fun removeRetryListener() {
        pvtRetryListener = null
    }
}