package com.vanniktech.music.mp3.processor.file

import com.vanniktech.music.autoCorrected
import com.vanniktech.music.logger.Logger
import java.io.File

internal const val FILE_ENDING = "mp3"

internal class FileNamePreFileProcessor(
  private val logger: Logger,
  private val autoCorrect: Boolean,
) : PreFileProcessor {
  private var index = 0

  override fun process(file: File): File {
    val extension = file.extension
    if (extension.equals("wav", ignoreCase = true)) {
      logger.log("""🛎️""", index = 0, file, """ffmpeg -i "${file.absolutePath}" -vn -ab 320k -ar 44100 -y "${file.parentFile.resolve(file.nameWithoutExtension + "." + FILE_ENDING)}"""")
    }

    require(extension == FILE_ENDING) { "Invalid extension at $file. Expected $FILE_ENDING" }

    val currentName = file.name

    return if (autoCorrect) {
      val newName = currentName.autoCorrected()

      if (newName != currentName) {
        val newFile = file.parentFile.resolve(newName)
        require(!newFile.exists() || currentName.equals(newName, ignoreCase = true)) { "File $newFile already exists!" }
        logger.log("""🚧""", index++, file, """Autocorrecting to "$newName"""")
        file.renameTo(newFile)
        newFile
      } else {
        file
      }
    } else {
      file
    }
  }
}
