/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.Estar;
import entity.EstarId;
import entity.Etiqueta;
import entity.Oferta;
import entity.Publicar;
import entity.PublicarId;
import entity.Relacionetiqueta;
import entity.RelacionetiquetaId;
import entity.Supermercado;
import entity.Tener;
import entity.TenerId;
import entity.Usuario;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
                Session session = HibernateUtil.getSessionFactory().openSession();
                clientSocket = serverSocket.accept();
                System.out.println("New user conected");
                new User(clientSocket, session).start();
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
        Session session = null;

        User(Socket clientSocket, Session session) {
            this.clientSocket = clientSocket;
            this.session = session;
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
                        boolean check = checkUser(inputLine.split(":")[2], inputLine.split(":")[3], session);
                        if (check) {
                            username = inputLine.split(":")[2];
                            out.println("S:Login:true");

                        } else {
                            out.println("S:Login:false");
                        }
                    }

                    if (inputLine.split(":")[1].equals("register")) {
                        String passwordHashed = hashPassword(inputLine.split(":")[3]);

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
                        }

                        if (userID == 0) {
                            out.println("S:Register:false");
                        } else {
                            out.println("S:Register:true");
                        }
                    }

                    if (inputLine.split(":")[1].equals("Markets")) {
                        out.println("S:Markets:" + getMarkets(session));
                    }

                    if (inputLine.split(":")[1].equals("popularTags")) {
                        System.out.println(getPopularTags(session));
                        out.println("S:PopularTags:" + getPopularTags(session));
                    }

                    if (inputLine.split(":")[1].equals("relationTags")) {
                        out.println("S:relationTags:" + getRelationTags(session, inputLine.split(":")[2]));
                    }

                    if (inputLine.split(":")[1].equals("AddOffer")) {
                        /**
                         * Add offer.
                         */

                        Transaction tx = null;
                        Integer ofertaID = null;
                        Integer etiquetaID = null;
                        Integer supermercadoID = null;
                        Integer userID = null;
                        List<Integer> tagsID = new ArrayList();

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

                            String nombreSupermercado = inputLine.split(":")[7];
                            Float longitud = Float.parseFloat(inputLine.split(":")[10]);
                            Float latitud = Float.parseFloat(inputLine.split(":")[9]);

                            hql = "from Supermercado";
                            query = session.createQuery(hql);

                            List<Supermercado> listMarkets = query.list();
                            int marketID = -1;
                            for (Supermercado aMarket : listMarkets) {
                                double distancia = Math.sqrt((longitud - aMarket.getLongitud()) * (longitud - aMarket.getLongitud()) + (latitud - aMarket.getLatitud()) * (latitud - aMarket.getLatitud()));

                                if (distancia < 40 && aMarket.getNombre().equals(nombreSupermercado)) {
                                    marketID = aMarket.getId();
                                }
                            }

                            if (marketID == -1) {
                                Supermercado supermercado = new Supermercado(nombreSupermercado, longitud, latitud, inputLine.split(":")[8]);
                                supermercadoID = (Integer) session.save(supermercado);

                                EstarId estarId = new EstarId(supermercadoID,ofertaID);
                                Estar estar = new Estar(estarId);
                                session.save(estar);
                            } else {
                                EstarId estarId = new EstarId(marketID,ofertaID);
                                Estar estar = new Estar(estarId);
                                session.save(estar);
                            }

                            PublicarId publicarId = new PublicarId(userID, ofertaID);
                            Publicar publicar = new Publicar(publicarId);
                            session.save(publicar);

                            String[] tagsList = inputLine.split(":")[2].split(",");

                            hql = "select nombre from Etiqueta";
                            query = session.createQuery(hql);

                            List<String> allTags = query.list();

                            for (int i = 0; i < tagsList.length; i++) {
                                if (!tagsList[i].equals("-")) {
                                    if (!allTags.contains(tagsList[i].trim())) {
                                        Etiqueta etiqueta = new Etiqueta(tagsList[i].trim().toLowerCase(), 0);
                                        etiquetaID = (Integer) session.save(etiqueta);

                                        //añado los id de las etiquetas puestas por el usuario a una lista
                                        tagsID.add(etiquetaID);

                                        Tener tener = null;
                                        TenerId tenerId = null;
                                        if (i == 0) {
                                            tenerId = new TenerId(etiquetaID, ofertaID);
                                            tener = new Tener(tenerId, true);
                                        } else {
                                            tenerId = new TenerId(etiquetaID, ofertaID);
                                            tener = new Tener(tenerId, false);
                                        }
                                        session.save(tener);
                                    } else {

                                        hql = "update Etiqueta set contador = contador+1 where nombre like :name";
                                        query = session.createQuery(hql);
                                        query.setParameter("name", tagsList[i].trim());

                                        query.executeUpdate();

                                        hql = "select id from Etiqueta where nombre like :keyword";
                                        query = session.createQuery(hql);
                                        query.setParameter("keyword", tagsList[i].trim());

                                        List<Integer> listID = query.list();
                                        for (Integer etiqID : listID) {
                                            tagsID.add(etiqID);
                                            Tener tener = null;
                                            TenerId tenerId = null;
                                            if (i == 0) {
                                                tenerId = new TenerId(etiqID, ofertaID);
                                                tener = new Tener(tenerId, true);
                                            } else {
                                                tenerId = new TenerId(etiqID, ofertaID);
                                                tener = new Tener(tenerId, false);
                                            }
                                            session.save(tener);
                                        }
                                    }
                                }
                            }

                            Collections.sort(tagsID);

                            boolean comprobarSiExisteRelacion = false;
                            for (int i = 0; i < tagsID.size() - 1; i++) {
                                for (int j = i + 1; j < tagsID.size(); j++) {

                                    hql = "from Relacionetiqueta";
                                    query = session.createQuery(hql);

                                    List<Relacionetiqueta> relationsTags = query.list();

                                    for (Relacionetiqueta relation : relationsTags) {
                                        if (relation.getId().getIdEtq1() == tagsID.get(i) && relation.getId().getIdEtq2() == tagsID.get(j)) {

                                            RelacionetiquetaId RelacionetiquetaId = new RelacionetiquetaId(tagsID.get(i), tagsID.get(j));

                                            hql = "update Relacionetiqueta set contador = contador+1 where id = :id";
                                            query = session.createQuery(hql);
                                            query.setParameter("id", RelacionetiquetaId);

                                            int update = query.executeUpdate();
                                            comprobarSiExisteRelacion = true;
                                            break;
                                        } else if (relation.getId().getIdEtq1() == tagsID.get(j) && relation.getId().getIdEtq2() == tagsID.get(i)) {
                                            RelacionetiquetaId RelacionetiquetaId = new RelacionetiquetaId(tagsID.get(j), tagsID.get(i));

                                            hql = "update Relacionetiqueta set contador = contador+1 where id = :id";
                                            query = session.createQuery(hql);
                                            query.setParameter("id", RelacionetiquetaId);

                                            query.executeUpdate();
                                            comprobarSiExisteRelacion = true;
                                            break;
                                        } else {
                                            comprobarSiExisteRelacion = false;
                                        }
                                    }

                                    if (!comprobarSiExisteRelacion) {
                                        RelacionetiquetaId relacionEtiquetaId = new RelacionetiquetaId(tagsID.get(i), tagsID.get(j));
                                        Relacionetiqueta relacionEtiqueta = new Relacionetiqueta(relacionEtiquetaId, 1);
                                        session.save(relacionEtiqueta);
                                    }
                                }
                            }
                            tagsID.clear();
                            tx.commit();
                        } catch (HibernateException e) {
                            if (tx != null) {
                                tx.rollback();
                            }
                            e.printStackTrace();
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
                session.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Check if password provided (hashed) is equal to password on database.
     */
    private static boolean checkUser(String clientUsername, String clientPassword, Session session) {
        String passwordHashed = SHA.generate512(clientPassword);

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
    private static String getMarkets(Session session) {
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
     * Get top 3 popular tags.
     */
    private static String getPopularTags(Session session) {
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

    /**
     * Get top 3 relation tags.
     */
    private static String getRelationTags(Session session, String etiqueta) {
        String hql = "select id from Etiqueta where nombre like :nombre";
        Query query = session.createQuery(hql);
        query.setParameter("nombre", etiqueta);

        List<Integer> tag = query.list();
        String relaionTags = "";
        if (tag.size() > 0) {

            hql = "from Relacionetiqueta order by contador desc";
            query = session.createQuery(hql);

            List<Relacionetiqueta> relationTags = query.list();

            List<Integer> relationTagIDs = new ArrayList();
            for (Relacionetiqueta relation : relationTags) {
                if (relation.getId().getIdEtq1() == tag.get(0)) {
                    relationTagIDs.add(relation.getId().getIdEtq2());
                } else if (relation.getId().getIdEtq2() == tag.get(0)) {
                    relationTagIDs.add(relation.getId().getIdEtq1());
                }
            }

            for (int i = 0; i < relationTagIDs.size(); i++) {
                hql = "select nombre from Etiqueta where id = :id";
                query = session.createQuery(hql);
                query.setParameter("id", relationTagIDs.get(i));

                List<String> tagName = query.list();

                relaionTags += tagName.get(0) + ":";
            }
        }

        return relaionTags;
    }
}
