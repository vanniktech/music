package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.map

internal class SubtitleMp3AttributesProcessor : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes {
    val subtitles = subtitlesFromTitle(attributes.get(Mp3Tag.TITLE).value).joinToString(separator = SUBTITLE_SEPARATOR)

    return attributes.map(Mp3Tag.SUBTITLE) { subtitle ->
      // Override extra subtitle field that we maintain.
      subtitle.copy(value = subtitles)
    }.map(Mp3Tag.TITLE) { title ->
      // Now, resort the title with the same order as the subtitles.
      title.copy(value = "$subtitles $TITLE_SUBTITLE_DELIMITER${title.value?.split(TITLE_SUBTITLE_DELIMITER).orEmpty().drop(1).joinToString(separator = TITLE_SUBTITLE_DELIMITER)}")
    }
  }
}
