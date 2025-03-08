package com.example.musicplayer_vaibhav.musicPlayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.musicplayer_vaibhav.musicPlayer.viewmodel.MusicPlayerViewModel

// PIP Broadcast service to receive the action intent in PIP Mode
class PiPActionReceiver : BroadcastReceiver() {
    private lateinit var musicPlayerViewModel: MusicPlayerViewModel
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        musicPlayerViewModel.handlePiPAction(action)
    }

    fun setViewModel(viewModel: MusicPlayerViewModel){
        musicPlayerViewModel = viewModel
    }
}
