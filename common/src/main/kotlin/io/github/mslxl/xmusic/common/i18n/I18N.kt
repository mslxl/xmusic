package io.github.mslxl.xmusic.common.i18n

typealias I18NKey = String
typealias I18NPair = Pair<I18NKey, String>
typealias I18NLocalCode = String

interface I18N {
    val id: String
    val i18n: Map<I18NLocalCode, () -> List<Pair<I18NKey, String>>>
        get() = emptyMap()
}

