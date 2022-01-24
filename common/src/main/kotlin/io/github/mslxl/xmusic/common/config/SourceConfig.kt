package io.github.mslxl.xmusic.common.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.mslxl.xmusic.common.fs.FileSystem
import io.github.mslxl.xmusic.common.source.SourceID
import java.util.*

open class SourceConfig(internal val fs: FileSystem, val id: SourceID) {
    sealed class ItemType {
        object TEXT : ItemType()
        object PASSWORD : ItemType()
    }

    class ItemIndex(val key: String, val name: String, val type: ItemType)

    var gson: Gson = GsonBuilder().serializeNulls().serializeNulls().create()
    open var properties = Properties().apply {
        fs.openInputStream(listOf("src_cfg"), "$id.properties").bufferedReader().use {
            load(it)
        }
    }
        internal set

    /**
     * itemType: HashMap<ItemKey, Pair<ItemName, ItemType>>
     */
    private val itemType: HashMap<String, ItemIndex> = hashMapOf()

    fun save() {
        fs.openOutputStream(listOf("src_cfg"), "$id.properties").bufferedWriter().use {
            properties.store(it, "XMusic - $id")
        }
    }

    fun get(key: String, defaultValue: String): String {
        return properties.getOrPut(key) { defaultValue }.toString()
    }

    fun get(key: String, defaultValue: () -> String): String {
        return properties.getOrPut(key, defaultValue).toString()
    }

    fun set(key: String, value: String) {
        properties.setProperty(key, value)
    }

    fun getNullable(key: String) = properties[key]?.toString()
    inline fun <reified T> getObjNullable(key: String): T? {
        return getNullable(key)?.let { gson.fromJson(it, T::class.java) }
    }

    fun markType(key: String, name: String, type: ItemType) {
        itemType[key] = ItemIndex(key, name, type)
    }

    open fun listAllMarks() = itemType.entries

    fun setObj(key: String, value: Any) {
        val json = gson.toJson(value)
        properties.setProperty(key, json)
    }

    inline fun <reified T> getObj(key: String, defaultValue: T): T {
        return if (!properties.containsKey(key)) {
            val json = gson.toJson(defaultValue)
            properties.setProperty(key, json)
            defaultValue
        } else {
            val json = properties.getProperty(key)
            gson.fromJson(json, T::class.java)
        }
    }

    fun transact(): SourceConfigTran {
        return SourceConfigTran(this)
    }
}