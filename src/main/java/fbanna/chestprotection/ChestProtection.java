package fbanna.chestprotection;

import fbanna.chestprotection.config.Config;
import fbanna.chestprotection.protect.CheckProtected;

import fbanna.chestprotection.protect.types.sell.Sell;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ChestProtection implements ModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("ChestProtection");

  public static HashMap<GlobalPos, Sell> OPEN_SHOPS = new HashMap<>();

  public static final Set<Block> LOCKED_BLOCKS = new HashSet<>(Set.of(
          Blocks.CHEST,
          Blocks.BARREL
  ));

  @Override
  public void onInitialize() {

    LOGGER.info("Now protecting your chests!");

    Config.init();

    UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {


      boolean result = CheckProtected.createContainerOpen(hitResult.getBlockPos(), world).open(player, world.getServer());

      if (result) {
        return InteractionResult.PASS;
      }

      return InteractionResult.FAIL;

    });

    // CHECK FOR BLOCK BREAK

    PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {

        return CheckProtected.createContainerOpen(pos, world).playerBreak(player, world.getServer());

    });

  }

}
