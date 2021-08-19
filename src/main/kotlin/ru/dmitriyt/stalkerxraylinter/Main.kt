package ru.dmitriyt.stalkerxraylinter

import ru.dmitriyt.stalkerxraylinter.config.ConfigChecker
import java.io.File

fun main(_args: Array<String>) {
    val args = ArgsManager(_args)
    val gamedataPath = args.gamedataPath
    val configSections = ConfigChecker("$gamedataPath${File.separator}configs").getObjectSections()
    val configSectionForest = ConfigChecker("$gamedataPath${File.separator}configs").getObjectSectionForest()
    val a = 1
}