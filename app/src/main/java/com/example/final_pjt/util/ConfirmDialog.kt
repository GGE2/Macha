package com.example.final_pjt.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.final_pjt.activity.RoomActivity
import com.example.final_pjt.databinding.DialogCreateRoomBinding

class ConfirmDialog(
    confirmDialogInterface : ConfirmDialogInterface
): DialogFragment(){
    private var _binding : DialogCreateRoomBinding? = null
    private val binding get() = _binding!!
    private var confirmDialogInterface : ConfirmDialogInterface? = null
    init{
        this.confirmDialogInterface = confirmDialogInterface
    }
    fun onViewCreated(view: View, container: ViewGroup?, savedInstanceState: Bundle?):View {
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogCreateRoomBinding.inflate(layoutInflater,container,false)
        val view = binding.root

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding!!.alertCancleBtn.setOnClickListener {
            dismiss()
        }
        binding!!.alertOkBtn.setOnClickListener {
            this.confirmDialogInterface?.onYesButtonClick()
            dismiss()
        }
        return view
    }

    interface ConfirmDialogInterface{
        fun onYesButtonClick()
    }

}
