import java.io.*
import java.util.zip.*

class Archiever{
    fun createArchive(
        source: String,
        path: String,
        extens: List<String> = listOf(".txt", ".log")
    ) {
        val direct = File(source)
        require(direct.exists() && direct.isDirectory){
            " Error 404 : $source"
        }
    
    FileOutputStream(path).buffered().use { fos ->
        ZipOutputStream(fos).use { 
    zos -> 
    direct.walk()  
        .filter{ it.isFile && extens.any{ ext -> it.name.endsWith(ext, ignoreCase=true)}}
        .forEach {file ->
        val relpath=direct.toURI().relativize(file.toURI()).path
        file.inputStream().use
        {fis ->
        zos.putNextEntry(ZipEntry(relpath))
        fis.copyTo(zos)
        zos.closeEntry()
        println("Добавлен : $relpath" (${file.length()} байт)")
        }
        }
        }
        }
        }
fun main(){
    val archiever=Archiever()
    archiever.createArchive("project_data/", "archive.zip", listOf(".txt", ".log"))
    }
