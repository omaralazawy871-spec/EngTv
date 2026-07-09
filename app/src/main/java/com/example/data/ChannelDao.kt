package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface ChannelDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(
        channels: List<Channel>
    )


    @Query("SELECT * FROM channels ORDER BY name ASC")
    suspend fun getAllChannels(): List<Channel>


    @Query(
        "SELECT * FROM channels WHERE isFavorite = 1 ORDER BY name ASC"
    )
    suspend fun getFavorites(): List<Channel>


    @Query(
        """
        SELECT * FROM channels 
        WHERE name LIKE '%' || :query || '%'
        ORDER BY name ASC
        """
    )
    suspend fun searchChannels(
        query: String
    ): List<Channel>


    @Query(
        """
        SELECT * FROM channels 
        WHERE category = :category
        ORDER BY name ASC
        """
    )
    suspend fun getByCategory(
        category: String
    ): List<Channel>


    @Update
    suspend fun updateChannel(
        channel: Channel
    )


    @Query(
        """
        UPDATE channels 
        SET isFavorite = :favorite 
        WHERE id = :id
        """
    )
    suspend fun updateFavorite(
        id: Int,
        favorite: Boolean
    )


    @Query(
        """
        UPDATE channels 
        SET lastPlayed = 0
        """
    )
    suspend fun clearLastPlayed()


    @Query(
        """
        UPDATE channels 
        SET lastPlayed = 1 
        WHERE id = :id
        """
    )
    suspend fun setLastPlayed(
        id: Int
    )


    @Delete
    suspend fun deleteChannel(
        channel: Channel
    )


    @Query("DELETE FROM channels")
    suspend fun deleteAll()

}
