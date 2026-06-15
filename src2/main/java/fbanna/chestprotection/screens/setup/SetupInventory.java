package fbanna.chestprotection.ui.setup;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.protect.CheckProtected;
import java.util.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SetupInventory extends SimpleContainer {

    Player player;
    CheckProtected trade;
    SimpleGui screen;

    public SetupInventory(ServerPlayer player, CheckProtected trade, SimpleGui screen){
        super(2);
        this.player = player;
        this.trade = trade;
        this.screen = screen;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);

        if(stack.isEmpty()){
            for(int i = (slot*27); i<(slot*27+17); i++) {
                screen.setSlot(i, ItemStack.EMPTY);
            }
        } else {

            int pos = slot * 27;

            {
                GuiElementBuilder component = new GuiElementBuilder()
                        .setItem(Items.GREEN_WOOL)
                        .setName(Component.nullToEmpty("Item"));

                component.setCallback((index, clickType, action, gui) -> {



                    if(Objects.requireNonNull(gui.getGuiElement(index)).getItemStack().getItem().equals(Items.GREEN_WOOL)) {
                        gui.setSlot(index, component.setItem(Items.RED_WOOL));
                    } else {
                        gui.setSlot(index, component.setItem(Items.GREEN_WOOL));
                    }

                });

                screen.setSlot(pos, component);
            }

            pos++;


            for (DataComponentType<?> type: stack.getComponents().keySet()) {
                //ChestProtection.LOGGER.info(type.toString(), pos);

                GuiElementBuilder component = new GuiElementBuilder()
                        .setItem(Items.GREEN_WOOL)
                        .setName(Component.nullToEmpty(type.toString()));

                component.setCallback((index, clickType, action, gui) -> {

                    if(Objects.requireNonNull(gui.getGuiElement(index)).getItemStack().getItem().equals(Items.GREEN_WOOL)) {
                        gui.setSlot(index, component.setItem(Items.RED_WOOL));
                    } else {
                        gui.setSlot(index, component.setItem(Items.GREEN_WOOL));
                    }



                    //ChestProtection.LOGGER.info(gui.getSlot(index).getItemStack() + type.toString());

                });

                screen.setSlot(pos, component);
                pos++;
            }
        }
    }

    public void dropAll() {
        boolean[] isItem = {true,true};
        ItemStack[] stacks = new ItemStack[2];

        for (int slot = 0; slot < 2; slot++) {


            //ChestProtection.LOGGER.info(String.valueOf(slot));
            ItemStack stack = this.getItem(slot).copy();

            if(!stack.isEmpty()){
                int pos = slot * 27;

                //ChestProtection.LOGGER.info(pos +", "+ screen.getSlot(pos).getItemStack());

                if(Objects.requireNonNull(screen.getGuiElement(pos)).getItemStack().getItem().equals(Items.RED_WOOL)){

                    isItem[slot] = false;

                    /*for(ComponentType<?> type: stack.getComponents().getTypes()){
                        stack.remove(type);
                    }*/

                }

                pos++;


                for (DataComponentType<?> type: this.getItem(slot).getComponents().keySet()) {

                /*if(type.equals(DataComponentTypes.CONTAINER)) {

                }*/

                    if(Objects.requireNonNull(screen.getGuiElement(pos)).getItemStack().getItem().equals(Items.RED_WOOL)) {
                        stack.remove(type);
                    }
                    pos++;
                }

                stacks[slot] = stack;
            }
        }

        /*

        ItemStack[] stacks = {
                this.getStack(0),
                this.getStack(1)
        };*/


        //ChestProtection.LOGGER.info(Arrays.toString(stacks) + stacks[0].getCount());
        this.trade.saveTrade(isItem,stacks);

        for (int slot = 0; slot < 2; slot++) {
            ItemStack stack = this.getItem(slot);
            if(stack != null){
                this.player.drop(stack, false);
            }
        }
        /*
        ItemStack stack;
        for(int i = 0; i < this.size(); i++){
            stack = this.getStack(i);
            if (!stack.isEmpty()){

                boolean wasAdded = this.player.getInventory().insertStack(stack);

                if (!wasAdded) {

                    this.player.dropItem(stack, false);

                }
            }
        }*/
    }
}
