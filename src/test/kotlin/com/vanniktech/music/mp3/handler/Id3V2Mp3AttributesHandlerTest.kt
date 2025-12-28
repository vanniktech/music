package com.vanniktech.music.mp3.handler

import com.vanniktech.music.mp3.Mp3Attribute
import com.vanniktech.music.mp3.Mp3Tag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

internal class Id3V2Mp3AttributesHandlerTest {
  private val mp3Handler = Id3V2Mp3AttributesHandler()

  @Test fun empty() {
    assertEquals(
      expected = null,
      actual = mp3Handler.fromString(
        """
        |Acid Pauli x Viken Arman -  Journey to Inaccessible Places @ RAMBALKOSHE.mp3: No ID3 tag
        |
        """.trimMargin(),
      ),
    )
  }

  @Test fun failsOnUnknown() {
    try {
      mp3Handler.fromString(
        """
        id3v2 tag info for live│2022.mp3:
        TFLT (File Type): MP3
        """.trimIndent(),
      )
      fail("Should have thrown")
    } catch (throwable: Throwable) {
      assertEquals(expected = "Unknown tag TFLT", actual = throwable.message)
    }
  }

  @Test fun reversedIdTags() {
    assertEquals(
      expected = listOf(
        Mp3Attribute(Mp3Tag.ARTIST, "Frieder", inferred = false),
        Mp3Attribute(Mp3Tag.TITLE, "live│2022", inferred = false),
        Mp3Attribute(Mp3Tag.SUBTITLE, null, inferred = false),
        Mp3Attribute(Mp3Tag.ALBUM, null, inferred = false),
        Mp3Attribute(Mp3Tag.TRACK, null, inferred = false),
        Mp3Attribute(Mp3Tag.GENRE, "Different", inferred = false),
        Mp3Attribute(Mp3Tag.YEAR, "2022", inferred = false),
        Mp3Attribute(Mp3Tag.PICTURE, null, inferred = false),
        Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
        Mp3Attribute(Mp3Tag.POSITION, null, inferred = false),
        Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
        Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        Mp3Attribute(Mp3Tag.CUSTOM_TAG, null, inferred = false),
        Mp3Attribute(Mp3Tag.ENCODER_SETTINGS, null, inferred = false),
      ),
      actual = mp3Handler.fromString(
        """
        id3v2 tag info for live│2022.mp3:
        TPE1 (Lead performer(s)/Soloist(s)): Frieder
        TIT2 (Title/songname/content description): live│2022
        TYER (Year): 2022
        TCON (Content type): Different (255)
        id3v1 tag info for live│2022.mp3:
        Title  : live│2022                     Artist: Frieder
        Album  :                                 Year: 2022, Genre: Unknown (255)
        Comment:                                 Track: 0
        """.trimIndent(),
      ),
    )
  }

  @Test fun withImage() {
    assertEquals(
      expected = listOf(
        Mp3Attribute(Mp3Tag.ARTIST, "BlueMoon", inferred = false),
        Mp3Attribute(Mp3Tag.TITLE, "Progressive - Prog", inferred = false),
        Mp3Attribute(Mp3Tag.SUBTITLE, null, inferred = false),
        Mp3Attribute(Mp3Tag.ALBUM, "Progressive", inferred = false),
        Mp3Attribute(Mp3Tag.TRACK, "0/0", inferred = false),
        Mp3Attribute(Mp3Tag.GENRE, "Progressive", inferred = false),
        Mp3Attribute(Mp3Tag.YEAR, "2022", inferred = false),
        Mp3Attribute(Mp3Tag.PICTURE, "image/jpeg, 11396 bytes", inferred = false),
        Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
        Mp3Attribute(Mp3Tag.POSITION, "0/0", inferred = false),
        Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
        Mp3Attribute(Mp3Tag.COMMENTS, null, inferred = false),
        Mp3Attribute(Mp3Tag.CUSTOM_TAG, null, inferred = false),
        Mp3Attribute(Mp3Tag.ENCODER_SETTINGS, null, inferred = false),
      ),
      actual = mp3Handler.fromString(
        """
        id3v1 tag info for BlueMoon - Progressive - Prog.mp3:
        Title  : Progressive - Prog              Artist: BlueMoon
        Album  : Progressive                     Year: 2022, Genre: Unknown (255)
        Comment:                                 Track: 0
        id3v2 tag info for BlueMoon - Progressive - Prog.mp3:
        TPOS (Part of a set): 0/0
        APIC (Attached picture): ()[, 3]: image/jpeg, 11396 bytes
        PRIV (Private frame):  (unimplemented)
        TPE1 (Lead performer(s)/Soloist(s)): BlueMoon
        TIT2 (Title/songname/content description): Progressive - Prog
        TALB (Album/Movie/Show title): Progressive
        TRCK (Track number/Position in set): 0/0
        TCON (Content type): Progressive (255)
        TYER (Year): 2022
        """.trimIndent(),
      ),
    )
  }

  @Test fun differentAttributes() {
    assertEquals(
      expected = listOf(
        Mp3Attribute(Mp3Tag.ARTIST, "A.D.H.S.", inferred = false),
        Mp3Attribute(Mp3Tag.TITLE, "Grillen auf der Dachterrasse", inferred = false),
        Mp3Attribute(Mp3Tag.SUBTITLE, null, inferred = false),
        Mp3Attribute(Mp3Tag.ALBUM, null, inferred = false),
        Mp3Attribute(Mp3Tag.TRACK, "0/0", inferred = false),
        Mp3Attribute(Mp3Tag.GENRE, "Oldschool", inferred = false),
        Mp3Attribute(Mp3Tag.YEAR, "2014", inferred = false),
        Mp3Attribute(Mp3Tag.PICTURE, "image/jpeg, 133941 bytes", inferred = false),
        Mp3Attribute(Mp3Tag.COMPOSER, null, inferred = false),
        Mp3Attribute(Mp3Tag.POSITION, "0/0", inferred = false),
        Mp3Attribute(Mp3Tag.ARTIST_2, null, inferred = false),
        Mp3Attribute(Mp3Tag.COMMENTS, "F�r die Ewigwachgebliebenen", inferred = false),
        Mp3Attribute(Mp3Tag.CUSTOM_TAG, null, inferred = false),
        Mp3Attribute(Mp3Tag.ENCODER_SETTINGS, null, inferred = false),
      ),
      actual = mp3Handler.fromString(
        """
        id3v2 tag info for A.D.H.S. - Grillen auf der Dachterrasse.mp3:
        TIT2 (Title/songname/content description): Grillen auf der Dachterrasse
        TPE1 (Lead performer(s)/Soloist(s)): A.D.H.S.
        TYER (Year): 2014
        TRCK (Track number/Position in set): 0/0
        TPOS (Part of a set): 0/0
        TCON (Content type): Oldschool (255)
        APIC (Attached picture): ()[, 3]: image/jpeg, 133941 bytes
        PRIV (Private frame):  (unimplemented)
        COMM (Comments): (ID3v1 Comment)[XXX]: F�r die Ewigwachgebliebenen
        A.D.H.S. - Grillen auf der Dachterrasse.mp3: No ID3v1 tag
        """.trimIndent(),
      ),
    )
  }
}
