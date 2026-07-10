package com.example.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.DefaultPlaylist
import com.example.data.IptvDatabase
import com.example.data.IptvRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AdminPanel(onClose: () -> Unit = {}) {
    var url by remember { mutableStateOf(DefaultPlaylist.URL) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text(text = "Remote playlist URL") })
        Button(onClick = {
            // perform sync
            val context = androidx.compose.ui.platform.LocalContext.current
            val db = IptvDatabase.getDatabase(context)
            val repo = IptvRepository(db.iptvDao())
            CoroutineScope(Dispatchers.IO).launch {
                repo.syncRemotePlaylist(url)
            }
            onClose()
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text(text = "Sync Now")
        }
    }
}
