package ru.dmitriyt.stalkerxraylinter.config

data class ConfigSection(
    val name: String,
    val dependencies: List<String>,
)