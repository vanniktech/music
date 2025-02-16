package com.vanniktech.music

import com.vanniktech.music.mp3.Mp3Attribute
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.SUBTITLE_TODO_PREFIX
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.processor.file.FILE_ENDING
import com.vanniktech.music.mp3.processor.mp3attributes.RecoverableException
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.time.LocalDate

internal val REGEX_DOUBLE_SPACINGS = Regex(" {2,}")

internal fun modifyAttributes(
  source: Source,
  attributes: List<Mp3Attribute>,
  allAttributes: Mp3Attributes,
  mandatory: Boolean,
) = if (attributes.size == 1) {
  println("""🦫 ${source.name}.$FILE_ENDING (${allAttributes.joinToString()})""")
  val attribute = attributes[0]
  print("$attribute:")
  pasteIntoClipboard(attribute.value ?: source.name)

  attribute.copy(
    value = when (val value = readln().autoCorrected().takeIfNotBlank() ?: attribute.value?.autoCorrected().takeIfNotBlank()) {
      "null" -> null
      else -> map(mandatory, attribute.tag, value)
    },
    inferred = false,
  )
} else {
  println("""🤯 ${source.name}.$FILE_ENDING ${allAttributes.joinToString()}""")
  attributes.forEach { println(it) }
  pasteIntoClipboard(source.name)

  val input = readln()
  val answer = input.split("=")
  val tag = Mp3Tag.entries.firstOrNull { it.name.lowercase() == answer[0].lowercase() } ?: error("Wrong input: $input, should be: name=<value>")

  Mp3Attribute(
    tag = tag,
    value = when (val value = answer.getOrNull(1)?.autoCorrected().takeIfNotBlank()) {
      "null" -> null
      else -> map(mandatory, tag, value)
    },
    inferred = false,
  )
}

private fun map(mandatory: Boolean, tag: Mp3Tag, value: String?): String? {
  val enhanced = when (tag) {
    Mp3Tag.GENRE -> value.takeIfNotBlank() ?: "Uncategorized"
    Mp3Tag.YEAR -> when {
      value?.startsWith("-") == true -> (LocalDate.now().year - value.removePrefix("-").toInt()).toString()
      else -> value
    }
    else -> value
  }

  if (mandatory) {
    require(!enhanced.isNullOrBlank()) { "$tag is mandatory" }
  }

  return enhanced
}

internal fun String.prependTrack() = when (val track = takeIfNotBlank()?.padStart(3, '0')) {
  null -> null
  else -> "#$track"
}

internal fun pasteIntoClipboard(text: String) {
  val selection = StringSelection(text)
  Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
}

internal val yearRegex = Regex("\\d{4}")
internal val trackNumberRegex = Regex("(#\\d+)|(\\(\\d+)|( \\d{2,3}(?!(\\d|\\.|-))( |\$))|((?<!\\d)\\d{2,3} )|([A-Z]{3}\\d{2,3})")

internal fun String.track() = trackNumberRegex.findAll(replace(Regex("${SUBTITLE_TODO_PREFIX}\\d{1,3}"), "")).firstNotNullOfOrNull { it.value.takeIfNotBlank() }
internal fun String.cleanTrack() = filter { it.isDigit() }
  .removePrefix("0")
  .removePrefix("0")

internal fun String?.takeIfNotBlank() = takeIf { it?.isNotBlank() == true }

internal fun String.specialContains(other: String) = contains(" $other ") ||
  endsWith(" $other") || startsWith("$other ")

internal fun String.trim(what: String) = removePrefix(what).removePrefix(what.capitalized())
  .removeSuffix(what).removeSuffix(what.capitalized())
  .trim()

