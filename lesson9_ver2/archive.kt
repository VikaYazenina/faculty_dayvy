import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun createCustomArchive(sourceDirPath: String, archivePath: String) {
    val sourceDir = File(sourceDirPath)

    if (!sourceDir.exists() || !sourceDir.isDirectory) {
        println("Ошибка: Директория $sourceDirPath не существует или не является папкой.")
        return
    }

    try {
        ZipOutputStream(FileOutputStream(archivePath)).use { zos ->

            
            fun addFileToZip(file: File, entryName: String) {
                if (file.isDirectory) {
                    file.listFiles()?.forEach { child ->
                        val nextPath = if (entryName.isEmpty()) child.name else "$entryName/${child.name}"
                        addFileToZip(child, nextPath)
                    }
                } else {
                    val fileName = file.name.lowercase()
                    if (fileName.endsWith(".txt") || fileName.endsWith(".log")) {

                        val entry = ZipEntry(entryName)
                        zos.putNextEntry(entry)

                        FileInputStream(file).use { fis ->
                            fis.copyTo(zos) 
                        }

                        zos.closeEntry()
                        println("Добавлен файл: $entryName, размер: ${file.length()} байт")
                    }
                }
            }

            sourceDir.listFiles()?.forEach { file ->
                addFileToZip(file, file.name)
            }

            println(nАрхив создан: $archivePath")
        }
    } catch (e: IOException) {
        println("Произошла ошибка при создании архива: ${e.message}")
        e.printStackTrace()
    }
}

fun main() {
    createCustomArchive("project_data", "archive.zip")
}
