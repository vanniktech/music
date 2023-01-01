package com.vanniktech.music

import com.vanniktech.music.mp3.handler.FRONT_COVER
import java.io.File

internal object Eye3D {
  fun extractImageFrom(file: File): File {
    val imageDirectory = file.parentFile
    val imageCommands = listOf(
      "eyeD3",
      "--write-images=$imageDirectory",
      file.absolutePath,
    )

    require(ProcessBuilder(imageCommands).start().waitFor() == 0) {
      "Error extracting image at $file"
    }

    val imageFile = file.frontCoverFile()
    imageDirectory.resolve(FRONT_COVER).renameTo(imageFile)
    return imageFile
  }

  fun writeImage(file: File) {
    val frontCover = file.frontCoverFile()
    if (frontCover.exists()) {
      val imageCommands = listOf(
        "eyeD3",
        "--add-image",
        "${frontCover.absolutePath}:FRONT_COVER",
        file.absolutePath,
      )

      try {
        require(ProcessBuilder(imageCommands).directory(file.parentFile).start().waitFor() == 0) {
          "Error writing image to $file"
        }
      } finally {
        frontCover.delete()
      }
    }
  }

  private fun File.frontCoverFile() = parentFile.resolve("$nameWithoutExtension.jpg")
}
