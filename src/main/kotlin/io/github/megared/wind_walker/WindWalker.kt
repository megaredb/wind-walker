package io.github.megared.wind_walker

import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object WindWalker : ModInitializer {
    private val LOGGER: Logger = LoggerFactory.getLogger("Wind Walker")
    val CONFIG: WindWalkerConfig = WindWalkerConfig.createAndLoad()

    override fun onInitialize(mod: ModContainer) {
        LOGGER.info("Hello Quilt world from {}!", mod.metadata()?.name())
    }
}
