/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:53
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.routing.api

import cn.rtast.krepo.entity.FrontendConfig
import cn.rtast.krepo.DEFAULT_CONFIG
import cn.rtast.krepo.configManager
import cn.rtast.krepo.entity.res.CommonDataResponse
import cn.rtast.krepo.entity.res.CommonResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAPIFrontendConfigRouting() {
    routing {
        route("/@/api/config") {
            configureGetFrontendConfigRouting()
            authenticate("api") {
                configureUpdateFrontendConfigRouting()
                configureResetFrontendConfigRouting()
            }
        }
    }
}

private fun Route.configureResetFrontendConfigRouting() {
    put("/frontend/reset") {
        val newConfig = configManager.getConfig()
            .copy(
                frontendConfig = FrontendConfig(
                    DEFAULT_CONFIG.frontendConfig.pageTitle,
                    DEFAULT_CONFIG.frontendConfig.icpLicense,
                    DEFAULT_CONFIG.frontendConfig.description,
                    DEFAULT_CONFIG.frontendConfig.copyright
                ),
            )
        configManager.write(newConfig)
        call.respond(HttpStatusCode.OK, CommonResponse(200, "重置成功"))
    }
}

private fun Route.configureUpdateFrontendConfigRouting() {
    put("/frontend") {
        val putConfig = call.receive<FrontendConfig>()
        val currentConfig = configManager.getConfig()
        val icpLicense = if (putConfig.icpLicense.isNullOrBlank()) null
        else putConfig.icpLicense
        val newConfig = currentConfig.copy(
            frontendConfig = FrontendConfig(
                putConfig.pageTitle,
                icpLicense,
                putConfig.description,
                putConfig.copyright
            ),
        )
        configManager.write(newConfig)
        call.respond(HttpStatusCode.OK, CommonResponse(200, "更新成功"))
    }
}

private fun Route.configureGetFrontendConfigRouting() {
    get("/frontend") {
        val config = configManager.getConfig().frontendConfig
        call.respond(
            HttpStatusCode.OK, CommonDataResponse(
                200,
                FrontendConfig(
                    config.pageTitle,
                    config.icpLicense,
                    config.description,
                    config.copyright
                )
            )
        )
    }
}