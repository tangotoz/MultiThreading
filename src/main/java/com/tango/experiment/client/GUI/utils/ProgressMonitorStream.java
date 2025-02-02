package com.tango.experiment.client.GUI.utils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InterruptedIOException;

public class ProgressMonitorStream{
    private ProgressMonitor monitor;
    private int nread = 0;

    public ProgressMonitorStream(String message, int size) {
        monitor = new ProgressMonitor(new Frame(), message, null, 0, size);
    }

    public int setProgress(int nr) throws IOException {

        if (nr > 0) monitor.setProgress(nread += nr);
        if (monitor.isCanceled()) {
            InterruptedIOException exc =
                    new InterruptedIOException("progress");
            exc.bytesTransferred = nread;
            throw exc;
        }
        return nr;
    }

    public ProgressMonitor getProgressMonitor(){
        return monitor;
    }

}

