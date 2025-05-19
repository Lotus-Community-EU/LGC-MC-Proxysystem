package eu.lotusgaming.mc.misc.util;

import java.util.UUID;

public class PlayerRecord {

    private final UUID uuid;
    private final String refreshToken;

    public PlayerRecord(UUID uuid, String refreshToken) {
        this.uuid = uuid;
        this.refreshToken = refreshToken;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getSpotifyId() {
        return refreshToken;
    }
}