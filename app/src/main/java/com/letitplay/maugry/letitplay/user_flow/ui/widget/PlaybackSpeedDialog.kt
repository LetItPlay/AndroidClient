package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.PlaybackSpeed
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import kotlinx.android.synthetic.main.playback_speed_item.view.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


class PlaybackSpeedDialog @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttrs: Int = 0
): FrameLayout(context, attributeSet, defStyleAttrs) {
    private val recyclerView: RecyclerView

    var onOptionClick: (PlaybackSpeed) -> Unit = {}
    lateinit var optionAdapter: OptionAdapter

    init {
        LayoutInflater.from(context).inflate(R.layout.playback_speed_dialog, this)
        recyclerView = findViewById(R.id.options_recycler)
    }

    fun setItems(options: List<PlaybackSpeed>, currentIndex: Int) {
        recyclerView.adapter = OptionAdapter(options, currentIndex).also {
            optionAdapter = it
        }
    }

    fun selectAt(index: Int) {
        optionAdapter.checkedIndex = index
        optionAdapter.notifyDataSetChanged()
    }

    inner class OptionAdapter(
            private val options: List<PlaybackSpeed>,
            var checkedIndex: Int
    ): RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
            return OptionViewHolder(parent).apply {
                itemView.setOnClickListener {
                    if (adapterPosition == RecyclerView.NO_POSITION) {
                        onOptionClick(options[adapterPosition])
                    }
                }
            }
        }

        override fun getItemCount(): Int = options.size

        override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
            holder.bind(options[position])
            holder.bindChecked(position == checkedIndex)
        }

        inner class OptionViewHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.playback_speed_item) {

            fun bind(speed: PlaybackSpeed) {
                val caption =  formatValue(speed.value)
                itemView.option_caption.text = caption
            }

            fun bindChecked(value: Boolean) {
                itemView.option_check.visibility = if (value) View.VISIBLE else View.GONE
            }

            private fun formatValue(value: Float): String {
                val df = DecimalFormat("#.##x")
                val sym = DecimalFormatSymbols.getInstance()
                sym.decimalSeparator = '.'
                df.decimalFormatSymbols = sym
                return df.format(value)
            }
        }
    }
}