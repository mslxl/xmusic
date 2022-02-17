package io.github.mslxl.xmusic.common.manager

import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.SourceID
import io.github.mslxl.xmusic.common.addon.XMusicEventRegister
import io.github.mslxl.xmusic.common.events.XMusicEvent
import io.github.mslxl.xmusic.common.logger
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

object AddonsMan {
    private val logger = this::class.logger
    private val registeredAddons = HashSet<KClass<MusicSource>>()

    private val addonsMutable = HashMap<SourceID, MusicSource>()
    val addons get() = addonsMutable.toMap()


    private val eventReceivers = HashMap<KClass<*>, HashSet<Pair<SourceID, KCallable<*>>>>()

    fun <T : MusicSource> register(addon: KClass<T>) {
        val instance = addon.createInstance()
        addonsMutable[instance.id] = instance

        logger.info("register addon ${addon.qualifiedName}")
        val members = addon.members
        // Find all function that have [XMusicEventRegister] modified
        // and add it to eventReceivers.
        for (elem in members) {
            elem.findAnnotation<XMusicEventRegister>()?.let {
                val eventType = elem.parameters.lastOrNull()?.type?.classifier as? KClass<*>
                if (eventType?.isSubclassOf(XMusicEvent::class) == true) {
                    logger.info("register ${elem.name} as event ${eventType.qualifiedName}")
                    eventReceivers.getOrPut(eventType) { HashSet() }.add(instance.id to elem)
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        registeredAddons.add(addon as KClass<MusicSource>)
    }

    fun <T : XMusicEvent<*>> sentEvent(event: T, clazz: KClass<T>) {
        logger.info("send event $event")
        eventReceivers[clazz]?.let {
            it.forEach { eventReceiver ->
                eventReceiver.second.call(getInstance(eventReceiver.first), event)
            }
        }
    }


    inline fun <reified T : XMusicEvent<*>> sentEvent(event: T) {
        sentEvent(event, T::class)
    }

    fun <T> getInstance(id: SourceID): T? {
        @Suppress("UNCHECKED_CAST") return addons[id] as? T
    }

    inline fun <reified T> getInstance(): T? {
        return addons.values.find { T::class == it::class } as T
    }
}