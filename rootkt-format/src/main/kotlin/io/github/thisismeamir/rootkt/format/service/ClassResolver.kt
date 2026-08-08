package io.github.thisismeamir.rootkt.format.service


class ClassResolver private constructor(
    private val clIdxShift: Int,
    private val basePosition: Int,
    private val registry: HashMap<Int, String>
) {
    constructor(clIdxShift: Int) : this(clIdxShift, 0, HashMap())

    fun setResolverZero(currentPosition: Int): ClassResolver {
        return ClassResolver(clIdxShift, basePosition + currentPosition, registry)
    }

    fun registerNewClass(localPosition: Int, className: String) {
        registry[basePosition + localPosition] = className
    }

    fun resolveClass(clIdx: Int): String {
        val truePosition = clIdx - clIdxShift
        return registry[truePosition]
            ?: error("Unresolved class reference: clIdx=$clIdx, truePosition=$truePosition")
    }
}