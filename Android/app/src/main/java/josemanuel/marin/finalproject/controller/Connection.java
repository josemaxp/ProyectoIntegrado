package josemanuel.marin.finalproject.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
    static Socket socket;
    public static String IP;

    public Connection() {
        try  {
            socket = new Socket(IP, 4444);
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

    public static void setIP(String ip){
        IP = ip;
    }
}
