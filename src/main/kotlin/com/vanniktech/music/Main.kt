package com.vanniktech.music

import com.vanniktech.music.logger.ConsoleLogger
import com.vanniktech.music.mp3.FileSource
import com.vanniktech.music.mp3.Mp3
import com.vanniktech.music.mp3.Mp3Tag.ARTIST_2
import com.vanniktech.music.mp3.Mp3Tag.COMMENTS
import com.vanniktech.music.mp3.Mp3Tag.COMPOSER
import com.vanniktech.music.mp3.Mp3Tag.POSITION
import com.vanniktech.music.mp3.diff
import com.vanniktech.music.mp3.handler.FRONT_COVER
import com.vanniktech.music.mp3.handler.Id3V2Mp3AttributesHandler
import com.vanniktech.music.mp3.processor.file.FileNamePreFileProcessor
import com.vanniktech.music.mp3.processor.file.WavToMp3FileProcessor
import com.vanniktech.music.mp3.processor.mp3.MissingPictureTrackMp3Processor
import com.vanniktech.music.mp3.processor.mp3attributes.AlbumMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.AlbumTrackMismatchMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.ArtistMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.AutoCorrectMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.AutocorrectSubtitleMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.ClearMp3TagsAttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.GenreMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.RecoverableException
import com.vanniktech.music.mp3.processor.mp3attributes.InferringMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.RenameMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.SubtitleMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.TitleMismatchMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.TitleMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.TrackMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.YearMp3AttributesProcessor
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File

private const val ANDROID_PATH = "/storage/emulated/0/Music/m/"
private const val GOOGLE_DRIVE_DIRECTORY = "\$GOOGLE_DRIVE/m/"

fun main() {
  // val root = File("/Volumes/Niklas/m/")
  val root = File("/Users/niklas/Downloads")

  // Clean up left over images.
  root.listFiles().orEmpty().filter { it.extension in setOf("jpg", "png") }.forEach { it.delete() }

  val files = root
    .listFiles()
    .orEmpty()
    .filterNot { it.isDirectory }
    .filterNot { it.isHidden }
    .filterNot { it.name == FRONT_COVER }
    .sorted()

  val clock = Clock.System
  val now = clock.now()

  val logsDirectory = File("logs/")
  logsDirectory.mkdir()
  val logger = ConsoleLogger(file = logsDirectory.resolve("${now.toEpochMilliseconds()}.log"))

  val diffFile = File("diff.sh")
  require(!diffFile.exists() || diffFile.length() == 0L) { "diff.sh is not empty, either execute or delete the file" }

  val timeZone = TimeZone.currentSystemDefault()
  val localDate = now.toLocalDateTime(timeZone).date
  val attributesHandler = Id3V2Mp3AttributesHandler()

  val preFileProcessors = listOf(
    WavToMp3FileProcessor(logger = logger),
    FileNamePreFileProcessor(logger = logger, autoCorrect = false),
  )

  val isDownloads = root.absolutePath.contains("Downloads")
  val mp3AttributeProcessors = listOf(
    ClearMp3TagsAttributesProcessor(tags = setOf(COMMENTS, COMPOSER, ARTIST_2, POSITION)),
    AutoCorrectMp3AttributesProcessor(),
    InferringMp3AttributesProcessor(),
    ArtistMp3AttributesProcessor(),
    TitleMp3AttributesProcessor(),
    AutocorrectSubtitleMp3AttributesProcessor(),
    AlbumMp3AttributesProcessor(),
    TrackMp3AttributesProcessor(),
    GenreMp3AttributesProcessor(mandatory = isDownloads),
    YearMp3AttributesProcessor(localDate = localDate, mandatory = isDownloads),
    AlbumTrackMismatchMp3AttributesProcessor(localDate = localDate),
    TitleMismatchMp3AttributesProcessor(),
    SubtitleMp3AttributesProcessor(),
  )

  val mp3Processors = listOf(
    MissingPictureTrackMp3Processor(),
    RenameMp3AttributesProcessor(logger = logger),
  )

  val recoverableExceptions = mutableListOf<RecoverableException>()

  val fileRemovals = mutableListOf<File>()
  val fileAdditions = mutableListOf<File>()

  files
    .map { preFileProcessors.fold(it) { file, processor -> processor.process(file) } }
    .forEachIndexed { index, file ->
      val initialAttributes = attributesHandler.read(file)
      val source = FileSource(file)

      try {
        val postProcessedAttributes = mp3AttributeProcessors.fold(initialAttributes) { attributes, processor -> processor.process(source, attributes) }
        val diff = diff(initialAttributes, postProcessedAttributes)
        val hasDiff = diff.isNotEmpty()

        if (hasDiff) {
          logger.log("""🔧""", index, file, "changing ${diff.joinToString()}")
          attributesHandler.write(file, diff)
        } else {
          logger.log("""✅""", index, file, postProcessedAttributes.joinToString())
        }

        val initial = Mp3(file, postProcessedAttributes)
        val result = mp3Processors.fold(initial) { mp3, processor -> processor.process(mp3, index) }

        if (hasDiff || result != initial) {
          fileRemovals += file
          fileAdditions += result.file
        }
      } catch (recoverableException: RecoverableException) {
        recoverableExceptions.add(recoverableException)
      }
    }

  if (!isDownloads) {
    val hasRemovals = fileRemovals.isNotEmpty()
    val hasAdditions = fileAdditions.isNotEmpty()
    val willWriteFile = hasRemovals || hasAdditions

    if (willWriteFile) {
      diffFile.appendText("#!/bin/bash\nset -e\n\n")
      diffFile.setExecutable(true)
    }

    if (hasRemovals) {
      diffFile.appendText(fileRemovals.joinToString(postfix = "\n", separator = "\n") { "adb shell \"rm -f '$ANDROID_PATH${it.name}'\"" })
      diffFile.appendText(fileRemovals.joinToString(postfix = "\n", separator = "\n") { "rm -f \"$GOOGLE_DRIVE_DIRECTORY${it.name}\"" })
    }

    if (hasAdditions) {
      diffFile.appendText(fileAdditions.joinToString(postfix = "\n", separator = "\n") { "adb push \"${it.absolutePath}\" \"$ANDROID_PATH\"" })
      diffFile.appendText(fileAdditions.joinToString(postfix = "\n", separator = "\n") { "cp \"${it.absolutePath}\" \"$GOOGLE_DRIVE_DIRECTORY\"" })
    }

    if (diffFile.length() > 0) {
      diffFile.appendText("rm ${diffFile.absolutePath}")
    }
  }

  recoverableExceptions.forEach { it.printStackTrace() }
}
