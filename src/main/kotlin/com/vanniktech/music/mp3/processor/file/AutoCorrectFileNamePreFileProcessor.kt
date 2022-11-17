package com.vanniktech.music.mp3.processor.file

import com.vanniktech.music.autoCorrected
import com.vanniktech.music.logger.Logger
import java.io.File

internal const val FILE_ENDING = "mp3"

internal class AutoCorrectFileNamePreFileProcessor(
  private val logger: Logger,
) : PreFileProcessor {
  private var index = 0

  override fun process(file: File): File {
    require(file.extension == FILE_ENDING) { "Invalid extension at $file. Expected $FILE_ENDING" }

    val currentName = file.name
    val newName = currentName.autoCorrected()

    return if (newName != currentName) {
      val newFile = file.parentFile.resolve(newName)
      require(!newFile.exists() || currentName.equals(newName, ignoreCase = true)) { "File $newFile already exists!" }
      logger.log("""🚧""", index++, file, """Autocorrecting to "$newName"""")
      file.renameTo(newFile)
      newFile
    } else {
      file
    }
  }
}
