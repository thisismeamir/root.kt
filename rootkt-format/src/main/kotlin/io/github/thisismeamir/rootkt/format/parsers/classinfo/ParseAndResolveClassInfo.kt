package io.github.thisismeamir.rootkt.format.parsers.classinfo

import io.github.thisismeamir.rootkt.format.service.ClassResolver
import java.nio.ByteBuffer

fun ByteBuffer.parseAndResolveClassInfo(classResolver: ClassResolver): String {
    val tagStart = position()
    val info = parseClassInfo()
    return if (info.isNewClass) {
        classResolver.registerNewClass(tagStart, info.className!!)
        info.className
    } else {
        classResolver.resolveClass(info.clIdx!!)
    }
}