package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.modifyAttributes
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.map

internal class ArtistMp3AttributesProcessor : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes = attributes.map(Mp3Tag.ARTIST) { artist ->
    val isMissing = artist.value.isNullOrBlank()
    val isInferred = artist.inferred
    if (isMissing || isInferred) {
      modifyAttributes(
        source = source,
        attributes = listOf(artist),
        allAttributes = attributes,
        mandatory = true,
      )
    } else {
      artist
    }
  }
}
