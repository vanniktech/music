package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.modifyAttributes
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.albumYearly
import com.vanniktech.music.mp3.albumsWithoutTracks
import kotlinx.datetime.LocalDate

internal class AlbumTrackMismatchMp3AttributesProcessor(
  private val localDate: LocalDate,
) : Mp3AttributesProcessor {
  private val albumsWithoutTrack = albumsWithoutTracks + albumYearly.flatMap { festival -> (2000..localDate.year).map { "$festival $it" } }

  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes {
    val album = attributes.get(Mp3Tag.ALBUM)
    val track = attributes.get(Mp3Tag.TRACK)

    val hasTrackButNoAlbum = !track.value.isNullOrBlank() && album.value == null
    val isAlbumWithoutTrack = !album.value.isNullOrBlank() && track.value.isNullOrBlank() && !albumsWithoutTrack.contains(album.value)

    val attribute = if (hasTrackButNoAlbum || isAlbumWithoutTrack) {
      modifyAttributes(
        source = source,
        attributes = listOf(album, track),
        allAttributes = attributes,
        mandatory = false,
      )
    } else {
      null
    }

    return Mp3Attributes(
      attributes.map {
        when (it.tag) {
          attribute?.tag -> attribute
          else -> it
        }
      },
    )
  }
}
