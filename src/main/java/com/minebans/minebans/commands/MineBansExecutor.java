package com.minebans.minebans.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import uk.co.jacekk.bukkit.baseplugin.v9.command.BaseCommandExecutor;
import uk.co.jacekk.bukkit.baseplugin.v9.command.CommandHandler;
import uk.co.jacekk.bukkit.baseplugin.v9.command.CommandTabCompletion;
import uk.co.jacekk.bukkit.baseplugin.v9.command.SubCommandHandler;

import com.minebans.minebans.Config;
import com.minebans.minebans.MineBans;
import com.minebans.minebans.Permission;
import com.minebans.minebans.api.APIException;
import com.minebans.minebans.api.callback.OpenAppealsCallback;
import com.minebans.minebans.api.callback.PlayerBansCallback;
import com.minebans.minebans.api.callback.StatusCallback;
import com.minebans.minebans.api.callback.StatusMessageCallback;
import com.minebans.minebans.api.data.OpenAppealsData;
import com.minebans.minebans.api.data.OpenAppealsData.AppealData;
import com.minebans.minebans.api.data.PlayerBansData;
import com.minebans.minebans.api.data.StatusData;
import com.minebans.minebans.api.data.StatusMessageData;
import com.minebans.minebans.api.request.OpenAppealsRequest;
import com.minebans.minebans.api.request.PlayerBansRequest;
import com.minebans.minebans.api.request.StatusMessageRequest;
import com.minebans.minebans.api.request.StatusRequest;
import com.minebans.minebans.bans.BanReason;
import com.minebans.minebans.bans.BanSeverity;

public class MineBansExecutor extends BaseCommandExecutor<MineBans> {
	
	public MineBansExecutor(MineBans plugin){
		super(plugin);
	}
	
	@CommandHandler(names = {"minebans", "mbans", "mb"}, description = "Provides various commands relating to the system.", usage = "<option> [args]")
	@CommandTabCompletion({"status|update|reasons|lookup|listtemp|exec|import"})
	public void minebans(CommandSender sender, String label, String[] args){
		sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Usage: /" + label + " <option> [args]"));
		sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Options:"));
		
		if (Permission.ADMIN_STATUS.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   status - Gets the status of the API."));
		}
		
		if (Permission.ADMIN_UPDATE.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   update - Checks for new versions of the plugin."));
		}
		
		if (Permission.ADMIN_BAN.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   reasons - Lists all of the ban reasons."));
		}
		
		if (Permission.ADMIN_LOOKUP.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   lookup - Gets a summary of a players bans."));
		}
		
		if (Permission.ADMIN_LISTTEMP.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   listtemp - Lists all of the players that are temporarily banned."));
		}
		
		if (Permission.ADMIN_BAN_COMMAND.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   exec - Executes the commands for the last ban made."));
		}
		
		if (Permission.ADMIN_IMPORT.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   import - Imports any bans made using other systems."));
		}
		
		if (Permission.ADMIN_APPEALS.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   appeals - Lists open ban appeals for this server."));
		}
	}
	
