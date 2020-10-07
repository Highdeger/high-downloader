package highdeger.highdownloader;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class HighUtil {

    enum FileType {unknown, compressed, document, video, audio, program}
    enum Status {all, completed, uncompleted}

    static String bytesBeautify(long bytes) {
        String[] units = {"Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        int weight = 0;
        double bytes_floating = bytes;
        while (bytes_floating >= 1024) {
            weight++;
            bytes_floating = bytes_floating / 1024;
            if (weight == units.length - 1) {
                break;
            }
        }
        String u;
        if (weight < units.length)
            u = units[weight];
        else
            u = units[units.length - 1];
        return String.format("%.3f %s", bytes_floating, u);
    }

    static String bareUrl(String url) {
        if (url.contains("?"))
            return url.split("\\?")[0];
        else
            return url;
    }

    // unused downloading method
    static void downloadUsingChannel(String urlStr, String filepath) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(filepath);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
}
