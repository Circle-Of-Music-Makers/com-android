package com.sidzi.circleofmusic.models


data class ComTracksResponse(
        val data: List<DataItem?>? = null,
        val success: Boolean? = null
)

data class DataItem(
        val relationships: Relationships? = null,
        val attributes: Attributes? = null,
        val id: String? = null,
        val type: String? = null
)

data class Attributes(
        val path: String? = null,
        val artist: String? = null,
        val title: String? = null,
        val username: String? = null
)

data class Relationships(
        val uploader: Uploader? = null
)

data class Uploader(
        val links: String? = null,
        val username: String? = null
)
