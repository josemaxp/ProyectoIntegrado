package josemanuel.marin.finalproject.controller;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
    static Socket socket;
    public static String IP = "192.168.1.139";

    public Connection() {
        try {
            socket = new Socket(IP, 4444);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: .");
            System.exit(1);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.err.println("Couldn't get I/O for the connection to: .");
            System.exit(1);
        }
    }

    public static Socket getSocket() {
        return socket;
    }
}
