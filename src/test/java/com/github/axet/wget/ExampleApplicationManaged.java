package com.github.axet.wget;

import java.io.File;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.DownloadInfo.Part;
import com.github.axet.wget.info.DownloadInfo.Part.States;
import com.github.axet.wget.info.ex.DownloadMultipartError;

public class ExampleApplicationManaged {

    AtomicBoolean stop = new AtomicBoolean(false);
    DownloadInfo info;
    long last;
    SpeedInfo speedInfo = new SpeedInfo();

    public static String formatSpeed(int bytes) {
        String str = "";
        float speed = bytes;
        if (speed < 1000000) {
            speed /= 1024;
            str += String.format("%.02f", speed) + " KB/s";
        } else {
            speed /= 1024 * 1024;
            str += String.format("%.02f", speed) + " MB/s";
        }
        return str;
    }

    public void run() {
        try {
            Runnable notify = new Runnable() {
                @Override
                public void run() {
                    // notify app or save download state
                    // you can extract information from DownloadInfo info;
                    switch (info.getState()) {
                    case EXTRACTING:
                    case EXTRACTING_DONE:
                    case DONE:
                        System.out.println(info.getState());
                        break;
                    case RETRYING:
                        System.out.println(info.getState() + " " + info.getDelay());
                        break;
                    case DOWNLOADING:
                        speedInfo.step(info.getCount());
                        long now = System.currentTimeMillis();
                        if (now - 1000 > last) {
                            last = now;

                            String parts = "";

                            for (Part p : info.getParts()) {
                                if (p.getState().equals(States.DOWNLOADING)) {
                                    parts += String.format("Part#%d(%.2f) ", p.getNumber(),
                                            p.getCount() / (float) p.getLength());
                                }
                            }

                            float p = info.getCount() / (float) info.getLength();

                            // finish speed calculation by adding remaining bytes speed
                            if (p == 1) {
                                speedInfo.end(info.getCount());
                            }

                            System.out.println(String.format("%.2f %s (%s / %s)", p, parts,
                                    formatSpeed(speedInfo.getCurrentSpeed()),
                                    formatSpeed(speedInfo.getAverageSpeed())));
                        }
                        break;
                    default:
                        break;
                    }
                }
            };

            // choice file
            URL url = new URL("http://download.virtualbox.org/virtualbox/5.0.16/VirtualBox-5.0.16-105871-OSX.dmg");

            // initialize url information object with or without proxy
            info = new DownloadInfo(url); // new DownloadInfo(url, new
                                          // ProxyInfo("proxy_addr", 8080,
                                          // "login", "password"))

            // extract information from the web
            info.extract(stop, notify);

            // enable multipart download
            info.enableMultipart();

            // Choice target file or set download folder
            File target = new File("/Users/axet/Downloads/VirtualBox-5.0.16-105871-OSX.dmg");

            // create wget downloader
            WGet w = new WGet(info, target);

            // init speedinfo
            speedInfo.start(0);

            // will blocks until download finishes
            w.download(stop, notify);
        } catch (DownloadMultipartError e) {
            for (Part p : e.getInfo().getParts()) {
                Throwable ee = p.getException();
                if (ee != null)
                    ee.printStackTrace();
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ExampleApplicationManaged e = new ExampleApplicationManaged();
        e.run();
    }
}