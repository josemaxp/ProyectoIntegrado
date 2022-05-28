package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
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

    public static String IP = "";
    public static Socket kkSocket = null;
    public static PrintWriter out = null;
    public static BufferedReader in = null;

    private Connection connection;
    private static ConnectionManager instance;

    public ConnectionManager() {
        try ( InputStream is = new FileInputStream("warnmarket.ini")) {

            Properties prop = new Properties();
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

    public synchronized Connection getConnection() {
        try {
            kkSocket = new Socket(IP, 4444);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }
}
