package app.vesisika;

import app.vesisika.commands.Vesisika;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Plugin extends JavaPlugin {

    private static SentryClient sentry;

    @Override
    public void onEnable() {
        getLogger().info("Vesisika has enabled.");

        Sentry.init("https://c16fc30563004acd98ee1cd406c354ff@o394539.ingest.sentry.io/5283510");
        Metrics metrics = new Metrics(this, 7924);

        this.getCommand("vesisika").setExecutor(new Vesisika()); // register Vesisika command

        File configFile = new File("vesisika.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (config.isSet("backend.key")) {

            String url = "https://us-central1-vesisika.cloudfunctions.net/app/update/" + config.getString("backend.key");
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

        }

    }
    @Override
    public void onDisable() {
        getLogger().info("Vesisika has disabled.");
    }

}
