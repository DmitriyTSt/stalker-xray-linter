package ru.dmitriyt.stalkerxraylinter.validators

import ru.dmitriyt.stalkerxraylinter.config.ConfigSection
import java.io.File
import java.nio.charset.Charset

class ConfigValidator(private val file: File) {
    companion object {
        private val INCLUDE_LINE_REGEX = "\"[A-Za-z_\\-.0-9]+\"".toRegex()
        private const val LINE_COMMENT_SYMBOL = ";"
        private val SECTION_LINE_REGEX = "\\[[A-Za-z_]+](:[A-Za-z_,]+)?".toRegex()
        private const val KEY_VALUE_SEPARATOR = "="
        private val ROW_KEY_REGEX = "[0-9A-Za-z_\$]+".toRegex()
        private val ROW_VALUE_REGEX = "[=A-Za-zа-яА-Я0-9\\\\_\".,{}%():\\\\%@]*".toRegex()
    }

    private val sections = mutableListOf<ConfigSection>()

    /**
     * @return Список секций, описанных в файле
     * Если файл некорректный, возвращает null
     */
    fun getSections(): List<ConfigSection>? {
        if (file.name.contains("artefa")) {
            val a = 1
        }
        file.readLines(Charset.forName("windows-1251")).forEach { rawLine ->
            // избавляяемся от пробелов и комментариев в коде
            val line = rawLine.filter { it != ' ' && it != '\t' }.split(LINE_COMMENT_SYMBOL)[0]
            if (line.isNotEmpty() && !line.startsWith("--") && !line.startsWith("//")) {
                when {
                    line.contains(INCLUDE_LINE_REGEX) -> {
                        // это строка инклуда
                    }
                    line.contains(SECTION_LINE_REGEX) -> {
                        // это заголовок секции
                        sections.add(getSectionFromLine(line))
                    }
                    else -> {
                        try {
                            val separatorIdx = line.indexOf(KEY_VALUE_SEPARATOR)
                            val (key, value) = line.substring(0, separatorIdx) to
                                    line.substring(separatorIdx + 1, line.length)
                            if (key.contains(ROW_KEY_REGEX) && value.contains(ROW_VALUE_REGEX)) {
                                // это нормальная строка параметра
                            } else {
                                System.err.println("-----")
                                System.err.println("Error ltx syntax")
                                System.err.println("File: ${file.absolutePath}")
                                System.err.println("Line: $line")
                                return null
                            }
                        } catch (e: IndexOutOfBoundsException) {
                            if (line.contains(ROW_VALUE_REGEX)) {
                                // внезапно есть параметры без =
                            } else {
                                System.err.println("-----")
                                System.err.println("IndexOutOfBoundsException")
                                System.err.println("File: ${file.absolutePath}")
                                System.err.println("Line: $line")
                            }
                        }
                    }
                }
            }
        }
        return sections
    }

    private fun getSectionFromLine(line: String): ConfigSection {
        val section = line.substring(line.indexOf("[") + 1, line.indexOf("]"))
        val dependencies = if (line.indexOf(":") == -1) {
            emptyList()
        } else {
            line.substring(line.indexOf(":") + 1).split(",")
        }
        return ConfigSection(section, dependencies)
    }
}