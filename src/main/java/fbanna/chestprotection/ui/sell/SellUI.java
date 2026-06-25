package fbanna.chestprotection.ui.sell;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.types.Sell.Sell;
import fbanna.chestprotection.protect.types.Sell.SellData;
import fbanna.chestprotection.protect.types.Sell.TradeItem;
import fbanna.chestprotection.ui.profit.ProfitUI;
import fbanna.chestprotection.ui.sellsetup.SellSetupInventory;
import fbanna.chestprotection.util.TradeInventoryUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class SellUI extends SimpleGui {

    private final Sell cp;

    private final SellInventory container;

    private static final int ACCEPT_SLOT = 17;
    private final static int[] PANES = {7, 16, 25};

    public SellUI(ServerPlayer player, Sell cp) {
        super(MenuType.GENERIC_9x3, player, false);

        this.cp = cp;
        this.container = new SellInventory(this);


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
                //.setName(Component.literal("%d ".formatted(costTradeItem.getCount())).append(costTradeItem.getStack().getHoverName()))
                .setName(Component.literal("%d %s".formatted(costTradeItem.getCount(), costTradeItem.getStack().getHoverName().getString())))
                .hideDefaultTooltip();

        TradeItem productTradeItem = sd.getProduct().get();
        GuiElementBuilder product = new GuiElementBuilder(productTradeItem.getStack().copyWithCount(1))
                //.setName(Component.literal("%d ".formatted(productTradeItem.getCount())).append(productTradeItem.getStack().getHoverName()))
                .setName(Component.literal("%d %s".formatted(productTradeItem.getCount(), productTradeItem.getStack().getHoverName().getString())))
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

            cost
                    //.setName(Component.literal("View profit's"))
                    .setCallback(() -> {

                        this.close();

                        ProfitUI ui = new ProfitUI(player, this.cp.cpdata.sellData.get().getProfitInventory());
                        ui.open();
                        //ChestProtection.LOGGER.info("open profit inventory here");
                    });

            product
                    //.setName(Component.literal("View inventory"))
                    .setCallback(() -> {

                        this.close();

                        player.openMenu((MenuProvider) this.cp.protectedInventory); // dont know how this works!

                    });
        }

        this.setSlot(8, cost);
        this.setSlot(26, product);

        int i = 0;
        for (int y = 0; y < 3; y++){ // 0,1,2

            for (int x = 0; x < 7; x++) { //0,1,2,3,4,5,6
                this.setSlot(x + y*9, new Slot(this.container, i, x,y));
                i++;
            }
        }

        updateButton();

    }



    public void updateButton() {

        SellData data = this.cp.cpdata.sellData.get();

        // SHOP ERRORS: full profit -> no stock
        // USER ERRORS: no cost
        // ACCEPT

        Optional<NameAndId> ownersNameOptional = player.level().getServer().services().nameToIdCache().get(this.cp.cpdata.getAuthorised().getAuthor());

        if (ownersNameOptional.isEmpty()) {
            ChestProtection.LOGGER.error("Could not find owner's name!");
            this.clearSlot(ACCEPT_SLOT);
            return;
        }

        if (!TradeInventoryUtils.isPresent(this.cp.protectedInventory, data.getProduct().get())) {
            this.setSlot(ACCEPT_SLOT, new GuiElementBuilder(Items.BARRIER)
                    .setName(
                            Component.literal("No stock! Contact %s".formatted(ownersNameOptional.get().name()))
                                    .withStyle(ChatFormatting.RED)
                    )
            );
            return;
        }

        if (!data.getProfitInventory().canAddItem(data.getProduct().get().copyStackWithCount())) {
            this.setSlot(ACCEPT_SLOT, new GuiElementBuilder(Items.BARRIER)
                    .setName(
                            Component.literal("Profit inventory filled! Contact %s".formatted(ownersNameOptional.get().name()))
                                    .withStyle(ChatFormatting.RED)
                    )
            );
            return;
        }

        if (!TradeInventoryUtils.isPresent(this.container, data.getCost().get())) {
            this.setSlot(ACCEPT_SLOT, new GuiElementBuilder(Items.WOOL.red())
                    .setName(Component.literal("No money!").withStyle(ChatFormatting.RED))
            );
            return;
        }

        this.setSlot(ACCEPT_SLOT, new GuiElementBuilder(Items.WOOL.green())
                .setName(Component.literal("Confirm"))
                .setCallback(() -> {


                    // Product: Chest -> SellUI
                    TradeInventoryUtils.tradeInto(
                            this.cp.protectedInventory,
                            this.cp.cpdata.sellData.get().getProduct().get(),
                            this.container
                    );

                    ChestProtection.LOGGER.info("doing trade");
                })

        );


//        if (
//                TradeInventoryUtils.isPresent(this.container, data.getCost().get())
//                && TradeInventoryUtils.isPresent(this.cp.protectedInventory, data.getProduct().get())
//                &&
//                //&& data.getProfitInventory()
//        ){
//
//        }

    }



}
