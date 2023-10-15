package io.sandbox.dimensions.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.sandbox.dimensions.commands.autoComplete.DimensionAutoComplete;
import io.sandbox.dimensions.commands.autoComplete.DimensionRulesAutoComplete;
import io.sandbox.dimensions.dimension.DimensionSave;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class DimensionRule {
  public static LiteralArgumentBuilder<ServerCommandSource> register() {
    return CommandManager.literal("rule")
      .then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).suggests(DimensionAutoComplete.Instance())
        .then(CommandManager.argument("dimensionRule", StringArgumentType.word()).suggests(DimensionRulesAutoComplete.Instance())
          .executes(ctx -> getDimensionRule(
            DimensionArgumentType.getDimensionArgument(ctx, "dimension"),
            StringArgumentType.getString(ctx, "dimensionRule"),
            ctx.getSource()
          ))
          .then(CommandManager.argument("value", BoolArgumentType.bool())
            .executes(ctx -> setDimensionRule(
              DimensionArgumentType.getDimensionArgument(ctx, "dimension"),
              StringArgumentType.getString(ctx, "dimensionRule"),
              BoolArgumentType.getBool(ctx, "value"),
              ctx.getSource()
            ))
          )
        )
      )
      // TODO: add specific blockPos as secondary argument
      .executes(context -> {
        System.out.println("Fallback????");
        return 1;
      });
  }

  private static int getDimensionRule(ServerWorld dimension, String rule, ServerCommandSource source) {
    // Get rule
    DimensionSave dimensionSave = DimensionSave.getDimensionState(dimension);
    Boolean ruleValue = dimensionSave.getRule(rule);
    source.sendFeedback(() -> {
      return Text.translatable("sandbox-dimensions.commands.dimensionrules.get", new Object[]{rule, ruleValue.toString()});
    }, false);
    return 1;
  }

  private static int setDimensionRule(ServerWorld dimension, String rule, Boolean value, ServerCommandSource source) throws CommandSyntaxException {
    // Set dimension rule
    DimensionSave dimensionSave = DimensionSave.getDimensionState(dimension);
    dimensionSave.setRule(rule, value);
    source.sendFeedback(() -> {
      return Text.translatable("sandbox-dimensions.commands.dimensionrules.set", new Object[]{rule, value.toString()});
    }, false);
    return 1;
  }
}
