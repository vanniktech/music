package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Source

internal interface Mp3AttributesProcessor {
  fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes
}
