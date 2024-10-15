package fbanna.chestprotection.trade.setup;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.check.CheckChest;
import fbanna.chestprotection.trade.TradeInventory;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.*;
import java.util.function.Predicate;

public class SetupInventory extends SimpleInventory {

    PlayerEntity player;
    CheckChest trade;
    SimpleGui screen;

    public SetupInventory(ServerPlayerEntity player, CheckChest trade, SimpleGui screen){
        super(2);
        this.player = player;
        this.trade = trade;
        this.screen = screen;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        super.setStack(slot, stack);

        if(stack.isEmpty()){
            for(int i = (slot*27); i<(slot*27+17); i++) {
                screen.setSlot(i, ItemStack.EMPTY);
            }
        } else {

            int pos = slot * 27;

            {
                GuiElementBuilder component = new GuiElementBuilder()
                        .setItem(Items.GREEN_WOOL)
                        .setName(Text.of("Item"));

                component.setCallback((index, clickType, action, gui) -> {

                    if(Objects.requireNonNull(gui.getSlot(index)).getItemStack().getItem().equals(Items.GREEN_WOOL)) {
                        gui.setSlot(index, component.setItem(Items.RED_WOOL));
                    } else {
                        gui.setSlot(index, component.setItem(Items.GREEN_WOOL));
                    }

                });

                screen.setSlot(pos, component);
            }

            pos++;


            for (ComponentType<?> type: stack.getComponents().getTypes()) {
                ChestProtection.LOGGER.info(type.toString(), pos);

                GuiElementBuilder component = new GuiElementBuilder()
                        .setItem(Items.GREEN_WOOL)
                        .setName(Text.of(type.toString()));

                component.setCallback((index, clickType, action, gui) -> {

                    if(Objects.requireNonNull(gui.getSlot(index)).getItemStack().getItem().equals(Items.GREEN_WOOL)) {
                        gui.setSlot(index, component.setItem(Items.RED_WOOL));
                    } else {
                        gui.setSlot(index, component.setItem(Items.GREEN_WOOL));
                    }



                    ChestProtection.LOGGER.info(gui.getSlot(index).getItemStack() + type.toString());

                });

                screen.setSlot(pos, component);
                pos++;
            }
        }
    }

    public void dropAll() {
        ItemStack[] stacks = new ItemStack[2];

        boolean noItem = false;
        for (int slot = 0; slot < 2; slot++) {
            ChestProtection.LOGGER.info(String.valueOf(slot));
            ItemStack stack = this.getStack(slot).copy();

            int pos = slot * 27;

            if(Objects.requireNonNull(screen.getSlot(pos)).getItemStack().getItem().equals(Items.RED_WOOL)){
                stack = stack.withItem(Items.AIR);
                noItem = true;
            }

            pos++;


            for (ComponentType<?> type: this.getStack(slot).getComponents().getTypes()) {

                if(type.equals(DataComponentTypes.CONTAINER)) {

                }

                if(Objects.requireNonNull(screen.getSlot(pos)).getItemStack().getItem().equals(Items.RED_WOOL)) {
                    stack.remove(type);
                }
                pos++;
            }

            stacks[slot] = stack;
        }

        /*

        ItemStack[] stacks = {
                this.getStack(0),
                this.getStack(1)
        };*/


        ChestProtection.LOGGER.info(Arrays.toString(stacks) + stacks[0].getCount());
        this.trade.saveTrade(stacks);

        for (int slot = 0; slot < 2; slot++) {
            ItemStack stack = this.getStack(slot);
            if(stack != null){
                this.player.dropItem(stack, false);
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
