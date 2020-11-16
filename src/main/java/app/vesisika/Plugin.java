package app.vesisika;

import app.vesisika.commands.Vesisika;
import app.vesisika.listeners.PlayerLeaveEventListener;
import app.vesisika.loops.Update;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Plugin extends JavaPlugin {

    //private static SentryClient sentry;

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    private static Plugin instance;

    @Override
    public void onEnable() {
        getLogger().info("Vesisika has enabled.");

        instance = this;

        //Sentry.init("https://c16fc30563004acd98ee1cd406c354ff@o394539.ingest.sentry.io/5283510");
        Metrics metrics = new Metrics(this, 7924);

        this.getCommand("vesisika").setExecutor(new Vesisika()); // register Vesisika command

        getServer().getPluginManager().registerEvents(new PlayerLeaveEventListener(), this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "vesisika:sync");

        if (!setupEconomy() ) {

        } else {

            setupPermissions();
            setupChat();

        }

        File configFile = new File("vesisika.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            String url = "https://api.vesisika.app/update/" + config.getString("backend.key");
            try {
                HttpsURLConnection httpClient = (HttpsURLConnection) new URL(url).openConnection();
                httpClient.setRequestMethod("POST");

                String urlParameters = "version=" + this.getDescription().getVersion();

                httpClient.setDoOutput(true);
                try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
                    wr.writeBytes(urlParameters);
                    wr.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        /*Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {

                new Update().sendUpdates();

                getServer().getLogger().log(Level.INFO, "Your server has synced with the Vesisika backend.");

            }
        }, 0L, 20L * 3600);*/

        }

    @Override
    public void onDisable() {
        getLogger().info("Vesisika has disabled.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    public static Plugin getInstance() {
        return instance;
    }

}
