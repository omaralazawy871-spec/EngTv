package com.example.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.data.Channel


@Composable
fun PlayerScreen(
    channel: Channel,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val player = remember {

        ExoPlayer.Builder(context)
            .build()
            .apply {

                setMediaItem(
                    MediaItem.fromUri(
                        Uri.parse(channel.streamUrl)
                    )
                )

                prepare()
                play()

            }

    }


    AndroidView(
        factory = {

            PlayerView(it).apply {

                this.player = player

            }

        },
        modifier = Modifier
    )

}
