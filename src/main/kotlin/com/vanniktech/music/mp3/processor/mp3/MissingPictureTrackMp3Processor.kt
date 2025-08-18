package com.vanniktech.music.mp3.processor.mp3

import com.vanniktech.music.Eye3D
import com.vanniktech.music.askDownloadImageFile
import com.vanniktech.music.logger.Logger
import com.vanniktech.music.mp3.Mp3
import com.vanniktech.music.mp3.Mp3Tag
import io.ktor.client.HttpClient

internal class MissingPictureTrackMp3Processor(
  private val logger: Logger,
  private val httpClient: HttpClient,
) : Mp3Processor {
  override suspend fun process(mp3: Mp3, index: Int): Mp3 {
    if (mp3.attributes.get(Mp3Tag.PICTURE).value.isNullOrBlank()) {
      val file = mp3.file

      logger.log("""🖼️""", index, file, """Missing picture, please paste image url or css from soundcloud""")

      askDownloadImageFile(
        file = file,
        httpClient = httpClient,
      )

      Eye3D.writeImage(file)
    }

    return mp3
  }
}
