package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.REGEX_DOUBLE_SPACINGS
import com.vanniktech.music.cleanTrack
import com.vanniktech.music.modifyAttributes
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import com.vanniktech.music.mp3.albumYearly
import com.vanniktech.music.mp3.albumsWithTracks
import com.vanniktech.music.mp3.albumsWithoutTracks
import com.vanniktech.music.mp3.map
import com.vanniktech.music.prependTrack
import com.vanniktech.music.specialContains
import com.vanniktech.music.takeIfNotBlank
import com.vanniktech.music.track
import com.vanniktech.music.trim

internal class TitleMismatchMp3AttributesProcessor : Mp3AttributesProcessor {
  override fun process(source: Source, attributes: Mp3Attributes): Mp3Attributes =
    attributes.map(Mp3Tag.TITLE) { title ->
      val artist = attributes.get(Mp3Tag.ARTIST)
      val album = attributes.get(Mp3Tag.ALBUM)
      val track = attributes.get(Mp3Tag.TRACK)
      val year = attributes.get(Mp3Tag.YEAR).value

      val viaAlbum = (albumsWithTracks + albumsWithoutTracks).firstNotNullOfOrNull { value ->
        value.takeIf {
          title.value?.specialContains(it) == true
        }
      }
      val viaAlbumYearly = albumYearly.firstNotNullOfOrNull { value ->
        "$value $year".takeIf {
          val regex = Regex(it)
          regex.containsMatchIn(title.value.orEmpty())
        }
      }

      val isDifferentViaLive = source.name.contains("live", ignoreCase = true) && !source.name.contains("oliver", ignoreCase = true) && !source.name.contains("Elliver", ignoreCase = true) && title.value?.contains("live", ignoreCase = true) == false
      val isDifferentViaTrack = !track.value.isNullOrBlank() && track.value != title.value?.track()?.cleanTrack()
      val isDifferentViaAlbum = (viaAlbum ?: viaAlbumYearly) != album.value
      val containsArtist = title.value?.contains(artist.value!!, ignoreCase = true) == true && artist.value != "Places" && artist.value != "Solstice"
      val doesNotContainAlbumTrack = when {
        album.value != null && !track.value.isNullOrBlank() -> title.value?.contains("${album.value} ${track.value.prependTrack()}") == false
        else -> false
      }

      val isDifferent = (containsArtist || isDifferentViaLive || isDifferentViaAlbum || isDifferentViaTrack || doesNotContainAlbumTrack)

      if (isDifferent) {
        val inferredTitle = title.value.takeIf { isDifferentViaLive && album.value == null }
        val value = (
          listOfNotNull(
            "Live -".takeIf { inferredTitle != null } ?: "Live @".takeIf { isDifferentViaLive },
            album.value,
            track.value?.prependTrack(),
            inferredTitle,
          )
            .joinToString(separator = " ")
            .takeIfNotBlank() ?: title.value.orEmpty().trim(artist.value.orEmpty())
          )
          .trim()
          .trim("&")
          .trim("-")
          .trim("@")
          .replace(artist.value.orEmpty(), "", ignoreCase = true)
          .replace(REGEX_DOUBLE_SPACINGS, " ")
          .trim("at")
          .trim("by")
          .trim("von")

        modifyAttributes(
          source = source,
          attributes = listOf(title.copy(value = value, inferred = true)),
          allAttributes = attributes,
          mandatory = true,
        )
      } else {
        title
      }
    }
}
