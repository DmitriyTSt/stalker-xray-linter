package ru.dmitriyt.stalkerxraylinter.config

data class ConfigSectionReverse(
    val name: String,
    val children: MutableList<ConfigSectionReverse> = mutableListOf(),
)