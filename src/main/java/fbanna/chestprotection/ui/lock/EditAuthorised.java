package fbanna.chestprotection.ui.lock;


import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.yggdrasil.response.NameAndId;
import eu.pb4.sgui.api.SguiUtils;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CPdata;
import fbanna.chestprotection.protect.types.Lock.LockBook;
import fbanna.chestprotection.util.CustomProfileRepository;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static fbanna.chestprotection.ui.ControlTextures.GUI_QUESTION_MARK;

public class EditAuthorised extends AnvilInputGui {

    LockBook cp;

    String searchedName = "";
    NameAndId searchedPlayerResult = null;
    GameProfileRepository profileRepo;






    public EditAuthorised(ServerPlayer player, LockBook cp) {
        super(player, true);
        super.setTitle(Component.literal("Edit Authorisation"));
        this.searchedPlayerResult = null;
        this.searchedName = "";
        this.cp = cp;

        MinecraftServer server = player.level().getServer();

        this.profileRepo = new CustomProfileRepository(server.getProxy());

        //updateSearchedPlayerResult();






    }

    private void updateSearchedPlayerResult() {
        //this.updateBack();

        if (this.searchedPlayerResult == null) {

            this.setSlot(2, new GuiElementBuilder(Items.WOOL.gray())
                    .setName(Component.literal("Could not find player!").withStyle(ChatFormatting.RED))
                    .hideDefaultTooltip());

        } else {

            if (this.cp.cpdata.getAuthorised().isAuthor(this.searchedPlayerResult.id())) {


                this.setSlot(2, new GuiElementBuilder(Items.BARRIER)
                        .setName(Component.literal("Cannot deauthorise author!").withStyle(ChatFormatting.RED))
                        .hideDefaultTooltip());


            }

            else if (this.cp.cpdata.getAuthorised().isAuthorised(this.searchedPlayerResult.id())) {

                this.setSlot(2, new GuiElementBuilder(Items.WOOL.red())
                        .setName(Component.literal("Deauthorise %s?".formatted(this.searchedPlayerResult.name())))
                        .setCallback(() -> {

                            ChestProtection.LOGGER.info("deauthroised %s".formatted(this.searchedPlayerResult.name()));
                            this.cp.removeAuthorised(this.searchedPlayerResult.id());

                            updateSearchedPlayerResult();
                        })
                        .hideDefaultTooltip());

            } else {

                this.setSlot(2, new GuiElementBuilder(Items.WOOL.green())
                        .setName(Component.literal("Authorise %s?".formatted(this.searchedPlayerResult.name())))
                        .setCallback(() -> {
                            ChestProtection.LOGGER.info("authorised %s".formatted(this.searchedPlayerResult.name()));
                            this.cp.addAuthorised(this.searchedPlayerResult.id());

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


            this.searchedPlayerResult = null;
            updateSearchedPlayerResult();
            return;
        }

        ChestProtection.LOGGER.info("updating the player search field!");
        ChestProtection.LOGGER.info(this.cp.cpdata.authorised.getAuthorised().toString());


        CompletableFuture.supplyAsync(() -> this.profileRepo.findProfileByName(playerName)).thenAccept((potentialProfile) -> {
            this.player.level().getServer().execute(() -> {

                if (playerName != this.searchedName) {
                    ChestProtection.LOGGER.info("WE JUST EXPERIENCED A COLLISION!");
                    return;
                }


                if (potentialProfile.isEmpty()) {
                    this.setSlot(1, new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setName(Component.literal("Could not find player!").withStyle(ChatFormatting.RED))
                            .hideDefaultTooltip()
                            .setProfileSkinTexture(GUI_QUESTION_MARK));

                    this.searchedPlayerResult = null;


                } else {

                    NameAndId profile = potentialProfile.get();


                    this.setSlot(1, new GuiElementBuilder(Items.PLAYER_HEAD)
                            .setName(Component.literal(profile.name()))
                            .hideDefaultTooltip()
                            .setProfile(profile.id())
                    );

                    searchedPlayerResult = profile;

                }

                updateSearchedPlayerResult();



            });



        });

        //this.updateBack();


    }


    @Override
    public void onInput(String input) {
        super.onInput(input);
        this.searchedName = input;

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
