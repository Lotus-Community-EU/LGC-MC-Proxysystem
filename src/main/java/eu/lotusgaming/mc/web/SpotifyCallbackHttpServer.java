//Created by Maurice H. at 10.05.2025
package eu.lotusgaming.mc.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import eu.lotusgaming.mc.misc.MySQL;
import fi.iki.elonen.NanoHTTPD;

public class SpotifyCallbackHttpServer extends NanoHTTPD {
	
	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;
	private final Gson gson = new Gson();
	
	public SpotifyCallbackHttpServer(String clientId, String clientSecret, String redirectUri) throws IOException {
		super(8081);
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUri;
		start(SOCKET_READ_TIMEOUT, false);
	}
	
	@Override
	public Response serve(IHTTPSession session) {
		if(!"/spotify-callback".equals(session.getUri()) || session.getMethod() != Method.GET) {
			return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 Not found");
		}
		
		String code = session.getParameters().getOrDefault("code", null).get(0);
		String state = session.getParameters().getOrDefault("state", null).get(0);
		
		if(code == null || state == null) {
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Missing Parameters");
		}
		
		try {
			String refreshToken = exchangeCodeForRefreshToken(code);
			saveRefreshToken(state, refreshToken);
			return newFixedLengthResponse("Spotify connected. You may close this window now.");
		}catch (IOException e) {
			e.printStackTrace();
			return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error exchanging code.");
		}
	}
	
	private String exchangeCodeForRefreshToken(String code) throws IOException {
		URL url = new URL("https://accounts.spotify.com/api/token");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		
		String auth = clientId + ":" + clientSecret;
		String basicAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
		
		conn.setRequestProperty("Authorization", "Basic " + basicAuth);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		String body = "grant_type=authorization_code"
				+ "&code=" + URLEncoder.encode(code, "UTF-8")
				+ "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8");
		
		try (OutputStream os = conn.getOutputStream()){
			os.write(body.getBytes(StandardCharsets.UTF_8));
		}
		
		if(conn.getResponseCode() != 200) {
			throw new IOException("Failed token exchange" + conn.getResponseCode());
		}
		
		try(InputStream is = conn.getInputStream()) {
			Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
			JsonObject json = gson.fromJson(reader, JsonObject.class);
			return json.get("refresh_token").getAsString();
		}
	}
	
	void saveRefreshToken(String uuid, String refreshToken) {
		try (PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE mc_users SET spotifyRefreshToken = ? WHERE mcuuid = ?")){
			ps.setString(1, refreshToken);
			ps.setString(2, uuid);
			ps.executeUpdate();
			ps.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
}