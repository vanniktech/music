package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.map

internal class SubtitleMp3AttributesProcessor : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes =
    attributes.map(Mp3Tag.SUBTITLE) { subtitle ->
      subtitle.copy(
        value = subtitlesFromTitle(attributes.get(Mp3Tag.TITLE).value).sorted().distinct().joinToString(separator = SUBTITLE_DELIMITER),
      )
    }
}
