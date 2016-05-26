package sid.comslav.com.circleofmusic.helper;

import android.os.Environment;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.GregorianCalendar;

public class loggingHandler {
    public static PrintWriter logWriter;

    public loggingHandler() {
        logWriter = null;
        try {
            logWriter = new PrintWriter(new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/com-log.txt", false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLog(String logMessage) {
        GregorianCalendar gc = new GregorianCalendar();
        logMessage = gc.getTime().toString() + "\t" + logMessage;
        assert logWriter != null;
        logWriter.append(logMessage);
        logWriter.close();
    }
}
