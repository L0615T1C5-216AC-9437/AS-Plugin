package AS;

import arc.util.Log;
import mindustry.entities.type.Player;
import mindustry.gen.Call;

import java.util.concurrent.TimeUnit;

import static mindustry.Vars.*;

public class auto extends Thread {
    private Thread mt;

    public auto(Thread _mt) {
        mt = _mt;
    }

    public void run(){
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.info("AS started Successfully!");
        while (this.mt.isAlive() && state.wave < 35) {
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
                TimeUnit.SECONDS.sleep(5 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.info("AS switching to every wave to reduce lag.");
    }
}