fun String.capitalized() = replaceFirstChar {
  when {
    it.isLowerCase() -> it.titlecase()
    else -> it.toString()
  }
}
internal fun String.autoCorrected() = trim()
  // Fix some dates.
  .replace(" January, ", ".01.", ignoreCase = true)
  .replace(" Januar, ", ".01.", ignoreCase = true)
  .replace(" February, ", ".02.", ignoreCase = true)
  .replace(" Februar, ", ".02.", ignoreCase = true)
  .replace(" March, ", ".03.", ignoreCase = true)
  .replace(" März, ", ".03.", ignoreCase = true)
  .replace(" April, ", ".04.", ignoreCase = true)
  .replace(" May, ", ".05.", ignoreCase = true)
  .replace(" Mai, ", ".05.", ignoreCase = true)
  .replace(" June, ", ".06.", ignoreCase = true)
  .replace(" Juni, ", ".06.", ignoreCase = true)
  .replace(" July, ", ".07.", ignoreCase = true)
  .replace(" Juli, ", ".07.", ignoreCase = true)
  .replace(" August, ", ".08.", ignoreCase = true)
  .replace(" September, ", ".09.", ignoreCase = true)
  .replace(" October, ", ".10.", ignoreCase = true)
  .replace(" Oktober, ", ".10.", ignoreCase = true)
  .replace(" November, ", ".11.", ignoreCase = true)
  .replace(" Dezember, ", ".12.", ignoreCase = true)
  .replace(" December, ", ".12.", ignoreCase = true)
  .replace("Jan.", "January ", ignoreCase = true)
  .replace("Feb.", "February ", ignoreCase = true)
  .replace("Mar.", "March ", ignoreCase = true)
  .replace("Apr.", "April ", ignoreCase = true)
  .replace("May.", "May ", ignoreCase = true)
  .replace("Jun.", "June ", ignoreCase = true)
  .replace("Jul.", "July ", ignoreCase = true)
  .replace("Aug.", "August ", ignoreCase = true)
  .replace("Sep.", "September ", ignoreCase = true)
  .replace("Oct.", "October ", ignoreCase = true)
  .replace("Nov.", "November ", ignoreCase = true)
  .replace("Dec.", "December ", ignoreCase = true)
  // Numbering
  .replace("down.cast °", "down.cast #", ignoreCase = true)
  .replace("Schleichcast°", "Schleichcast #", ignoreCase = true)
  .replace(" Vol. ", "#", ignoreCase = true)
  .replace(" Nr. ", "#", ignoreCase = true)
  .replace("- #", "#", ignoreCase = true)
  // Audio quality.
  .replace("(320kbps)", "")
  // Fix some Artists / Podcasts.
  .replace("rāga", "raga", ignoreCase = true)
  .replace("luçïd", "lucid", ignoreCase = true)
  .replace("T⨋₼₱L⨊₡ĄS৳", "Templecast", ignoreCase = true)
  .replace("cedd fuze", "CeddFUZE", ignoreCase = true)
  .replace("ceddfuze", "CeddFUZE", ignoreCase = true)
  .replace("súlfur", "sulfur", ignoreCase = true)
  .replace("Hrααch", "Hraach", ignoreCase = true)
  .replace("Mușză", "Musza", ignoreCase = true)
  .replace("Artišoko", "Artisoko", ignoreCase = true)
  .replace("N'to", "NTO", ignoreCase = true)
  .replace("TooL...8", "TooL8", ignoreCase = true)
  .replace("Temo Sayın", "Temo Sayin", ignoreCase = true)
  .replace("Nadīm", "Nadim", ignoreCase = true)
  .replace("Heimlich Podcasts", "Heimlich Podcast", ignoreCase = true)
  .replace("Sarah Kreis & Caleesi", "Caleesi & Sarah Kreis", ignoreCase = true)
  .replace("Leichtigkeit Des Seins", "Leichtigkeit des Seins", ignoreCase = true)
  .replace("GLYKMIX", "GLYK", ignoreCase = true)
  .replace("sound(ge)cloud", "soundgecloud", ignoreCase = true)
  .replace("TRNDMUSIK Podcast", "trndmsk Podcast", ignoreCase = true)
  .replace("Im Strom Der Zeit", "Im Strom der Zeit", ignoreCase = true)
  .replace(" @ 320FM", " ", ignoreCase = true)
  .replace("Melt!", "Melt", ignoreCase = true)
  .replace("KLANGWELT.FM", "KLANGWELT", ignoreCase = true)
  .replace("Fusion Festival", "Fusion", ignoreCase = true)
  .replace("DJ-Set", "DJ Set", ignoreCase = true)
  .replace("My - My ", "My ", ignoreCase = true)
  .replace("Mendel", "Mendel", ignoreCase = true)
  .replace("BERKOFFSET", "Berk Offset", ignoreCase = true)
  .replace("VOICES OF VALLEY", "Voices Of Valley ", ignoreCase = true)
  .replace("Bergwacht", "BergWacht", ignoreCase = true)
  .replace("Resident Advisor Podcast", "Resident Advisor Podcast", ignoreCase = true)
  .replace("DAYS like NIGHTS", "DAYS like NIGHTS", ignoreCase = true)
  .replace("arutani", "Arutani", ignoreCase = true)
  .replace("Christian Loffler", "Christian Loeffler", ignoreCase = true)
  .replace("Daniele di Martino", "Daniele Di Martino", ignoreCase = true)
  .replace("Sonne, Strand Und Meer", "Sonne, Strand und Meer", ignoreCase = true)
  .replace("Sonne, Strand & Meer", "Sonne, Strand und Meer", ignoreCase = true)
  .replace("Acid Puli", "Acid Pauli", ignoreCase = true)
  .replace("BOILER ROOM", "Boiler Room", ignoreCase = true)
  .replace("Voodoofox", "voodoofox", ignoreCase = true)
  .replace("Katerblau", "Kater Blau", ignoreCase = true)
  .replace("werliebt podcast", "werliebt Podcast", ignoreCase = true)
  .replace(" Bloc. Londo", " London", ignoreCase = true)
  .replace("Rebirth Of Spring", "Rebirth of Spring", ignoreCase = true)
  .replace("MICROTRAUMA", "Microtrauma", ignoreCase = true)
  .replace("LAKE AVALON", "Lake Avalon", ignoreCase = true)
  .replace("MELLOW MOTION", "Mellow Motion", ignoreCase = true)
  .replace("Einmusik & Jonas Saalbach", "Einmusik b2b Jonas Saalbach", ignoreCase = true)
  .replace("EINMUSIK", "EINMUSIK", ignoreCase = true)
  .replace("EINMUSIKa Podcast", "Einmusika Podcast", ignoreCase = true)
  .replace("EINMUSIKa Radio Show", "Einmusika Radio Show", ignoreCase = true)
  .replace("EINMUSIKa Showcase", "Einmusika Showcase", ignoreCase = true)
  .replace("Ibiza Global Radio - Einmusika Radio Show", "Einmusika Radio Show", ignoreCase = true)
  .replace("FRANZ ALICE STERN", "Franz Alice Stern", ignoreCase = true)
  .replace("ALYNE", "Alyne", ignoreCase = false)
  .replace("Felde", "felde", ignoreCase = false)
  .replace("Nhxe", "Nixe", ignoreCase = false)
  .replace("Doyeq", "doyeq", ignoreCase = false)
  .replace("Arkadiusz", "arkadiusz  ", ignoreCase = false)
  .replace("Foot", "FooT", ignoreCase = false)
  .replace("Doob", "doob", ignoreCase = false)
  .replace("MOLO", "MOLØ", ignoreCase = false)
  .replace("Bloom", "Bloom", ignoreCase = false)
  .replace("Marc DePulse", "Marc DePulse", ignoreCase = true)
  .replace("Marc De Pulse", "Marc DePulse", ignoreCase = true)
  .replace("Prince of Denmark", "Prince of Denmark", ignoreCase = true)
  .replace("Sebastian Mullaert", "Sebastian Mullaert", ignoreCase = true)
  .replace("Township Rebellion", "Township Rebellion", ignoreCase = true)
  .replace("Wide Awake", "Wide Awake", ignoreCase = true)
  .replace("unueberlegt", "unueberlegt", ignoreCase = true)
  .replace("Jonas Saalbach & Tschoris", "Jonas Saalbach b2b Tschoris", ignoreCase = false)
  .replace("Ambients1_ ", "Ambients1 ", ignoreCase = true)
  .replace("sur*faces", "surfaces", ignoreCase = true)
  .replace("Christian Lffler", "Christian Löffler", ignoreCase = true)
  .replace(" - www.klangextase.de", "", ignoreCase = true)
  .replace(" (The lost Show)", "", ignoreCase = true)
  .replace("Nɨxe", "Nixe", ignoreCase = true)
  .replace("ᏗrutanᎥ", "Arutani", ignoreCase = true)
  .replace("Ƞɑʈure", "nature", ignoreCase = true)
  .replace("Ꮧru", "Aru", ignoreCase = true)
  .replace("Gun⁄lla", "Gunilla", ignoreCase = true)
  .replace("My set from Warm Up", "My Set From Warm Up", ignoreCase = true)
  .replace("presents_", "presents ", ignoreCase = true)
  .replace("Podcast*", "Podcast")
  .replace("*live", "live")
  .replace("_live", "live")
  .replace("Influenza* ", "Influenza ")
  .replace("2o15", "2015")
  // Normalize some abbreviations.
  .replace("Live ", "Live ", ignoreCase = true)
  .replace("Live at ", "Live @ ", ignoreCase = true)
  .replace(" w/ ", " with ")
  .replace("n°", "#", ignoreCase = true)
  .replace("Presents", "presents")
  .replace("Pres.", "presents")
  // Weird spacings / symbols usage.
  .replace("：", " -")
  .replace("-  ", "- ")
  .replace(" • ", " ")
  .replace(" - .mp3", ".mp3")
  .replace(" -.mp3", ".mp3")
  .replace(" .mp3", ".mp3")
  .replace(" / ", " - ")
  .replace("Deep session", "Deep Session")
  .replace(" // ", " ")
  .replace(" \" ", " ")
  .replace(" _ ", " ")
  .replace("｜", " - ")
  .replace(" _ .mp3", ".mp3")
  .replace(" _.mp3", ".mp3")
  .replace(" w_.mp3", ".mp3")
  .replace(" www..mp3", ".mp3")
  .replace(" x ", " & ")
  .replace(" — ", " ")
  .replace(" — .mp3", ".mp3")
  .replace(" —.mp3", ".mp3")
  .replace("\"\"", "")
  .replace("|", "|")
  .replace("Café", "Cafe")
  // Fix some accents.
  .replace("""é""", """é""")
  .replace("""è""", """è""")
  // Remove invalid characters.
  .replace("!", "")
  .replace("~", "")
  .replace("❖", "")
  .replace("+", "")
  .replace("☆", "")
  .replace("�", "")
  .replace("""🌀""", "")
  .map {
    when (it) {
      // Normal characters.
      in 'a'..'z', in 'A'..'Z', in '0'..'9' -> it.toString()
      // Special.
      ' ', '.', '#', '-', ',', '@', '&', '\'', '[', ']' -> it.toString()
      // Special umlauts.
      'ú', 'ç', 'ã', 'â', 'ë', 'Î', 'Â', 'Ø', 'é', 'è', 'á', 'Ó', 'š', 'ø', 'í', 'ó', 'ß', 'Ф', 'у', 'з', 'и', 'о', 'н', 'ē', 'ô' -> it.toString()
      // Autocorrect some.
      'ä' -> "ae"
      'ö' -> "oe"
      'ü' -> "ue"
      'Ä' -> "Ae"
      'Ö' -> "Oe"
      'Ü' -> "Ue"
      '_', '–', '─' -> "-"
      '´', '’' -> '\''
      // Ignore them.
      '(', ')' -> ""
      else -> throw RecoverableException("""Invalid "$it" (code=${it.code}) in "$this"""")
    }
  }
  .joinToString(separator = "")
  .replace(REGEX_DOUBLE_SPACINGS, " ")
  .trim()
