package com.example.demodatadog.data

data class PokemonListResponse(
    val results: List<PokemonListItem> = emptyList(),
)

data class PokemonListItem(
    val name: String,
    val url: String,
)

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<PokemonTypeSlot> = emptyList(),
)

data class PokemonTypeSlot(
    val type: PokemonType,
)

data class PokemonType(
    val name: String,
)
