package fbanna.chestprotection.protect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fbanna.chestprotection.ChestProtection;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.core.util.UuidUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Authorised {

    private final UUID author;
    private final List<UUID> authorised;

    public static final MapCodec<Authorised> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("author").forGetter(Authorised::getAuthor),
            UUIDUtil.CODEC.listOf().fieldOf("authorised").forGetter(Authorised::getAuthorised)
    ).apply(instance, Authorised::new));


    public Authorised(UUID author, List<UUID> authorised){
        this.author = author;
        this.authorised = authorised;
    }

    public Authorised(String name, Level level){


        ServerPlayer authorPlayer = Objects.requireNonNull(level.getServer()).getPlayerList().getPlayer(name);

        if (authorPlayer == null){
            ChestProtection.LOGGER.info("author is not online for first open! ERROR");
            this.author = null;
            this.authorised = new ArrayList<>();
            return;
        }

        this.author = authorPlayer.getUUID();
        this.authorised = new ArrayList<>();



    }

    public UUID getAuthor() {
        return this.author;
    }

    public List<UUID> getAuthorised() {
        return this.authorised;
    }

    public boolean isAuthorised(UUID player){

        return this.authorised.contains(player);

    }

    public void addAuthorised(UUID player){
        this.authorised.add(player);
    }

    public void removeAuthorised(UUID player){
        this.authorised.remove(player);
    }



}
