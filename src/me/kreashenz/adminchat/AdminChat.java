package me.kreashenz.adminchat;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AdminChat extends JavaPlugin implements Listener {

	private HashMap<String, Boolean> toggle = new HashMap<String, Boolean>();

	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);

		saveDefaultConfig();
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("adminchat")){
			if(s instanceof Player){
				Player p = (Player)s;
				if(p.hasPermission("adminchat.adminchat")){
					if(args.length == 0){
						if(toggle.get(p.getName())){
							toggle.put(p.getName(), false);
							p.sendMessage("§6You have §cDISABLED §6admin chat.");
						} else {
							toggle.put(p.getName(), true);
							p.sendMessage("§6You have §cENABLED §6admin chat.");
						}
					} else {
						String path = getConfig().getString("format");
						String msg = "";
						for(int i = 0; i < args.length; i++){
							msg = msg + args[i] + ' ';
						}
						path = path.replace("{NAME}", p.getName());
						path = path.replace("{MESSAGE}", msg);
						sendAdmins(path);
					}
				}
			}
		}
		return true;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		String path = getConfig().getString("format");
		if(toggle.containsKey(p.getName()) && toggle.get(p.getName())){
			path = path.replace("{NAME}", p.getName());
			path = path.replace("{MESSAGE}", e.getMessage());
			e.setCancelled(true);
			sendAdmins(path);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		toggle.put(e.getPlayer().getName(), false);
	}

	private void sendAdmins(String msg){
		msg = format(msg);
		for(Player ps : getServer().getOnlinePlayers()){
			if(ps.hasPermission("mcrp.adminchat")){
				ps.sendMessage(msg);
			}
		}
	}

	private String format(String input){
		return ChatColor.translateAlternateColorCodes('&', input);
	}

}
