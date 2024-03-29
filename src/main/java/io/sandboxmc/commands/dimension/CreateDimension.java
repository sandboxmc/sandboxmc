package io.sandboxmc.commands.dimension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import io.sandboxmc.dimension.DimensionManager;
import io.sandboxmc.commands.autoComplete.StringListAutoComplete;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;

public class CreateDimension {
  public static LiteralArgumentBuilder<ServerCommandSource> register() {
    return CommandManager.literal("create").then(
      CommandManager
      .argument("namespace", StringArgumentType.word())
      // TODO:BRENT not sure how to get a suggestion for these without a namespace
      // .suggests(new StringListAutoComplete(getNamespaceAutoCompleteOptions()))
      .then(
        CommandManager
        .argument("dimensionName", StringArgumentType.word())
        .then(
          CommandManager
          .argument("dimension", IdentifierArgumentType.identifier())
          .suggests(new StringListAutoComplete(getDimensionAutoCompleteOptions()))
          .executes(context -> createDimension(context))
        ).executes(context -> createDimension(context))
      )
    ).executes(context -> {
      // No arguments given, do nothing.
      return 0;
    });
  }

  private static Function<CommandContext<ServerCommandSource>, List<String>> getDimensionAutoCompleteOptions() {
    return (context) -> {
      Set<RegistryKey<DimensionOptions>> dimensionTypes = context.getSource().getServer()
        .getRegistryManager().get(RegistryKeys.DIMENSION).getKeys();
      List<String> dimensionTypeList = new ArrayList<>();

      for (RegistryKey<DimensionOptions> dimensionType : dimensionTypes) {
        dimensionTypeList.add(dimensionType.getValue().toString());
      }

      return dimensionTypeList;
    };
  }

  private static int createDimension(CommandContext<ServerCommandSource> context) {
    String namespace = StringArgumentType.getString(context, "namespace");
    String dimensionName = StringArgumentType.getString(context, "dimensionName");
    Identifier dimensionOptions = IdentifierArgumentType.getIdentifier(context, "dimension");
    Identifier dimensionIdentifier = new Identifier(namespace, dimensionName);
    ServerCommandSource source = context.getSource();
    MinecraftServer server = source.getServer();

    // Create the dimension
    DimensionManager.createDimensionWorld(server, dimensionIdentifier, dimensionOptions, server.getWorld(World.OVERWORLD).getSeed());

    source.sendFeedback(() -> {
      return Text.literal("Created new Dimension: " + dimensionName);
    }, false);
    return 1;
  }
}
