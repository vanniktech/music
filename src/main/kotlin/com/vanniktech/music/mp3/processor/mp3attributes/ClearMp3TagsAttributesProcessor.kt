package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source

internal class ClearMp3TagsAttributesProcessor(
  private val tags: Set<Mp3Tag>,
) : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes = Mp3Attributes(
    attributes.map {
      if (it.tag in tags && it.value?.isNotEmpty() == true) {
        it.copy(value = "", inferred = false)
      } else {
        it
      }
    },
  )
}