	@SubCommandHandler(parent = "minebans", name = "status")
	public void minebansStatus(final CommandSender sender, String label, String[] args){
		if (!Permission.ADMIN_STATUS.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		final String senderName = sender.getName();
		final long timeStart = System.currentTimeMillis();
		
		(new StatusRequest(plugin, senderName)).process(new StatusCallback(plugin){
			
			@Override
			public void onSuccess(StatusData data){
				Double[] loadAvg = data.getLoadAverage();
				
				sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "The API responded in " + (data.getResponceTime() - timeStart) + "ms"));
				sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Server Load Average: " + ((loadAvg[0] > 8L) ? ChatColor.RED : ChatColor.GREEN) + loadAvg[0] + " " + loadAvg[1] + " " + loadAvg[2]));
			}
			
			@Override
			public void onFailure(Exception exception){
				plugin.api.handleException(exception, sender);
				
				if (!(exception instanceof APIException)){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Checking for known problems..."));
					
					(new StatusMessageRequest(plugin)).process(new StatusMessageCallback(plugin){
						
						public void onSuccess(StatusMessageData data){
							sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Result: " + data.getMessage()));
						}
						
						public void onFailure(Exception e){
							plugin.log.warn("We use Dropbox to provide the status announcements, for some reason it did not respond within 12 seconds.");
							sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Status: " + ChatColor.RED + "Unable to get info, check your server.log"));
						}
						
					});
				}
			}
			
		});
	}
	
	@SubCommandHandler(parent = "minebans", name = "update")
	public void minebansUpdate(final CommandSender sender, String label, final String[] args){
		if (!Permission.ADMIN_UPDATE.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		plugin.scheduler.runTaskAsynchronously(plugin, new Runnable(){
			
			public void run(){
				final boolean update = plugin.updateChecker.updateNeeded();
				
				plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
					
					@Override
					public void run(){
						if (update){
							sender.sendMessage(plugin.formatMessage(ChatColor.RED + "A new version is available, v" + plugin.updateChecker.getVersion()));
							sender.sendMessage(plugin.formatMessage(ChatColor.RED + plugin.updateChecker.getLink()));
						}else{
							if (args.length != 1 || !args[0].equals("-q")){
								sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Up to date \\o/"));
							}
						}
					}
					
				});
			}
			
		});
	}
	
	@SubCommandHandler(parent = "minebans", name = "reasons")
	public void minebansReasons(final CommandSender sender, String label, String[] args){
		if (!Permission.ADMIN_BAN.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Available ban reasons:"));
		
		BanReason[] reasons = BanReason.getAll();
		BanReason reason;
		
		StringBuilder line;
		List<String> keywords;
		int i, c;
		
		for (i = 0; i < reasons.length; ++i){
			reason = reasons[i];
			
			if (plugin.config.getBoolean(Config.getReasonEnabled(reason))){
				line = new StringBuilder();
				
				keywords = reason.getKeywords();
				
				line.append(ChatColor.GREEN);
				line.append(String.valueOf(i + 1));
				
				if (i < 9){
					line.append(" ");
				}
				
				line.append(" - ");
				line.append(reason.getDescription());
				line.append(ChatColor.GRAY);
				line.append(" ");
				
				line.append(keywords.get(0));
				
				for (c = 1; c < keywords.size(); ++c){
					line.append(", ");
					line.append(keywords.get(c));
				}
				
				sender.sendMessage(line.toString());
			}
		}
	}
	
	@SubCommandHandler(parent = "minebans", name = "lookup")
	@CommandTabCompletion({"<player>"})
	public void minebansLookup(final CommandSender sender, String label, final String[] args){
		if (!Permission.ADMIN_LOOKUP.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		if (args.length != 1){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Usage: /minebans lookup <player_name>"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example: /minebans lookup wide_load"));
			return;
		}
		
		final String senderName = sender.getName();
		
		(new PlayerBansRequest(plugin, senderName, args[0])).process(new PlayerBansCallback(plugin){
			
			@Override
			public void onSuccess(PlayerBansData data){
				Long total = data.getTotal();
				Long last24 = data.getLast24();
				Long removed = data.getRemoved();
				
				sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Summary for " + args[0]));
				
				sender.sendMessage(ChatColor.GREEN + "Total bans on record: " + ((total <= 5L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + total);
				sender.sendMessage(ChatColor.GREEN + "Bans in the last 24 hours: " + ((last24 == 0L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + last24);
				sender.sendMessage(ChatColor.GREEN + "Bans that have been removed: " + ((removed <= 10L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + removed);
				
				if (data.getTotalRulesBroken() > 0){
					sender.sendMessage(ChatColor.GREEN + "Rules broken:");
					
					Long unconfirmed, confirmed, low, medium, high;
					
					for (BanReason banReason : data.getBans().keySet()){
						if (banReason.getSeverties().contains(BanSeverity.HIGH)){
							unconfirmed	= data.get(banReason, BanSeverity.UNCONFIRMED);
							low			= data.get(banReason, BanSeverity.LOW);
							medium		= data.get(banReason, BanSeverity.MEDIUM);
							high		= data.get(banReason, BanSeverity.HIGH);
							
							sender.sendMessage(ChatColor.GREEN + "   - " + banReason.getDescription() + ": " + ChatColor.GRAY + unconfirmed + " " + ChatColor.DARK_GREEN + low + " " + ChatColor.YELLOW + medium + " " + ChatColor.DARK_RED + high);
						}else{
							unconfirmed	= data.get(banReason, BanSeverity.UNCONFIRMED);
							confirmed	= data.get(banReason, BanSeverity.CONFIRMED);
							
							sender.sendMessage(ChatColor.GREEN + "   - " + banReason.getDescription() + ": " + ChatColor.GRAY + unconfirmed + " " + ChatColor.DARK_RED + confirmed);
						}
					}
				}
			}
			
			@Override
			public void onFailure(Exception exception){
				plugin.api.handleException(exception, sender);
			}
			
		});
	}
	
	@SubCommandHandler(parent = "minebans", name = "listtemp")
	public void minebansListTemp(final CommandSender sender, String label, String[] args){
		if (!Permission.ADMIN_LISTTEMP.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		List<String> playerNames = plugin.banManager.getTempBannedPlayers();
		
		sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "There are " + playerNames.size() + " players temporarily banned."));
		
		for (String playerName : playerNames){
			sender.sendMessage(ChatColor.GREEN + "  " + playerName + " - " + plugin.banManager.getTempBanRemaining(playerName) / 3600 + " hours");
		}
	}
	
	@SubCommandHandler(parent = "minebans", name = "exec")
	public void minebansExec(final CommandSender sender, String label, String[] args){
		if (!Permission.ADMIN_BAN_COMMAND.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		final String senderName = sender.getName();
		
		ArrayList<String> cmds = plugin.banCommands.get(senderName);
		
		if (cmds == null){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "There are no commands to be executed."));
			return;
		}
		
		for (String cmd : cmds){
			plugin.server.dispatchCommand(sender, cmd);
		}
		
		plugin.banCommands.remove(senderName);
	}
	
	@SubCommandHandler(parent = "minebans", name = "import")
	public void minebansImport(final CommandSender sender, String label, String[] args){
		if (!Permission.ADMIN_IMPORT.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		String issuedBy = sender.getName();
		
		if (issuedBy.equals("CONSOLE")){
			issuedBy = "console";
		}
		
		Set<OfflinePlayer> players = plugin.server.getBannedPlayers();
		
		for (OfflinePlayer player : players){
			plugin.banManager.locallyBanPlayer(player.getName(), issuedBy, true, false);
		}
		
		sender.sendMessage(plugin.formatMessage(ChatColor.GREEN.toString() + players.size() + " bans have been imported"));
	}
	
	@SubCommandHandler(parent = "minebans", name = "appeals")
	public void minebansAppeals(final CommandSender sender, String label, final String[] args){
		if (!Permission.ADMIN_APPEALS.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		new OpenAppealsRequest(plugin, sender.getName()).process(new OpenAppealsCallback(plugin){
			
			@Override
			public void onSuccess(OpenAppealsData data){
				List<AppealData> appeals = data.getAppeals();
				int total = appeals.size();
				
				if (args.length == 1 && args[0].equals("-q") && total == 0){
					return;
				}
				
				if (total == 1){
					sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "There is 1 open ban appeal"));
				}else{
					sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "There are " + total + " open ban appeals"));
				}
				
				for (AppealData appeal : appeals){
					sender.sendMessage(ChatColor.GREEN + " " + appeal.getPlayerName() + " - " + appeal.getBanReason().getShortDescription());
				}
			}
			
			@Override
			public void onFailure(Exception exception){
				plugin.api.handleException(exception, sender);
			}
			
		});
	}
	
}
