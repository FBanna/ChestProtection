package fbanna.chestprotection.protect.types;

import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.server.level.ServerPlayer;

public class Clear extends CheckProtected {

//    private CheckProtected cp;
//
//    public Clear(CheckProtected cp){
//
//        this.cp = cp;
//
//    }

    @Override
    public boolean open(ServerPlayer player) {

        if (super.chestStatus == ProtectedStatus.LOCK){
            return false;
        }

        return true;
    }
}
