package aal.syslearner;

import java.io.IOException;

public class AalSysLearner
{
    public static void main(String args[]) throws IOException {
        DataController dc = new DataController();
        dc.getTraces();
    }
}
