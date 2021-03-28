package com.github.skiller9090.rosehomes;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandCompletions;
import com.github.skiller9090.rosehomes.command.RoseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;


public class RoseHomes extends JavaPlugin {
    public File playerDataFile;
    public FileConfiguration playerData;
    public BukkitCommandManager commandManager;

    @Override
    public void onEnable() {
        this.commandManager = new BukkitCommandManager(this);
        this.playerDataFile = new File(getDataFolder(), "data.yml");
        if (!playerDataFile.exists()){
            saveResource(playerDataFile.getName(), false);
        }
        this.playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        commandManager.registerCommand(new RoseCommand(this));
        commandManager.enableUnstableAPI("help");
        setAutoCompletions();
    }

    public void savePlayerData(){
        try {
            playerData.save(playerDataFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + playerData.getName(), ex);
        }
    }

    public void setAutoCompletions(){
        CommandCompletions<BukkitCommandCompletionContext> completions = commandManager.getCommandCompletions();
        completions.registerAsyncCompletion("playerHomes", context -> {
            CommandSender sender = context.getSender();
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String player_uuid = player.getUniqueId().toString();
                ConfigurationSection section = playerData.getConfigurationSection("Homes." + player_uuid);
                if (section != null) {
                    return section.getKeys(false);
                }
            }
            return null;
        });
    }

    @Override
    public void onDisable() {
    }
}
