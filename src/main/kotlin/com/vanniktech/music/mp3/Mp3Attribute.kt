package com.vanniktech.music.mp3

@JvmInline internal value class Mp3Attributes(
  private val attributes: List<Mp3Attribute>,
) : Collection<Mp3Attribute> by attributes {
  fun get(tag: Mp3Tag) = requireNotNull(getOrNull(tag)) { "Can't find $tag in $attributes" }
  fun getOrNull(tag: Mp3Tag) = firstOrNull { it.tag == tag }
}

internal fun diff(self: Mp3Attributes, other: Mp3Attributes): List<Mp3AttributeDiff> = Mp3Tag.parseable().mapNotNull {
  val selfAttribute = self.getOrNull(it)?.copy(inferred = false)
  val otherAttribute = other.getOrNull(it)?.copy(inferred = false)

  if (selfAttribute != otherAttribute) {
    Mp3AttributeDiff(
      tag = it,
      old = selfAttribute?.value,
      new = otherAttribute?.value,
    )
  } else {
    null
  }
}

internal data class Mp3AttributeDiff(
  val tag: Mp3Tag,
  val old: String?,
  val new: String?,
) {
  override fun toString() =
    "$tag from \"$old\" to \"$new\""
}

internal data class Mp3Attribute(
  val tag: Mp3Tag,
  val value: String?,
  val inferred: Boolean,
) {
  override fun toString(): String = "$tag=$value" + when (inferred) {
    true -> " (inferred)"
    else -> ""
  }
}

internal fun Mp3Attributes.map(tag: Mp3Tag, function: ((Mp3Attribute) -> Mp3Attribute)): Mp3Attributes =
  Mp3Attributes(
    map {
      when (it.tag) {
        tag -> function.invoke(it)
        else -> it
      }
    },
  )
