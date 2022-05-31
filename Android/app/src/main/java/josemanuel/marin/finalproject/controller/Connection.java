package josemanuel.marin.finalproject.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
    static Socket socket = null;
    public static String IP = "";

    public Connection() {
        try {
            socket = new Socket(getIP(), 4444);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:");
            IP = "";
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: .");
            IP = "";
        }
    }

    public static Socket getSocket() {
        return socket;
    }

    public static String getIP() {
        return IP;
    }

    public static void setIP(String ip){
        IP = ip;
    }
}
