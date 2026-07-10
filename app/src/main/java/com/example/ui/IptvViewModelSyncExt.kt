package com.example.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.data.IptvDatabase
import com.example.data.IptvRepository
import com.example.sync.PlaylistSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Extension to IptvViewModel to run initial sync from assets
fun IptvViewModel.startupSync() {
    try {
        val db = IptvDatabase.getDatabase(getApplication())
        val repo = IptvRepository(db.iptvDao())
        val manager = PlaylistSyncManager(getApplication(), repo)
        // Launch background sync
        CoroutineScope(Dispatchers.IO).launch {
            manager.syncFromAssets()
        }
    } catch (t: Throwable) {
        Log.w("IptvViewModel", "startupSync failed", t)
    }
}
