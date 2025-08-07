package com.vanniktech.music.mp3.processor.file

import com.vanniktech.music.Eye3D
import com.vanniktech.music.extractImageUrlFromString
import com.vanniktech.music.frontCoverFile
import com.vanniktech.music.logger.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import okio.Sink
import okio.buffer
import okio.sink
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
      logger.log("""🚧""", index - 1, file, """Missing picture, please paste from soundcloud""")
      val answer = readln()
      val image = extractImageUrlFromString(answer)

      if (image == null) {
        error("Could not find an image url from $answer")
      } else {
        httpClient.prepareGet {
          url(image)
        }.execute {
          logger.log("""🚧""", index - 1, file, """Downloading $image""")
          it.bodyAsChannel().readFully(mp3.frontCoverFile().sink())
        }
      }
    }

    Eye3D.writeImage(mp3)

    file.delete()
    mp3
  } else {
    file
  }
}

// Okio likes to use 8kb:
// https://github.com/square/okio/blob/a94c678de4e8a21e53126d42a1a3d897daa56a4a/recipes/index.html#L1322
private const val OKIO_RECOMMENDED_BUFFER_SIZE: Int = 8192

@Suppress("NAME_SHADOWING")
private suspend fun ByteReadChannel.readFully(sink: Sink) {
  val channel = this
  sink.buffer().use { sink ->
    while (!channel.isClosedForRead) {
      val packet = channel.readRemaining(OKIO_RECOMMENDED_BUFFER_SIZE.toLong())
      while (!packet.exhausted()) {
        sink.write(packet.readByteArray())
      }
    }
  }
}
