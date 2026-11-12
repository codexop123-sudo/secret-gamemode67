package me.secret.gamemode;

import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.help.HelpTopic;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public final class SecretGamemode extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        removeNamespacedAliases();
        removeHelpTopics();
        getLogger().info("SecretGamemode enabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        String name = command.getName();
        if (name.equalsIgnoreCase(".c")) {
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage("\u00a7aSwitched to Creative mode.");
            return true;
        }
        if (name.equalsIgnoreCase(".s")) {
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage("\u00a7aSwitched to Survival mode.");
            return true;
        }
        return false;
    }

    // Removes these commands from the list the client uses for "/" tab-completion suggestions.
    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event) {
        String prefix = getName().toLowerCase() + ":";
        event.getCommands().removeIf(cmd -> {
            String c = cmd.toLowerCase();
            return c.equals(".c") || c.equals(".s")
                    || c.equals(prefix + ".c") || c.equals(prefix + ".s");
        });
    }

    // Disables the automatic "secretgamemode:.gmcr" style shortcut entirely.
    private void removeNamespacedAliases() {
        try {
            Server server = getServer();
            Method getCommandMap = server.getClass().getMethod("getCommandMap");
            CommandMap commandMap = (CommandMap) getCommandMap.invoke(server);

            Field knownCommandsField;
            try {
                knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
            } catch (NoSuchFieldException e) {
                knownCommandsField = commandMap.getClass().getSuperclass().getDeclaredField("knownCommands");
            }
            knownCommandsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

            String prefix = getName().toLowerCase() + ":";
            Iterator<String> it = knownCommands.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (key.equals(prefix + ".c") || key.equals(prefix + ".s")) {
                    it.remove();
                }
            }
        } catch (Exception e) {
            getLogger().warning("Could not remove namespaced command aliases: " + e.getMessage());
        }
    }

    // Removes these commands from the server's "/help" list.
    private void removeHelpTopics() {
        try {
            Object helpMap = getServer().getHelpMap();
            Field topicsField = helpMap.getClass().getDeclaredField("helpTopics");
            topicsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            TreeMap<String, HelpTopic> topics = (TreeMap<String, HelpTopic>) topicsField.get(helpMap);

            String prefix = getName().toLowerCase();
            Iterator<String> it = topics.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next().toLowerCase();
                if (key.contains(".c") || key.contains(".s") || key.contains(prefix)) {
                    it.remove();
                }
            }
        } catch (Exception e) {
            getLogger().warning("Could not remove help topics: " + e.getMessage());
        }
    }
}
