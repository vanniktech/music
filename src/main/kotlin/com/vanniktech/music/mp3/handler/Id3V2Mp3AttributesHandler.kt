package com.vanniktech.music.mp3.handler

import com.vanniktech.music.Eye3D
import com.vanniktech.music.mp3.Mp3Attribute
import com.vanniktech.music.mp3.Mp3AttributeDiff
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.takeIfNotBlank
import java.io.File

internal const val FRONT_COVER = "FRONT_COVER.jpg"

internal class Id3V2Mp3AttributesHandler : Mp3AttributesHandler {
  override fun read(file: File): Mp3Attributes {
    val id3v2Process = ProcessBuilder("id3v2", "-l", file.absolutePath).start()
    val attributes = id3v2Process.inputStream.reader(Charsets.UTF_8).use {
      try {
        fromString(it.readText())
      } catch (throwable: Throwable) {
        throw IllegalArgumentException("Failed for ${file.absolutePath}", throwable)
      }
    }
    require(id3v2Process.waitFor() == 0) {
      "Error extracting tags from $file"
    }

    return if (attributes == null) {
      Eye3D.extractImageFrom(file)

      Mp3Attributes(
        Mp3Tag.entries.map {
          val value = when (it) {
            Mp3Tag.PICTURE -> "exists"
            else -> null
          }
          Mp3Attribute(it, value, inferred = false)
        },
      )
    } else {
      Mp3Attributes(attributes)
    }
  }

  override fun write(
    file: File,
    diff: List<Mp3AttributeDiff>,
  ) {
    val id3v2Commands = listOf("id3v2") +
      diff.flatMap { listOf("--${it.tag.id}", it.new.orEmpty()) } +
      file.absolutePath

    require(ProcessBuilder(id3v2Commands).start().waitFor() == 0) {
      "Error writing tags to $file"
    }

    Eye3D.writeImage(file)
  }

  internal fun fromString(text: String): List<Mp3Attribute>? {
    val lines = text.lines()
    val beginIndex = lines.indexOfFirst { it.startsWith("id3v2 tag info for ") }

    val map = lines
      .drop(beginIndex + 1)
      .takeWhile { !it.startsWith("id3v1 tag info for ") && !it.endsWith("No ID3v1 tag") && !it.endsWith("No ID3 tag") }
      .mapNotNull { it.takeIfNotBlank() }
      .associateBy { line ->
        val tag = line.take(4)
        Mp3Tag.fromIdOrNull(tag) ?: error("Unknown tag $tag")
      }

    if (map.isEmpty()) {
      return null
    }

    return Mp3Tag.parseable()
      .map { tag ->
        val value = map[tag]?.split(":")?.last()?.trim()
        Mp3Attribute(
          tag = tag,
          value = when (tag) {
            Mp3Tag.GENRE -> value?.replace(GENRE_IDENTIFIER, "")?.trim()
            else -> value
          },
          inferred = false,
        )
      }
  }

  private companion object {
    val GENRE_IDENTIFIER = Regex("\\(\\d+\\)")
  }
}
