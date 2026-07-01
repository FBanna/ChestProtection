package fbanna.chestprotection.ui.sell;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckProtected;
import fbanna.chestprotection.protect.types.sell.Sell;
import fbanna.chestprotection.protect.data.sell.SellData;
import fbanna.chestprotection.protect.data.sell.TradeItem;
import fbanna.chestprotection.ui.profit.ProfitUI;
import fbanna.chestprotection.util.TradeInventoryUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
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


//        Optional<NameAndId> optionalName = player.level().getServer().services().nameToIdCache().get(this.cp.cpdata.getAuthorised().getAuthor());
//
//        if (optionalName.isEmpty()) {
//            ChestProtection.LOGGER.error("Could not find owner's name!");
//            return;
//        }

        this.setTitle(Component.literal("%s's shop".formatted(this.cp.cpdata.getAuthorised().getAuthorName(player.level().getServer()))));

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


        if (this.cp.cpdata.getAuthorised().isAuthorised(player.getUUID()) || CheckProtected.isAlwaysAllowed(this.player)) {

            this.setSlot(25, new GuiElementBuilder(Items.PAPER)
                    .setName(Component.literal("Edit shop").withStyle(ChatFormatting.GRAY))
                    .setCallback(() -> {

                        //this.cp.removeFromOpenShops();
                        this.close();
                        this.cp.toSellBook().open(this.player, this.player.level().getServer());

                    })
                    .hideDefaultTooltip()
            );

            cost
                    //.setName(Component.literal("View profit's"))
                    .setCallback(() -> {

                        // retain Open Shop status!
                        this.close();
                        this.cp.addAndUpdatePlayers(this.player); // gank alert
                        //this.cp.addToOpenShops();
                        ProfitUI ui = new ProfitUI(this.player, this.cp);

                        ui.open();
                        //ChestProtection.LOGGER.info("open profit inventory here");
                    });

            product
                    //.setName(Component.literal("View inventory"))
                    .setCallback(() -> {

                        this.close();

                        this.cp.openProtectedInventory(this.player);

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

//        Optional<NameAndId> ownersNameOptional = player.level().getServer().services().nameToIdCache().get(this.cp.cpdata.getAuthorised().getAuthor());
//
        String name = this.cp.cpdata.getAuthorised().getAuthorName(this.player.level().getServer());

        if (name == null) {
            ChestProtection.LOGGER.error("Could not find owner's name!");
            this.clearSlot(ACCEPT_SLOT);
            return;
        }

        if (!TradeInventoryUtils.isPresent(this.cp.getProtectedInventory(), data.getProduct().get())) {
            this.setSlot(ACCEPT_SLOT, new GuiElementBuilder(Items.BARRIER)
                    .setName(
                            Component.literal("No stock! Contact %s".formatted(name))
                                    .withStyle(ChatFormatting.RED)
                    )
            );
            return;
        }

        if (!this.cp.getProfitInventory().canAddItem(data.getProduct().get().copyStackWithCount())) {
            this.setSlot(ACCEPT_SLOT, new GuiElementBuilder(Items.BARRIER)
                    .setName(
                            Component.literal("Profit inventory filled! Contact %s".formatted(name))
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


                    // Double check cost & product are present
                    if(
                            !TradeInventoryUtils.isPresent(this.container, data.getCost().get()) ||
                            !TradeInventoryUtils.isPresent(this.cp.getProtectedInventory(), data.getProduct().get())
                    ) {
                                return;
                    }

                    // Cost: SellUI -> ProfitInventory

                    ArrayList<ItemStack> remaining1 = TradeInventoryUtils.tradeInto(
                            this.container,
                            this.cp.cpdata.sellData.get().getCost().get(),
                            this.cp.getProfitInventory()
                    );

                    assert remaining1.isEmpty(): "Failed to check canFit properly!";


                    // Product: Chest -> SellUI
                    ArrayList<ItemStack> remaining2 = TradeInventoryUtils.tradeInto(
                            this.cp.getProtectedInventory(),
                            this.cp.cpdata.sellData.get().getProduct().get(),
                            this.container
                    );


                    // handle remaning items to sellUI
                    for (ItemStack stack: remaining2) {
                        this.player.handleExtraItemsCreatedOnUse(stack);
                    }

                    this.cp.writeCPdata();
                    this.updateButton();

                })

        );



    }

    @Override
    public void onTick() {
        boolean isPresent = this.cp.isPresentAndUpdate(this.player.level().getServer());

        if (!isPresent) {
            this.close();
        }
    }






    @Override
    public void onRemoved() {

        this.container.dropAll(this.player);
        this.cp.removeAndUpdatePlayers(this.player);

        super.onRemoved();
    }



}
