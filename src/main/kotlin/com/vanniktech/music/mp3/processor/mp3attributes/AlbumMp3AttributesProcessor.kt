package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.modifyAttributes
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.albumYearly
import com.vanniktech.music.mp3.map

internal class AlbumMp3AttributesProcessor : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes =
    attributes.map(Mp3Tag.ALBUM) { album ->
      val isInferred = album.inferred
      val isYearlyEventAndMissingDate = albumYearly.contains(album.value)

      if (isYearlyEventAndMissingDate || isInferred) {
        modifyAttributes(
          source = source,
          attributes = listOf(album),
          allAttributes = attributes,
          mandatory = isYearlyEventAndMissingDate,
        )
      } else {
        album
      }
    }
}
