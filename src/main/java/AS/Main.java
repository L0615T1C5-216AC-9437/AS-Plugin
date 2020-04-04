package AS;

//-----imports-----//
import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.struct.Array;
import mindustry.entities.type.Player;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.plugin.Plugin;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static mindustry.Vars.*;


public class Main extends Plugin {
    public Array<String> asyncList = new Array<>();

    private int timer = 5;

    public Main() throws InterruptedException {
        Events.on(EventType.ServerLoadEvent.class, event -> {
            try {
                FileInputStream loadFile = new FileInputStream("NAS.cn");
                ObjectInputStream in = new ObjectInputStream(loadFile);
                asyncList = (Array<String>) in.readObject();
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

            auto at = new auto(Thread.currentThread());
            at.setDaemon(false);
            at.start();
            Log.info("Attempting to start AS in 1 min...");
        });

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
            } catch (IOException | NullPointerException i) {
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
