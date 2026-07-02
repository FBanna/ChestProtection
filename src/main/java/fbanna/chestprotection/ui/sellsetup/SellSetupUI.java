package fbanna.chestprotection.ui.sellsetup;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.types.sell.SellBook;
import fbanna.chestprotection.protect.data.sell.TradeItem;
import fbanna.chestprotection.ui.lock.EditAuthorised;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import static fbanna.chestprotection.ui.ControlTextures.GUI_QUESTION_MARK;
import static fbanna.chestprotection.util.TradeInventoryUtils.COMPONENT_BLACK_LIST;

public class SellSetupUI extends SimpleGui {

    private final SellBook cp;

    private TradeItem[] tradeItems;

    private SellSetupInventory container;
    private boolean isCountMenu;

//            {
//            DataComponents.ENCHANTABLE,
//            DataComponents.ITEM_MODEL
//    };

//    private static final int[] light_grey_panes = {2, 11, 20, 29, 38, 47, 6, 15, 24, 33, 42, 51};
//    private static final int[] black_panes = {4, 13, 22, 31};

    public SellSetupUI(ServerPlayer player, SellBook cp) {

        this.cp = cp;
        this.tradeItems = new TradeItem[]{
                new TradeItem(ItemStack.EMPTY, 0, true),
                new TradeItem(ItemStack.EMPTY, 0, true)
        };

        super(MenuType.GENERIC_9x5, player, false);

        this.container = new SellSetupInventory(this);
        this.isCountMenu = false;

        this.setSlot(18, new Slot(this.container, 0,0,0));
        this.setSlot(26, new Slot(this.container, 1,0,0));


        super.setTitle(Component.literal("Setup Shop"));

        for (int i = 20; i < 25; i++) {
            this.setSlot(i, new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray())
                    .setName(Component.empty())
                    .hideDefaultTooltip()
            );
        }

        this.setSlot(21, new GuiElementBuilder(Items.OAK_DOOR)
                .setName(Component.literal("Quit").withStyle(ChatFormatting.GRAY))
                .setCallback(() -> {

                    boolean isAllowed = this.cp.openProtectedInventory(this.player);

                    if (isAllowed) {
                        this.close();
                    }
                })
                .hideDefaultTooltip()
        );

        this.setSlot(23, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setProfileSkinTexture(GUI_QUESTION_MARK)
                .setName(Component.literal("Edit Authorisation"))
                .setCallback(() -> {
                    EditAuthorised ea = new EditAuthorised(player, cp);
                    this.close();
                    ea.open();
                })
                .hideDefaultTooltip()

        );



        this.updateUI();

//        for (int i: light_grey_panes) {
//            this.setSlot(i, new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray())
//                    .setName(Component.empty())
//                    .hideDefaultTooltip()
//            );
//        }
//
//        for (int i: black_panes) {
//            this.setSlot(i, new GuiElementBuilder(Items.STAINED_GLASS_PANE.black())
//                    .setName(Component.empty())
//                    .hideDefaultTooltip()
//            );
//        }

