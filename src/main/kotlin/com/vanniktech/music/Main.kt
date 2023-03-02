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
import com.vanniktech.music.mp3.processor.mp3attributes.ClearMp3TagsAttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.GenreMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.InferringException
import com.vanniktech.music.mp3.processor.mp3attributes.InferringMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.RenameMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.TitleMismatchMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.TitleMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.TrackMp3AttributesProcessor
import com.vanniktech.music.mp3.processor.mp3attributes.YearMp3AttributesProcessor
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File

private const val ANDROID_PATH = "/storage/emulated/0/Music/m/"

/**
 * To check every now and then:
 *
 * https://soundcloud.com/search?q=something%20slow%200138
 * https://soundcloud.com/search?q=soulful%20techno%20120
 * https://soundcloud.com/search?q=SVT-Podcast134
 * https://soundcloud.com/mikehaddad/sets/mikes-sessions
 * https://soundcloud.com/sunset-grooves/sets/sunset-grooves-podcasts
 * https://soundcloud.com/magicianonduty/sets/journey-series
 * https://soundcloud.com/search?q=traumcast%2037
 */
fun main() {
  val root = File("/Volumes/Niklas/m/")
  // val root = File("/Users/niklas/Downloads")

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

  val androidDiffFile = File("diff.sh")
  require(!androidDiffFile.exists() || androidDiffFile.length() == 0L) { "diff.sh is not empty, either execute or delete the file" }

  val timeZone = TimeZone.currentSystemDefault()
  val localDate = now.toLocalDateTime(timeZone).date
  val attributesHandler = Id3V2Mp3AttributesHandler()

  val preFileProcessors = listOf(
    WavToMp3FileProcessor(logger = logger),
    FileNamePreFileProcessor(logger = logger, autoCorrect = false),
  )

  val mandatory = root.absolutePath.contains("Downloads")
  val mp3AttributeProcessors = listOf(
    ClearMp3TagsAttributesProcessor(tags = setOf(COMMENTS, COMPOSER, ARTIST_2, POSITION)),
    AutoCorrectMp3AttributesProcessor(),
    InferringMp3AttributesProcessor(),
    ArtistMp3AttributesProcessor(),
    TitleMp3AttributesProcessor(),
    AlbumMp3AttributesProcessor(),
    TrackMp3AttributesProcessor(),
    GenreMp3AttributesProcessor(mandatory = mandatory),
    YearMp3AttributesProcessor(localDate = localDate, mandatory = mandatory),
    AlbumTrackMismatchMp3AttributesProcessor(localDate = localDate),
    TitleMismatchMp3AttributesProcessor(),
  )

  val mp3Processors = listOf(
    MissingPictureTrackMp3Processor(),
    RenameMp3AttributesProcessor(logger = logger),
  )

  val inferringExceptions = mutableListOf<InferringException>()

  val androidRemovals = mutableListOf<File>()
  val androidAdditions = mutableListOf<File>()

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
          androidRemovals += file
          androidAdditions += result.file
        }
      } catch (inferringException: InferringException) {
        inferringExceptions.add(inferringException)
      }
    }

  if (androidRemovals.isNotEmpty()) {
    androidDiffFile.appendText(androidRemovals.joinToString(prefix = "adb shell \"", postfix = "\"\n") { "rm -f '$ANDROID_PATH${it.name}'" })
  }

  if (androidAdditions.isNotEmpty()) {
    androidDiffFile.appendText(androidAdditions.joinToString(postfix = "\n") { "adb push '${it.absolutePath}' '$ANDROID_PATH'" })
  }

  if (androidDiffFile.length() > 0) {
    androidDiffFile.appendText("rm ${androidDiffFile.absolutePath}")
    androidDiffFile.setExecutable(true)
  }

  inferringExceptions.forEach { it.printStackTrace() }
}
