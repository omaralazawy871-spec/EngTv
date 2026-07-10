package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class IptvViewModel(
    application: Application
) : AndroidViewModel(application) {


    private val repository: IptvRepository

    private val lastChannelManager =
        LastChannelManager(application)


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



    private val _selectedGroup =
        MutableStateFlow<String?>(null)

    val selectedGroup =
        _selectedGroup.asStateFlow()



    private val _searchQuery =
        MutableStateFlow("")

    val searchQuery =
        _searchQuery.asStateFlow()



    private val _importState =
        MutableStateFlow<ImportState>(ImportState.Idle)

    val importState =
        _importState.asStateFlow()



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

        restoreLastChannel()

        // Start background sync from assets (if configured)
        try {
            startupSync()
        } catch (_: Throwable) {
            // ignored - sync extension logs internally
        }

    }



    private fun loadDefault() {

        viewModelScope.launch {

            val existingPlaylists =
                repository.allPlaylists.first()


            if (existingPlaylists.isEmpty()) {

                val result =
                    repository.addPlaylistFromUrl(
                        DefaultPlaylist.NAME,
                        DefaultPlaylist.URL
                    )


                if (result.isSuccess) {

                    val playlist =
                        repository.allPlaylists.first()
                            .firstOrNull()


                    playlist?.let {

                        _selectedPlaylist.value = it

                        _selectedGroup.value = "All"

                    }
                }


            } else {

                _selectedPlaylist.value =
                    existingPlaylists.first()

                _selectedGroup.value =
                    "All"
            }
        }
    }



    private fun restoreLastChannel() {

        viewModelScope.launch {

            val saved =
                lastChannelManager.getLastChannel()
                    ?: return@launch


            val channel =
                repository.findChannelByUrl(
                    saved.url
                )


            if (channel != null) {

                _currentPlayingChannel.value =
                    channel

            }

        }

    }

    val channels: StateFlow<List<Channel>> =
        combine(
            _selectedPlaylist,
            _selectedGroup,
            _searchQuery
        ) { playlist, group, search ->

            Triple(
                playlist,
                group,
                search
            )

        }.flatMapLatest { (playlist, group, search) ->

            if (playlist == null) {

                flowOf(emptyList())

            } else if (search.isNotBlank()) {

                repository.searchChannels(
                    playlist.id,
                    search
                )

            } else {

                repository.getChannelsByGroup(
                    playlist.id,
                    group
                )
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    val groupTitles: StateFlow<List<String>> =
        _selectedPlaylist
            .flatMapLatest { playlist ->

                if (playlist == null)

                    flowOf(emptyList())

                else

                    repository.getGroupTitlesForPlaylist(
                        playlist.id
                    )

            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )


    fun selectPlaylist(
        playlist: Playlist?
    ) {

        _selectedPlaylist.value = playlist
        _selectedGroup.value = "All"
        _searchQuery.value = ""

    }


    fun selectGroup(
        group: String?
    ) {

        _selectedGroup.value = group

    }


    fun setSearchQuery(
        query: String
    ) {

        _searchQuery.value = query

    }


    fun playChannel(
        channel: Channel?
    ) {

        _currentPlayingChannel.value = channel


        channel?.let {

            viewModelScope.launch {

                lastChannelManager.saveChannel(
                    it
                )

            }

        }

    }

    fun toggleFavorite(
        channel: Channel
    ) {

        viewModelScope.launch {

            repository.updateFavoriteStatus(
                channel.id,
                !channel.isFavorite
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


            if (_selectedPlaylist.value?.id == playlist.id) {

                _selectedPlaylist.value = null
                _currentPlayingChannel.value = null

                lastChannelManager.clear()

            }

        }

    }

    fun refresh() {

        viewModelScope.launch {

            val playlist =
                _selectedPlaylist.value
                    ?: playlists.value.firstOrNull()


            playlist?.let {

                repository.refreshPlaylist(
                    it.id,
                    it.sourceUrl
                )

            }

        }

    }

    fun addPlaylistFromUrl(
        name: String,
        url: String
    ) {

        viewModelScope.launch {

            _importState.value =
                ImportState.Loading


            val result =
                repository.addPlaylistFromUrl(
                    name,
                    url
                )


            _importState.value =
                result.fold(

                    onSuccess = {

                        val newPlaylist =
                            repository.allPlaylists.first()
                                .firstOrNull()


                        newPlaylist?.let { playlist ->

                            _selectedPlaylist.value =
                                playlist

                            _selectedGroup.value =
                                "All"

                        }


                        ImportState.Success()
                    },

                    onFailure = {

                        ImportState.Error(
                            it.message
                                ?: "خطأ غير معروف"
                        )

                    }

                )

        }

    }

    fun addPlaylistFromContent(
        name: String,
        content: String
    ) {

        viewModelScope.launch {


            _importState.value =
                ImportState.Loading


            val result =
                repository.addPlaylistFromContent(
                    name,
                    content
                )


            _importState.value =
                result.fold(

                    onSuccess = {

                        val newPlaylist =
                            repository.allPlaylists.first()
                                .firstOrNull()


                        newPlaylist?.let { playlist ->

                            _selectedPlaylist.value =
                                playlist

                            _selectedGroup.value =
                                "All"

                        }


                        ImportState.Success()

                    },

                    onFailure = {

                        ImportState.Error(
                            it.message
                                ?: "خطأ غير معروف"
                        )

                    }

                )

        }

    }

    fun resetImportState() {

        _importState.value =
            ImportState.Idle

    }

    fun updatePlaylists() {

        addPlaylistFromUrl(
            name = "ALWAN VIP",
            url = DefaultPlaylist.URL
        )

    }

}
