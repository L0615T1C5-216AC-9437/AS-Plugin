package AS;

import arc.util.Log;
import mindustry.entities.type.Player;
import mindustry.gen.Call;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;

import static mindustry.Vars.*;

public class auto extends Thread {
    private Thread mt;
    private int timer;

    public auto(Thread _mt, int spacing) {
        mt = _mt;
        timer = spacing;
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
                FileOutputStream fileOut = new FileOutputStream("NAS.cn");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(Main.asyncList);
                out.close();
                fileOut.close();
                Log.info("done");
            } catch (IOException i) {
                i.printStackTrace();
            }
            try {
                TimeUnit.MINUTES.sleep(timer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.info("AS switching to every wave to reduce lag.");
        while (mt.isAlive()) {
            try {
                FileOutputStream fileOut = new FileOutputStream("NAS.cn");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(Main.asyncList);
                out.close();
                fileOut.close();
                Log.info("done");
            } catch (IOException i) {
                i.printStackTrace();
            }
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
