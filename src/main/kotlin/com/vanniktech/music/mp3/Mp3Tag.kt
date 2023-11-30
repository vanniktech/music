package com.vanniktech.music.mp3

/** https://id3.org/id3v2.3.0 */
enum class Mp3Tag(
  val id: String,
) {
  /** Attributes we want. */
  ARTIST(id = "TPE1"),
  TITLE(id = "TIT2"),
  SUBTITLE(id = "TIT3"),
  ALBUM(id = "TALB"),
  TRACK(id = "TRCK"),
  GENRE(id = "TCON"),
  YEAR(id = "TYER"),
  PICTURE(id = "APIC"),

  /** Attributes we do not want. */
  COMPOSER(id = "TCOM"),
  POSITION(id = "TPOS"),
  ARTIST_2(id = "TPE2"),
  COMMENTS(id = "COMM"),

  /** We know about this, but we don't parse it. */
  PRIVATE_FRAME(id = "PRIV"),
  ;

  override fun toString() =
    name.lowercase()

  companion object {
    fun parseable() = values().toList().minus(PRIVATE_FRAME)
    fun fromIdOrNull(id: String?) = values().firstOrNull { it.id == id }
  }
}
