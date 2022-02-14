package io.github.mslxl.xmusic.desktop.i18n

import io.github.mslxl.xmusic.common.i18n.I18NKey
import io.github.mslxl.xmusic.common.i18n.I18NLocalCode

val desktopI18nRes: Map<I18NLocalCode, () -> List<Pair<I18NKey, String>>> = mapOf(
        "zh" to {
            listOf(
                    "title" to "XMusic",
                    "sidebar.my" to "我的",
                    "sidebar.discovery" to "发现",
                    "sidebar.setting" to "设置",
                    "tab.about" to "关于",
                    "my.my" to "我的收藏",
                    "unsupported" to "%s: 暂不支持(但是会有的，咕)",
                    "detail.play" to "播放",
                    "detail.fav" to "收藏",
                    "detail.share" to "分享",
            )
        },
        "en" to {
            listOf(
                    "title" to "XMusic",
                    "sidebar.mine" to "Mine",
                    "sidebar.discovery" to "Discovery",
                    "sidebar.setting" to "Settings",
                    "tab.about" to "About",
                    "my.my" to "My Fav",
                    "unsupported" to "Unsupported %s but coming soon",
                    "detail.play" to "Play",
                    "detail.fav" to "Fav",
                    "detail.share" to "Share",
            )
        }
)
