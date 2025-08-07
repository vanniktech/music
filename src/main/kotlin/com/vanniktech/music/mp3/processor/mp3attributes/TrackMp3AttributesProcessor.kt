package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.modifyAttributes
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.albumsWithTracks
import com.vanniktech.music.mp3.albumsWithoutTracks
import com.vanniktech.music.mp3.map

internal class TrackMp3AttributesProcessor : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes = attributes.map(Mp3Tag.TRACK) { track ->
    val value = track.value
    val isInvalid = !value.isNullOrBlank() && value.toIntOrNull() == null
    val album = attributes.get(Mp3Tag.ALBUM).value
    val isInferred = track.inferred
    val isMissing = (albumsWithTracks.contains(album) && value.isNullOrBlank())
    val shouldBeMissing = albumsWithoutTracks.contains(album) && !value.isNullOrBlank()

    when {
      value?.endsWith("/0") == true -> track.copy(value = value.removeSuffix("/0"), inferred = false)
      value == "0/0" -> track.copy(value = "", inferred = false)
      isInvalid || isMissing || isInferred || shouldBeMissing -> modifyAttributes(
        source = source,
        attributes = listOf(track),
        allAttributes = attributes,
        mandatory = false,
      )
      else -> track
    }
  }
}
