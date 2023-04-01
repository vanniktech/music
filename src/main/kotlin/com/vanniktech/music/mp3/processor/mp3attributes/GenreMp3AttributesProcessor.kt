package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.modifyAttributes
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.genres
import com.vanniktech.music.mp3.map

internal class GenreMp3AttributesProcessor(
  private val mandatory: Boolean,
) : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes =
    attributes.map(Mp3Tag.GENRE) { genre ->
      val isMissing = genre.value.isNullOrBlank()
      val isInferred = genre.inferred
      val isInvalidGenre = !genres.contains(genre.value)

      if (isMissing || isInferred || isInvalidGenre) {
        val modified = modifyAttributes(
          source = source,
          attributes = listOf(genre),
          allAttributes = attributes,
          mandatory = mandatory,
        )
        require(genres.contains(modified.value)) { "Must be one of: ${genres.sorted().joinToString()}" }
        modified
      } else {
        genre
      }
    }
}
