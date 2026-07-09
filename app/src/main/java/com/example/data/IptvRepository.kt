package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class IptvRepository(private val iptvDao: IptvDao) {

    private val okHttpClient = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    val allPlaylists: Flow<List<Playlist>> = iptvDao.getAllPlaylists()
    val favoriteChannels: Flow<List<Channel>> = iptvDao.getFavoriteChannels()
    val allChannels: Flow<List<Channel>> = iptvDao.getAllChannels()

    fun getChannelsForPlaylist(playlistId: Int): Flow<List<Channel>> =
        iptvDao.getChannelsForPlaylist(playlistId)

    fun getGroupTitlesForPlaylist(playlistId: Int): Flow<List<String>> =
        iptvDao.getGroupTitlesForPlaylist(playlistId)

    fun getChannelsByGroup(playlistId: Int, groupTitle: String?): Flow<List<Channel>> =
        iptvDao.getChannelsByGroup(playlistId, groupTitle)

    // تم تعديل هذه الدالة لتأخذ playlistId كمعامل إضافي
    fun searchChannels(playlistId: Int, query: String): Flow<List<Channel>> =
        iptvDao.searchChannels(playlistId, query)

    suspend fun updateFavoriteStatus(channelId: Int, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            iptvDao.updateFavoriteStatus(channelId, isFavorite)
        }
    }

    /**
     * Downloads an M3U file from a URL and parses/stores it locally.
     * Native OkHttp completely bypasses browser CORS.
     */
    suspend fun addPlaylistFromUrl(name: String, url: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException("Failed to download playlist: HTTP ${response.code}"))
                }
                
                val m3uContent = response.body?.string() ?: ""
                if (m3uContent.isBlank()) {
                    return@withContext Result.failure(IOException("Downloaded playlist is empty"))
                }
                
                // 1. Insert playlist and get new ID
                val playlist = Playlist(name = name, sourceUrl = url)
                val playlistId = iptvDao.insertPlaylist(playlist).toInt()
                
                // 2. Parse channels
                val channels = M3uParser.parse(m3uContent, playlistId)
                if (channels.isEmpty()) {
                    // Cleanup inserted playlist if no channels could be parsed
                    iptvDao.deletePlaylist(playlistId)
                    return@withContext Result.failure(IOException("No valid channels found in M3U file"))
                }
                
                // 3. Insert channels in bulk
                iptvDao.insertChannels(channels)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e("IptvRepository", "Error adding playlist from URL", e)
            Result.failure(e)
        }
    }

    /**
     * Imports an M3U playlist from direct text content (e.g., from local file or copy-paste).
     */
    suspend fun addPlaylistFromContent(name: String, content: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (content.isBlank()) {
                return@withContext Result.failure(IOException("M3U content is empty"))
            }

            // 1. Insert playlist
            val playlist = Playlist(name = name, sourceUrl = "local_file")
            val playlistId = iptvDao.insertPlaylist(playlist).toInt()

            // 2. Parse channels
            val channels = M3uParser.parse(content, playlistId)
            if (channels.isEmpty()) {
                iptvDao.deletePlaylist(playlistId)
                return@withContext Result.failure(IOException("No valid channels found in M3U content"))
            }

            // 3. Insert channels
            iptvDao.insertChannels(channels)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("IptvRepository", "Error adding playlist from content", e)
            Result.failure(e)
        }
    }

    /**
     * Deletes a playlist and all its cached channels.
     */
    suspend fun deletePlaylist(playlistId: Int) = withContext(Dispatchers.IO) {
        iptvDao.deleteChannelsByPlaylistId(playlistId)
        iptvDao.deletePlaylist(playlistId)
    }
}
