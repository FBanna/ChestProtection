package fbanna.chestprotection.ui.lock;


import com.mojang.authlib.GameProfile;

import eu.pb4.sgui.api.SguiUtils;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CPdata;
import fbanna.chestprotection.protect.types.Lock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static fbanna.chestprotection.ui.ControlTextures.ERROR_PLAYER;
import static fbanna.chestprotection.ui.ControlTextures.GUI_QUESTION_MARK;

public class editAuthorised extends AnvilInputGui {

    CPdata cpdata;

    NameAndId searchedPlayer = null;






    public editAuthorised(ServerPlayer player, CPdata cpdata) {
        super(player, true);
        super.setTitle(Component.literal("Edit Authorisation"));
        this.searchedPlayer = null;
        this.cpdata = cpdata;

        //updateSearchedPlayerResult();






    }

    private void updateSearchedPlayerResult() {
        //this.updateBack();

        if (this.searchedPlayer == null) {

            this.setSlot(2, new GuiElementBuilder(Items.GRAY_WOOL)
                    .setName(Component.literal("Could not find player!"))
                    .hideDefaultTooltip());

        } else {

            if (this.cpdata.getAuthorised().isAuthorised(this.searchedPlayer.id())) {

                this.setSlot(2, new GuiElementBuilder(Items.RED_WOOL)
                        .setItemName(Component.literal("Deauthorise %s?".formatted(this.searchedPlayer.name())))
                        .setCallback(() -> {

                            ChestProtection.LOGGER.info("deauthroised %s".formatted(this.searchedPlayer.name()));
                            this.cpdata.getAuthorised().removeAuthorised(this.searchedPlayer.id());

                            updateSearchedPlayerResult();
                        })
                        .hideDefaultTooltip());

            } else {

                this.setSlot(2, new GuiElementBuilder(Items.GREEN_WOOL)
                        .setItemName(Component.literal("Authorise %s?".formatted(this.searchedPlayer.name())))
                        .setCallback(() -> {
                            ChestProtection.LOGGER.info("authorised %s".formatted(this.searchedPlayer.name()));
                            this.cpdata.getAuthorised().addAuthorised(this.searchedPlayer.id());

                            updateSearchedPlayerResult();
                        })
                        .hideDefaultTooltip());

            }

        }
        //this.updateBack();


    }

    private void updateSearchedPlayer(String playerName) {

        if(playerName.length() == 0 || playerName.length() < 3) {

            this.setSlot(1, new GuiElementBuilder(Items.PLAYER_HEAD)
                    .setName(Component.literal("Invalid name!").withStyle(ChatFormatting.RED))
                    .hideDefaultTooltip()
                    .setProfileSkinTexture(GUI_QUESTION_MARK));


            this.searchedPlayer = null;
            updateSearchedPlayerResult();
            return;
        }

        ChestProtection.LOGGER.info("updating the player search field!");
        ChestProtection.LOGGER.info(this.cpdata.authorised.getAuthorised().toString());

        //this.player.level().getServer().services().profileRepository().findProfileByName("")

        CompletableFuture.supplyAsync(() -> this.player.level().getServer().services().nameToIdCache().get(playerName)).thenAccept((potentialProfile) -> {
            this.player.level().getServer().execute(() -> {


                if (potentialProfile.isEmpty()) {
                    this.setSlot(1, new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setItemName(Component.literal("Could not find player!").withStyle(ChatFormatting.RED))
                            .hideDefaultTooltip()
                            .setProfileSkinTexture(GUI_QUESTION_MARK));

                    this.searchedPlayer = null;


                } else {

                    NameAndId profile = potentialProfile.get();


                    this.setSlot(1, new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setItemName(Component.literal(profile.name()))
                            .hideDefaultTooltip()
                            .setProfile(profile.id())
                    );

                    searchedPlayer = profile;

                }

                updateSearchedPlayerResult();



            });



        });

        //this.updateBack();


    }


    @Override
    public void onInput(String input) {
        super.onInput(input);

        updateSearchedPlayer(input);
        //updateSearchedPlayerResult();

        //this.updateBack();

    }

    private void updateBack() {
        if (this.wrappedMenu != null) {
            SguiUtils.sendSlotUpdate(this.player, this.wrappedMenu.containerId, 2, this.getGuiElement(2).getItemStackForDisplay(this));
        }
    }
}
