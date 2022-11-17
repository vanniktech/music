package com.vanniktech.music.mp3.processor.file

import java.io.File

internal interface PreFileProcessor {
  fun process(file: File): File
}
