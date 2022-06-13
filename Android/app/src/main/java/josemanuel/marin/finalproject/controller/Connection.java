package josemanuel.marin.finalproject.controller;

import java.io.IOException;
import java.net.Socket;

public class Connection extends Thread{
    static Socket socket = null;
    public static String IP = "";
    public static boolean state;

    public static synchronized Socket getSocket() {
        return socket;
    }

    public static synchronized void setIP(String ip) {
        IP = ip;
    }

    public void run() {
        try {
            socket = new Socket(IP, 4444);
            state = true;
        } catch (IOException ex) {
            IP = "";
            state = false;
        }
    }
}
