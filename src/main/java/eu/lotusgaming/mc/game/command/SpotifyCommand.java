//Created by Maurice H. at 10.05.2025
package eu.lotusgaming.mc.game.command;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eu.lotusgaming.mc.main.LotusController;
import eu.lotusgaming.mc.main.LotusManager;
import eu.lotusgaming.mc.main.Main;
import eu.lotusgaming.mc.misc.MySQL;
import eu.lotusgaming.mc.misc.Prefix;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SpotifyCommand extends Command{

	public SpotifyCommand(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			Main.logger.info(Main.consoleSend);
		}else {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			LotusController lc = new LotusController();
			if(hasSpotifyConnected(player)) {
				player.sendMessage(TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "§cYou already have Spotify connected!"));
			}else {
				try {
					String uri = "https://accounts.spotify.com/authorize?client_id=" + LotusManager.spotifyId
							+ "&response_type=code"
							+ "&redirect_uri=" + URLEncoder.encode("http://88.198.12.152:8081/spotify-callback", "UTF-8")
							+ "&scope=user-read-currently-playing"
							+ "&state=" + player.getUniqueId().toString();
					TextComponent message = new TextComponent(TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "§aPlease connect your Spotify account by clicking this message."));
					message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, uri));
					player.sendMessage(message);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	boolean hasSpotifyConnected(ProxiedPlayer player) {
		boolean hasConnected = false;
		try(PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT spotifyRefreshToken FROM mc_users WHERE mcuuid = ?")){
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				rs.getString("spotifyRefreshToken");
				if(rs.wasNull()) {
					hasConnected = false;
				}else {
					hasConnected = true;
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return hasConnected;
	}
}