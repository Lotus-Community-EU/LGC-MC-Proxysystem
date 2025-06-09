//Created by Maurice H. at 11.05.2025
package eu.lotusgaming.mc.misc.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SpotifyService {

	private final String clientId, clientSecret;
	private final Gson gson = new Gson();

	public SpotifyService(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public String refreshAccessToken(String refreshToken) throws IOException {
		URL url = new URL("https://accounts.spotify.com/api/token");
		HttpURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);

		String basicAuth = Base64.getEncoder()
				.encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

		conn.setRequestProperty("Authorization", "Basic " + basicAuth);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		String body = "grant_type=refresh_token&refresh_token=" + URLEncoder.encode(refreshToken, "UTF-8");

		try (OutputStream os = conn.getOutputStream()) {
			os.write(body.getBytes(StandardCharsets.UTF_8));
		}

		if (conn.getResponseCode() != 200) {
			throw new IOException("Failed to refresh token: " + conn.getResponseCode());
		}

		try (InputStream is = conn.getInputStream()) {
			Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
			JsonObject json = gson.fromJson(reader, JsonObject.class);
			return json.get("access_token").getAsString();
		}
	}

	public NowPlaying fetchNowPlaying(String accessToken) throws IOException {
		URL url = new URL("https://api.spotify.com/v1/me/player/currently-playing");
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);

		if (conn.getResponseCode() != 200) {
			throw new IOException("Failed to fetch now playing: " + conn.getResponseCode());
		}

		try (InputStream is = conn.getInputStream()) {
			Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
			JsonObject json = gson.fromJson(reader, JsonObject.class);
			boolean isPlaying = json.get("is_playing").getAsBoolean();
			String track = json.getAsJsonObject("item").get("name").getAsString();
			String trackId = "";
			if (json.getAsJsonObject("item").get("id").isJsonNull()) {
				trackId = "localTrack";
			} else {
				trackId = json.getAsJsonObject("item").get("id").getAsString();
			}
			boolean isLocal = json.getAsJsonObject("item").get("is_local").getAsBoolean();
			List<String> artists = new ArrayList<>();
			String artist = "";
			long progressMs = json.get("progress_ms").getAsLong();
			long durationMs = json.getAsJsonObject("item").get("duration_ms").getAsLong();
			if (!json.getAsJsonObject("item").getAsJsonArray("artists").isJsonNull()) {
				json.getAsJsonObject("item").getAsJsonArray("artists").forEach(rA -> {
					artists.add(rA.getAsJsonObject().get("name").getAsString());
				});
				if (artists.size() == 0) {
					artist = "No Artist!";
				} else {
					StringBuilder sb = new StringBuilder();
					for (String s : artists) {
						sb.append(s + ", ");
					}
					artist = sb.toString();
					artist = artist.substring(0, artist.length() - 2);
				}
			} else {
				artist = "Unknown Artist";
			}

			return new NowPlaying(track, artist, isPlaying, trackId, isLocal, progressMs, durationMs);
		}
	}

}