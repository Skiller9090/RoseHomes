package com.github.skiller9090.rosehomes.Utils;

import org.bukkit.Location;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class Utils {
    public static String parseLocationToString(Location loc) {
        String x = String.valueOf(Math.round(loc.getX()));
        String y = String.valueOf(Math.round(loc.getY()));
        String z = String.valueOf(Math.round(loc.getZ()));
        String worldName = "Unknown";
        if (loc.getWorld() != null) {
            worldName = loc.getWorld().getName();
        }
        return (ChatColor.AQUA + "World: " + ChatColor.GOLD + worldName
                + ChatColor.AQUA + " x: " + ChatColor.GOLD + x
                + ChatColor.AQUA + " y: " + ChatColor.GOLD + y
                + ChatColor.AQUA + " z: " + ChatColor.GOLD + z);
    }
    public static void RoseSend(CommandSender sender, String toSend){
        String prefix = (ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD.toString()
                + "[RoseHomes] " + ChatColor.RESET);
        sender.sendMessage((prefix + toSend));
    }
}

