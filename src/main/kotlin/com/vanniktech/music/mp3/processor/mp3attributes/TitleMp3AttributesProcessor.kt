package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.modifyAttributes
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.map

internal class TitleMp3AttributesProcessor : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes =
    attributes.map(Mp3Tag.TITLE) { title ->
      val isInferred = title.inferred
      val isMissing = title.value.isNullOrBlank()

      if (title.value != null && title.value.contains("#")) {
        val index = title.value.indexOf("#")

        if (title.value.getOrNull(index - 1)?.isWhitespace() == false) {
          title.copy(value = title.value.take(index) + " " + title.value.drop(index))
        } else {
          title
        }
      } else if (isMissing || isInferred) {
        modifyAttributes(
          source = source,
          attributes = listOf(title),
          allAttributes = attributes,
          mandatory = true,
        )
      } else {
        title
      }
    }
}
