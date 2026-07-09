package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Channel

@Composable
fun HomeScreen(
    viewModel: IptvViewModel = viewModel()
) {

    val playlists by viewModel.playlists.collectAsState()
    val channels by viewModel.channels.collectAsState()

    var selectedChannel by remember {
        mutableStateOf<Channel?>(null)
    }


    if (selectedChannel != null) {

        PlayerScreen(
            channel = selectedChannel!!,
            onBack = {
                selectedChannel = null
            }
        )

        return
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {


        Text(
            text = "EngTv",
            style = MaterialTheme.typography.headlineLarge
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        if (playlists.isEmpty()) {


            Button(
                onClick = {

                    viewModel.addPlaylistFromUrl(
                        "ALWAN SPORTS",
                        "https://m3uextractor.indexiptv212.workers.dev/download/0241cbac-61a4-4b8e-9973-1c289d2232fc/Live_AR%20_%20ALWAN%20VIP.m3u8"
                    )

                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("تحميل القنوات")

            }


        } else {


            Text(
                text = "القوائم",
                style = MaterialTheme.typography.titleLarge
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            playlists.forEach { playlist ->

                Button(
                    onClick = {
                        viewModel.selectPlaylist(playlist)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(playlist.name)

                }

            }


            Spacer(
                modifier = Modifier.height(15.dp)
            )


            LazyColumn {

                items(channels) { channel ->


                    ChannelItem(
                        channel = channel,
                        onClick = {

                            selectedChannel = channel

                        }
                    )


                }

            }

        }

    }

}



@Composable
fun ChannelItem(
    channel: Channel,
    onClick: () -> Unit
) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {

                onClick()

            }
    ) {


        Column(
            modifier = Modifier.padding(16.dp)
        ) {


            Text(
                text = channel.name,
                style = MaterialTheme.typography.titleMedium
            )


            Text(
                text = channel.groupTitle ?: "Other"
            )


        }


    }


}
