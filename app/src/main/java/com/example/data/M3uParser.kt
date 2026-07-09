package com.example.data

object M3uParser {

    fun parse(m3uContent: String, playlistId: Int): List<Channel> {

        val channels = mutableListOf<Channel>()

        var name = ""
        var logo: String? = null
        var group = ""

        m3uContent.lines().forEach { raw ->

            val line = raw.trim()

            if (line.startsWith("#EXTINF")) {

                logo = Regex("""tvg-logo="([^"]*)"""")
                    .find(line)
                    ?.groupValues
                    ?.getOrNull(1)

                val originalGroup =
                    Regex("""group-title="([^"]*)"""")
                        .find(line)
                        ?.groupValues
                        ?.getOrNull(1)
                        ?: ""

                name = line.substringAfterLast(",")

                val lower = name.lowercase()

                group = when {

                    lower.contains("alwan") ->
                        "ALWAN SPORTS"

                    lower.contains("bein") ||
                    lower.contains("bein sports") ->
                        "beIN SPORTS"

                    lower.contains("ssc") ->
                        "SSC SPORTS"

                    lower.contains("alkass") ||
                    name.contains("الكأس") ->
                        "ALKASS"

                    lower.contains("ad sport") ||
                    name.contains("أبوظبي") ->
                        "AD SPORTS"

                    lower.contains("dubai sport") ->
                        "DUBAI SPORTS"

                    lower.contains("iraq") ->
                        "IRAQ"

                    lower.contains("local") ||
                    lower.contains("kurd") ->
                        "KURDISH LOCAL"

                    originalGroup.isNotBlank() ->
                        originalGroup

                    else ->
                        "OTHER"
                }

            } else if (
                line.isNotBlank() &&
                !line.startsWith("#")
            ) {

                channels.add(
                    Channel(
                        playlistId = playlistId,
                        name = name,
                        streamUrl = line,
                        logoUrl = logo,
                        groupTitle = group
                    )
                )
            }
        }

        return channels
    }
}
