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

  override suspend fun process(file: File): File {
    require(file.extension == FILE_ENDING) { "Expected $FILE_ENDING. Invalid extension at $file" }

    return if (autoCorrect) {
      val currentName = file.name
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
