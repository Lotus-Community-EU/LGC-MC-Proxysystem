//Created by Maurice H. at 11.05.2025
package eu.lotusgaming.mc.misc.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import eu.lotusgaming.mc.main.Main;
import eu.lotusgaming.mc.misc.MySQL;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SpotifySyncTask {

	private final SpotifyService spotifyService;
	private final Map<UUID, String> trackCache = new ConcurrentHashMap<>();

	public SpotifySyncTask(SpotifyService spotifyService) {
		this.spotifyService = spotifyService;
	}

	public void start() {
		ProxyServer.getInstance().getScheduler().schedule(Main.main, new Runnable() {
			@Override
			public void run() {
				List<PlayerRecord> players = getAllWithSpotify();

				for (PlayerRecord record : players) {
					UUID uuid = record.getUniqueId();
					ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
					if (player == null || !player.isConnected())
						continue;
//gaga dudu
					CompletableFuture.runAsync(() -> {
						try {
							String accessToken = spotifyService.refreshAccessToken(record.getSpotifyId());
							NowPlaying nP = spotifyService.fetchNowPlaying(accessToken);
							if (nP == null)
								return;

							String lastTrackId = trackCache.get(uuid);
							if (nP.isPlaying() || !nP.getTrackId().equals(lastTrackId)) {
								trackCache.put(uuid, nP.getTrackId());
								updateNowPlaying(uuid, nP);
								Main.logger.info("Updated NowPlaying for " + uuid.toString() + " to " + nP.getTrack() + " by " + nP.getArtist() + " and is playing: " + nP.isPlaying() + " and is local: " + nP.isLocalTrack());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
				}
			}
		}, 0, 10, java.util.concurrent.TimeUnit.SECONDS);
	}

	public List<PlayerRecord> getAllWithSpotify() {
		List<PlayerRecord> players = new ArrayList<>();
		try (PreparedStatement ps = MySQL.getConnection()
				.prepareStatement("SELECT * FROM mc_users WHERE spotifyRefreshToken IS NOT NULL AND isOnline = 1")) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				UUID uuid = UUID.fromString(rs.getString("mcuuid"));
				String token = rs.getString("spotifyRefreshToken");
				players.add(new PlayerRecord(uuid, token));
			}
			return players;
		} catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}

	void updateNowPlaying(UUID uuid, NowPlaying nowPlaying) {
		try (PreparedStatement ps = MySQL.getConnection()
				.prepareStatement("UPDATE mc_users SET spotifyTrack = ?, spotifyArtist = ?, spotifyPlaying = ?, spotifyLocal = ? WHERE mcuuid = ?")) {
			ps.setString(1, nowPlaying.getTrack());
			ps.setString(2, nowPlaying.getArtist());
			ps.setBoolean(3, nowPlaying.isPlaying());
			ps.setBoolean(4, nowPlaying.isLocalTrack());
			ps.setString(5, uuid.toString());
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}