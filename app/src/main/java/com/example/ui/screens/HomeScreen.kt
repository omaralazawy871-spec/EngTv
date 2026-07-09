package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.Channel
import com.example.viewmodel.ChannelViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ChannelViewModel,
    onChannelClick: (Channel) -> Unit
) {


    val channels by viewModel.channels.collectAsState()

    var searchText by remember {
        mutableStateOf("")
    }


    LaunchedEffect(Unit) {
        viewModel.loadChannels()
    }


    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Eng TV")
                },

                actions = {

                    IconButton(
                        onClick = {
                            viewModel.loadChannels()
                        }
                    ) {

                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }


    ) { padding ->


        Column(

            modifier = Modifier
                .padding(padding)
                .fillMaxSize()

        ) {


            OutlinedTextField(

                value = searchText,

                onValueChange = {

                    searchText = it

                    viewModel.search(it)

                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),

                placeholder = {
                    Text("بحث عن قناة...")
                },

                leadingIcon = {

                    Icon(
                        Icons.Default.Search,
                        null
                    )
                }

            )



            LazyColumn(

                modifier = Modifier
                    .fillMaxSize()

            ) {


                items(channels) { channel ->


                    ChannelItem(

                        channel = channel,

                        onClick = {
                            onChannelClick(channel)
                        },

                        onFavorite = {
                            viewModel.favorite(channel)
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
    onClick: () -> Unit,
    onFavorite: () -> Unit
) {


    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
            .clickable {
                onClick()
            }

    ) {


        Row(

            modifier = Modifier
                .padding(12.dp),

            horizontalArrangement = Arrangement.SpaceBetween

        ) {


            Row(
                modifier = Modifier.weight(1f)
            ) {


                AsyncImage(

                    model = channel.logo,

                    contentDescription = null,

                    modifier = Modifier
                        .size(55.dp)

                )


                Spacer(
                    modifier = Modifier.width(12.dp)
                )


                Column {

                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.titleMedium
                    )


                    Text(
                        text = channel.category,
                        style = MaterialTheme.typography.bodySmall
                    )

                }

            }



            IconButton(
                onClick = {
                    onFavorite()
                }
            ) {

                Icon(
                    Icons.Default.Star,
                    contentDescription = "Favorite"
                )

            }

        }

    }

}
