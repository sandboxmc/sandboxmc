package io.sandboxmc.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.sandboxmc.Plunger;
import io.sandboxmc.commands.autoComplete.DimensionAutoComplete;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class RestoreDimension {
  public static LiteralArgumentBuilder<ServerCommandSource> register() {
    return CommandManager.literal("restore")
      .then(
        CommandManager.argument("dimension", DimensionArgumentType.dimension())
          .suggests(DimensionAutoComplete.Instance())
          .executes(context -> execute(context))
      )
      .executes(context -> {
        Plunger.debug("Fallback????");
        return 1;
      });
  }
    
  private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
    // ServerWorld dimension = DimensionArgumentType.getDimensionArgument(context, "dimension");

    // var dimensionSave = new DimensionSave();
    Plunger.debug("Restore Command not fully Implemented yet...");

    return 1;
  }
}
