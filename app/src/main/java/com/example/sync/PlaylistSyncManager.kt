package com.example.sync

import android.content.Context
import android.util.Log
import com.example.data.IptvRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader

class PlaylistSyncManager(private val context: Context, private val repository: IptvRepository) {

    fun syncFromAssets() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val assetManager = context.assets
                val input = assetManager.open("remote_playlist.json")
                val reader = BufferedReader(input.reader())
                val content = reader.readText()
                reader.close()

                val json = JSONObject(content)
                val remote = json.optJSONObject("remotePlaylist")
                val url = remote?.optString("url", null)
                if (!url.isNullOrBlank()) {
                    repository.syncRemotePlaylist(url)
                }
            } catch (t: Throwable) {
                Log.w("PlaylistSyncManager", "Failed to sync from assets", t)
            }
        }
    }
}
