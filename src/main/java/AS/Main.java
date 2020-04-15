package AS;

//-----imports-----//

import arc.Core;
import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.entities.type.Player;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.plugin.Plugin;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static mindustry.Vars.*;


public class Main extends Plugin {
    public static ArrayList<String> asyncList = new ArrayList<String>();
    public int spacing;

    private int timer = 5;

    private JSONObject alldata;
    private JSONObject data; //token, channel_id, role_id

    private HashMap<Long, String> cooldowns = new HashMap<Long, String>(); //uuid

    public Main() throws InterruptedException {
        try {
            FileInputStream loadFile = new FileInputStream("NAS.cn");
            ObjectInputStream in = new ObjectInputStream(loadFile);
            asyncList = (ArrayList<String>) in.readObject();
            in.close();
            loadFile.close();
            Log.info("Successfully loaded NAS list.");
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("NAS class not found");
            Log.info("do /gnnas to generate new nas.cn file");
            c.printStackTrace();
            return;
        }

        try {
            String pureJson = Core.settings.getDataDirectory().child("mods/settings.json").readString();
            alldata = new JSONObject(new JSONTokener(pureJson));
            if (!alldata.has("async")){
                Log.err("settings.json missing async");
                //this.makeSettingsFile("settings.json");
                return;
            } else {
                data = alldata.getJSONObject("async");
            }
        } catch (Exception e) {
            if (e.getMessage().contains("File not found: config\\mods\\settings.json")){
                Log.err("AS: settings.json file is missing.");
                return;
            } else {
                Log.err("AS: Error reading JSON.");
                e.printStackTrace();
                return;
            }
        }

        if (data.has("spacing")) {
            spacing = data.getInt("spacing");
            Log.info("Autosync set to every " + spacing + " minutes.");
            auto at = new auto(Thread.currentThread(), spacing);
            at.setDaemon(false);
            at.start();
            Log.info("Attempting to start AS in 1 min...");
        } else {
            Log.err("async missing key `spacing`");
        }


        Events.on(EventType.WaveEvent.class, event -> {
            if (state.wave >= 35) {
                if (timer == 0) {
                    timer = 5;
                    for (Player p : playerGroup.all()) {
                        Call.onWorldDataBegin(p.con);
                        netServer.sendWorldData(p);
                        Call.onInfoToast(p.con, "Auto Sync completed.", 5);
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    timer--;
                }
            }
        });
    }
    public void registerServerCommands(CommandHandler handler){
        handler.register("gnnas", "generates new nas.cn file", arg -> {
            asyncList.clear();
            asyncList.add("TEST");
            try {
                FileOutputStream fileOut = new FileOutputStream("NAS.cn");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(asyncList);
                out.close();
                fileOut.close();
                Log.info("done");
            } catch (IOException i) {
                i.printStackTrace();
            }
        });
    }

    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("autosync", "Toggles autosync on or of.", (arg, player) -> {
            if (asyncList.contains(player.uuid)) {
                asyncList.remove(player.uuid);
                player.sendMessage("Enabled autosync.");
            } else {
                asyncList.add(player.uuid);
                player.sendMessage("Disabled autosync");
            }
        });
    }
}
