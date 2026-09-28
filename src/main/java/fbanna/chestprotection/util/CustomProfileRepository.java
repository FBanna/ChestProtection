package fbanna.chestprotection.util;

import com.mojang.authlib.HttpDiscoveryService;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.authlib.services.MinecraftServicesDiscoveryService;
import com.mojang.authlib.services.MinecraftServicesProfileRepository;
import com.mojang.authlib.services.response.NameAndId;
import com.mojang.authlib.services.response.discovery.Service;


import java.net.Proxy;
import java.util.Locale;
import java.util.Optional;


/// Done in order to bypass mojang info Logger for failed Profile requests
public class CustomProfileRepository extends MinecraftServicesProfileRepository {

    private final MinecraftClient client;
    private final MinecraftServicesDiscoveryService environment;

    public CustomProfileRepository(Proxy proxy) {


        MinecraftServicesDiscoveryService environment = MinecraftServicesDiscoveryService.create(Proxy.NO_PROXY);

        super(proxy, environment);

        this.client = MinecraftClient.unauthenticated(proxy);
        this.environment = environment;

    }

    @Override
    public Optional<NameAndId> findProfileByName(final String name) {
        try {

            return Optional.ofNullable(client.get(HttpDiscoveryService.constantURL(environment.getUrl(Service.PROFILES, "getByName").replace("{name}", name.toLowerCase(Locale.ROOT))), NameAndId.class));

            // return Optional.ofNullable(this.client.get(HttpDiscoveryService.constantURL(nameLookupUrl + name.toLowerCase(Locale.ROOT)), NameAndId.class));

        } catch (final MinecraftClientException e) {
            return Optional.empty();
        }
    }
}
