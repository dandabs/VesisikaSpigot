package app.vesisika;

import io.sentry.Sentry;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;

public class Bungee extends Plugin implements Listener {

    @Override
    public void onEnable() {

        getProxy().registerChannel("vesisika:sync");


    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {

        if (!e.getTag().equals("vesisika:sync")) {

            return;

        }

        if (!(e.getSender() instanceof Server)) {

            return;

        }

        ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
        DataInputStream in = new DataInputStream(stream);

        try {

            String response = in.readUTF();

            String token = response.split("|")[1];
            String params = response.split("|")[0];

            String url = "https://api.vesisika.app/update/" + token;

            String finalUrlParameters = params;

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

                } catch (IOException ioException2) {

                }
            });

            System.out.println("Sending player data to the server.");

            newThread.start();

        } catch (IOException ioException) {
        }

    }

}
