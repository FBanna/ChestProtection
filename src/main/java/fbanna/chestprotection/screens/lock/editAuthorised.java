package fbanna.chestprotection.screens.lock;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class editAuthorised extends SimpleGui {

    public editAuthorised(ServerPlayer player){
        super(MenuType.ANVIL, player, false);

        this.setTitle(Component.literal("Edit Lock"));

    }

}
