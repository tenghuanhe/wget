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

                            System.out.println(
                                    String.format("%.2f %s", info.getCount() / (float) info.getLength(), parts));
                        }
                        break;
                    default:
                        break;
                    }
                }
            };

            // choise file
            URL url = new URL("http://download.virtualbox.org/virtualbox/4.2.4/VirtualBox-4.2.4-81684-OSX.dmg");
            // initialize url information object
            info = new DownloadInfo(url);
            // extract infromation from the web
            info.extract(stop, notify);
            // enable multipart donwload
            info.enableMultipart();
            // Choise target file
            File target = new File("/Users/axet/Downloads/VirtualBox-4.2.4-81684-OSX.dmg");
            // create wget downloader
            WGet w = new WGet(info, target);
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