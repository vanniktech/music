package com.vanniktech.music.mp3

import java.io.File

internal data class Mp3(
  val file: File,
  val attributes: Mp3Attributes,
)
