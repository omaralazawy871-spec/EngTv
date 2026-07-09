package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Channel
import com.example.data.DefaultPlaylist
import com.example.data.IptvDatabase
import com.example.data.IptvRepository
import com.example.data.Playlist
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


sealed interface ImportState {

    object Idle : ImportState

    object Loading : ImportState

    data class Success(
        val message: String
    ) : ImportState

    data class Error(
        val error: String
    ) : ImportState
}



@OptIn(ExperimentalCoroutinesApi::class)
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



    private val _selectedGroup =
        MutableStateFlow<String?>("All")

    val selectedGroup =
        _selectedGroup.asStateFlow()



    private val _currentPlayingChannel =
        MutableStateFlow<Channel?>(null)

    val currentPlayingChannel =
        _currentPlayingChannel.asStateFlow()



    private val _importState =
        MutableStateFlow<ImportState>(ImportState.Idle)

    val importState =
        _importState.asStateFlow()



    init {


        val database =
            IptvDatabase.getDatabase(application)


        repository =
            IptvRepository(database.iptvDao())



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



        loadDefaultPlaylist()

    }




    private fun loadDefaultPlaylist() {


        viewModelScope.launch {


            val exists =
                repository.allPlaylists
                    .first()
                    .isNotEmpty()



            if (!exists) {


                repository.addPlaylistFromUrl(
                    DefaultPlaylist.NAME,
                    DefaultPlaylist.URL
                )


            }


        }


    }





    val channels: StateFlow<List<Channel>> =
        _selectedPlaylist
            .flatMapLatest { playlist ->


                if (playlist == null) {

                    flowOf(emptyList())

                } else {

                    repository.getChannelsForPlaylist(
                        playlist.id
                    )

                }

            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )





    fun selectPlaylist(
        playlist: Playlist
    ) {

        _selectedPlaylist.value = playlist

    }





    fun playChannel(
        channel: Channel
    ) {

        _currentPlayingChannel.value =
            channel

    }





    fun addPlaylistFromUrl(
        name: String,
        url: String
    ) {


        viewModelScope.launch {


            repository.addPlaylistFromUrl(
                name,
                url
            )


        }


    }




    fun deletePlaylist(
        playlist: Playlist
    ) {


        viewModelScope.launch {


            repository.deletePlaylist(
                playlist.id
            )


        }


    }

}
