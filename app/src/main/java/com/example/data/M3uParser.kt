package com.example.data

object M3uParser {

    fun parse(
        m3uContent: String,
        playlistId: Int
    ): List<Channel> {

        val channels = mutableListOf<Channel>()

        val lines = m3uContent
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }


        var currentInfo = ""

        for (line in lines) {

            if (line.startsWith("#EXTINF")) {

                currentInfo = line

            } else if (
                line.startsWith("http")
            ) {

                val name = extractName(currentInfo)

                if (name.isNotEmpty()) {

                    channels.add(
                        Channel(
                            playlistId = playlistId,
                            name = name,
                            url = line,
                            logo = extractLogo(currentInfo),
                            groupTitle = extractGroup(currentInfo),
                            category = extractGroup(currentInfo)
                                ?: "General"
                        )
                    )
                }

                currentInfo = ""
            }
        }

        return channels
    }


    private fun extractName(info: String): String {

        if (!info.contains(",")) return ""

        return info.substringAfter(",")
            .trim()
    }


    private fun extractLogo(info: String): String? {

        val regex =
            Regex("""tvg-logo="(.*?)"""")

        return regex.find(info)
            ?.groupValues
            ?.get(1)
    }


    private fun extractGroup(info: String): String? {

        val regex =
            Regex("""group-title="(.*?)"""")

        return regex.find(info)
            ?.groupValues
            ?.get(1)
    }
}
