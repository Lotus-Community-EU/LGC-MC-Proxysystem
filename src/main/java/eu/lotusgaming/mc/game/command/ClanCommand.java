//Created by Maurice H. at 27.04.2025
package eu.lotusgaming.mc.game.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eu.lotusgaming.mc.main.LotusController;
import eu.lotusgaming.mc.main.Main;
import eu.lotusgaming.mc.misc.MySQL;
import eu.lotusgaming.mc.misc.Prefix;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ClanCommand extends Command{

	public ClanCommand(String name) {
		super(name);
	}
	
	/*       0              1            2
	 * /clan create        <Identifier> <Name>
	 * /clan transferowner <new Owner>
	 * /clan delete
	 * /clan invite        <player>
	 * /clan kick          <player>
	 * /clan leave
	 * /clan info
	 * /clan list
	 * /clan help
	 */

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			Main.logger.info("This command is only for ingame usage!");
		}else {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			LotusController lc = new LotusController();
			if(args.length == 0) {
				player.sendMessage(TextComponent.fromLegacy("§cPlease specify a clan command. /clan help for more information."));
			}else if(args.length == 1) {
				if (args[0].equalsIgnoreCase("help")) {
					player.sendMessage(TextComponent.fromLegacy("§7==========[" + lc.getPrefix(Prefix.SCOREBOARD) + "§7]=========="));
					player.sendMessage(TextComponent.fromLegacy("§a/clan create <Identifier> <Name> | Identifier must be between 2 - 4 chars long, Name between 3 and 16 chars long."));
					player.sendMessage(TextComponent.fromLegacy("§a/clan transferowner <new Owner> | Transfer your clan ownership to another player (Must be Owner)"));
					player.sendMessage(TextComponent.fromLegacy("§a/clan delete | Deletes the clan you are in (Must be Owner of it!)"));
					player.sendMessage(TextComponent.fromLegacy("§a/clan invite <player> | Invite up to 5 Players in a 24 hour period."));
					player.sendMessage(TextComponent.fromLegacy("§a/clan kick <player> | Remove a player from your clan (Must be Owner)"));
					player.sendMessage(TextComponent.fromLegacy("§a/clan leave | Leave the clan you are in (Only if you are not the Owner!)"));
					player.sendMessage(TextComponent.fromLegacy("§a/clan info | See all informations about your clan."));
					player.sendMessage(TextComponent.fromLegacy("§a/clan list | See all clans across Lotus Gaming"));
				} else if(args[0].equalsIgnoreCase("list")) {
					//list all clans across LGCMC
				}else if(args[0].equalsIgnoreCase("info")) {
					//show clan info or info not being in a clan
				}else if(args[0].equalsIgnoreCase("leave")) {
					//leave clan if not owner, otherwise print to delete/transfer
				}else if(args[0].equalsIgnoreCase("delete")) {
					//delete clan if owner, otherwise print to leave
				} else {
					player.sendMessage(TextComponent.fromLegacy("§cUnknown command. /clan help for more information."));
				}
			}else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("transferowner")) {
					//transfer ownership to another player
				}else if(args[0].equalsIgnoreCase("invite")) {
					//invite player to clan
				}else if(args[0].equalsIgnoreCase("kick")) {
					//kicks player from clan (only owner)
				}
			}else if(args.length == 3) {
				if (args[0].equalsIgnoreCase("create")) {
					// create clan
					String identifier = args[1];
					String name = args[2];
					if (identifier.length() < 2 || identifier.length() > 4) {
						player.sendMessage(TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "§cIdentifier must be between 2 and 4 characters long!"));
						return;
					}
					if (name.length() < 3 || name.length() > 16) {
						player.sendMessage(TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "§cName must be between 3 and 16 characters long!"));
						return;
					}
					if(isClanOwner(player)) {
						player.sendMessage(TextComponent.fromLegacy(lc.getPrefix(Prefix.MAIN) + "§cYou already own a clan!"));
						return;
					}
					createClan(identifier, name, player);
				}
			}
		}
	}
	
	boolean isClanOwner(ProxiedPlayer player) {
		try(PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT clanOwner FROM mc_clan WHERE clanOwner = ?")) {
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				rs.close();
				ps.close();
				return true;
			}else {
				rs.close();
				ps.close();
				return false;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	void createClan(String identifier, String name, ProxiedPlayer player) {
		try(PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO mc_clan (clanOwner, identifier, name) VALUES (?, ?, ?)")) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, identifier);
            ps.setString(3, name);
            ps.executeUpdate();
            ps.close();
            player.sendMessage(TextComponent.fromLegacy("§aClan " + identifier + " created successfully!"));
        }catch (SQLException e) {
            e.printStackTrace();
        }
	}
}