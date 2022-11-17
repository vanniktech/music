package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.logger.Logger
import com.vanniktech.music.mp3.Mp3
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.processor.file.FILE_ENDING
import com.vanniktech.music.mp3.processor.mp3.Mp3Processor

internal class RenameMp3AttributesProcessor(
  private val logger: Logger,
) : Mp3Processor {
  private var index = 0

  override fun process(mp3: Mp3): Mp3 {
    val file = mp3.file
    val currentName = file.name
    val newName = "${mp3.attributes.get(Mp3Tag.ARTIST).value} - ${mp3.attributes.get(Mp3Tag.TITLE).value}.$FILE_ENDING"

    return if (newName != currentName) {
      val newFile = file.parentFile.resolve(newName)
      require(!newFile.exists() || currentName.equals(newName, ignoreCase = true)) { "File $newFile already exists!" }
      logger.log("""✨""", index++, file, """Autocorrecting to "$newName"""")
      file.renameTo(newFile)
      mp3.copy(file = newFile)
    } else {
      mp3
    }
  }
}
