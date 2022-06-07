package josemanuel.marin.finalproject.controller;

import java.io.IOException;
import java.net.Socket;

public class Connection {
    static Socket socket = null;
    public static String IP = "";

    public Connection() {
    }

    public synchronized boolean getConnection() {
        try {
            socket = new Socket(IP, 4444);
        } catch (IOException ex) {
            IP = "";
            return false;
        }
        return true;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void setIP(String ip) {
        IP = ip;
    }
}
