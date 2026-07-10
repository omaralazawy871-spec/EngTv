package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.DefaultPlaylist
import com.example.data.IptvDatabase
import com.example.data.IptvRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = IptvDatabase.getDatabase(this)
        val repository = IptvRepository(db.iptvDao())

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val urlState = remember { mutableStateOf(DefaultPlaylist.URL) }

                    OutlinedTextField(
                        value = urlState.value,
                        onValueChange = { urlState.value = it },
                        label = { Text(text = "Remote playlist URL") },
                        modifier = Modifier.fillMaxSize()
                    )

                    Button(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            repository.syncRemotePlaylist(urlState.value)
                        }
                    }) {
                        Text(text = "Sync Now")
                    }
                }
            }
        }
    }
}
