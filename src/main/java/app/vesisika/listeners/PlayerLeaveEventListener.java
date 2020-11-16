package app.vesisika.listeners;

import app.vesisika.Plugin;
import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import io.sentry.Sentry;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

public class PlayerLeaveEventListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

        Player p = event.getPlayer();

        File configFile = new File("vesisika.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        String urlParameters = "version=" +  Plugin.getInstance().getDescription().getVersion();

        if (Plugin.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {

            if (Plugin.getInstance().getServer().getPluginManager().isPluginEnabled("Statz")) {

                JSONObject statz = new JSONObject();

                JSONObject deaths = new JSONObject();
                deaths.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_deaths%")));
                statz.put("deaths", deaths);

                JSONObject blocks_broken = new JSONObject();
                blocks_broken.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_blocks_broken%")));
                statz.put("blocks_broken", blocks_broken);

                JSONObject blocks_placed = new JSONObject();
                blocks_placed.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_blocks_placed%")));
                statz.put("blocks_placed", blocks_placed);

                JSONObject caught_items = new JSONObject();
                caught_items.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_caught_items%")));
                statz.put("caught_items", caught_items);

                JSONObject crafted_items = new JSONObject();
                crafted_items.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_crafted_items%")));
                statz.put("crafted_items", crafted_items);

                JSONObject damage_taken = new JSONObject();
                damage_taken.put(p.getUniqueId().toString(), Long.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_damage_taken%")));
                statz.put("damage_taken", damage_taken);

                JSONObject distance_traveled_walk = new JSONObject();
                distance_traveled_walk.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_distance_traveled_allworlds_walk%")));
                statz.put("distance_traveled_walk", distance_traveled_walk);

                JSONObject distance_traveled_fly = new JSONObject();
                distance_traveled_fly.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_distance_traveled_allworlds_fly%")));
                statz.put("distance_traveled_fly", distance_traveled_fly);

                JSONObject food_eaten = new JSONObject();
                food_eaten.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_food_eaten%")));
                statz.put("food_eaten", food_eaten);

                JSONObject joins = new JSONObject();
                joins.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_joins%")));
                statz.put("joins", joins);

                JSONObject mobs_killed = new JSONObject();
                mobs_killed.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_mobs_killed%")));
                statz.put("mobs_killed", mobs_killed);

                JSONObject players_killed = new JSONObject();
                players_killed.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_players_killed%")));
                statz.put("players_killed", players_killed);

                int time = (Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_time_day%")) * 1440) + (Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_time_hour%")) * 60) + Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_time_minute%"));

                JSONObject time_played = new JSONObject();
                time_played.put(p.getUniqueId().toString(), time);
                statz.put("time_played", time_played);

                JSONObject villager_trades = new JSONObject();
                villager_trades.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_villager_trades%")));
                statz.put("villager_trades", villager_trades);

                JSONObject xp_gained = new JSONObject();
                xp_gained.put(p.getUniqueId().toString(), Integer.valueOf(PlaceholderAPI.setPlaceholders(p, "%statz_xp_gained%")));
                statz.put("xp_gained", xp_gained);

                urlParameters += "&statz=" + statz.toString();

            }

            if (Plugin.getInstance().getServer().getPluginManager().isPluginEnabled("Vault")) {

                Economy econ = Plugin.getEconomy();

                JSONObject obj = new JSONObject();

                double bal = econ.getBalance(p);
                obj.put(p.getUniqueId(), String.valueOf(bal));

                urlParameters += "&economy=" + obj.toString();

            }

            if (Plugin.getInstance().getServer().getPluginManager().isPluginEnabled("CMI")) {

                JSONObject obj = new JSONObject();

                CMIUser user;

                try {
                    user = CMI.getInstance().getPlayerManager().getUser(p.getUniqueId());
                } catch (NullPointerException e) {

                    return;

                }

                obj.put(p.getUniqueId(), String.valueOf(user.getBalance()));

                JSONObject cmistats = new JSONObject();

                JSONObject time_played = new JSONObject();
                long time = user.getTotalPlayTimeClean(); // assuming this is in minutes
                time_played.put(p.getUniqueId().toString(), time);
                cmistats.put("time_played", time_played);

                JSONObject deaths = new JSONObject();
                deaths.put(p.getUniqueId().toString(), user.getStats().getStat(StatsManager.CMIStatistic.DEATHS));
                cmistats.put("deaths", deaths);

                JSONObject blocks_broken = new JSONObject();
                blocks_broken.put(p.getUniqueId().toString(), user.getStats().getStat(StatsManager.CMIStatistic.MINE_BLOCK));
                cmistats.put("blocks_broken", blocks_broken);

                JSONObject damage_taken = new JSONObject();
                damage_taken.put(p.getUniqueId().toString(), user.getStats().getStat(StatsManager.CMIStatistic.DAMAGE_TAKEN));
                cmistats.put("damage_taken", damage_taken);

                JSONObject distance_traveled_walk = new JSONObject();
                distance_traveled_walk.put(p.getUniqueId().toString(), user.getStats().getStat(StatsManager.CMIStatistic.WALK_ONE_CM));
                cmistats.put("distance_traveled_walk", distance_traveled_walk);

                JSONObject distance_traveled_fly = new JSONObject();
                distance_traveled_fly.put(p.getUniqueId().toString(), user.getStats().getStat(StatsManager.CMIStatistic.FLY_ONE_CM));
                cmistats.put("distance_traveled_fly", distance_traveled_fly);

                JSONObject mobs_killed = new JSONObject();
                mobs_killed.put(p.getUniqueId().toString(), user.getStats().getStat(StatsManager.CMIStatistic.MOB_KILLS));
                cmistats.put("mobs_killed", mobs_killed);

                JSONObject players_killed = new JSONObject();
                players_killed.put(p.getUniqueId().toString(), user.getStats().getStat(StatsManager.CMIStatistic.PLAYER_KILLS));
                cmistats.put("players_killed", players_killed);

                urlParameters += "&cmieconomy=" + obj.toString() + "&cmistats=" + cmistats.toString();

            }

        }

        String url = "https://api.vesisika.app/update/" + config.getString("backend.key");

        String finalUrlParameters = urlParameters;

        Thread newThread = new Thread(() -> {
            try {
                HttpsURLConnection httpClient = (HttpsURLConnection) new URL(url).openConnection();
                httpClient.setRequestMethod("POST");
                httpClient.setConnectTimeout(60000);

                httpClient.setDoOutput(true);
                try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
                    wr.writeBytes(finalUrlParameters);
                    wr.flush();
                }

                int responseCode = httpClient.getResponseCode();
                System.out.println(responseCode);

            } catch (IOException e) {
            }
        });

        System.out.println("Sending player data to the server.");

        sendMessage(finalUrlParameters + "|" + config.getString("backend.key"), p);

        newThread.start();

    }

    public void sendMessage(String message, Player p) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(message);
        } catch (IOException e) {
        }

        p.sendPluginMessage(Plugin.getInstance(), "vesisika:sync", stream.toByteArray());
    }

}
