package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Channel
import com.example.data.ChannelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ChannelViewModel(
    private val repository: ChannelRepository
) : ViewModel() {


    private val _channels =
        MutableStateFlow<List<Channel>>(emptyList())

    val channels: StateFlow<List<Channel>>
        get() = _channels



    private val _favorites =
        MutableStateFlow<List<Channel>>(emptyList())

    val favorites: StateFlow<List<Channel>>
        get() = _favorites



    fun loadChannels() {

        viewModelScope.launch {

            _channels.value =
                repository.getChannels()

        }
    }



    fun loadFavorites() {

        viewModelScope.launch {

            _favorites.value =
                repository.getFavorites()

        }
    }



    fun search(
        text: String
    ) {

        viewModelScope.launch {

            _channels.value =
                repository.search(text)

        }
    }



    fun favorite(
        channel: Channel
    ) {

        viewModelScope.launch {

            repository.toggleFavorite(channel)

            loadChannels()
        }
    }



    fun playChannel(
        id: Int
    ) {

        viewModelScope.launch {

            repository.setLastChannel(id)

        }
    }

}
