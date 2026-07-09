package com.example.ui

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
import com.example.data.Channel


@Composable
fun HomeScreen(
    viewModel: IptvViewModel = viewModel()
) {

    val channels by viewModel.channels.collectAsState()
    val favorites by viewModel.favoriteChannels.collectAsState()


    var selectedChannel by remember {
        mutableStateOf<Channel?>(null)
    }


    var searchText by remember {
        mutableStateOf("")
    }


    var showFavorites by remember {
        mutableStateOf(false)
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
            .padding(12.dp)
    ) {



        Text(
            text = "EngTv",
            style = MaterialTheme.typography.headlineLarge
        )



        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            IconButton(
                onClick = {

                    // البحث موجود بالحقل

                }
            ) {

                Icon(
                    Icons.Default.Search,
                    "Search"
                )

            }



            IconButton(
                onClick = {

                    viewModel.refresh()

                }
            ) {

                Icon(
                    Icons.Default.Refresh,
                    "Refresh"
                )

            }



            IconButton(
                onClick = {

                    showFavorites = !showFavorites

                }
            ) {

                Icon(
                    Icons.Default.Star,
                    "Favorites"
                )

            }



        }




        OutlinedTextField(

            value = searchText,

            onValueChange = {
                searchText = it
            },

            modifier = Modifier.fillMaxWidth(),

            placeholder = {
                Text("بحث عن قناة")
            }

        )



        Spacer(
            modifier = Modifier.height(15.dp)
        )



        val displayChannels =
            if (showFavorites)
                favorites
            else
                channels



        LazyColumn {


            val filtered =
                displayChannels.filter {

                    it.name.contains(
                        searchText,
                        ignoreCase = true
                    )

                }



            val groups =
                filtered.groupBy {

                    it.groupTitle ?: "OTHER SPORTS"

                }



            groups.forEach { (group, list) ->



                item {


                    Text(

                        text = "📂 $group",

                        style =
                            MaterialTheme.typography.titleLarge,

                        modifier =
                            Modifier.padding(8.dp)

                    )


                }



                items(list) { channel ->



                    Card(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .clickable {

                                    selectedChannel =
                                        channel

                                }

                    ) {


                        Text(

                            text = channel.name,

                            modifier =
                                Modifier.padding(16.dp)

                        )


                    }


                }


            }


        }


    }


}
