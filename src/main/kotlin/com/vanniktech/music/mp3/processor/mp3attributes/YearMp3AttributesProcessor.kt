package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.modifyAttributes
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.map
import kotlinx.datetime.LocalDate

internal class YearMp3AttributesProcessor(
  private val localDate: LocalDate,
  private val mandatory: Boolean,
) : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes = attributes.map(Mp3Tag.YEAR) { year ->
    val isMissing = year.value.asYear() == null && mandatory

    if (isMissing) {
      modifyAttributes(
        source = source,
        attributes = listOf(year),
        allAttributes = attributes,
        mandatory = true,
      )
    } else {
      year
    }
  }

  private fun String?.asYear() = this?.toIntOrNull()?.takeIf { it in 1995..localDate.year }
}
