package fbanna.chestprotection;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.check.CheckChest;
import fbanna.chestprotection.screens.trade.TradeScreen;
import fbanna.chestprotection.screens.setup.SetupScreen;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChestProtection implements ModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("ChestProtection");

  public static List<CheckChest> SHOPS = new ArrayList<>();

  @Override
  public void onInitialize() {

    LOGGER.info("Now protecting your chests!");
    // CHECK FOR BLOCK USE

    UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

      if (!player.isSpectator()) {

        CheckChest book = new CheckChest(hitResult.getBlockPos(), world);

        if (book.chestStatus == CheckChest.status.LOCK) {
          if (!Objects.equals(book.author, player.getName().getString())) {
            player.displayClientMessage(
                Component.translatable("Chest is locked by %s!".formatted(book.author)).withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
          }
        } else if (book.chestStatus == CheckChest.status.SELL) {

          SHOPS.add(book);

          SimpleGui gui = new TradeScreen((ServerPlayer) player, book);
          gui.open();

          return InteractionResult.FAIL;

        } else if (book.chestStatus == CheckChest.status.ERROR) {

          if (Objects.equals(book.author, player.getName().getString())) {
            SimpleGui gui = new SetupScreen((ServerPlayer) player, book);
            gui.open();
          } else {
            player.displayClientMessage(Component.translatable("Shop is in an error state. Contact %s!".formatted(book.author))
                .withStyle(ChatFormatting.RED), true);
          }

          return InteractionResult.FAIL;
        }

      }
      return InteractionResult.PASS;
    });

    // CHECK FOR BLOCK BREAK

    PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
      if (!player.isSpectator()) {

        CheckChest book = new CheckChest(pos, world);

        if (book.chestStatus != CheckChest.status.CLEAR) {
          if (!Objects.equals(book.author, player.getName().getString())) {
            player.displayClientMessage(
                Component.translatable("Chest is locked by %s!".formatted(book.author)).withStyle(ChatFormatting.RED), true);
            return false;
          }
        }

      }
      return true;
    });

  }

}
