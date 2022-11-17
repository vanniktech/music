package com.vanniktech.music.mp3

import java.io.File

internal interface Source {
  val name: String
}

internal class FileSource(private val file: File) : Source {
  override val name: String get() = file.nameWithoutExtension
}
