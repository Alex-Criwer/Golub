package tools

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageInfo(
    @SerialName("id")
    val id: Int,
    @SerialName("description")
    val description: String,
    @SerialName("gifURL")
    val gifURL: String
)