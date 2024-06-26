package io.sandboxmc.commands.dimension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.sandboxmc.Plunger;
import io.sandboxmc.commands.autoComplete.DimensionAutoComplete;
import io.sandboxmc.dimension.DimensionManager;
import io.sandboxmc.dimension.DimensionSave;
import io.sandboxmc.dimension.SandboxWorldConfig;
import io.sandboxmc.mixin.MinecraftServerAccessor;
import io.sandboxmc.zip.ZipUtility;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage.Session;

public class CopyDimension {
  public static LiteralArgumentBuilder<ServerCommandSource> register() {
    return CommandManager.literal("copy")
      .then(
        CommandManager.argument("dimensionToCopy", DimensionArgumentType.dimension())
        .suggests(DimensionAutoComplete.Instance())
        .then(
          CommandManager
          .argument("namespace", StringArgumentType.word())
          .then(
            CommandManager
            .argument("dimensionName", StringArgumentType.word())
            .executes(context -> copyDimension(context))
          )
        )
      )
      .executes(context -> {
        context.getSource().sendFeedback(() -> {
          return Text.literal("Please select a valid Dimension");
        }, false);
        return 1;
      });
  }

  private static int copyDimension(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
    MinecraftServer server = context.getSource().getServer();
    Session session = ((MinecraftServerAccessor)context.getSource().getServer()).getSession();
    ServerWorld dimension = DimensionArgumentType.getDimensionArgument(context, "dimensionToCopy");
    String namespace = StringArgumentType.getString(context, "namespace");
    String dimensionName = StringArgumentType.getString(context, "dimensionName");
    Identifier dimensionId = dimension.getRegistryKey().getValue();
    Identifier newDimensionId = new Identifier(namespace, dimensionName);
    String rootPath = session.getDirectory(WorldSavePath.ROOT).toString();

    // Get directory to copy
    Path dimensionDirectory = session.getWorldDirectory(dimension.getRegistryKey());

    // Create directory and copy dimensionDirectory there
    File copyTargetDirectory = Paths.get(rootPath, "dimensions", namespace, dimensionName).toFile();
    if (!copyTargetDirectory.exists()) {
      copyTargetDirectory.mkdirs();
    } else {
      // It should not exist...
      Plunger.debug("Already exists...");
      return 0;
    }

    try {
      ZipUtility.copyDirectory(dimensionDirectory, copyTargetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
      context.getSource().sendFeedback(() -> {
        return Text.literal("Failed to copy Dimension: " + dimensionId);
      }, false);
      return 0;
    }

    // Create world once files are in place using dimension data
    SandboxWorldConfig config = new SandboxWorldConfig(server);
    config.setSeed(dimension.getSeed());
    // TODO: figure out how to get or pass DimensionOptions
    // DimensionOptions is not on the dimesion, we are storing it in generatedWorlds list
    // But that is only there for worlds we create
    // can we assume that dimensions created outside out system have a dimension.json file?
    config.setDimensionOptionsId(dimension.getDimensionKey().getValue());
    server.getRegistryManager().get(RegistryKeys.DIMENSION).get(dimensionId);
    
    Plunger.info("OptionsId: " + dimension.getDimensionKey().getValue());
    DimensionSave dimensionSave = DimensionManager.buildDimensionSaveFromConfig(newDimensionId, config);
    dimensionSave.generateConfigFiles();

    context.getSource().sendFeedback(() -> {
      return Text.literal("Copied Dimension: " + dimensionId + " as " + newDimensionId);
    }, false);

    return 1;
  }
}
