package io.sandboxmc.web;

import com.mojang.brigadier.context.CommandContext;

import io.sandboxmc.Plunger;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class Common {
  protected CommandContext<ServerCommandSource> context;
  protected ServerCommandSource source = null;

  public Common() {}

  public Common(CommandContext<ServerCommandSource> theContext) {
    context = theContext;
    source = context.getSource();
  }

  public Common(ServerCommandSource theSource) {
    source = theSource;
  }

  protected void printMessage(MutableText message) {
    if (source == null) {
      Plunger.debug(message.toString());
      return;
    }

    source.sendFeedback(() -> {
      return message;
    }, false);
  }

  protected void printMessage(String message) {
    printMessage(Text.literal(message));
  }
}
