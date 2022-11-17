package com.vanniktech.music.mp3.handler

import com.vanniktech.music.mp3.Mp3AttributeDiff
import com.vanniktech.music.mp3.Mp3Attributes
import java.io.File

internal interface Mp3AttributesHandler {
  fun read(file: File): Mp3Attributes
  fun write(file: File, diff: List<Mp3AttributeDiff>)
}
