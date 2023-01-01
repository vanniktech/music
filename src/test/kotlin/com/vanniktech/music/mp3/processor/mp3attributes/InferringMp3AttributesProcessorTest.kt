package com.vanniktech.music.mp3.processor.mp3attributes

import com.vanniktech.music.mp3.Mp3Attribute
import com.vanniktech.music.mp3.Mp3Attributes
import com.vanniktech.music.mp3.Mp3Tag
import com.vanniktech.music.mp3.Source
import org.junit.Test
import kotlin.test.assertEquals

class InferringMp3AttributesProcessorTest {
  private val processor = InferringMp3AttributesProcessor()

  @Test fun empty() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, null, inferred = false),
          Mp3Attribute(Mp3Tag.TITLE, null, inferred = false),
          Mp3Attribute(Mp3Tag.ALBUM, null, inferred = false),
          Mp3Attribute(Mp3Tag.TRACK, null, inferred = false),
          Mp3Attribute(Mp3Tag.GENRE, null, inferred = false),
          Mp3Attribute(Mp3Tag.YEAR, null, inferred = false),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource(""),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  @Test fun base() {
    val base = Mp3Attributes(Mp3Tag.parseable().map { Mp3Attribute(it, "Fallback", inferred = false) })

    assertEquals(
      expected = base,
      actual = processor.process(
        source = FakeSource(""),
        attributes = base,
      ),
    )
  }

  @Test fun titleOnly() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "Live", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "Live 2022", inferred = true),
          Mp3Attribute(Mp3Tag.ALBUM, null, inferred = false),
          Mp3Attribute(Mp3Tag.TRACK, null, inferred = false),
          Mp3Attribute(Mp3Tag.GENRE, null, inferred = false),
          Mp3Attribute(Mp3Tag.YEAR, "2022", inferred = true),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("live 2022"),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  @Test fun baseTakesPrecedence() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "live", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "base", inferred = false),
          Mp3Attribute(Mp3Tag.ALBUM, null, inferred = false),
          Mp3Attribute(Mp3Tag.TRACK, null, inferred = false),
          Mp3Attribute(Mp3Tag.GENRE, null, inferred = false),
          Mp3Attribute(Mp3Tag.YEAR, "2022", inferred = true),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("live-2022"),
        attributes = Mp3Attributes(
          listOf(
            Mp3Attribute(Mp3Tag.TITLE, "base", inferred = false),
          ),
        ),
      ),
    )
  }

  @Test fun simple() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "Nils Hoffmann", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "Watergate Berlin", inferred = true),
          Mp3Attribute(Mp3Tag.ALBUM, "Watergate Berlin", inferred = true),
          Mp3Attribute(Mp3Tag.TRACK, null, inferred = false),
          Mp3Attribute(Mp3Tag.GENRE, null, inferred = false),
          Mp3Attribute(Mp3Tag.YEAR, null, inferred = false),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("Nils Hoffmann - Watergate Berlin"),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  @Test fun backToBack() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "Acid Pauli & Viken Arman", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "Journey to Inaccessible Places @ RAMBALKOSHE", inferred = true),
          Mp3Attribute(Mp3Tag.ALBUM, null, inferred = false),
          Mp3Attribute(Mp3Tag.TRACK, null, inferred = false),
          Mp3Attribute(Mp3Tag.GENRE, null, inferred = false),
          Mp3Attribute(Mp3Tag.YEAR, null, inferred = false),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("Acid Pauli & Viken Arman - Journey to Inaccessible Places @ RAMBALKOSHE"),
        attributes = Mp3Attributes(
          listOf(
            Mp3Attribute(Mp3Tag.ARTIST, null, inferred = false),
          ),
        ),
      ),
    )
  }

  @Test fun festival() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "Miyagi", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "Saturday Night Closing Dance", inferred = true),
          Mp3Attribute(Mp3Tag.ALBUM, "Skyland 2022", inferred = true),
          Mp3Attribute(Mp3Tag.TRACK, null, inferred = false),
          Mp3Attribute(Mp3Tag.GENRE, null, inferred = false),
          Mp3Attribute(Mp3Tag.YEAR, "2022", inferred = true),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("Miyagi - Skyland 2022 - Saturday Night Closing Dance"),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  @Test fun heimlichPodcastNumberOne() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "Oberst & Buchner", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "Heimlich Podcast #1", inferred = true),
          Mp3Attribute(Mp3Tag.ALBUM, "Heimlich Podcast", inferred = true),
          Mp3Attribute(Mp3Tag.TRACK, "1", inferred = true),
          Mp3Attribute(Mp3Tag.GENRE, null, inferred = false),
          Mp3Attribute(Mp3Tag.YEAR, null, inferred = false),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("Heimlich Podcast #1 by Oberst & Buchner\n"),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  @Test fun gabrielAnanda() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "Gabriel Ananda", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "Mees Salome", inferred = true),
          Mp3Attribute(Mp3Tag.ALBUM, null, inferred = false),
          Mp3Attribute(Mp3Tag.TRACK, "72", inferred = true),
          Mp3Attribute(Mp3Tag.GENRE, "Techno", inferred = true),
          Mp3Attribute(Mp3Tag.YEAR, null, inferred = false),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("Gabriel Ananda - Gabriel Ananda presents Soulful Techno 72 - Mees Salome"),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  @Test fun longName() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, null, inferred = false),
          Mp3Attribute(Mp3Tag.TITLE, null, inferred = false),
          Mp3Attribute(Mp3Tag.ALBUM, null, inferred = false),
          Mp3Attribute(Mp3Tag.TRACK, null, inferred = false),
          Mp3Attribute(Mp3Tag.GENRE, null, inferred = false),
          Mp3Attribute(Mp3Tag.YEAR, null, inferred = false),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("Sainte Vie - Live Set - The Residency with...WhoMadeWho - Episode 4  @Beatport Live"),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  @Test fun cercle() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "Teho", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "Teho Live Colorado Provencal In Rustrel France For Cercle", inferred = true),
          Mp3Attribute(Mp3Tag.ALBUM, "Cercle", inferred = true),
          Mp3Attribute(Mp3Tag.TRACK, null, inferred = false),
          Mp3Attribute(Mp3Tag.GENRE, null, inferred = false),
          Mp3Attribute(Mp3Tag.YEAR, null, inferred = false),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("Teho Live Colorado Provencal In Rustrel France For Cercle"),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  @Test fun metanoiaRythm() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "Eli", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "metanoia RHYTHM #028", inferred = true),
          Mp3Attribute(Mp3Tag.ALBUM, "metanoia RHYTHM", inferred = true),
          Mp3Attribute(Mp3Tag.TRACK, "28", inferred = true),
          Mp3Attribute(Mp3Tag.GENRE, null, inferred = false),
          Mp3Attribute(Mp3Tag.YEAR, null, inferred = false),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("metanoia RHYTHM #028 - Eli"),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  @Test fun podcast() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "Janoma", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "Mystic Cast #14", inferred = true),
          Mp3Attribute(Mp3Tag.ALBUM, "Mystic Cast", inferred = true),
          Mp3Attribute(Mp3Tag.TRACK, "14", inferred = true),
          Mp3Attribute(Mp3Tag.GENRE, null, inferred = false),
          Mp3Attribute(Mp3Tag.YEAR, null, inferred = false),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("Mystic Cast #14  Janoma"),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  @Test fun withGenre() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "SES", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "SES", inferred = true),
          Mp3Attribute(Mp3Tag.ALBUM, "Melodays 2017", inferred = true),
          Mp3Attribute(Mp3Tag.TRACK, null, inferred = false),
          Mp3Attribute(Mp3Tag.GENRE, "Mindfuck Hypno", inferred = true),
          Mp3Attribute(Mp3Tag.YEAR, "2017", inferred = true),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("Mindfuck Hypno - SES - Melodays 2017Melodays 2017 - SES"),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  @Test fun futureStars() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "unueberlegt", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "Spring Awakening", inferred = true),
          Mp3Attribute(Mp3Tag.ALBUM, "trndmsk Future Stars", inferred = true),
          Mp3Attribute(Mp3Tag.TRACK, "6", inferred = true),
          Mp3Attribute(Mp3Tag.GENRE, null, inferred = false),
          Mp3Attribute(Mp3Tag.YEAR, null, inferred = false),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("trndmsk Future Stars #6 - unueberlegt - Spring Awakening"),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  @Test fun genreSpacyIndian() {
    assertEquals(
      expected = Mp3Attributes(
        listOf(
          Mp3Attribute(Mp3Tag.ARTIST, "Milo Haefliger", inferred = true),
          Mp3Attribute(Mp3Tag.TITLE, "The Awakening", inferred = true),
          Mp3Attribute(Mp3Tag.ALBUM, null, inferred = false),
          Mp3Attribute(Mp3Tag.TRACK, null, inferred = false),
          Mp3Attribute(Mp3Tag.GENRE, "Spacy Indian", inferred = true),
          Mp3Attribute(Mp3Tag.YEAR, null, inferred = false),
          Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
          Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
          Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
          Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        ),
      ),
      actual = processor.process(
        source = FakeSource("Spacy Indian - Milo Haefliger - The Awakening"),
        attributes = Mp3Attributes(emptyList()),
      ),
    )
  }

  internal class FakeSource(override val name: String) : Source
}
