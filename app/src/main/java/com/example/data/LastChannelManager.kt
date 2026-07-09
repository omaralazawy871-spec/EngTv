package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(
    name = "iptv_settings"
)

class LastChannelManager(
    private val context: Context
) {

    companion object {

        private val LAST_CHANNEL_URL =
            stringPreferencesKey("last_channel_url")

        private val LAST_CHANNEL_NAME =
            stringPreferencesKey("last_channel_name")

        private val LAST_CHANNEL_LOGO =
            stringPreferencesKey("last_channel_logo")

        private val LAST_CHANNEL_GROUP =
            stringPreferencesKey("last_channel_group")
    }


    suspend fun saveChannel(channel: Channel) {

        context.dataStore.edit { preferences ->

            preferences[LAST_CHANNEL_URL] =
                channel.url

            preferences[LAST_CHANNEL_NAME] =
                channel.name

            channel.logo?.let {
                preferences[LAST_CHANNEL_LOGO] = it
            }

            channel.groupTitle?.let {
                preferences[LAST_CHANNEL_GROUP] = it
            }
        }
    }



    suspend fun getLastChannel(): SavedChannel? {

        val preferences =
            context.dataStore.data.first()


        val url =
            preferences[LAST_CHANNEL_URL]
                ?: return null


        return SavedChannel(

            url = url,

            name =
            preferences[LAST_CHANNEL_NAME]
                ?: "Unknown",

            logo =
            preferences[LAST_CHANNEL_LOGO],

            groupTitle =
            preferences[LAST_CHANNEL_GROUP]
        )
    }



    suspend fun clear() {

        context.dataStore.edit {
            it.clear()
        }

    }
}



data class SavedChannel(

    val url: String,

    val name: String,

    val logo: String?,

    val groupTitle: String?

)
