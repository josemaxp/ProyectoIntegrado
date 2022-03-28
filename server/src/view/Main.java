/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.Usuarios;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;
import util.SHA;

/**
 *
 * @author josem
 */
public class Main {

    public static void main(String[] args) {
        int portService = 4444;

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portService);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + portService);
            System.exit(0);
        }

        //2-Accept a connection from a client
        Socket clientSocket = null;

        try {
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("New user conected");
                new User(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        try {
            clientSocket.close();
            serverSocket.close();
        } catch (IOException ex) {
            System.out.println("Close conexion error");
        }
    }

    public static class User extends Thread {

        Socket clientSocket = null;
        String username = null;

        User(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException ex) {
                System.out.println("Error en los flujos.");
            }
            String inputLine;
            try {
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.split(":")[1].equals("login")) {
                        boolean check = checkUser(inputLine.split(":")[2], inputLine.split(":")[3]);
                        if (check) {
                            out.println("S:Login:true");
                        } else {
                            out.println("S:Login:false");
                        }
                    }

                    if (inputLine.split(":")[1].equals("register")) {
                        String passwordHashed = hashPassword(inputLine.split(":")[3]);

                        Session session = HibernateUtil.getSessionFactory().openSession();
                        Transaction tx = null;
                        Integer userID = null;

                        try {
                            tx = session.beginTransaction();
                            Usuarios user = new Usuarios(inputLine.split(":")[2], passwordHashed);
                            userID = (Integer) session.save(user);
                            tx.commit();
                        } catch (HibernateException e) {
                            if (tx != null) {
                                tx.rollback();
                            }
                            e.printStackTrace();
                        } finally {
                            session.close();
                        }

                        if (userID == 0) {
                            out.println("S:Register:false");
                        } else {
                            out.println("S:Register:true");
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            try {
                out.close();
                in.close();
                clientSocket.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Check if password provided (hashed) is equal to password on database.
     */
    private static boolean checkUser(String clientUsername, String clientPassword) {
        String passwordHashed = SHA.generate512(clientPassword);

        Session session = HibernateUtil.getSessionFactory().openSession();

        String hql = "from Usuarios where username like :keyword";
        Query query = session.createQuery(hql);
        query.setParameter("keyword", clientUsername);

        String passwordDB = "";

        List<Usuarios> listUser = query.list();
        for (Usuarios aUser : listUser) {
            passwordDB = aUser.getPass();
        }

        return passwordDB.equals(passwordHashed);
    }

    private static String hashPassword(String passwordToHash) {
        return SHA.generate512(passwordToHash);
    }

}
