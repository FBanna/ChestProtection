package fbanna.chestprotection.screens.trade;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckChest;
import fbanna.chestprotection.screens.profit.ProfitScreen;
import fbanna.chestprotection.screens.setup.SetupScreen;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TradeScreen extends SimpleGui {


    private final CheckChest trade;
    private TradeInventory tradeInventory;

    private final GuiElementBuilder accept;



    public TradeScreen(ServerPlayer player, CheckChest trade) {
        super(MenuType.GENERIC_9x3, player, false);

        trade.setScreen(this);

        this.trade = trade;

        this.accept = new GuiElementBuilder()
                .setItem(Items.RED_BANNER)
                .setName(Component.nullToEmpty("NO MONEY"))

                .setCallback(((index, clickType, action, s) -> {
                    this.tradeInventory.doTrade();
                }));


        this.tradeInventory = new TradeInventory(this, trade, player, 21);






        // SET UP GLASS PANELS & storage


        getBanner();

        for( int i = 0; i<3; i++ ){
            setSlot((i*9)+7, new ItemStack(Items.BLACK_STAINED_GLASS_PANE, 1)
                    //.setCustomName(Text.empty())
            );
        }

        //ChestProtection.LOGGER.info(String.valueOf(this.trade.cost) + String.valueOf(this.trade.product));

        //ItemStack[] tradeItemsOriginal = {this.trade.cost, this.trade.product};

        GuiElementBuilder[] tradeItems = new GuiElementBuilder[2];

        /*tradeItems[0] = new GuiElementBuilder()
                .setItem(this.trade.cost.getItem());

        tradeItems[1] = new GuiElementBuilder()
                .setItem(this.trade.product.getItem());*/

        for (int i = 0; i < tradeItems.length; i++) {

            tradeItems[i] = new GuiElementBuilder().setItem(this.trade.tradeItems.get(i).getStack().getItem());;

            /*if(tradeItemsOriginal[i].getItem() == Items.STRUCTURE_VOID){
                for (ComponentType<?> type: tradeItemsOriginal[i].copy().getComponents().getTypes()) {


                    // ADD MORE DEFAULTS
                    if (type.equals(DataComponentTypes.CONTAINER)) {
                        tradeItems[i].setItem(Items.SHULKER_BOX);
                    }
                }
            } else {*/


            DataComponentMap components = this.trade.tradeItems.get(i).getStack().copy().getComponents();

            for(DataComponentType<?> component: components.keySet()) {
                tradeItems[i].setComponent((DataComponentType) component, this.trade.tradeItems.get(i).getStack().getComponents().get(component));
            }

            tradeItems[i].setLore(List.of(Component.nullToEmpty(String.valueOf(this.trade.tradeItems.get(i).getStack().getCount()))));


        }

        if(Objects.equals(this.trade.author, player.getName().getString())){
            this.setTitle(Component.nullToEmpty("Your shop"));



            GuiElementBuilder setup = new GuiElementBuilder()
                    .setItem(Items.PAPER)
                    .setName(Component.nullToEmpty("Setup"))
                    .setCallback((index, clickType, action, s) -> {
                        this.close();

                        SimpleGui gui = new SetupScreen(this.getPlayer(), this.trade);
                        ChestProtection.SHOPS.add(this.trade);
                        gui.open();
                    });

            setSlot(25, setup);

            tradeItems[0].setCallback((index, clickType, action, s) -> {
                //SimpleGui gui = new ProfitScreen(this.getPlayer(), this.trade);
                this.close();
                SimpleGui gui = new ProfitScreen(this.getPlayer(), this.trade);
                ChestProtection.SHOPS.add(this.trade);
                gui.open();
            }).glow();


            tradeItems[1].setCallback((index, clickType, action, s) -> {
                this.close();

                //ChestBlock chestBlock = (ChestBlock) this.trade.world.getBlockState(this.trade.position).getBlock();
                //player.openHandledScreen(chestBlock.createScreenHandlerFactory(this.trade.world.getBlockState(this.trade.position),this.trade.world, this.trade.position));
                player.openMenu((this.trade.world.getBlockState(this.trade.position)).getMenuProvider(this.trade.world, this.trade.position));
            }).glow();
        } else {

            this.setTitle(Component.nullToEmpty(trade.author + "'s shop"));
        }


        //ComponentMap costComponents = this.trade.cost.copy().getComponents();


        //for(ComponentType<?> component: costComponents.getTypes()) {
        //    cost.setComponent((ComponentType) component, this.trade.cost.getComponents().get(component));
        //}
        //cost.setLore(List.of(Text.of(String.valueOf(this.trade.cost.getCount()))));







        setSlot(8, tradeItems[0]);
        setSlot(26, tradeItems[1]);






        int slotNumber = 0;

        while(getFirstEmptySlot() != -1){

            setSlot(getFirstEmptySlot(), new Slot(tradeInventory, slotNumber, 0,0));
            slotNumber++;
        }
    }

    public void getBanner() {

        /*if(this.trade.chestStatus == CheckChest.status.ERROR) {
            this.accept.setItem(Items.BARRIER)
                    .setName(Text.of("Error! contact " + this.trade.author));

        } else*/ if (!this.trade.isStock(this.trade.tradeItems.getProduct())) {

            this.accept.setItem(Items.BARRIER)
                    .setName(Component.nullToEmpty("No stock! contact " + this.trade.author));


        } else if (!this.tradeInventory.canFit()) {

            this.accept.setItem(Items.BARRIER)
                    .setName(Component.nullToEmpty("Profit full! contact " + this.trade.author));


        } else if (!this.tradeInventory.checkTrade()){

            this.accept.setItem(Items.RED_BANNER)
                    .setName(Component.nullToEmpty("NO MONEY"));


        }  else {

            this.accept.setItem(Items.LIME_BANNER)
                    .setName(Component.nullToEmpty("PURCHASE"));




        }
        this.setSlot(17,this.accept);
    }


    @Override
    public void close(boolean skipSync){


        this.tradeInventory.dropAll();
        this.close();
        ChestProtection.SHOPS.remove(this.trade);

        //ChestProtection.LOGGER.info("IM HERE "+ChestProtection.SHOPS.size());
    }



}
