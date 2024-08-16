package com.example.musicapp.ui.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.data.MusicData
import com.example.musicapp.databinding.ItemMusicBinding
import com.example.musicapp.utils.*


class MusicListAdapter : ListAdapter<MusicData, MusicListAdapter.MusicHolder>(MusicDiffUtil) {

    private var onClickMusic: ((MusicData) -> Unit)? = null
    private var oldSelectedMusicPos: Int = -1
    private var playingState = false

    private var time = 0L

    inner class MusicHolder(private val binding: ItemMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (System.currentTimeMillis() - time >= 500) {
                    time = System.currentTimeMillis()
                    selectMusicPos = absoluteAdapterPosition
                    onClickMusic?.invoke(
                        getItem(absoluteAdapterPosition)
                    )
                }
            }
        }

        fun bind() {
            getItem(absoluteAdapterPosition)?.apply {
                if (image != null) {
                    binding.musicImg.setImageBitmap(image)
                    binding.musicImg.setPadding(0, 0, 0, 0)
                } else {
                    binding.musicImg.setImageResource(R.drawable.ic_music_note_svgrepo_com)
                    binding.musicImg.setPadding(35, 40, 40, 40)
                }
                val color = binding.root.context.getColor(R.color.active_music_text_color)
                binding.lottie.setColorFilter(color)
                if (absoluteAdapterPosition == selectMusicPos) {
                    binding.lottie.isGone = false
                    binding.musicName.setTextColor(color)
                    binding.musicImg.foreground = ColorDrawable(Color.parseColor("#6F000000"))
                } else {
                    binding.lottie.isGone = true
                    binding.musicName.setTextColor(Color.parseColor("#FFFFFF"))
                    binding.musicImg.foreground = ColorDrawable(Color.TRANSPARENT)
                }
                if (playingState) {
                    binding.lottie.playAnimation()
                } else {
                    binding.lottie.pauseAnimation()
                }
                musicLogger(
                    "adapter bind ga keldi pos $absoluteAdapterPosition playingState : $playingState",
                    "MMM"
                )
                binding.musicName.text = title
                binding.artist.text = artist
                binding.btnOption.setOnClickListener {

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        return MusicHolder(
            ItemMusicBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        holder.bind()
    }

    fun setOnClickMusic(onClickMusic: ((MusicData) -> Unit)) {
        this.onClickMusic = onClickMusic
    }

    fun playingChange() {
        musicLogger(
            "adapaterga o'zgartirish keldi: selce $selectMusicPos old $oldSelectedMusicPos",
            "MMM"
        )
        if (oldSelectedMusicPos != -1) {
            notifyItemChanged(oldSelectedMusicPos)
        }
        if (selectMusicPos != -1) {
            notifyItemChanged(selectMusicPos)
        }
        oldSelectedMusicPos = selectMusicPos
    }

    fun setManageState(state: Boolean) {
        playingState = state
        notifyItemChanged(selectMusicPos)
    }

    object MusicDiffUtil : DiffUtil.ItemCallback<MusicData>() {
        override fun areItemsTheSame(oldItem: MusicData, newItem: MusicData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MusicData, newItem: MusicData): Boolean {
            return oldItem == newItem
        }
    }
}