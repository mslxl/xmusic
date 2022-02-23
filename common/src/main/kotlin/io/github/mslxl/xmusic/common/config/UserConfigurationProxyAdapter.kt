package io.github.mslxl.xmusic.common.config

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class UserConfigurationProxyAdapter(configuration: Configuration) : InvocationHandler {
    val journal = configuration.journal()
    fun proxyToString(proxy: Any): String {
        return "Proxy class($proxy)"

    }

    fun getItem(method: Method): Any? {
        val name = if (method.name.startsWith("get")) {
            method.name.substring(3).let {
                it.first().lowercaseChar() + it.drop(1)
            }
        } else if (method.name.startsWith("is")) {
            method.name.substring(2).let {
                it.first().lowercaseChar() + it.drop(1)
            }
        } else {
            error("Unknown function ${method.name}")
        }

        val v = journal.get(name) {
            journal.exposedDefaultValue[name]
        }
        return when (method.returnType) {
            Integer::class.java, Int::class.java -> v?.toString()?.toIntOrNull()
            Double::class.java -> v?.toString()?.toDoubleOrNull()
            Float::class.java -> v?.toString()?.toFloatOrNull()
            Boolean::class.java -> v?.toString()?.toBoolean()
            String::class.java -> v?.toString()
            else -> v
        }
    }

    fun setItem(method: Method, value: Any): Any {
        val name = method.name.substring(3).let {
            it.first().lowercaseChar() + it.drop(1)
        }
        journal.set(name, value)
        return value
    }

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        return if (method.name == "getConfiguration") {
            return journal
        } else if (method.name.startsWith("get") || method.name.startsWith("is")) {
            getItem(method)
        } else if (method.name.startsWith("set")) {
            setItem(method, args!![0])
        } else if (method.name == "toString") {
            proxyToString(proxy)
        } else {
            error("Unknown proxy method ${method.name}")
        }
    }
}