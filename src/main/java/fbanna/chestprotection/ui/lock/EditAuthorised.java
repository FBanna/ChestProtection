package fbanna.chestprotection.ui.lock;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.yggdrasil.response.NameAndId;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckProtected;
import fbanna.chestprotection.util.CustomProfileRepository;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.LightBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static fbanna.chestprotection.ui.ControlTextures.*;

public class EditAuthorised extends AnvilInputGui {

    private final CheckProtected cp;

    private final MinecraftServer server;
    private String searchedName = "";
    private NameAndId searchedPlayerResult = null;
    private final GameProfileRepository profileRepo;
    private int currentPage = 0;
    private boolean showAuthorised = false;

    private static final int PAGESIZE = 27;


    public EditAuthorised(ServerPlayer player, CheckProtected cp) {

        super(player, true);
        super.setTitle(Component.literal("Edit Authorisation"));
        this.searchedPlayerResult = null;
        this.searchedName = "";
        this.cp = cp;

        this.server = player.level().getServer();

        this.profileRepo = new CustomProfileRepository(server.getProxy());



        //ChestProtection.LOGGER.info(String.valueOf(this.getSize()));


        for (int i = 30; i < 39; i++) {
            this.setSlot(i, new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray()).hideDefaultTooltip().setName(Component.empty()));
        }

