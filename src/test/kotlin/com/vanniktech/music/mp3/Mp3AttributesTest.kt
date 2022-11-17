package com.vanniktech.music.mp3

import kotlin.test.Test
import kotlin.test.assertEquals

internal class Mp3AttributesTest {
  private val empty = Mp3Attributes(emptyList())

  private val artistNik = Mp3Attribute(Mp3Tag.ARTIST, "Nik", inferred = false)
  private val artistAlex = Mp3Attribute(Mp3Tag.ARTIST, "Alex", inferred = false)
  private val privateFrameSomething = Mp3Attribute(Mp3Tag.PRIVATE_FRAME, "Something", inferred = false)

  @Test fun empty() {
    assertEquals(
      expected = emptyList(),
      actual = diff(
        self = empty,
        other = empty,
      ),
    )
  }

  @Test fun adding() {
    assertEquals(
      expected = listOf(Mp3AttributeDiff(Mp3Tag.ARTIST, null, "Nik")),
      actual = diff(
        self = empty,
        other = Mp3Attributes(listOf(artistNik)),
      ),
    )
  }

  @Test fun changing() {
    assertEquals(
      expected = listOf(Mp3AttributeDiff(Mp3Tag.ARTIST, "Alex", "Nik")),
      actual = diff(
        self = Mp3Attributes(listOf(artistAlex)),
        other = Mp3Attributes(listOf(artistNik)),
      ),
    )
  }

  @Test fun removing() {
    assertEquals(
      expected = listOf(Mp3AttributeDiff(Mp3Tag.ARTIST, "Nik", null)),
      actual = diff(
        self = Mp3Attributes(listOf(artistNik)),
        other = empty,
      ),
    )
  }

  @Test fun ignorePrivateFrame() {
    assertEquals(
      expected = emptyList(),
      actual = diff(
        self = Mp3Attributes(listOf(privateFrameSomething)),
        other = empty,
      ),
    )
  }
}
