package com.graduation.travelbook2.search.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.compose.ui.res.colorResource
import androidx.core.graphics.red
import com.graduation.travelbook2.R
import com.graduation.travelbook2.databinding.DialogSelColorBinding

class DialogSelectColor(context: Context): Dialog(context) {
    private lateinit var itemClickListener: ItemClickListener
    private lateinit var binding: DialogSelColorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSelColorBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setCancelable(false)

        binding.apply {
            btnRed.setOnClickListener {
                // 색깔을 클릭할때, 리스너에 클릭되 색 전달
                itemClickListener.onClick(Color.RED)
                dismiss()
            }

            btnBlue.setOnClickListener {
                // 색깔을 클릭할때, 리스너에 클릭되 색 전달
                itemClickListener.onClick(Color.BLUE)
                dismiss()
            }

            btnGreen.setOnClickListener {
                // 색깔을 클릭할때, 리스너에 클릭되 색 전달
                itemClickListener.onClick(Color.GREEN)
                dismiss()
            }

            btnYellow.setOnClickListener {
                // 색깔을 클릭할때, 리스너에 클릭되 색 전달
                itemClickListener.onClick(Color.YELLOW)
                dismiss()
            }

            btnWhite.setOnClickListener {
                // 색깔을 클릭할때, 리스너에 클릭되 색 전달
                itemClickListener.onClick(Color.WHITE)
                dismiss()
            }

            btnBlack.setOnClickListener {
                // 색깔을 클릭할때, 리스너에 클릭되 색 전달
                itemClickListener.onClick(Color.BLACK)
                dismiss()
            }

        }
    }

    interface ItemClickListener {
        fun onClick(color: Int)
    }

    fun setItemClickListener(itemCLIckListener: ItemClickListener){
        this.itemClickListener = itemCLIckListener
    }
}