        updateOnlinePages();

    }


    private void updateOnlinePages() {



        ArrayList<GameProfile> players = new ArrayList<>();

        if (showAuthorised) {
            ArrayList<UUID> playersUUID = this.cp.cpdata.getAuthorised().getAuthorised();

            for (UUID id: playersUUID) {

                Optional<GameProfile> optionalGameProfile = this.server.services().profileResolver().fetchById(id);

                if (optionalGameProfile.isEmpty()) {
                    ChestProtection.LOGGER.info("Could not find a player!");
                    continue;
                }

                players.add(optionalGameProfile.get());

            }
        } else {

            for(ServerPlayer player: this.server.getPlayerList().getPlayers()) {
                players.add(player.getGameProfile());
            }

        }


        int pages =  (int) Math.ceil(players.size() / ((double) PAGESIZE));

        //ChestProtection.LOGGER.info(String.valueOf(pages));


        // RESET

        if (showAuthorised) {
            this.setSlot(38, new GuiElementBuilder(LightBlock.setLightOnStack(new ItemStack(Items.LIGHT), 15))
                    .setName(Component.literal("Show online players").withStyle(ChatFormatting.GRAY))
                    .setCallback(() -> {
                        this.showAuthorised = false;
                        updateOnlinePages();
                    })
                    .hideDefaultTooltip()
            );
        } else {

            this.setSlot(38, new GuiElementBuilder(LightBlock.setLightOnStack(new ItemStack(Items.LIGHT), 0))
                    .setName(Component.literal("Show only authorised").withStyle(ChatFormatting.GRAY))
                    .setCallback(() -> {
                        this.showAuthorised = true;
                        updateOnlinePages();
                    })
                    .hideDefaultTooltip()
            );
        }

//        this.setSlot(38, new GuiElementBuilder(Items.LIGHT)
//                .setName(Component.literal("View Authorised").withStyle(ChatFormatting.GRAY))
//                .setCallback()
//                .hideDefaultTooltip()
//        );

        this.setSlot(33, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setProfileSkinTexture(GUI_PREVIOUS_PAGE)
                .setName(Component.literal("Previous").withStyle(ChatFormatting.GRAY))
                .setCallback(() -> {

                    this.currentPage--;
                    updateOnlinePages();

                })
                .hideDefaultTooltip()
        );

        this.setSlot(35, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setProfileSkinTexture(GUI_NEXT_PAGE)
                .setName(Component.literal("Next").withStyle(ChatFormatting.GRAY))
                .setCallback(() -> {

                    this.currentPage++;
                    updateOnlinePages();

                })
                .hideDefaultTooltip()
        );

        for (int i = 0; i < PAGESIZE; i ++) {

            this.clearSlot(i+3);

        }

        // BUTTONS


        if (this.currentPage == 0 || pages == 0) {

            this.setSlot(33, new GuiElementBuilder(Items.PLAYER_HEAD)
                    .setProfileSkinTexture(GUI_PREVIOUS_PAGE_BLOCKED)
                    .setName(Component.literal("Previous").withStyle(ChatFormatting.GRAY))
                    .hideDefaultTooltip()
            );



        }



        if((pages - 1) == this.currentPage || pages == 0) {

            this.setSlot(35, new GuiElementBuilder(Items.PLAYER_HEAD)
                    .setProfileSkinTexture(GUI_NEXT_PAGE_BLOCKED)
                    .setName(Component.literal("Next").withStyle(ChatFormatting.GRAY))
                    .hideDefaultTooltip()
            );

        }



        for (int i = 0; i < PAGESIZE; i ++) {

            //ChestProtection.LOGGER.info("slot index: " + String.valueOf(i));


            int playerNum = (PAGESIZE*this.currentPage) + i;

            if(playerNum == (players.size())){
                //ChestProtection.LOGGER.info("BREAK");
                break;
            }

            GameProfile player = players.get(playerNum);

            //ChestProtection.LOGGER.info("player index: " + String.valueOf(playerNum));

            this.setSlot(i+3, new GuiElementBuilder(Items.PLAYER_HEAD)
                    .setProfile(player.id())
                    .setName(Component.literal(player.name()))
                    .setCallback(() -> {
                        this.playerAuthoriseScreen(player);
                    })
                    .hideDefaultTooltip()

            );

//            if (playerNum == (players.size()-1)) {
//                //ChestProtection.LOGGER.info("BREAK");
//                break;
//            }

        }

    }

    private void playerAuthoriseScreen(GameProfile selectedPlayer) {

        class playerAuthoriseScreen extends SimpleGui {

            private final EditAuthorised oldGui;

            public playerAuthoriseScreen(ServerPlayer player, EditAuthorised oldGui, GameProfile selectedPlayer, CheckProtected cp) {
                this.oldGui = oldGui;
                oldGui.close();
                super(MenuType.HOPPER, player, false);
                this.open();

                this.setTitle(Component.literal("Authorise %s?".formatted(selectedPlayer.name())));

                this.setSlot(0, new GuiElementBuilder(Items.PLAYER_HEAD)
                        .setProfile(selectedPlayer.id())
                        .setName(Component.literal(selectedPlayer.name()))
                        .hideDefaultTooltip()
                );

                updateAuthorisedWool(selectedPlayer);

                for (int i = 2; i < 4; i ++) {
                    this.setSlot(i, new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray()).hideDefaultTooltip().setName(Component.empty()));
                }

                this.setSlot(4, new GuiElementBuilder(Items.OAK_DOOR)
                        .setName(Component.literal("Quit").withStyle(ChatFormatting.GRAY))
                        .setCallback(() -> this.close())
                        .hideDefaultTooltip()
                );
            }

            private void updateAuthorisedWool(GameProfile player) {


                if (cp.cpdata.getAuthorised().isAuthor(player.id())) {

                    this.setSlot(1, new GuiElementBuilder(Items.BARRIER)
                            .setName(Component.literal("Cannot deauthorise author!").withStyle(ChatFormatting.RED))
                            .hideDefaultTooltip());


                } else if (cp.cpdata.getAuthorised().isAuthorised(player.id())) {

                    this.setSlot(1, new GuiElementBuilder(Items.WOOL.red())
                            .setName(Component.literal("Deauthorise %s?".formatted(player.name())))
                            .setCallback(() -> {

                                //ChestProtection.LOGGER.info("deauthroised %s".formatted(player.name()));
                                cp.removeAuthorised(player.id());

                                updateAuthorisedWool(player);
                            })
                            .hideDefaultTooltip());

                } else {

                    this.setSlot(1, new GuiElementBuilder(Items.WOOL.green())
                            .setName(Component.literal("Authorise %s?".formatted(player.name())))
                            .setCallback(() -> {
                                //ChestProtection.LOGGER.info("authorised %s".formatted(player.name()));
                                cp.addAuthorised(player.id());

                                updateAuthorisedWool(player);
                            })
                            .hideDefaultTooltip());

                }

            }

            @Override
            public void close() {
                super.close();
                oldGui.open();
                oldGui.updateOnlinePages();
            }


        }

        SimpleGui playerAuthoriseGui = new playerAuthoriseScreen(this.player, this, selectedPlayer, this.cp);


    }


    private void updateSearchedPlayerResult() {

        if (this.searchedPlayerResult == null) {

            this.setSlot(2, new GuiElementBuilder(Items.WOOL.gray())
                    .setName(Component.literal("Could not find player!").withStyle(ChatFormatting.RED))
                    .hideDefaultTooltip());

        } else {

            if (this.cp.cpdata.getAuthorised().isAuthor(this.searchedPlayerResult.id())) {


                this.setSlot(2, new GuiElementBuilder(Items.BARRIER)
                        .setName(Component.literal("Cannot deauthorise author!").withStyle(ChatFormatting.RED))
                        .hideDefaultTooltip());


            } else if (this.cp.cpdata.getAuthorised().isAuthorised(this.searchedPlayerResult.id())) {

                this.setSlot(2, new GuiElementBuilder(Items.WOOL.red())
                        .setName(Component.literal("Deauthorise %s?".formatted(this.searchedPlayerResult.name())))
                        .setCallback(() -> {

                            //ChestProtection.LOGGER.info("deauthroised %s".formatted(this.searchedPlayerResult.name()));
                            this.cp.removeAuthorised(this.searchedPlayerResult.id());

                            updateSearchedPlayerResult();
                            updateOnlinePages();
                        })
                        .hideDefaultTooltip());

            } else {

                this.setSlot(2, new GuiElementBuilder(Items.WOOL.green())
                        .setName(Component.literal("Authorise %s?".formatted(this.searchedPlayerResult.name())))
                        .setCallback(() -> {
                            //ChestProtection.LOGGER.info("authorised %s".formatted(this.searchedPlayerResult.name()));
                            this.cp.addAuthorised(this.searchedPlayerResult.id());

                            updateSearchedPlayerResult();
                            updateOnlinePages();
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

        //ChestProtection.LOGGER.info("updating the player search field!");
        //ChestProtection.LOGGER.info(this.cp.cpdata.authorised.getAuthorised().toString());


        CompletableFuture.supplyAsync(() -> this.profileRepo.findProfileByName(playerName)).thenAccept((potentialProfile) -> {
            this.server.execute(() -> {

                if (playerName != this.searchedName) {
                    //ChestProtection.LOGGER.info("WE JUST EXPERIENCED A COLLISION!");
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
    }

    @Override
    public void onInput(String input) {
        super.onInput(input);
        this.searchedName = input;

        updateSearchedPlayer(input);


    }


}
