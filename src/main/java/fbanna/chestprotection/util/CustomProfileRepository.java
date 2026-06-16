package fbanna.chestprotection.util;

import com.mojang.authlib.Environment;
import com.mojang.authlib.EnvironmentParser;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import com.mojang.authlib.yggdrasil.response.NameAndId;

import java.net.Proxy;
import java.util.Locale;
import java.util.Optional;


/// Done in order to bypass mojang info Logger for failed Profile requests
public class CustomProfileRepository extends YggdrasilGameProfileRepository {

    private final MinecraftClient client;
    private final String nameLookupUrl;

    public CustomProfileRepository(Proxy proxy) {


        Environment environment = EnvironmentParser
                .getEnvironmentFromProperties()
                .orElse(YggdrasilEnvironment.PROD.getEnvironment());

        super(proxy, environment);

        this.client = MinecraftClient.unauthenticated(proxy);
        nameLookupUrl = environment.profilesHost() + "/minecraft/profile/lookup/name/";

    }

    @Override
    public Optional<NameAndId> findProfileByName(final String name) {
        try {
            return Optional.ofNullable(this.client.get(HttpAuthenticationService.constantURL(nameLookupUrl + name.toLowerCase(Locale.ROOT)), NameAndId.class));
        } catch (final MinecraftClientException e) {
            return Optional.empty();
        }
    }
}
