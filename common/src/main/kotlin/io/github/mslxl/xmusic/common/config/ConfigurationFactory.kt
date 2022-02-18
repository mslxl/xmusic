package io.github.mslxl.xmusic.common.config

import io.github.mslxl.xmusic.common.addon.SourceID
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class ConfigurationFactory {
    private val exposedKey = HashMap<String, Type>()
    private val exposedKeyDefaultValue = HashMap<String, Any>()

    fun expose(key: String, type: Type): ConfigurationFactory {
        exposedKey[key] = type
        return this
    }

    fun expose(key: String, type: Type, default: Any): ConfigurationFactory {
        expose(key, type)
        exposedKeyDefaultValue[key] = default
        return this
    }

    fun build(id: SourceID) = Configuration(id, exposedKey, exposedKeyDefaultValue)

    fun <T : UserConfiguration> buildFrom(id: SourceID, clazz: KClass<T>): T {
        val exposed = clazz.members.filter { it.hasAnnotation<Expose>() }
        exposed.map { it.name to it.findAnnotation<Expose>()!! }.map {
            exposedKey[it.first] = it.second.type
        }
        exposed.filter { it.hasAnnotation<Default>() }.map {
            it.name to it.findAnnotation<Default>()!!
        }.forEach {
            exposedKeyDefaultValue[it.first] = it.second.value
        }
        val config = build(id)
        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(this::class.java.classLoader, arrayOf(clazz.java), UserConfigurationProxyAdapter(config)) as T
    }

    inline fun <reified T : UserConfiguration> buildFrom(id: SourceID): T {
        return buildFrom(id, T::class)
    }
}