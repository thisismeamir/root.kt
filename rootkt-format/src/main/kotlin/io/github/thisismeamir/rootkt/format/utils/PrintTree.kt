package io.github.thisismeamir.rootkt.format.utils

import io.github.thisismeamir.rootkt.format.models.TDirectoryNode
import io.github.thisismeamir.rootkt.format.models.TDirectoryRoot

fun TDirectoryNode.printTree(name: String = "/", prefix: String = "", isLast: Boolean = true) {
    println(prefix + (if (prefix.isEmpty()) "" else if (isLast) "└── " else "├── ") + name)
    val childPrefix = prefix + (if (isLast) "    " else "│   ")
    val entries: List<Pair<String, Boolean>> =
        objectKeys.map { it.name to false } + children.map { (it.key.name) to true }
    entries.forEachIndexed { index, (entryName, isDir) ->
        val last = index == entries.lastIndex
        if (isDir) {
            val childNode = children[children.indexOfFirst { (it.key.name) == entryName }]
            childNode.printTree(entryName, childPrefix, last)
        } else {
            println(childPrefix + (if (last) "└── " else "├── ") + entryName)
        }
    }
}
fun TDirectoryRoot.printTree(name: String = "/") {
    println(name)
    val entries: List<Pair<String, Boolean>> =
        objectKeys.map { it.name to false } + children.map { it.key.name to true }
    entries.forEachIndexed { index, (entryName, isDir) ->
        val last = index == entries.lastIndex
        if (isDir) {
            children[children.indexOfFirst { it.key.name == entryName }].printTree(entryName, "", last)
        } else {
            println((if (last) "└── " else "├── ") + entryName)
        }
    }
}