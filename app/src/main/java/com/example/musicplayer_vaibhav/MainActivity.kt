package com.example.musicplayer_vaibhav

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.example.musicplayer_vaibhav.musicPlayer.MusicPlayerScreen
import com.example.musicplayer_vaibhav.musicPlayer.PiPActionReceiver
import com.example.musicplayer_vaibhav.musicPlayer.viewmodel.MusicPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var isInPiPMode by mutableStateOf(false) // Track PiP state to handle the Ui for PIP Mode
    private lateinit var pipReceiver: PiPActionReceiver
    // initializing view model here to use same instance for UI and PIP Service
    private val viewModel: MusicPlayerViewModel by viewModels()

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // using compose for the UI and passing viewmodel instance
        setContent {
            MusicPlayerScreen(isInPiPMode,viewModel)
        }
        pipReceiver = PiPActionReceiver(viewModel)

        val filter = IntentFilter().apply {
            addAction(PlayerNotificationManager.ACTION_PLAY)
            addAction(PlayerNotificationManager.ACTION_PREVIOUS)
            addAction(PlayerNotificationManager.ACTION_NEXT)
        }
        registerReceiver(
            pipReceiver, filter, RECEIVER_EXPORTED
        )

    }

    @OptIn(UnstableApi::class)
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPipMode()
    }

    @UnstableApi
    private fun enterPipMode() {
        val previousIntent = PendingIntent.getBroadcast(
            this, 0, Intent(PlayerNotificationManager.ACTION_PREVIOUS), PendingIntent.FLAG_IMMUTABLE
        )
        val pauseIntent = PendingIntent.getBroadcast(
            this, 0, Intent(PlayerNotificationManager.ACTION_PLAY), PendingIntent.FLAG_IMMUTABLE
        )
        val nextIntent = PendingIntent.getBroadcast(
            this, 0, Intent(PlayerNotificationManager.ACTION_NEXT), PendingIntent.FLAG_IMMUTABLE
        )

        // adding actions to listen intent from PIP Mode.
        val actions = listOf(
            RemoteAction(
                Icon.createWithResource(this, R.drawable.previous),
                "Previous",
                "Previous",
                previousIntent
            ),
            RemoteAction(
                Icon.createWithResource(this, R.drawable.play_pause),
                "Pause",
                "Pause",
                pauseIntent
            ),
            RemoteAction(
                Icon.createWithResource(this, R.drawable.forward),
                "Next",
                "Next",
                nextIntent
            )
        )

        val params = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(1, 1)) // Square PiP window
            .setActions(actions)
            .build()

        enterPictureInPictureMode(params)
    }


    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    )  {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        // updating pipMode state here
        isInPiPMode = isInPictureInPictureMode
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(pipReceiver) //unregister to avoid any memory leaks
    }
}

