package AS;

//-----imports-----//
import arc.Events;
import arc.util.Log;
import mindustry.entities.type.Player;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.plugin.Plugin;

import java.util.concurrent.TimeUnit;

import static mindustry.Vars.*;


public class Main extends Plugin {
    public boolean ass = false; // ass -> auto sync started, not THAT

    public Main() throws InterruptedException {
        Events.on(EventType.WorldLoadEvent.class, event -> {
            if(!ass) {
                Thread AS = new Thread() {
                    public void run() {
                        Log.info("AS started Successfully!");
                        while (true) {
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
                            try {
                                TimeUnit.SECONDS.sleep(4 * 60);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Call.onInfoToast("Auto Sync in 1 minute.", 10);
                            try {
                                TimeUnit.SECONDS.sleep(60);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                AS.start();
                Log.info("Attempting to start AS...");
            }
        });
    }
}
