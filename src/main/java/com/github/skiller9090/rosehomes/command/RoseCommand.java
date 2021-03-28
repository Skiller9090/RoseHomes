package com.github.skiller9090.rosehomes.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.github.skiller9090.rosehomes.RoseHomes;
import com.github.skiller9090.rosehomes.Utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.util.Set;

@CommandAlias("rh|rosehomes|homes|rosehome|home")
public class RoseCommand extends BaseCommand {

    protected final RoseHomes rosePlugin;

    public RoseCommand(Plugin rosePlugin){
        this.rosePlugin = (RoseHomes) rosePlugin;
    }

    @Default
    @CatchUnknown
    @Syntax(" - The base command for rosehomes")
    public void onCommand(CommandSender sender, String argument){
        if (sender instanceof Player){
            Player player = (Player) sender;
            String player_uuid = player.getUniqueId().toString();
            ConfigurationSection section = rosePlugin.playerData.getConfigurationSection("Homes."+player_uuid);
            if (section != null){
                Set<String> homes = section.getKeys(false);
                if (!homes.isEmpty()){
                    if (homes.contains(argument)){
                        onHome(sender, argument);
                        return;
                    }
                }
            }
        }
        Utils.RoseSend(sender, "Unknown command\n   Try /rh help for the help menu");
    }

    @HelpCommand
    @Subcommand("help")
    @Syntax("<page> - Shows the help menu for rosehomes")
    public void onHelp(CommandSender sender, CommandHelp help){
        Utils.RoseSend(sender,ChatColor.AQUA + "Help menu");
        help.showHelp();
    }

    @Subcommand("getpos")
    @CommandPermission("rosehomes.getpos")
    @Syntax(" - Shows your current world and position")
    public void onGetPosition(CommandSender sender){
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location loc = player.getLocation();
            World world = loc.getWorld();
            String world_name = "Unknown";
            if (world != null) {
                world_name = world.getName();
            }
            Utils.RoseSend(sender, Utils.parseLocationToString(loc));
        }else{
            Utils.RoseSend(sender, "Couldn't get location");
        }
    }

    @Subcommand("set")
    @CommandCompletion("@playerHomes")
    @CommandPermission("roseshomes.set")
    @Syntax("<home name> - Sets a home at your current position with a name you specify")
    public void onSet(CommandSender sender, String home_name){
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String player_uuid = player.getUniqueId().toString();
            Location loc = player.getLocation();
            rosePlugin.playerData.set("Homes."+player_uuid+"."+home_name, loc);
            Utils.RoseSend(sender, "Home " + ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + home_name
                    + ChatColor.RESET +" set at "+ Utils.parseLocationToString(loc));
            rosePlugin.savePlayerData();
        }else{
            sender.sendMessage("Couldn't set home");
        }
    }

    @Subcommand("remove")
    @CommandCompletion("@playerHomes")
    @CommandPermission("roseshomes.remove")
    @Syntax("<home name> - Removes the home at your specification")
    public void onRemove(CommandSender sender, String home_name){
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String player_uuid = player.getUniqueId().toString();
            rosePlugin.playerData.set("Homes."+player_uuid+"."+home_name, null);
            Utils.RoseSend(sender, "Home " + ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + home_name
                    + ChatColor.RESET +" removed");
            rosePlugin.savePlayerData();
        }else{
            sender.sendMessage("Couldn't remove home");
        }
    }

    @Subcommand("list")
    @CommandPermission("rosehomes.list")
    @Syntax(" - Lists all your homes with location details")
    public void onList(CommandSender sender){
        if (sender instanceof Player){
            Player player = (Player) sender;
            String player_uuid = player.getUniqueId().toString();
            ConfigurationSection section = rosePlugin.playerData.getConfigurationSection("Homes."+player_uuid);
            Utils.RoseSend(player, ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Your Current Homes:");
            if (section != null) {
                Set<String> keys = section.getKeys(false);
                if (!keys.isEmpty()){
                    for (String key: keys) {
                        Location loc = rosePlugin.playerData.getLocation("Homes."+player_uuid+"."+key);
                        if (loc != null) {
                            player.sendMessage(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + key
                                    + ": " + Utils.parseLocationToString(loc));
                        }else{
                            player.sendMessage(key+ ": Error getting location");
                        }
                    }
                    return;
                }
            }
            player.sendMessage("No homes...");
        }
    }

    @Subcommand("show")
    @CommandPermission("rosehomes.show")
    @CommandCompletion("@playerHomes")
    @Syntax("<home name> - Shows the location of a specific home")
    public void onShow(CommandSender sender, String homeName){
        if (sender instanceof Player){
            Player player = (Player) sender;
            String player_uuid = player.getUniqueId().toString();
            ConfigurationSection section = rosePlugin.playerData.getConfigurationSection("Homes."+player_uuid);
            if (section != null){
                Set<String> homes = section.getKeys(false);
                for (String home: homes){
                    if (home.equals(homeName)){
                        Location location = rosePlugin.playerData.getLocation("Homes."+player_uuid+"."+home);
                        Utils.RoseSend(player, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + home + ": " +
                                ChatColor.RESET + Utils.parseLocationToString(location));
                        return;
                    }
                }
            }
            Utils.RoseSend(player, "Home not found");
        }
    }

    @Subcommand("home")
    @CommandCompletion("@playerHomes")
    @CommandPermission("rosehomes.home")
    @Syntax("<home name> - Teleports you to your home of specification")
    public void onHome(CommandSender sender, String homeName){
        if (sender instanceof Player){
            Player player = (Player) sender;
            String player_uuid = player.getUniqueId().toString();
            Location loc = rosePlugin.playerData.getLocation("Homes."+player_uuid+"."+homeName);
            if (loc != null){
                PlayerTeleportEvent teleportEvent = new PlayerTeleportEvent(player,
                        player.getLocation(), loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                if (!teleportEvent.isCancelled()) {
                    player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    Utils.RoseSend(player, ChatColor.AQUA + "Teleported to: " +
                            ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + homeName);
                }else{
                    Utils.RoseSend(player, ChatColor.RED + "Teleport was cancelled");
                }
                return;
            }
        }
        Utils.RoseSend(sender, "Couldn't find home...");
    }
}

