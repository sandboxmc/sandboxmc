package io.sandboxmc.commands;

import java.util.Set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.sandboxmc.dimension.DimensionManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ListDimensions {
  public static LiteralArgumentBuilder<ServerCommandSource> register() {
    return CommandManager.literal("list")
      .executes(context -> listDimensions(context));
  }

  private static int listDimensions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
    Set<String> dimensionList = DimensionManager.getDimensionList();
    for (String dimensionName : dimensionList) {
      context.getSource().sendFeedback(() -> {
        return Text.literal(dimensionName);
      }, false);
    }
    return 1;
  }
}
