package com.vanniktech.music.mp3.processor.mp3

import com.vanniktech.music.mp3.Mp3

internal interface Mp3Processor {
  fun process(mp3: Mp3): Mp3
}
