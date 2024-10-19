package fbanna.chestprotection.trade;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.check.CheckChest;
import fbanna.chestprotection.trade.profit.ProfitInventory;
import net.minecraft.component.Component;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

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

    //public static boolean ItemsEqual(ItemStack stack1ORIGINAL, ItemStack tradeStack1ORIGINAL) {
    public static boolean ItemsEqual(ItemStack stack1ORIGINAL, TradeItem tradeItemORIGINAL){

        ItemStack stack = stack1ORIGINAL.copy();
        TradeItem tradeStack = tradeItemORIGINAL.copy();

        if(tradeStack.getIsItem() && stack.getItem() != tradeStack.getStack().getItem()){
            return false;
        }

        for (Component<?> type: tradeStack.getStack().getComponents()) {
            //ChestProtection.LOGGER.info(type.toString());

            if(!ItemComponentEquals(stack, type)){
                return false;
            }


        }

        return true;
    }

    public boolean canFit() {

        List<ItemStack> stacks = getCostStacks();

        if(stacks.isEmpty()) {
            stacks.add(this.trade.tradeItems.getProductStack());
        }

        ProfitInventory profitInventory = this.trade.profitInventory.clone();

        for(ItemStack costStack: stacks){
            ItemStack remaining = profitInventory.addStack(costStack);

            if(!remaining.isEmpty()){
                return false;
            }

        }
        return true;
        /*

        int count = stack.getCount();

        /*for (int slot: this.profitInventory) {

            count -= stack.getMaxCount() - slot;

            if (count <= 0) {
                return true;
            }
        }*/
        /*

        for(int i = 0; i < this.profitInventory.size(); i ++) {
            if(!this.profitInventory.getStack(i).isEmpty()){
                count -= this.profitInventory.getStack(i).getMaxCount() - this.profitInventory.getStack(i).getCount();


            } else {
                count -= stack.getMaxCount();
            }

            if( count <= 0 ) {
                return true;
            }

        }
        return false;*/
    }

    public List<ItemStack> getCostStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for(ItemStack stack: this.heldStacks) {
            if(ItemsEqual(stack, this.trade.tradeItems.getCost())){
                stacks.add(stack);
            }
        }
        return stacks;

    }

    public boolean checkTrade(){

        int total = 0;
        /*ItemStack stack;

        for(int i = 0; i < this.size(); i++){
            stack = this.getStack(i);
            if(ItemsEqual(this.getStack(i), this.trade.tradeItems.getCost())) {
                total += stack.getCount();
            }

        }*/
        for(ItemStack stack: getCostStacks()){
            total += stack.getCount();
        }
        /*} else if (this.getStack(index).getItem() == this.trade.cost.getItem()){
            return index;
        }*/

        if (total >= this.trade.tradeItems.getCostStack().getCount()){
            return true;
        } else {
            return false;
        }

    }

    public void doTrade() {


        // CHECK NEEDED ITEMS

        ArrayList<Integer> costSlots = new ArrayList<>();
        List<ItemStack> costStacks = new ArrayList<>();
        int costTotal = 0;
        int costCountdown;

        ArrayList<Integer> productSlots = new ArrayList<>();
        List<ItemStack> productStacks = new ArrayList<>();
        int productTotal = 0;
        int productCountdown;

        for(int i = 0; i < this.size(); i++){
            if (ItemsEqual(this.getStack(i), this.trade.tradeItems.getCost())){
                costTotal += this.getStack(i).getCount();
                costSlots.add(i);
            }
        }



        for(int i = 0; i < this.trade.chestInventory.size(); i++){
            if (ItemsEqual(this.trade.chestInventory.getStack(i), this.trade.tradeItems.getProduct())){
                productTotal += this.trade.chestInventory.getStack(i).getCount();
                productSlots.add(i);
            }
        }

        //REMOVE NEEDED ITEMS - PRODUCT & COST

        if(
                costTotal >= this.trade.tradeItems.getCostStack().getCount() &&
                productTotal >= this.trade.tradeItems.getProductStack().getCount() &&
                //this.trade.canFit(this.trade.tradeItems.getCostStack())
                this.canFit()
        ){

            costCountdown = this.trade.tradeItems.getCostStack().getCount();
            productCountdown = this.trade.tradeItems.getProductStack().getCount();



            ItemStack stack;


            for(int slot: costSlots){

                if ( costCountdown > 0 ){

                    stack = this.getStack(slot);
                    if(costCountdown >= stack.getCount()){
                        costStacks.add(stack);
                        this.setStack(slot, Items.AIR.getDefaultStack());
                        costCountdown -= stack.getCount();
                    } else {
                        costStacks.add(stack.copyWithCount(costCountdown));
                        //this.setStack(slot, this.trade.tradeItems.getCostStack().copyWithCount(stack.getCount() - costCountdown));
                        this.setStack(slot, stack.copyWithCount(stack.getCount() - costCountdown));
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
                        productStacks.add(stack);
                        this.trade.chestInventory.setStack(slot, Items.AIR.getDefaultStack());
                        //ChestProtection.LOGGER.info("1removed item with air from "+ slot);
                        productCountdown -= stack.getCount();
                    } else {
                        productStacks.add(stack.copyWithCount(productCountdown));
                        //this.trade.chestInventory.setStack(slot, this.trade.tradeItems.getProductStack().copyWithCount(stack.getCount() - productCountdown));
                        this.trade.chestInventory.setStack(slot, stack.copyWithCount(stack.getCount() - productCountdown));
                        //ChestProtection.LOGGER.info("2reduced item count from " + stack.getCount() + " to " + (stack.getCount() - productCountdown));
                        //this.trade.chestInventory.setStack(slot, new ItemStack(this.trade.product.getItem(), stack.getCount() - productCountdown));
                        productCountdown = 0;
                    }

                } else {
                    break;
                }


            }

            // ADD NEEDED ITEMS - PRODUCT

            //costCountdown = this.trade.cost.getCount();
            productCountdown = this.trade.tradeItems.getProductStack().getCount();

            //ChestProtection.LOGGER.info("before "+ productStacks.toString());
            for(ItemStack productStack: productStacks){

                player.dropItem(this.addStack(productStack), false, false);
            }
            //ChestProtection.LOGGER.info("after "+ productStacks.toString());

            for(ItemStack costStack: costStacks){
                ItemStack remaining = this.trade.profitInventory.addStack(costStack);

                if(!remaining.isEmpty()){
                    ChestProtection.LOGGER.info("COULDNT FIT IT IN ????");
                }

            }


            /*
            for (int i = 0; i < this.size(); i++){
                stack = this.getStack(i);
                if(stack.isEmpty()) {

                    if(productCountdown > this.trade.tradeItems.getProductStack().getMaxCount()){
                        productCountdown -= this.trade.tradeItems.getProductStack().getMaxCount();
                        this.setStack(i, this.trade.tradeItems.getProductStack().copyWithCount(this.trade.tradeItems.getProductStack().getMaxCount()));
                        //ChestProtection.LOGGER.info("1added " + this.trade.tradeItems.getProductStack().getMaxCount() + " to " + i);
                        //this.setStack(i, new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()));
                    } else {
                        //ChestProtection.LOGGER.info("RUNN" + productCountdown);
                        this.setStack(i, this.trade.tradeItems.getProductStack().copyWithCount(productCountdown));
                        //ChestProtection.LOGGER.info("2added " + productCountdown + " to " + i);
                        //this.setStack(i, new ItemStack(this.trade.product.getItem(), productCountdown));
                        productCountdown = 0;

                    }
                }  ItemsEqual(stack, this.trade.tradeItems.getProduct()) && stack.getCount() != stack.getMaxCount()) {

                    if (productCountdown + stack.getCount() <= this.trade.tradeItems.getProductStack().getMaxCount()){
                        this.setStack(i, this.trade.tradeItems.getProductStack().copyWithCount(productCountdown + stack.getCount()));

                        //ChestProtection.LOGGER.info("3added " + (productCountdown + stack.getCount()) + " to " + i);
                        //this.setStack(i, new ItemStack(this.trade.product.getItem(), productCountdown + stack.getCount()));
                        productCountdown = 0;
                    } else {
                        this.setStack(i, this.trade.tradeItems.getProductStack().copyWithCount(this.trade.tradeItems.getProductStack().getMaxCount()));
                        //ChestProtection.LOGGER.info("4added " + (this.trade.tradeItems.getProductStack().getMaxCount()) + " to " + i);
                        //this.setStack(i, new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()));
                        productCountdown -= (this.trade.tradeItems.getProductStack().getMaxCount() - stack.getCount());
                    }

                }
            }*/

            /*

            if (productCountdown != 0) {

                for(int i = 0; i <= (productCountdown/this.trade.tradeItems.getProductStack().getMaxCount()); i++){

                    if (productCountdown > this.trade.tradeItems.getProductStack().getMaxCount()) {


                        //boolean wasAdded = this.player.getInventory().insertStack(new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()));
                        boolean wasAdded = this.player.getInventory().insertStack(this.trade.tradeItems.getProductStack().copyWithCount(this.trade.tradeItems.getProductStack().getMaxCount()));

                        if (!wasAdded) {

                            //this.player.dropItem(new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()), false);
                            this.player.dropItem(this.trade.tradeItems.getProductStack().copyWithCount(this.trade.tradeItems.getProductStack().getMaxCount()), false);

                        }
                        productCountdown -= this.trade.tradeItems.getProductStack().getMaxCount();
                    } else {

                        //boolean wasAdded = this.player.getInventory().insertStack(new ItemStack(this.trade.product.getItem(), productCountdown));
                        boolean wasAdded = this.player.getInventory().insertStack(this.trade.tradeItems.getProductStack().copyWithCount(productCountdown));

                        if (!wasAdded) {

                            //this.player.dropItem(new ItemStack(this.trade.product.getItem(), productCountdown), false);
                            this.player.dropItem(this.trade.tradeItems.getProductStack().copyWithCount(productCountdown), false);

                        }
                        productCountdown = 0;

                    }
                }
            }*/

            // COST
            /*

            costCountdown = this.trade.tradeItems.getCostStack().getCount();

            ProfitInventory profitInventory = this.trade.profitInventory;

            for (int i = 0; i < profitInventory.size(); i++){
                stack = profitInventory.getStack(i);
                if(stack.isEmpty()) {

                    if(costCountdown > this.trade.tradeItems.getCostStack().getMaxCount()){
                        costCountdown -= this.trade.tradeItems.getCostStack().getMaxCount();
                        profitInventory.set(i, this.trade.tradeItems.getCostStack().copyWithCount(this.trade.tradeItems.getCostStack().getMaxCount()));
                        //ChestProtection.LOGGER.info("1added " + this.trade.tradeItems.getProductStack().getMaxCount() + " to " + i);
                        //this.setStack(i, new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()));
                    } else {
                        //ChestProtection.LOGGER.info("RUNN" + productCountdown);
                        profitInventory.set(i, this.trade.tradeItems.getCostStack().copyWithCount(costCountdown));
                        //ChestProtection.LOGGER.info("2added " + productCountdown + " to " + i);
                        //this.setStack(i, new ItemStack(this.trade.product.getItem(), productCountdown));
                        costCountdown = 0;

                    }
                } ItemsEqual(stack, this.trade.tradeItems.getCost()) && stack.getCount() != stack.getMaxCount()) {

                    if (costCountdown + stack.getCount() <= this.trade.tradeItems.getCostStack().getMaxCount()){
                        profitInventory.set(i, this.trade.tradeItems.getCostStack().copyWithCount(costCountdown + stack.getCount()));

                        //ChestProtection.LOGGER.info("3added " + (productCountdown + stack.getCount()) + " to " + i);
                        //this.setStack(i, new ItemStack(this.trade.product.getItem(), productCountdown + stack.getCount()));
                        costCountdown = 0;
                    } else {
                        profitInventory.set(i, this.trade.tradeItems.getCostStack().copyWithCount(this.trade.tradeItems.getCostStack().getMaxCount()));
                        //ChestProtection.LOGGER.info("4added " + (this.trade.tradeItems.getProductStack().getMaxCount()) + " to " + i);
                        //this.setStack(i, new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()));
                        costCountdown -= (this.trade.tradeItems.getCostStack().getMaxCount() - stack.getCount());
                    }

                }
            }*/

            this.trade.writeProfitInventory();

            //this.trade.insertProfitInventory(this.trade.tradeItems.getCostStack());
        }

        screen.getBanner();
    }
}

