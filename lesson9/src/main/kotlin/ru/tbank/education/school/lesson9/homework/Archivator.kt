package ru.tbank.education.school.lesson9

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun zipDirectory(
    sourceDirPath: String,
    zipFilePath: String,
    allowedExtensions: Set<String> = setOf("txt", "log")
) {
    val sourceDir = File(sourceDirPath)
    if (!sourceDir.exists() || !sourceDir.isDirectory) {
        println("Ошибка: папка не существует")
        return
    }

    FileOutputStream(zipFilePath).use { fos ->
        ZipOutputStream(fos).use { zos ->
            addFilesToZip(sourceDir, sourceDir, zos, allowedExtensions)
        }
    }
    println("Архивация завершена: $zipFilePath")
}

private fun addFilesToZip(
    rootDir: File,
    currentFile: File,
    zos: ZipOutputStream,
    allowedExtensions: Set<String>
) {
    if (currentFile.isDirectory) {
        currentFile.listFiles()?.forEach { file ->
            addFilesToZip(rootDir, file, zos, allowedExtensions)
        }
    } else {
        val ext = currentFile.extension.lowercase()
        if (ext !in allowedExtensions) return

        val zipEntryName = rootDir.toPath().relativize(currentFile.toPath()).toString().replace("\\", "/")

        FileInputStream(currentFile).use { fis ->
            val entry = ZipEntry(zipEntryName)
            zos.putNextEntry(entry)

            val buffer = ByteArray(4096)
            var length: Int
            var totalBytes = 0L
            while (fis.read(buffer).also { length = it } > 0) {
                zos.write(buffer, 0, length)
                totalBytes += length
            }
            zos.closeEntry()

            println("Добавлен файл: $zipEntryName")
        }
    }
}

fun main() {
    val sourceDir = "project_data"
    val zipFile = "archive.zip"
    zipDirectory(sourceDir, zipFile)
}