package fbanna.chestprotection.trade;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.check.CheckChest;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Iterator;

public class TradeInventory extends SimpleInventory {

    private final TradeScreen screen;
    private final CheckChest trade;
    private final ServerPlayerEntity player;

    public TradeInventory(TradeScreen screen, CheckChest trade, ServerPlayerEntity player, int size) {
        super(size);
        this.screen = screen;
        this.trade = trade;
        this.player = player;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        super.setStack(slot, stack);

        screen.getBanner();

    }

    public void dropAll() {

        ItemStack stack;
        for(int i = 0; i < this.size(); i++){
            stack = this.getStack(i);
            if (!stack.isEmpty()){

                boolean wasAdded = this.player.getInventory().insertStack(stack);

                if (!wasAdded) {

                    this.player.dropItem(stack, false);

                }
            }
        }
    }

    private static boolean ItemComponentEquals(ItemStack original, Component component) {
        for (Component<?> type: original.getComponents()) {
            if(type.equals(component)) {
                return true;
            }
        }
        return false;
    }

    public static boolean ItemsEqual(ItemStack stack1ORIGINAL, ItemStack tradeStack1ORIGINAL) {

        ItemStack stack = stack1ORIGINAL.copy();
        ItemStack tradeStack = tradeStack1ORIGINAL.copy();

        for (Component<?> type: tradeStack.getComponents()) {
            //ChestProtection.LOGGER.info(type.toString());

            if(!ItemComponentEquals(stack, type)){
                return false;
            }


        }

        return true;
    }

    public boolean checkTrade(){

        int total = 0;
        ItemStack stack;

        for(int i = 0; i < this.size(); i++){
            stack = this.getStack(i);
            if(ItemsEqual(this.getStack(i), this.trade.cost)) {
                total += stack.getCount();
            }

        }
        /*} else if (this.getStack(index).getItem() == this.trade.cost.getItem()){
            return index;
        }*/

        if (total >= this.trade.cost.getCount()){
            return true;
        } else {
            return false;
        }

    }

