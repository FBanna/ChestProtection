package fbanna.chestprotection.protect.data;

import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fbanna.chestprotection.ChestProtection;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class Authorised {

    private UUID author;
    private ArrayList<UUID> authorised;

    public static final MapCodec<Authorised> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("author").forGetter(Authorised::getAuthor),
            UUIDUtil.CODEC.listOf().fieldOf("authorised").forGetter(Authorised::getAuthorised)
    ).apply(instance, Authorised::new));


    private Authorised(UUID author, ArrayList<UUID> authorised){
        this.author = author;
        this.authorised = authorised;
    }

    private Authorised(UUID author, List<UUID> authorised){
        this.author = author;
        this.authorised = new ArrayList<>(authorised);
    }

    public Authorised(String name, MinecraftServer server){

        Optional<NameAndId> optionProfile = server.services().nameToIdCache().get(name);

        if (optionProfile.isEmpty()) {
            ChestProtection.LOGGER.error("author does not exist! ERROR");
            this.author = null;
            this.authorised = new ArrayList<>();
            return;
        }


//        ServerPlayer authorPlayer = Objects.requireNonNull(level.getServer()).getPlayerList().getPlayer(name);
//
//        if (authorPlayer == null){
//            ChestProtection.LOGGER.info("author is not online for first open! ERROR");
//            this.author = null;
//            this.authorised = new ArrayList<>();
//            return;
//        }

        this.author = optionProfile.get().id();
        this.authorised = new ArrayList<>();



    }

    public UUID getAuthor() {
        return this.author;
    }

    public ArrayList<UUID> getAuthorised() {
        return this.authorised;
    }

    public boolean isAuthorised(UUID player){

        return this.authorised.contains(player) || this.author.equals(player);

    }

    public boolean isAuthor(UUID player) {
        return this.author.equals(player);
    }

    public void addAuthorised(UUID player){
        this.authorised.add(player);
    }

    public void removeAuthorised(UUID player){
        this.authorised.remove(player);
    }

    @Nullable
    public NameAndId getAuthorNameAndId(MinecraftServer server){
        return getNameAndId(server, this.author);

    }



    @Nullable
    public String getAuthorName(MinecraftServer server) {
        NameAndId nameAndId = getAuthorNameAndId(server);

        if (nameAndId == null) {
            return null;
        }

        return nameAndId.name();
    }

    @Nullable
    public static NameAndId getNameAndId(MinecraftServer server, UUID id) {
        Optional<NameAndId> nameAndId = server.services().nameToIdCache().get(id);

        if(nameAndId.isPresent()){
            return nameAndId.get();
        } else {
            ProfileResult result = server.services().sessionService().fetchProfile(id, true);

            if (result == null) {
                return null;
            }
            return (new NameAndId(result.profile()));
        }
    }



    @Override
    public boolean equals(Object o) {

        if(o == this) {
            return true;
        }

        if(!(o instanceof Authorised)) {
            return false;
        }

        Authorised other = (Authorised) o;

        if (!this.author.equals(other.author)) {
            return false;
        }

        if (!this.authorised.equals(other.authorised)) {
            return false;
        }

        return true;
    }


}
