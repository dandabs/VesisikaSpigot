package app.vesisika.commands;

import io.sentry.Sentry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

public class Vesisika implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            ((Player) sender).getPlayer().sendMessage("Vesisika commands can only be run from the console.");
            return true;

        }

        File configFile = new File("vesisika.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (args.length <= 1) {

            sender.sendMessage("Please specify the method in argument 1.");
            return true;

        }

        if (args[0].equals("init")) {

            if (args.length != 2) {

                sender.sendMessage("Please provide the owner's username in argument 2.");
                return true;

            }

            if (!config.isSet("backend.key")) {

                String key = UUID.randomUUID().toString(); // generate unique uuid for token, it's unlikely to collide

                if (!Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {

                    sender.sendMessage("That player has never been online before.");
                    return true;

                }

                String url = "https://us-central1-vesisika.cloudfunctions.net/app/init";

                try {

                    HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
                    httpClient.setRequestMethod("POST");
                    httpClient.setConnectTimeout(60000); // 60000ms = 60s = 1m

                    // headers
                    String urlParameters = "token=" + key + "&uuid=" + Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString() + "&ip=" + Bukkit.getServer().getIp() + "&port=" + Bukkit.getServer().getPort() + "&name=" + Bukkit.getServer().getName();

                    // send request
                    httpClient.setDoOutput(true);
                    try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
                        wr.writeBytes(urlParameters);
                        wr.flush();
                    }

                    int responseCode = httpClient.getResponseCode();
                    Bukkit.getServer().getLogger().log(Level.INFO, String.valueOf(responseCode));

                    if (responseCode == 200) {
                        config.set("backend.key", key);
                        config.save(configFile);
                        sender.sendMessage("Your Vesisika herd has been created. You will be able to find it at the bottom of your herd list.");
                    }

                    if (responseCode == 403) {
                        sender.sendMessage("An unknown error occurred. Please contact the developers through the Vesisika app.");
                    }

                    if (responseCode == 401) {
                        sender.sendMessage("That user doesn't have a Vesisika account. Find out how to create one at https://vesisika.app/.");
                    }

                    if (responseCode == 409) {
                        sender.sendMessage("Your automatically generated invite code was already used. Please run the command again.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Sentry.capture(e);
                }

            } else sender.sendMessage("You already have a Vesisika herd attached to this server.");

        }

        return true;

    }

}
