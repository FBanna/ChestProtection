package fbanna.chestprotection.trade;


import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.check.CheckChest;
import fbanna.chestprotection.trade.profit.ProfitScreen;
import fbanna.chestprotection.trade.setup.SetupScreen;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TradeScreen extends SimpleGui {


    private final CheckChest trade;
    private TradeInventory tradeInventory;

    private final GuiElementBuilder accept;



    public TradeScreen(ServerPlayerEntity player, CheckChest trade) {
        super(ScreenHandlerType.GENERIC_9X3, player, false);


        this.trade = trade;


        this.accept = new GuiElementBuilder()
                .setItem(Items.RED_BANNER)
                .setName(Text.of("NO MONEY"))
                .setCallback(((index, clickType, action) -> {
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

        ChestProtection.LOGGER.info(String.valueOf(this.trade.cost) + String.valueOf(this.trade.product));




        GuiElementBuilder cost = new GuiElementBuilder()
                .setItem(this.trade.cost.getItem());


        ComponentMap costComponents = this.trade.cost.copy().getComponents();


        for(ComponentType component: costComponents.getTypes()) {
            cost.setComponent(component, this.trade.cost.getComponents().get(component));
        }
        cost.setLore(List.of(Text.of(String.valueOf(this.trade.cost.getCount()))));



        GuiElementBuilder product = new GuiElementBuilder()
                .setItem(this.trade.product.getItem());

        ComponentMap productComponents = this.trade.product.getComponents();

        for(ComponentType component: productComponents.getTypes()) {
            product.setComponent(component, this.trade.product.getComponents().get(component));
        }
        product.setLore(List.of(Text.of(String.valueOf(this.trade.product.getCount()))));


        if(Objects.equals(this.trade.author, player.getName().getString())){

            this.setTitle(Text.of("Your shop"));

            //setSlot(7, new ItemStack(Items.BLACK_STAINED_GLASS_PANE));
            //setSlot(25, new ItemStack(Items.PAPER));

            //GuiElementBuilder edit = new GuiElementBuilder()
            //        .setItem(Items.PAPER)
            //        .setName(Text.of("edit"))
            //                .


            //setSlot(16, new ItemStack(Items.BLACK_STAINED_GLASS_PANE));


            cost.setCallback((index, clickType, action) -> {
                //SimpleGui gui = new ProfitScreen(this.getPlayer(), this.trade);
                SimpleGui gui = new ProfitScreen(this.getPlayer(), this.trade);
                this.close();
                gui.open();
            }).glow();


            product.setCallback((index, clickType, action) -> {
                this.close();

                //ChestBlock chestBlock = (ChestBlock) this.trade.world.getBlockState(this.trade.position).getBlock();
                //player.openHandledScreen(chestBlock.createScreenHandlerFactory(this.trade.world.getBlockState(this.trade.position),this.trade.world, this.trade.position));
                player.openHandledScreen((this.trade.world.getBlockState(this.trade.position)).createScreenHandlerFactory(this.trade.world, this.trade.position));
            }).glow();

            GuiElementBuilder setup = new GuiElementBuilder()
                    .setItem(Items.PAPER)
                    .setName(Text.of("Setup"))
                    .setCallback((index, clickType, action) -> {
                        SimpleGui gui = new SetupScreen(this.getPlayer(), this.trade);
                        this.close();
                        gui.open();
                    });

            setSlot(25, setup);

        } else {

            this.setTitle(Text.of(trade.author + "'s shop"));
        }




        setSlot(8, cost);
        setSlot(26, product);

        int slotNumber = 0;

        while(getFirstEmptySlot() != -1){
            setSlotRedirect(getFirstEmptySlot(), new Slot(tradeInventory, slotNumber, 0,0));
            slotNumber++;
        }
    }

    public void getBanner() {

        /*if(this.trade.chestStatus == CheckChest.status.ERROR) {
            this.accept.setItem(Items.BARRIER)
                    .setName(Text.of("Error! contact " + this.trade.author));

        } else*/ if (!this.trade.isStock(this.trade.product)) {

            this.accept.setItem(Items.BARRIER)
                    .setName(Text.of("No stock! contact " + this.trade.author));


        } else if (!this.trade.canFit(this.trade.cost)) {

            this.accept.setItem(Items.BARRIER)
                    .setName(Text.of("Profit full! contact " + this.trade.author));


        } else if (!this.tradeInventory.checkTrade()){

            this.accept.setItem(Items.RED_BANNER)
                    .setName(Text.of("NO MONEY"));


        }  else {

            this.accept.setItem(Items.LIME_BANNER)
                    .setName(Text.of("PURCHASE"));




        }
        this.setSlot(17,this.accept);
    }


    @Override
    public void onClose(){
        this.tradeInventory.dropAll();
    }



}
