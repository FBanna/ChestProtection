package fbanna.chestprotection.trade;

import fbanna.chestprotection.check.CheckChest;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;

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

    public boolean checkTrade(){

        int total = 0;
        ItemStack stack;

        for(int i = 0; i < this.size(); i++){
            stack = this.getStack(i);
            if (stack.getItem() == this.trade.cost.getItem()){
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
            if (this.getStack(i).getItem() == this.trade.cost.getItem()){
                costTotal += this.getStack(i).getCount();
                costSlots.add(i);
            }
        }



        for(int i = 0; i < this.trade.chestInventory.size(); i++){
            if (this.trade.chestInventory.getStack(i).getItem() == this.trade.product.getItem()){
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

                        this.setStack(slot, new ItemStack(this.trade.cost.getItem(), stack.getCount() - costCountdown));
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
                        productCountdown -= stack.getCount();
                    } else {

                        this.trade.chestInventory.setStack(slot, new ItemStack(this.trade.product.getItem(), stack.getCount() - productCountdown));
                        productCountdown = 0;
                    }

                } else {
                    break;
                }


            }

            // ADD NEEDED ITEMS

            costCountdown = this.trade.cost.getCount();
            productCountdown = this.trade.product.getCount();



            for (int i = 0; i < this.size(); i++){
                stack = this.getStack(i);
                if(stack.isEmpty()) {

                    if(productCountdown > this.trade.product.getMaxCount()){
                        productCountdown -= this.trade.product.getMaxCount();
                        this.setStack(i, new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()));
                    } else {
                        //ChestProtection.LOGGER.info("RUNN" + productCountdown);
                        this.setStack(i, new ItemStack(this.trade.product.getItem(), productCountdown));
                        productCountdown = 0;

                    }
                } else if ( stack.getItem() == this.trade.product.getItem() && stack.getCount() != stack.getMaxCount()) {


                    if (productCountdown + stack.getCount() <= this.trade.product.getMaxCount()){
                        this.setStack(i, new ItemStack(this.trade.product.getItem(), productCountdown + stack.getCount()));
                        productCountdown = 0;
                    } else {
                        this.setStack(i, new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()));
                        productCountdown -= (this.trade.product.getMaxCount() - stack.getCount());
                    }

                }
            }

            if (productCountdown != 0) {

                for(int i = 0; i <= (productCountdown/this.trade.product.getMaxCount()); i++){

                    if (productCountdown > this.trade.product.getMaxCount()) {


                        boolean wasAdded = this.player.getInventory().insertStack(new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()));

                        if (!wasAdded) {

                            this.player.dropItem(new ItemStack(this.trade.product.getItem(), this.trade.product.getMaxCount()), false);

                        }
                        productCountdown -= this.trade.product.getMaxCount();
                    } else {

                        boolean wasAdded = this.player.getInventory().insertStack(new ItemStack(this.trade.product.getItem(), productCountdown));

                        if (!wasAdded) {

                            this.player.dropItem(new ItemStack(this.trade.product.getItem(), productCountdown), false);

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