    public void doTrade() {


        // CHECK NEEDED ITEMS

        ArrayList<Integer> costSlots = new ArrayList<>();
        int costTotal = 0;
        int costCountdown;

        ArrayList<Integer> productSlots = new ArrayList<>();
        int productTotal = 0;
        int productCountdown;

        for(int i = 0; i < this.size(); i++){
            if (ItemsEqual(this.getStack(i), this.trade.cost)){
                costTotal += this.getStack(i).getCount();
                costSlots.add(i);
            }
        }



        for(int i = 0; i < this.trade.chestInventory.size(); i++){
            if (ItemsEqual(this.trade.chestInventory.getStack(i), this.trade.product)){
                productTotal += this.trade.chestInventory.getStack(i).getCount();
                productSlots.add(i);
            }
        }

        //REMOVE NEEDED ITEMS

        if(costTotal >= this.trade.cost.getCount() && productTotal >= this.trade.product.getCount() && trade.canFit(this.trade.cost)){

            costCountdown = this.trade.cost.getCount();
            productCountdown = this.trade.product.getCount();



            ItemStack stack;


            for(int slot: costSlots){

                if ( costCountdown > 0 ){

                    stack = this.getStack(slot);
                    if(costCountdown >= stack.getCount()){
                        this.setStack(slot, Items.AIR.getDefaultStack());
                        costCountdown -= stack.getCount();
                    } else {
                        this.setStack(slot, this.trade.cost.copyWithCount(stack.getCount() - costCountdown));
                        //this.setStack(slot, new ItemStack(this.trade.cost.getItem(), stack.getCount() - costCountdown));
                        costCountdown = 0;
                    }

                } else {
                    break;
                }


            }

            for(int slot: productSlots){
                //this.setStack();

                if ( productCountdown > 0 ){

                    stack = this.trade.chestInventory.getStack(slot);
                    if(productCountdown >= stack.getCount()){
                        this.trade.chestInventory.setStack(slot, Items.AIR.getDefaultStack());
                        ChestProtection.LOGGER.info("1removed item with air from "+ slot);
                        productCountdown -= stack.getCount();
                    } else {
                        this.trade.chestInventory.setStack(slot, this.trade.product.copyWithCount(stack.getCount() - productCountdown));
                        ChestProtection.LOGGER.info("2reduced item count from " + stack.getCount() + " to " + (stack.getCount() - productCountdown));
                        //this.trade.chestInventory.setStack(slot, new ItemStack(this.trade.product.getItem(), stack.getCount() - productCountdown));
                        productCountdown = 0;
                    }

                } else {
                    break;
                }


            }

            // ADD NEEDED ITEMS

            //costCountdown = this.trade.cost.getCount();
            productCountdown = this.trade.product.getCount();



            for (int i = 0; i < this.size(); i++){
                stack = this.getStack(i);
                if(stack.isEmpty()) {

                    if(productCountdown > this.trade.product.getMaxCount()){
                        productCountdown -= this.trade.product.getMaxCount();
                        this.setStack(i, this.trade.product.copyWithCount(this.trade.product.getMaxCount()));
                        ChestProtection.LOGGER.info("1added " + this.trade.product.getMaxCount() + " to " + i);
                        //this.setStack(i, new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()));
                    } else {
                        //ChestProtection.LOGGER.info("RUNN" + productCountdown);
                        this.setStack(i, this.trade.product.copyWithCount(productCountdown));
                        ChestProtection.LOGGER.info("2added " + productCountdown + " to " + i);
                        //this.setStack(i, new ItemStack(this.trade.product.getItem(), productCountdown));
                        productCountdown = 0;

                    }
                } else if ( /*stack.getItem() == this.trade.product.getItem()*/ ItemsEqual(stack, this.trade.product) && stack.getCount() != stack.getMaxCount()) {

                    if (productCountdown + stack.getCount() <= this.trade.product.getMaxCount()){
                        this.setStack(i, this.trade.product.copyWithCount(productCountdown + stack.getCount()));

                        ChestProtection.LOGGER.info("3added " + (productCountdown + stack.getCount()) + " to " + i);
                        //this.setStack(i, new ItemStack(this.trade.product.getItem(), productCountdown + stack.getCount()));
                        productCountdown = 0;
                    } else {
                        this.setStack(i, this.trade.product.copyWithCount(this.trade.product.getMaxCount()));
                        ChestProtection.LOGGER.info("4added " + (this.trade.product.getMaxCount()) + " to " + i);
                        //this.setStack(i, new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()));
                        productCountdown -= (this.trade.product.getMaxCount() - stack.getCount());
                    }

                }
            }

            if (productCountdown != 0) {

                for(int i = 0; i <= (productCountdown/this.trade.product.getMaxCount()); i++){

                    if (productCountdown > this.trade.product.getMaxCount()) {


                        //boolean wasAdded = this.player.getInventory().insertStack(new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()));
                        boolean wasAdded = this.player.getInventory().insertStack(this.trade.product.copyWithCount(this.trade.product.getMaxCount()));

                        if (!wasAdded) {

                            //this.player.dropItem(new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()), false);
                            this.player.dropItem(this.trade.product.copyWithCount(this.trade.product.getMaxCount()), false);

                        }
                        productCountdown -= this.trade.product.getMaxCount();
                    } else {

                        //boolean wasAdded = this.player.getInventory().insertStack(new ItemStack(this.trade.product.getItem(), productCountdown));
                        boolean wasAdded = this.player.getInventory().insertStack(this.trade.product.copyWithCount(productCountdown));

                        if (!wasAdded) {

                            //this.player.dropItem(new ItemStack(this.trade.product.getItem(), productCountdown), false);
                            this.player.dropItem(this.trade.product.copyWithCount(productCountdown), false);

                        }
                        productCountdown = 0;

                    }
                }
            }

            this.trade.insertProfitInventory(this.trade.cost);
        }

        screen.getBanner();
    }
}

