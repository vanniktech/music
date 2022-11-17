package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.autoCorrected
import com.vanniktech.music.cleanTrack
import com.vanniktech.music.mp3.Mp3Attribute
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.albumYearly
import com.vanniktech.music.mp3.albumsWithTracks
import com.vanniktech.music.mp3.albumsWithoutTracks
import com.vanniktech.music.mp3.genres
import com.vanniktech.music.specialContains
import com.vanniktech.music.takeIfNotBlank
import com.vanniktech.music.track
import com.vanniktech.music.trackNumberRegex
import com.vanniktech.music.yearRegex
import java.io.Serial

internal class InferringException(override val message: String) : IllegalStateException() {
  companion object {
    @Serial private const val serialVersionUID: Long = -7092876544398384715L
  }
}

internal class InferringMp3AttributesProcessor : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes {
    val name = source.name
    val track = name.track()
    val album = albumsWithoutTracks.firstOrNull { name.specialContains(it) }
      ?: albumsWithTracks.firstOrNull { name.contains("$it $track") }
      ?: albumYearly.firstNotNullOfOrNull {
        Regex("$it [\\d]{4}").findAll(name).firstOrNull()?.value
      }
    val year = yearRegex.findAll(name).firstOrNull()?.value
    val genre = genres.firstOrNull { name.contains(it) }

    val separator = "-"
    val allSplits = name
      .split(separator)
      .mapNotNull { it.autoCorrected().trim().takeIfNotBlank() }

    val filteredSplits = allSplits
      .filterNot { it == album || it == "$album $track" || it == genre }

    val artist = when (filteredSplits.size) {
      0 -> null
      1 -> {
        val trackNumber = trackNumberRegex.findAll(filteredSplits[0]).toList().firstOrNull()

        if (trackNumber != null) {
          filteredSplits[0].substring(trackNumber.range.last + 1).trim()
        } else if (allSplits.size == 2) {
          filteredSplits[0]
        } else {
          filteredSplits[0].takeWhile { !it.isWhitespace() }
        }
      }
      2 -> filteredSplits[0]
      3 -> filteredSplits[0]
      4 -> null
      else -> throw InferringException("""Please handle "$name""")
    }

    val title = when (filteredSplits.size) {
      0 -> null
      1 -> {
        val value = filteredSplits[filteredSplits.size - 1].removeSuffix(artist.orEmpty()).trim()
        when {
          track != null && album != null -> "$album $track"
          else -> value.takeIfNotBlank() ?: album
        }
      }
      2 -> filteredSplits[1]
      3 -> filteredSplits[2]
      4 -> null
      else -> throw InferringException("""Please handle "$name""")
    }

    return Mp3Attributes(
      Mp3Tag.parseable().map { key ->
        val value = when (key) {
          Mp3Tag.ARTIST -> artist
          Mp3Tag.ALBUM -> album
          Mp3Tag.TRACK -> track?.cleanTrack()
          Mp3Tag.GENRE -> genre
          Mp3Tag.TITLE -> title
          Mp3Tag.YEAR -> year
          Mp3Tag.COMMENTS -> null
          Mp3Tag.ARTIST_2 -> null
          Mp3Tag.COMPOSER -> null
          Mp3Tag.POSITION -> null
          Mp3Tag.PICTURE -> null
          Mp3Tag.PRIVATE_FRAME -> null
        }

        attributes.firstOrNull { it.tag == key && it.value != null } ?: Mp3Attribute(
          tag = key,
          value = value,
          inferred = value != null,
        )
      },
    )
  }
}
