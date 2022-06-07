package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connection Manager class
 *
 * @author gabri
 */
public class ConnectionManager {

    public static String IP = "";//192.168.1.139
    public static Socket kkSocket = null;
    public static PrintWriter out = null;
    public static BufferedReader in = null;
    static Properties prop;

    public static Connection connection;
    private static ConnectionManager instance;

    public ConnectionManager() {
        try ( InputStream is = new FileInputStream("warnmarket.ini")) {
            prop = new Properties();
            prop.load(is);
            IP = prop.getProperty("IP");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }

        return instance;
    }

    public static void setIP(String IP) {
        try ( FileWriter config = new FileWriter("warnmarket.ini", false);  PrintWriter pw = new PrintWriter(config);) {
            pw.println("IP = " + IP);

        } catch (IOException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized boolean getConnection() {
        try {
            kkSocket = new Socket(IP, 4444);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
}
