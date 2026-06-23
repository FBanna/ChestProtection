package fbanna.chestprotection.ui.sellsetup;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.StringUtils;

public class SellSetupCountUI extends AnvilInputGui {

    private final SellSetupUI setupParent;

    private final int tradeItemSlot;

    public SellSetupCountUI(ServerPlayer player, SellSetupUI setupParent, int tradeItem) {
        this.setupParent = setupParent;
        super(player, false);

        this.tradeItemSlot = tradeItem;

        this.setSlot(1, this.setupParent.getTradeItem(this.tradeItemSlot).stack.copyWithCount(1));

        this.setDefaultInputValue(String.valueOf(this.setupParent.getTradeItem(this.tradeItemSlot).count));


        //this.setSlot(1, );
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
        //this.searchedName = input;

        //updateSearchedPlayer(input);


    }

    @Override
    public void close() {
        super.close();
        this.setupParent.open();
    }


}
