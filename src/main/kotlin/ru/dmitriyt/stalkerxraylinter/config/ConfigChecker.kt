package ru.dmitriyt.stalkerxraylinter.config

import ru.dmitriyt.stalkerxraylinter.validators.ConfigValidator
import java.io.File

class ConfigChecker(private val configPath: String) {
    companion object {
        private const val CONFIG_EXTENSION = "ltx"
    }

    private val sections = mutableListOf<ConfigSection>()

    fun getObjectSections(): List<ConfigSection> {
        processDir(File(configPath))
        return sections
    }

    fun getObjectSectionForest(): List<ConfigSectionReverse> {
        processDir(File(configPath))
        return buildSectionsForest()
    }

    private fun buildSectionsForest(): List<ConfigSectionReverse> {
        val rootSections = sections.filter { it.dependencies.isEmpty() }.map { ConfigSectionReverse(it.name) }
        var errorCount = -1
        var oldErrorCount = 0
        val errors = mutableListOf<String>()
        while (errorCount != oldErrorCount) {
            oldErrorCount = errorCount
            errorCount = 0
            errors.clear()
            sections.forEach { section ->
                if (rootSections.find { it.name == section.name } == null) {
                    val reversedSection = ConfigSectionReverse(section.name)
                    section.dependencies.forEach { sectionName ->
                        val foundSection = findSection(rootSections, sectionName)
                        if (foundSection != null) {
                            foundSection.children.add(reversedSection)
                        } else {
                            errorCount++
                            errors.add("Not found section: \"$sectionName\" parent for section \"${section.name}\"")
                        }
                    }
                }
            }
        }
        errors.forEach { System.err.println(it) }
        return rootSections
    }

    private fun findSection(rootSections: List<ConfigSectionReverse>, sectionName: String): ConfigSectionReverse? {
        val rootSection = rootSections.find { it.name == sectionName }
        if (rootSection != null) {
            return rootSection
        } else {
            rootSections.forEach { section ->
                val foundSection = findSection(section.children, sectionName)
                if (foundSection != null) {
                    return foundSection
                }
            }
            return null
        }
    }

    private fun processDir(dir: File) {
        dir.listFiles()?.toList().orEmpty().forEach {
            if (it.isDirectory) {
                processDir(it)
            } else {
                if (it.extension == CONFIG_EXTENSION) {
                    val sections = ConfigValidator(it).getSections()
                    if (sections != null) {
                        this.sections.addAll(sections)
                    }
                }
            }
        }
    }
}