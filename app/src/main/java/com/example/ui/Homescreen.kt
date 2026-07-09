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
    val selectedPlaylist by viewModel.selectedPlaylist.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "EngTv",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(20.dp))


        if (playlists.isEmpty()) {

            Button(
                onClick = {

                    viewModel.addPlaylistFromUrl(
                        "ALWAN SPORTS",
                        "https://m3uextractor.indexiptv212.workers.dev/download/0241cbac-61a4-4b8e-9973-1c289d2232fc/Live_AR%20_%20ALWAN%20VIP.m3u8"
                    )

                }
            ) {

                Text("تحميل القنوات")

            }

        } else {


            Text(
                text = "القوائم",
                style = MaterialTheme.typography.titleLarge
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


            Spacer(modifier = Modifier.height(20.dp))


            LazyColumn {

                items(channels) { channel ->

                    ChannelItem(channel)

                }

            }

        }

    }

}



@Composable
fun ChannelItem(channel: Channel) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {

                // لاحقاً نربط المشغل هنا

            }
    ) {

        Text(
            text = channel.name,
            modifier = Modifier.padding(16.dp)
        )

    }

}
