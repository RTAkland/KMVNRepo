/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/18 21:54
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalForeignApi::class)

package cn.rtast.kmvnrepo.util.string

import cn.rtast.kmvnrepo.time.get_file_modified_time
import cn.rtast.kmvnrepo.time.is_directory
import cn.rtast.kmvnrepo.time.is_regular_file
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path

actual fun Path.getFileModifiedTimestamp(): Long {
    return try {
        get_file_modified_time(this.toString())
    } catch (_: Exception) {
        -26034180322L
    }
}

actual fun Path.isRegularFile(): Boolean {
    return is_regular_file(this.toString()) != 0
}

actual fun Path.cIsDirectory(): Boolean {
    return is_directory(this.toString()) != 0
}