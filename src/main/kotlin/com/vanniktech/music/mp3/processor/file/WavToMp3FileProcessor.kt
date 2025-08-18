package com.vanniktech.music.mp3.processor.file

import com.vanniktech.music.Eye3D
import com.vanniktech.music.askDownloadImageFile
import com.vanniktech.music.logger.Logger
import io.ktor.client.HttpClient
import java.io.File

internal class WavToMp3FileProcessor(
  private val logger: Logger,
  private val httpClient: HttpClient,
) : PreFileProcessor {
  private var index = 0

  override suspend fun process(file: File): File = if (file.extension.equals("wav", ignoreCase = true) || file.extension.equals("m4a", ignoreCase = true)) {
    logger.log("""🚧""", index++, file, """Converting from ${file.extension} to $FILE_ENDING""")

    val imageFile = Eye3D.extractImageFrom(file)

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
      "Error converting $file to $FILE_ENDING"
    }

    if (!imageFile.exists()) {
      logger.log("""🚧""", index - 1, file, """Missing picture, please paste image url or css from soundcloud""")

      askDownloadImageFile(
        file = mp3,
        httpClient = httpClient,
      )
    }

    Eye3D.writeImage(mp3)

    file.delete()
    mp3
  } else {
    file
  }
}
