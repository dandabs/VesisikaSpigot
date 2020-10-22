package app.vesisika.loops;

import app.vesisika.Plugin;
import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Update {

    public boolean sendUpdates() {

        File configFile = new File("vesisika.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        String urlParameters = "version=" +  Plugin.getInstance().getDescription().getVersion();

        if (config.isSet("backend.key")) {



            if (Plugin.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {

                OfflinePlayer[] players = Bukkit.getOfflinePlayers();

                for (OfflinePlayer p : players) {



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

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            newThread.start();
            return true;

        }

        return true;

    }

}
