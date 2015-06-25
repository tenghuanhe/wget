package com.github.axet.wget;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class ExampleDirectDownload {

    public static void main(String[] args) {
        try {
            // choise internet url (ftp, http)
            URL url = new URL("http://www.dd-wrt.com/routerdb/de/download/D-Link/DIR-300/A1/ap61.ram/2049");
            // choise target folder or filename "/Users/axet/Downloads/ap61.ram"
            File target = new File("/Users/axet/Downloads/");
            // initialize wget object
            WGet w = new WGet(url, target);
            // single thread download. will return here only when file download
            // is complete (or error raised).
            w.download();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RuntimeException allDownloadExceptions) {
            allDownloadExceptions.printStackTrace();
        }
    }
}