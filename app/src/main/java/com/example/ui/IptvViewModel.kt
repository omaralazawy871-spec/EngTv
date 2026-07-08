package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Channel
import com.example.data.IptvDatabase
import com.example.data.IptvRepository
import com.example.data.Playlist
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ImportState {
    object Idle : ImportState
    object Loading : ImportState
    data class Success(val message: String) : ImportState
    data class Error(val error: String) : ImportState
}

@OptIn(ExperimentalCoroutinesApi::class)
class IptvViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: IptvRepository

    val playlists: StateFlow<List<Playlist>>
    val favoriteChannels: StateFlow<List<Channel>>

    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist = _selectedPlaylist.asStateFlow()

    private val _selectedGroup = MutableStateFlow<String?>("All")
    val selectedGroup = _selectedGroup.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _currentPlayingChannel = MutableStateFlow<Channel?>(null)
    val currentPlayingChannel = _currentPlayingChannel.asStateFlow()

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState = _importState.asStateFlow()

    init {
        val database = IptvDatabase.getDatabase(application)
        repository = IptvRepository(database.iptvDao())

        playlists = repository.allPlaylists
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        favoriteChannels = repository.favoriteChannels
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // Reactively fetch group titles when selected playlist changes
    val groupTitles: StateFlow<List<String>> = _selectedPlaylist
        .flatMapLatest { playlist ->
            if (playlist == null) {
                flowOf(emptyList())
            } else {
                repository.getGroupTitlesForPlaylist(playlist.id)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reactively filter channels based on playlist, group, and search query
    val channels: StateFlow<List<Channel>> = combine(
        _selectedPlaylist,
        _selectedGroup,
        _searchQuery
    ) { playlist, group, query ->
        Triple(playlist, group, query)
    }.flatMapLatest { (playlist, group, query) ->
        when {
            query.isNotEmpty() -> {
                repository.searchChannels(query)
            }
            playlist == null -> {
                // If no playlist is selected, return empty (or we can return all channels if preferred)
                flowOf(emptyList())
            }
            group == "All" || group == null -> {
                repository.getChannelsForPlaylist(playlist.id)
            }
            else -> {
                repository.getChannelsByGroup(playlist.id, group)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectPlaylist(playlist: Playlist?) {
        _selectedPlaylist.value = playlist
        _selectedGroup.value = "All" // Reset group selection
        _searchQuery.value = "" // Reset search
    }

    fun selectGroup(group: String?) {
        _selectedGroup.value = group
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun playChannel(channel: Channel?) {
        _currentPlayingChannel.value = channel
    }

    fun toggleFavorite(channel: Channel) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(channel.id, !channel.isFavorite)
            // If the current playing channel is the one updated, update its reference to reflect favorite icon change
            if (_currentPlayingChannel.value?.id == channel.id) {
                _currentPlayingChannel.value = channel.copy(isFavorite = !channel.isFavorite)
            }
        }
    }

    fun addPlaylistFromUrl(name: String, url: String) {
        if (name.isBlank() || url.isBlank()) {
            _importState.value = ImportState.Error("Please fill out both fields")
            return
        }
        
        _importState.value = ImportState.Loading
        viewModelScope.launch {
            val result = repository.addPlaylistFromUrl(name, url)
            result.onSuccess {
                _importState.value = ImportState.Success("Playlist loaded successfully!")
                // Automatically select the newly added playlist if possible
                viewModelScope.launch {
                    playlists.collect { list ->
                        val newlyAdded = list.firstOrNull { it.name == name && it.sourceUrl == url }
                        if (newlyAdded != null) {
                            selectPlaylist(newlyAdded)
                        }
                    }
                }
            }
            result.onFailure { error ->
                _importState.value = ImportState.Error(error.localizedMessage ?: "Unknown error while downloading")
            }
        }
    }

    fun addPlaylistFromContent(name: String, content: String) {
        if (name.isBlank() || content.isBlank()) {
            _importState.value = ImportState.Error("Please fill out both fields")
            return
        }

        _importState.value = ImportState.Loading
        viewModelScope.launch {
            val result = repository.addPlaylistFromContent(name, content)
            result.onSuccess {
                _importState.value = ImportState.Success("Playlist imported successfully!")
                // Automatically select the newly added playlist if possible
                viewModelScope.launch {
                    playlists.collect { list ->
                        val newlyAdded = list.firstOrNull { it.name == name && it.sourceUrl == "local_file" }
                        if (newlyAdded != null) {
                            selectPlaylist(newlyAdded)
                        }
                    }
                }
            }
            result.onFailure { error ->
                _importState.value = ImportState.Error(error.localizedMessage ?: "Unknown error during parsing")
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            // If the deleted playlist has the current playing channel, stop playing
            if (_currentPlayingChannel.value?.playlistId == playlist.id) {
                _currentPlayingChannel.value = null
            }
            
            // If the deleted playlist is currently selected, clear selection
            if (_selectedPlaylist.value?.id == playlist.id) {
                _selectedPlaylist.value = null
            }
            
            repository.deletePlaylist(playlist.id)
        }
    }

    fun resetImportState() {
        _importState.value = ImportState.Idle
    }
}
