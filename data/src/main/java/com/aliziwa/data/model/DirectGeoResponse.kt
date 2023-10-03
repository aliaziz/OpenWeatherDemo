package com.aliziwa.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

typealias DirectGeoResponse = List<DirectGeoResponseElement>

@JsonClass(generateAdapter = true)
data class DirectGeoResponseElement(
    val name: String? = null,
    @Json(name = "local_names")
    val localNames: LocalNames? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val country: String? = null,
    val state: String? = null
)

data class LocalNames(
    val en: String? = null
)
