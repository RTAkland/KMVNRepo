/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/15 12:08
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util.string

import nl.adaptivity.xmlutil.serialization.XML

inline fun <reified T : Any> String.fromXmlString(): T {
    return XML.decodeFromString<T>(this)
}

inline fun <reified T : Any> T.toXmlString(): String {
    return XML.encodeToString<T>(this)
}