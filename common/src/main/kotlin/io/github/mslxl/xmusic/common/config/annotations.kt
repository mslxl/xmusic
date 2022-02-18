package io.github.mslxl.xmusic.common.config

enum class Type {
    Text, EncryptText, Number, Float, Percentage, FilePath
}

@Target(AnnotationTarget.PROPERTY)
annotation class Range(val min: Int, val max: Int)

@Target(AnnotationTarget.PROPERTY)
annotation class RangeF(val min: Double, val max: Double)

@Target(AnnotationTarget.PROPERTY)
annotation class Select(vararg val choices: String)

@Target(AnnotationTarget.PROPERTY)
annotation class Expose(val type: Type = Type.Text)

@Target(AnnotationTarget.PROPERTY)
annotation class Default(val value: String)

