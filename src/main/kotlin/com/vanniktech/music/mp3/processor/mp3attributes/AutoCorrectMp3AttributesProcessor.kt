package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.autoCorrected
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source

internal class AutoCorrectMp3AttributesProcessor : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes =
    Mp3Attributes(
      attributes.map {
        when (it.tag) {
          Mp3Tag.PICTURE, Mp3Tag.PRIVATE_FRAME -> it
          else -> it.copy(value = it.value?.autoCorrected()) // We'll leave inferred as it is.
        }
      },
    )
}
