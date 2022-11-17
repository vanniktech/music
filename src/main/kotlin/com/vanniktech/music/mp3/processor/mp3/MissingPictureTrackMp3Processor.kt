package com.vanniktech.music.mp3.processor.mp3

import com.vanniktech.music.mp3.Mp3
import com.vanniktech.music.mp3.Mp3Tag
import java.io.File

internal class MissingPictureTrackMp3Processor : Mp3Processor {
  private val file = File("images.sh").apply {
    setExecutable(true)
  }

  override fun process(mp3: Mp3): Mp3 {
    if (mp3.attributes.get(Mp3Tag.PICTURE).value.isNullOrBlank()) {
      file.appendText(
        """
        |./image.sh "${mp3.file.name}" '
        |
        """.trimMargin(),
      )
    }

    return mp3
  }
}
