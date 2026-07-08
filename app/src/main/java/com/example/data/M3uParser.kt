package com.example.data

import android.util.Log

object M3uParser {
    private const val TAG = "M3uParser"

    fun parse(m3uContent: String, playlistId: Int): List<Channel> {
        val channels = mutableListOf<Channel>()
        
        // Split by lines, ignoring empty lines
        val lines = m3uContent.lines().map { it.trim() }.filter { it.isNotEmpty() }
        
        var currentName = ""
        var currentLogoUrl: String? = null
        var currentGroup: String? = null
        
        for (i in lines.indices) {
            val line = lines[i]
            if (line.startsWith("#EXTINF:")) {
                try {
                    // Reset fields for the new channel
                    currentLogoUrl = null
                    currentGroup = null
                    
                    // Parse logo URL: tvg-logo="url" or logo="url"
                    currentLogoUrl = parseAttribute(line, "tvg-logo") ?: parseAttribute(line, "logo")
                    
                    // Parse group title: group-title="category"
                    currentGroup = parseAttribute(line, "group-title") ?: parseAttribute(line, "category")
                    
                    // Parse channel name: find the last comma which separates attributes from the channel name
                    val commaIndex = line.lastIndexOf(',')
                    currentName = if (commaIndex != -1 && commaIndex < line.length - 1) {
                        line.substring(commaIndex + 1).trim()
                    } else {
                        "Unknown Channel"
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing EXTINF line: $line", e)
                    currentName = "Malformed Channel"
                }
            } else if (!line.startsWith("#") && line.contains("://")) {
                // This is a stream URL! Ensure we have a name
                val channelName = if (currentName.isEmpty()) "Channel ${channels.size + 1}" else currentName
                
                channels.add(
                    Channel(
                        playlistId = playlistId,
                        name = channelName,
                        streamUrl = line,
                        logoUrl = currentLogoUrl,
                        groupTitle = currentGroup ?: "Other",
                        isFavorite = false
                    )
                )
                
                // Reset temporary states
                currentName = ""
                currentLogoUrl = null
                currentGroup = null
            }
        }
        
        return channels
    }

    private fun parseAttribute(line: String, attributeName: String): String? {
        // Look for pattern: attributeName="value" or attributeName = "value"
        val key = "$attributeName="
        val index = line.indexOf(key)
        if (index == -1) return null
        
        val valueStart = index + key.length
        if (valueStart >= line.length) return null
        
        val quoteChar = line[valueStart]
        if (quoteChar == '"' || quoteChar == '\'') {
            val nextQuoteIndex = line.indexOf(quoteChar, valueStart + 1)
            if (nextQuoteIndex != -1) {
                return line.substring(valueStart + 1, nextQuoteIndex)
            }
        } else {
            // Unquoted attribute, parse until space or comma
            val nextSpaceIndex = line.indexOf(' ', valueStart)
            val nextCommaIndex = line.indexOf(',', valueStart)
            val end = listOf(nextSpaceIndex, nextCommaIndex, line.length)
                .filter { it > valueStart }
                .minOrNull() ?: line.length
            return line.substring(valueStart, end).trim()
        }
        return null
    }
}
