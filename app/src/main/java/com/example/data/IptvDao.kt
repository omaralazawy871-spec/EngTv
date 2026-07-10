package com.example.data

import androidx.room.*

@Dao
interface IptvDao {

    @Query("SELECT * FROM playlists ORDER BY addedAt DESC")
    fun getAllPlaylists(): kotlinx.coroutines.flow.Flow<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(playlist: Playlist): Long

    @Delete
    fun deletePlaylist(playlistId: Int)

    @Query("DELETE FROM channels WHERE playlistId = :playlistId")
    fun deleteChannelsByPlaylistId(playlistId: Int)

    @Query("SELECT * FROM channels WHERE playlistId = :playlistId")
    fun getChannelsForPlaylist(playlistId: Int): kotlinx.coroutines.flow.Flow<List<Channel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChannels(channels: List<Channel>)

    @Query("SELECT * FROM channels WHERE isFavorite = 1")
    fun getFavoriteChannels(): kotlinx.coroutines.flow.Flow<List<Channel>>

    @Query("SELECT * FROM channels")
    fun getAllChannels(): kotlinx.coroutines.flow.Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE url = :url LIMIT 1")
    fun findChannelByUrl(url: String): Channel?

    @Query("UPDATE channels SET isFavorite = :isFavorite WHERE id = :channelId")
    fun updateFavoriteStatus(channelId: Int, isFavorite: Boolean)

    @Query("SELECT DISTINCT groupTitle FROM channels WHERE playlistId = :playlistId")
    fun getGroupTitlesForPlaylist(playlistId: Int): kotlinx.coroutines.flow.Flow<List<String>>

    @Query("SELECT * FROM channels WHERE playlistId = :playlistId AND (name LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%')")
    fun searchChannels(playlistId: Int, query: String): kotlinx.coroutines.flow.Flow<List<Channel>>
}
