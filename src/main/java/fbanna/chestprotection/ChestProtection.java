package fbanna.chestprotection;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.check.CheckChest;
import fbanna.chestprotection.trade.TradeScreen;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ChestProtection implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("chest-protection");

	@Override
	public void onInitialize() {

		LOGGER.info("Now protecting your chests!");

		// CHECK FOR BLOCK USE

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			

			if(!player.isSpectator()){

				CheckChest book = new CheckChest(hitResult.getBlockPos(), world);

				if(book.chestStatus == CheckChest.status.LOCK){
					if(!Objects.equals(book.author, player.getName().getString())){
						player.sendMessage(Text.translatable("Chest is locked!").formatted(Formatting.RED), true);
						return ActionResult.FAIL;
					}
				} else if (book.chestStatus == CheckChest.status.SELL) {

					SimpleGui gui = new TradeScreen((ServerPlayerEntity) player, book);
					gui.open();

					return ActionResult.FAIL;

				}

			}
            return ActionResult.PASS;
        });



		// CHECK FOR BLOCK BREAK

		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
			if(!player.isSpectator()){

				CheckChest book = new CheckChest(pos, world);

				if(book.chestStatus != CheckChest.status.CLEAR){
					if(!Objects.equals(book.author, player.getName().getString())){
						player.sendMessage(Text.translatable("Chest is locked!").formatted(Formatting.RED), true);
						return false;
					}
				}

			}
			return true;
		});

	}



}
