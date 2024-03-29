package io.sandboxmc.commands.datapack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import io.sandboxmc.commands.autoComplete.StringListAutoComplete;
import io.sandboxmc.datapacks.DatapackManager;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class InstallDatapack {
  public static LiteralArgumentBuilder<ServerCommandSource> register() {
    return CommandManager.literal("install").then(
      CommandManager
      .argument("datapack", IdentifierArgumentType.identifier())
      .suggests(new StringListAutoComplete(getDownloadedDatapackAutocomplete()))
      .then(
        CommandManager
        .argument("overwrite", BoolArgumentType.bool())
        .executes(context -> installDatapack(
          context,
          IdentifierArgumentType.getIdentifier(context, "datapack"),
          BoolArgumentType.getBool(context, "overwrite"))
        )
      )
      .executes(context -> installDatapack(
        context,
        IdentifierArgumentType.getIdentifier(context, "datapack"),
        false // default to false
      ))
    ).executes(context -> {
      // no arguments given, do nothing
      return 0;
    });
  }

  private static Function<CommandContext<ServerCommandSource>, List<String>> getDownloadedDatapackAutocomplete() {
    return (context) -> {
      List<String> output = new ArrayList<>();
      for (String keyEntry : DatapackManager.getDownloadedDatapacks()) {
        output.add(keyEntry);
      }

      return output;
    };
  }

  private static int installDatapack(CommandContext<ServerCommandSource> context, Identifier datapackIdentifier, Boolean shouldOverwrite) {
    ServerCommandSource source = context.getSource();
    DatapackManager.installDownloadedDatapack(source.getServer(), datapackIdentifier.toString());
    source.sendFeedback(() -> {
      return Text.literal("Installed datapack as: " + datapackIdentifier);
    }, false);
    return 1;
  }
}
