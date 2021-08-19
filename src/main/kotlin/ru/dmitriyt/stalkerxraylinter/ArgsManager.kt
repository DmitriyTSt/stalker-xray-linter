package ru.dmitriyt.stalkerxraylinter

class ArgsManager(_args: Array<String>) {
    private val args = _args.toList()

    val gamedataPath = getParam("-g") ?: DefaultConfig.GAMEDATA_PATH

    private fun getParam(key: String): String? {
        return args.indexOf(key).takeIf { it > -1 }?.let { args.getOrNull(it + 1) }
    }
}