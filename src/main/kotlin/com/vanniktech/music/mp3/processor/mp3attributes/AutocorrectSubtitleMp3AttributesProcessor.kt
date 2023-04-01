package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.map

const val SUBTITLE_DELIMITER = ","

internal class AutocorrectSubtitleMp3AttributesProcessor : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes {
    return attributes.map(Mp3Tag.SUBTITLE) { attribute ->
      attribute.copy(value = attribute.value?.split(SUBTITLE_DELIMITER)?.sorted()?.joinToString(separator = SUBTITLE_DELIMITER))
    }
  }
}
