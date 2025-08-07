package com.vanniktech.music

import kotlin.test.Test
import kotlin.test.assertEquals

internal class UtilsTest {
  @Test fun extractImageUrlFromString() {
    assertEquals(
      expected = null,
      actual = extractImageUrlFromString("foo"),
    )

    assertEquals(
      expected = "https://i1.sndcdn.com/artworks-tdCceW5y1FlKzBkI-zWjPOA-t1080x1080.jpg",
      actual = extractImageUrlFromString("https://i1.sndcdn.com/artworks-tdCceW5y1FlKzBkI-zWjPOA-t1080x1080.jpg"),
    )

    assertEquals(
      expected = "https://i1.sndcdn.com/artworks-tdCceW5y1FlKzBkI-zWjPOA-t1080x1080.jpg",
      actual = extractImageUrlFromString("background-image: url(&quot;https://i1.sndcdn.com/artworks-tdCceW5y1FlKzBkI-zWjPOA-t1080x1080.jpg&quot;); opacity: 1;"),
    )
  }

  @Test fun prependTrack() {
    assertEquals(expected = "#001", actual = "1".prependTrack())
    assertEquals(expected = "#010", actual = "10".prependTrack())
    assertEquals(expected = "#100", actual = "100".prependTrack())
    assertEquals(expected = null, actual = "".prependTrack())
  }

  @Test fun track() {
    assertEquals(expected = null, actual = "Todo102 - BergWacht Artheater Cologne 07.05.2016".track()?.cleanTrack())
    assertEquals(expected = null, actual = "A Fortego Heartfeels Radioshow 10dens Guest Mix".track()?.cleanTrack())
    assertEquals(expected = "10", actual = "Places #10".track()?.cleanTrack())
    assertEquals(expected = "113", actual = "Montagssorbet 113 mit Laut & Luise".track()?.cleanTrack())
    assertEquals(expected = "268", actual = "8dayCast 268".track()?.cleanTrack())
    assertEquals(expected = "4", actual = "1979 - KLANGWELT.FM(004)004 KLANGWELT.FM invite".track()?.cleanTrack())
    assertEquals(expected = "14", actual = "ABÎME - MELODIC DEEP IN DEPTH PODCAST(014)MELOD".track()?.cleanTrack())
    assertEquals(expected = "4", actual = "004 KLANGWELT.FM invites 1979".track()?.cleanTrack())
    assertEquals(expected = "28", actual = "Aaryon - Global Transmission EP 028 II".track()?.cleanTrack())
    assertEquals(expected = "24", actual = "Behind LOKD Doors 24".track()?.cleanTrack())
    assertEquals(expected = "94", actual = "FWD94".track()?.cleanTrack())
    assertEquals(expected = null, actual = "Springfestival Graz 2020 Dachstein Glacier".track()?.cleanTrack())
    assertEquals(expected = null, actual = "Colyn @ Audio Obscura Revere Series at Scheepvaartmuseum, 16.06.2020.mp3".track()?.cleanTrack())
    assertEquals(expected = null, actual = "UFO Vorfall 27-4 Rayk's Session".track()?.cleanTrack())
  }

  @Test fun correctedName() {
    listOf(
      "Places #10 – Skyline.mp3 " to "Places #10 - Skyline.mp3",
      "Miyagi – Skyland 2022 - Saturday Night Closing Dance.mp3" to "Miyagi - Skyland 2022 - Saturday Night Closing Dance.mp3",
      "Places #11 – Terminal.mp3" to "Places #11 - Terminal.mp3",
      "Places #6 – Boat.mp3" to "Places #6 - Boat.mp3",
      "Stimming At Theater Rüsselsheim Feb 22.mp3" to "Stimming At Theater Ruesselsheim Feb 22.mp3",
      "Solee @ Fusion Festival 2022  Turmbühne Opening Full set with live ambience.mp3" to "Solee @ Fusion 2022 Turmbuehne Opening Full set with Live ambience.mp3",
      "Places #5 – Airport.mp3" to "Places #5 - Airport.mp3",
      "SVT–Podcast107 - Sainte Vie DJ-Set.mp3" to "SVT-Podcast107 - Sainte Vie DJ Set.mp3",
      "Places #1 – Halloween.mp3" to "Places #1 - Halloween.mp3",
      "Places #24 – Woods.mp3" to "Places #24 - Woods.mp3",
      "Milo Häfliger live recording @ Gravity Fantasy Land Tunisia   27 May 2022.mp3" to "Milo Haefliger Live recording @ Gravity Fantasy Land Tunisia 27 May 2022.mp3",
      "Für Dich Mix.mp3" to "Fuer Dich Mix.mp3",
      "Frau Blau Radio #026 ─ Stimming live.mp3" to "Frau Blau Radio #026 - Stimming live.mp3",
      "Ólafur Arnalds & Nils Frahm live improvisation at Roter Salon - Volksbühne Berlin.mp3" to "Ólafur Arnalds & Nils Frahm Live improvisation at Roter Salon - Volksbuehne Berlin.mp3",
      "Gabriel Ananda Presents Soulful Techno 111" to "Gabriel Ananda presents Soulful Techno 111",
    ).forEach { (input, output) ->
      assertEquals(
        expected = output,
        actual = input.autoCorrected(),
      )
    }
  }
}
