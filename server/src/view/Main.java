/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.Estar;
import entity.Etiqueta;
import entity.Oferta;
import entity.Supermercado;
import entity.Tener;
import entity.Usuario;
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
            String username = null;
            try {
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.split(":")[1].equals("login")) {
                        boolean check = checkUser(inputLine.split(":")[2], inputLine.split(":")[3]);
                        if (check) {
                            username = inputLine.split(":")[2];
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
                            Usuario user = new Usuario("admin", inputLine.split(":")[2], passwordHashed, 0);
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

                    if (inputLine.split(":")[1].equals("Markets")) {
                        out.println("S:Markets:" + getMarkets());
                    }
                    
                    if (inputLine.split(":")[1].equals("popularTags")) {
                        System.out.println(getPopularTags());
                        out.println("S:PopularTags:" + getPopularTags());
                    }

                    if (inputLine.split(":")[1].equals("AddOffer")) {
                        /**
                         * Add offer.
                         */

                        for (int i = 0; i < inputLine.split(":").length; i++) {
                            System.out.println(inputLine.split(":")[i]);
                        }

                        Session session = HibernateUtil.getSessionFactory().openSession();
                        Transaction tx = null;
                        Integer ofertaID = null;
                        Integer etiquetaID = null;
                        Integer supermercadoID = null;
                        Integer estarID = null;
                        Integer tenerID = null;
                        Integer userID = null;

                        String hql = "from Usuario where username like :keyword";
                        Query query = session.createQuery(hql);
                        query.setParameter("keyword", username);

                        List<Usuario> listUser = query.list();
                        for (Usuario aUser : listUser) {
                            userID = aUser.getId();
                        }

                        try {
                            tx = session.beginTransaction();
                            Oferta oferta = new Oferta(Float.parseFloat(inputLine.split(":")[3]), inputLine.split(":")[4] + " " + inputLine.split(":")[5], inputLine.split(":")[6], userID);
                            ofertaID = (Integer) session.save(oferta);

                            Supermercado supermercado = new Supermercado(inputLine.split(":")[7], Float.parseFloat(inputLine.split(":")[10]), Float.parseFloat(inputLine.split(":")[9]), inputLine.split(":")[8]);
                            supermercadoID = (Integer) session.save(supermercado);

                            Estar estar = new Estar(supermercadoID, ofertaID);
                            estarID = (Integer) session.save(estar);

                            String[] tagsList = inputLine.split(":")[2].split(",");

                            hql = "select nombre from Etiqueta";
                            query = session.createQuery(hql);

                            List<String> allTags = query.list();

                            for (int i = 0; i < tagsList.length; i++) {
                                if (!tagsList[i].equals("-")) {
                                    if (!allTags.contains(tagsList[i])) {
                                        Etiqueta etiqueta = new Etiqueta(tagsList[i].toLowerCase(), 0);
                                        etiquetaID = (Integer) session.save(etiqueta);
                                        Tener tener = null;
                                        if (i == 0) {
                                            tener = new Tener(etiquetaID, ofertaID, true);
                                        } else {
                                            tener = new Tener(etiquetaID, ofertaID, false);
                                        }
                                        tenerID = (Integer) session.save(tener);
                                    } else {
                                        hql = "select contador from Etiqueta where nombre like :keyword";
                                        query = session.createQuery(hql);
                                        query.setParameter("keyword", tagsList[i]);
                                        int contador = 0;

                                        List<Integer> listContador = query.list();
                                        for (Integer etiqContador : listContador) {
                                            contador = etiqContador + 1;

                                        }

                                        hql = "update Etiqueta set contador = :keyword where nombre like :name";
                                        query = session.createQuery(hql);
                                        query.setParameter("keyword", contador);
                                        query.setParameter("name", tagsList[i]);

                                        query.executeUpdate();

                                        hql = "select id from Etiqueta where nombre like :keyword";
                                        query = session.createQuery(hql);
                                        query.setParameter("keyword", tagsList[i]);

                                        List<Integer> listID = query.list();
                                        for (Integer etiqID : listID) {
                                            Tener tener = null;
                                            if (i == 0) {
                                                tener = new Tener(etiqID, ofertaID, true);
                                            } else {
                                                tener = new Tener(etiqID, ofertaID, false);
                                            }
                                            tenerID = (Integer) session.save(tener);
                                        }
                                    }
                                }
                            }

                            tx.commit();
                        } catch (HibernateException e) {
                            if (tx != null) {
                                tx.rollback();
                            }
                            e.printStackTrace();
                        } finally {
                            session.close();
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

        String hql = "from Usuario where username like :keyword";
        Query query = session.createQuery(hql);
        query.setParameter("keyword", clientUsername);

        String passwordDB = "";

        List<Usuario> listUser = query.list();
        for (Usuario aUser : listUser) {
            passwordDB = aUser.getPassword();
        }

        return passwordDB.equals(passwordHashed);
    }

    private static String hashPassword(String passwordToHash) {
        return SHA.generate512(passwordToHash);
    }

    /**
     * Get all markets.
     */
    private static String getMarkets() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String hql = "select distinct nombre from Supermercado";
        Query query = session.createQuery(hql);

        String markets = "";

        List<String> listMarkets = query.list();
        for (String aMarkets : listMarkets) {
            markets += aMarkets + ":";
        }

        return markets;
    }

    /**
     * Get all markets.
     */
    private static String getPopularTags() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String hql = "select nombre from Etiqueta order by contador desc";
        Query query = session.createQuery(hql);

        String popularTags = "";
        int contador = 0;

        List<String> listPopularTags = query.list();
        for (String aPTags : listPopularTags) {
            if (contador < 3) {
                popularTags += aPTags + ":";
                contador++;
            }
        }

        return popularTags;
    }
}
