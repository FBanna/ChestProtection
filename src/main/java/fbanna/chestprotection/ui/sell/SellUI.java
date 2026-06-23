package fbanna.chestprotection.ui.sell;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.types.Sell.Sell;
import fbanna.chestprotection.protect.types.Sell.SellData;
import fbanna.chestprotection.protect.types.Sell.TradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class SellUI extends SimpleGui {

    private final Sell cp;


    private final static int[] PANES = {7, 16, 25};

    public SellUI(ServerPlayer player, Sell cp) {
        this.cp = cp;
        super(MenuType.GENERIC_9x3, player, false);

        Optional<NameAndId> optionalName = player.level().getServer().services().nameToIdCache().get(this.cp.cpdata.getAuthorised().getAuthor());

        if (optionalName.isEmpty()) {
            ChestProtection.LOGGER.error("Could not find owner's name!");
            return;
        }

        this.setTitle(Component.literal("%s's shop".formatted(optionalName.get().name())));

        for (int i: PANES) {
            this.setSlot(i, new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray())
                    .setName(Component.empty())
                    .hideDefaultTooltip()
            );
        }

        SellData sd = this.cp.cpdata.sellData.get();

        TradeItem costTradeItem = sd.getCost().get();
        GuiElementBuilder cost = new GuiElementBuilder(costTradeItem.getStack().copyWithCount(1))
                .setName(Component.literal("%d ".formatted(costTradeItem.getCount())).append(costTradeItem.getStack().getHoverName()))
                //.setName(Component.literal("%d %s".formatted(costTradeItem.getCount(), costTradeItem.getStack().getDisplayName().getString())))
                .hideDefaultTooltip();

        TradeItem productTradeItem = sd.getProduct().get();
        GuiElementBuilder product = new GuiElementBuilder(productTradeItem.getStack().copyWithCount(1))
                .setName(Component.literal("%d ".formatted(productTradeItem.getCount())).append(productTradeItem.getStack().getHoverName()))
                //.setName(Component.literal("%d %s".formatted(productTradeItem.getCount(), productTradeItem.getStack().getDisplayName().getString())))
                .hideDefaultTooltip();


        if (this.cp.cpdata.getAuthorised().isAuthorised(player.getUUID())) {

            this.setSlot(25, new GuiElementBuilder(Items.PAPER)
                    .setName(Component.literal("Edit shop").withStyle(ChatFormatting.GRAY))
                    .setCallback(() -> {

                        this.close();
                        this.cp.toSellBook().open(player, player.level().getServer());

                    })
                    .hideDefaultTooltip()
            );

            cost.setName(Component.literal("View profit's"))
                    .setCallback(() -> {
                        ChestProtection.LOGGER.info("open profit inventory here");
                    });

            product.setName(Component.literal("View inventory"))
                    .setCallback(() -> {
                        ChestProtection.LOGGER.info("open normal inventory here!");
                    });
        }

        this.setSlot(8, cost);
        this.setSlot(26, product);

    }



    private void updateButton() {



    }


}
