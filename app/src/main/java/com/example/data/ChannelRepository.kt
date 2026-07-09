package com.example.data

class ChannelRepository(
    private val dao: ChannelDao
) {


    suspend fun getChannels(): List<Channel> {
        return dao.getAllChannels()
    }


    suspend fun getFavorites(): List<Channel> {
        return dao.getFavorites()
    }


    suspend fun search(
        text: String
    ): List<Channel> {

        return dao.searchChannels(text)
    }


    suspend fun getCategory(
        category: String
    ): List<Channel> {

        return dao.getByCategory(category)
    }


    suspend fun saveChannels(
        channels: List<Channel>
    ) {

        dao.deleteAll()
        dao.insertChannels(channels)
    }


    suspend fun toggleFavorite(
        channel: Channel
    ) {

        dao.updateFavorite(
            channel.id,
            !channel.isFavorite
        )
    }


    suspend fun setLastChannel(
        id: Int
    ) {

        dao.clearLastPlayed()
        dao.setLastPlayed(id)
    }

}
