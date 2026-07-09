package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Refresh
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

    var selectedChannel by remember {
        mutableStateOf<Channel?>(null)
    }


    var search by remember {
        mutableStateOf("")
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
            .padding(10.dp)
    ) {


        Text(
            text = "EngTv",
            style = MaterialTheme.typography.headlineLarge
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )



        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            IconButton(
                onClick = {}
            ) {

                Icon(
                    Icons.Default.Search,
                    "Search"
                )

            }


            IconButton(
                onClick = {}
            ) {

                Icon(
                    Icons.Default.Refresh,
                    "Refresh"
                )

            }



            IconButton(
                onClick = {}
            ) {

                Icon(
                    Icons.Default.Star,
                    "Favorites"
                )

            }



        }



        OutlinedTextField(

            value = search,

            onValueChange = {
                search = it
            },

            modifier = Modifier.fillMaxWidth(),

            placeholder = {
                Text("بحث عن قناة")
            }

        )



        Spacer(
            modifier = Modifier.height(15.dp)
        )



        LazyColumn {


            val groups =
                channels
                    .filter {
                        it.name.contains(
                            search,
                            ignoreCase = true
                        )
                    }
                    .groupBy {
                        it.groupTitle ?: "OTHER SPORTS"
                    }



            groups.forEach { (group, list) ->



                item {


                    Text(
                        text = "📂 $group",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(8.dp)
                    )


                }



                items(list) { channel ->



                    Card(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .clickable {

                                    selectedChannel = channel

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
