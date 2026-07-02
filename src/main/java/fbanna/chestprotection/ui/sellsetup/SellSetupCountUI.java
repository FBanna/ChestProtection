package fbanna.chestprotection.ui.sellsetup;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import fbanna.chestprotection.ChestProtection;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.StringUtils;

public class SellSetupCountUI extends AnvilInputGui {

    private final SellSetupUI setupParent;

    private final int tradeItemSlot;

    private boolean closed;

    public SellSetupCountUI(ServerPlayer player, SellSetupUI setupParent, int tradeItem) {
        this.setupParent = setupParent;
        super(player, false);

        this.tradeItemSlot = tradeItem;
        this.closed = false;

        this.setSlot(1, this.setupParent.getTradeItem(this.tradeItemSlot).stack.copyWithCount(1));

        this.setDefaultInputValue(String.valueOf(this.setupParent.getTradeItem(this.tradeItemSlot).count));

    }



    @Override
    public void onInput(String input) {
        super.onInput(input);

        if (input == null || input == "") {

            this.setSlot(2, new GuiElementBuilder(Items.WOOL.red())
                    .hideDefaultTooltip()
                    .setName(Component.literal("No item count provided!").withStyle(ChatFormatting.RED))
            );

        } else if (!StringUtils.isNumeric(input)) {

            this.setSlot(2, new GuiElementBuilder(Items.WOOL.red())
                    .hideDefaultTooltip()
                    .setName(Component.literal("Input is not a number!").withStyle(ChatFormatting.RED))
            );

        } else {

            this.setSlot(2, new GuiElementBuilder(Items.WOOL.green())
                    .hideDefaultTooltip()
                    .setName(Component.literal("Set item count?"))
                    .setCallback(() -> {

                        this.setupParent.setCount(this.tradeItemSlot, Integer.parseInt(input));
                        this.close();
                    })
            );


        }

    }


    // Added to prevent item loss for geyser accounts
    @Override
    public void onTick() {

        if (this.closed){
            this.setupParent.close();
            this.close();
        }

    }

    @Override
    public void afterRemoval() {

        if(!this.closed) {

            this.closed = true;
            this.setupParent.beforeReopen();
            this.setupParent.open();

        }

        super.afterRemoval();

    }


}
