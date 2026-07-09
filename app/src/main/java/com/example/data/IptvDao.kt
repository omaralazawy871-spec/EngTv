package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IptvDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long


    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Int)


    @Query("SELECT * FROM playlists ORDER BY addedAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<Channel>)


    @Query("DELETE FROM channels WHERE playlistId = :playlistId")
    suspend fun deleteChannelsByPlaylistId(playlistId: Int)


    @Query("""
        SELECT * FROM channels
        WHERE playlistId = :playlistId
        ORDER BY name COLLATE NOCASE ASC
    """)
    fun getChannelsForPlaylist(
        playlistId: Int
    ): Flow<List<Channel>>


    @Query("""
        SELECT DISTINCT groupTitle
        FROM channels
        WHERE playlistId = :playlistId
        AND groupTitle IS NOT NULL
        AND groupTitle != ''
        ORDER BY groupTitle ASC
    """)
    fun getGroupTitlesForPlaylist(
        playlistId: Int
    ): Flow<List<String>>


    @Query("""
        SELECT * FROM channels
        WHERE playlistId = :playlistId
        AND (:groupTitle IS NULL 
             OR :groupTitle = 'All'
             OR groupTitle = :groupTitle)
        ORDER BY name COLLATE NOCASE ASC
    """)
    fun getChannelsByGroup(
        playlistId: Int,
        groupTitle: String?
    ): Flow<List<Channel>>


    @Query("""
        SELECT * FROM channels
        WHERE isFavorite = 1
        ORDER BY name COLLATE NOCASE ASC
    """)
    fun getFavoriteChannels(): Flow<List<Channel>>


    @Query("""
        UPDATE channels
        SET isFavorite = :isFavorite
        WHERE id = :channelId
    """)
    suspend fun updateFavoriteStatus(
        channelId: Int,
        isFavorite: Boolean
    )


    @Query("""
        SELECT * FROM channels
        WHERE playlistId = :playlistId
        AND name LIKE '%' || :query || '%'
        ORDER BY name COLLATE NOCASE ASC
    """)
    fun searchChannels(
        playlistId: Int,
        query: String
    ): Flow<List<Channel>>


    @Query("""
        SELECT * FROM channels
        ORDER BY name COLLATE NOCASE ASC
    """)
    fun getAllChannels(): Flow<List<Channel>>

    @Query("""
        SELECT * FROM channels
        WHERE streamUrl = :url
        LIMIT 1
    """)
    suspend fun findChannelByUrl(
        url: String
    ): Channel?

}
