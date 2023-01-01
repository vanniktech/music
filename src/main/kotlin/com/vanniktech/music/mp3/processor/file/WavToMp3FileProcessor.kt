package com.vanniktech.music.mp3.processor.file

import com.vanniktech.music.Eye3D
import com.vanniktech.music.logger.Logger
import java.io.File

internal class WavToMp3FileProcessor(
  private val logger: Logger,
) : PreFileProcessor {
  private var index = 0

  override fun process(file: File): File {
    return if (file.extension.equals("wav", ignoreCase = true)) {
      logger.log("""🚧""", index++, file, """Converting from wav to mp3""")

      Eye3D.extractImageFrom(file)

      val mp3 = file.parentFile.resolve(file.nameWithoutExtension + "." + FILE_ENDING)
      val toMp3Commands = listOf(
        "ffmpeg",
        "-i",
        file.absolutePath,
        "-vn",
        "-ab",
        "320k",
        "-ar",
        "44100",
        "-y",
        mp3.absolutePath,
      )

      require(ProcessBuilder(toMp3Commands).start().waitFor() == 0) {
        "Error converting $file to wav"
      }

      Eye3D.writeImage(mp3)

      file.delete()
      mp3
    } else {
      file
    }
  }
}
