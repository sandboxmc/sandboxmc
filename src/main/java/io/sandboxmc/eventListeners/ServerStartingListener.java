package io.sandboxmc.eventListeners;

import io.sandboxmc.Config;
import io.sandboxmc.web.Server;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting;
import net.minecraft.server.MinecraftServer;

public class ServerStartingListener implements ServerStarting {
  @Override
  public void onServerStarting(MinecraftServer server) {
    Config.setConfigs(server);
    Server.authOnBoot(server);
  }
}