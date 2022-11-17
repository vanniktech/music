package com.vanniktech.music.logger

import java.io.File

internal interface Logger {
  fun log(emoji: String, index: Int, file: File, message: String)
}

internal class ConsoleLogger(
  private val file: File,
) : Logger {
  override fun log(
    emoji: String,
    index: Int,
    file: File,
    message: String,
  ) {
    log("""$emoji ${index + 1}/ ${file.name} $message""")
  }

  private fun log(log: String) {
    println(log)
    file.appendText(log + "\n")
  }
}
