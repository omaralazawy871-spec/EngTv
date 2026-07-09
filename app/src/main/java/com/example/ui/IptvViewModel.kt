package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class IptvViewModel(
    application: Application
) : AndroidViewModel(application) {


    private val repository: IptvRepository


    val playlists: StateFlow<List<Playlist>>

    val favoriteChannels: StateFlow<List<Channel>>



    private val _selectedPlaylist =
        MutableStateFlow<Playlist?>(null)

    val selectedPlaylist =
        _selectedPlaylist.asStateFlow()



    private val _currentPlayingChannel =
        MutableStateFlow<Channel?>(null)

    val currentPlayingChannel =
        _currentPlayingChannel.asStateFlow()



    init {


        val db =
            IptvDatabase.getDatabase(application)


        repository =
            IptvRepository(db.iptvDao())



        playlists =
            repository.allPlaylists
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    emptyList()
                )



        favoriteChannels =
            repository.favoriteChannels
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    emptyList()
                )



        loadDefault()

    }




    private fun loadDefault() {

        viewModelScope.launch {


            if (repository.allPlaylists.first().isEmpty()) {


                repository.addPlaylistFromUrl(
                    DefaultPlaylist.NAME,
                    DefaultPlaylist.URL
                )


            }


        }

    }




    val channels: StateFlow<List<Channel>> =
        _selectedPlaylist
            .flatMapLatest {

                playlist ->


                if (playlist == null)

                    flowOf(emptyList())

                else

                    repository.getChannelsForPlaylist(
                        playlist.id
                    )

            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )





    fun selectPlaylist(
        playlist: Playlist
    ) {

        _selectedPlaylist.value =
            playlist

    }





    fun refresh() {


        viewModelScope.launch {


            val playlist =
                _selectedPlaylist.value
                    ?: playlists.value.firstOrNull()


            if (playlist != null) {


                repository.refreshPlaylist(
                    playlist.id,
                    playlist.sourceUrl
                )


            }


        }

    }




    fun playChannel(
        channel: Channel
    ) {

        _currentPlayingChannel.value =
            channel

    }


}