        //new ItemStack(Items.WOOL.gray()).tags().forEach(itemTagKey -> ChestProtection.LOGGER.info(itemTagKey.toString()));
//        new ItemStack(Items.DYED_SHULKER_BOX.lime()).tags().forEach(itemTagKey -> ChestProtection.LOGGER.info(itemTagKey.toString()));


    }

    public void updateUI() {
        this.updateConfirmButton();
        this.updateCountButtons();
        this.updateComponentSelector();
    }

    private void updateConfirmButton() {

        for (TradeItem tradeItem: this.tradeItems) {

            if (tradeItem.stack == null || tradeItem.stack == ItemStack.EMPTY) {
                this.setSlot(22, new GuiElementBuilder(Items.WOOL.red())
                        .setName(Component.literal("Invalid"))
                        .setCallback(() -> {
                            this.close();

                        })
                        .hideDefaultTooltip()
                );

                return;
            }
        }

        this.setSlot(22, new GuiElementBuilder(Items.WOOL.green())
                .setName(Component.literal("Confirm"))
                .setCallback(() -> {
                    this.cp.cpdata.sellData.get().setTradeItems(this.tradeItems);
                    this.cp.writeCPdata();
                    this.close();
                })
                .hideDefaultTooltip()
        );

    }

    private void updateCountButtons() {

        int i = 0;

        for (TradeItem tradeItem: this.tradeItems) {

            if (tradeItem.stack == null || tradeItem.stack == ItemStack.EMPTY) {
                //this.clearSlot(i*7+19);

                this.setSlot(i*6 + 19, new GuiElementBuilder(Items.PAPER)
                        .setName(Component.literal("Place item first!").withStyle(ChatFormatting.RED))
                        .hideDefaultTooltip()
                );

            } else {


                int j = i;

                this.setSlot(i*6 + 19, new GuiElementBuilder(Items.PAPER)
                        .setName(Component.literal("Change count from %d".formatted(this.getTradeItem(j).count)).withStyle(ChatFormatting.GRAY))
                        .setCallback(() -> {

                            this.isCountMenu = true;

                            SimpleGui select_count = new SellSetupCountUI(this.player, this, j);
                            select_count.open();
                        })
                        .hideDefaultTooltip()
                );


            }


            i++;
        }

    }



    private void updateComponentSelector() {

        int pos = 0; // slot index
        int i = 0; // tradeItem index



        for (ItemStack stack: this.container.items) {

            if (!stack.isEmpty()) {
                //ChestProtection.LOGGER.info(stack.toString());

                int i_final = i;

                if (this.tradeItems[i].isItem()) {

                    this.setSlot(pos, new GuiElementBuilder(Items.WOOL.green())
                            .setName(Component.literal("Item"))
                            .setCallback(() -> {

                                this.tradeItems[i_final].isItem = false;
                                this.updateUI();

                            })
                            .hideDefaultTooltip()
                    );


                } else {

                    this.setSlot(pos, new GuiElementBuilder(Items.WOOL.red())
                            .setName(Component.literal("Item"))
                            .setCallback(() -> {

                                this.tradeItems[i_final].isItem = true;
                                this.updateUI();

                            })
                            .hideDefaultTooltip()
                    );
                }

                pos++;


                for (TypedDataComponent<?> component: stack.getComponents().filter(c -> !COMPONENT_BLACK_LIST.contains(c))) {

                    if (component.type().equals(DataComponents.ENCHANTMENTS)){
                        TypedDataComponent<ItemEnchantments> enchants = (TypedDataComponent<ItemEnchantments>) component;

                        if (enchants.value().isEmpty()) {
                            this.tradeItems[i].stack.remove(DataComponents.ENCHANTMENTS);
                            //ChestProtection.LOGGER.info("skipping empty enchants");
                            continue;
                        }


                    }

                    placeComponentSelector(
                            component,
                            pos,
                            i,
                            this.tradeItems[i].stack.has(component.type())
                    );

                    pos++;

                }
            }

            // Clear old slots
            while (pos != 18 && pos != 45) {
                this.clearSlot(pos);
                pos++;
            }

            pos = 27;
            i++;
        }
    }

    private void placeComponentSelector(TypedDataComponent<?> component, int pos, int i, boolean isPresent) {
        if (isPresent) {

            this.setSlot(pos, new GuiElementBuilder(Items.WOOL.green())
                    .setName(Component.nullToEmpty(component.type().toString()))
                    .setCallback(() -> {

                        this.tradeItems[i].stack.remove(component.type());
                        this.updateUI();

                    })
                    .hideDefaultTooltip()
            );


        } else {

            this.setSlot(pos, new GuiElementBuilder(Items.WOOL.red())
                    .setName(Component.nullToEmpty(component.type().toString()))
                    .setCallback(() -> {

                        this.tradeItems[i].stack.applyComponents(DataComponentPatch.builder().set(component).build());
                        this.updateUI();

                    })
                    .hideDefaultTooltip()
            );
        }
    }

    public void setCount(int slot, int count){
        this.tradeItems[slot].count = count;
        this.updateUI();
    }

    public void beforeReopen() {
        this.isCountMenu = false;
        this.updateUI();
    }


    protected void setTradeItems(NonNullList<ItemStack> items) {
        int i = 0;
        for (ItemStack stack: items) {



            this.tradeItems[i].stack = stack.copy();

            this.tradeItems[i].count = stack.count();

            i++;


        }

        this.updateUI();
    }

    public TradeItem getTradeItem(int slot) {

        return this.tradeItems[slot];

    }


    @Override
    public void onRemoved() {
        if (!this.isCountMenu) {

            this.container.dropAll(this.player);

        }

        super.onRemoved();
    }

    //protected void updateCount()
}


