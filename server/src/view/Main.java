/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import dao.EtiquetaDAO;
import dao.OfertaDAO;
import dao.SupermercadoDAO;
import dao.UsuarioDAO;
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
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import org.hibernate.Session;
import util.HibernateUtil;

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

            UsuarioDAO userDAO = new UsuarioDAO();
            SupermercadoDAO marketDAO = new SupermercadoDAO();
            EtiquetaDAO tagDAO = new EtiquetaDAO();
            OfertaDAO ofertaDAO = new OfertaDAO();
            String inputLine;
            String username = "";

            try {
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.split(":")[1].equals("login")) {
                        String loginUsername = inputLine.split(":")[2];
                        String loginPassword = inputLine.split(":")[3];

                        boolean check = userDAO.checkUser(loginUsername, loginPassword, session);

                        if (check) {
                            username = loginUsername;
                            out.println("S:login:true");
                        } else {
                            out.println("S:login:false");
                        }
                    }

                    if (inputLine.split(":")[1].equals("register")) {
                        String registerUsername = inputLine.split(":")[2];
                        String registerPassword = inputLine.split(":")[3];
                        String registerEmail = inputLine.split(":")[4];

                        Integer userID = null;
                        userID = userDAO.registerUser(session, registerUsername, registerPassword, registerEmail);

                        if (userID == 0) {
                            out.println("S:register:false");
                        } else {
                            out.println("S:register:true");
                        }
                    }

                    if (inputLine.split(":")[1].equals("Markets")) {
                        String markets = marketDAO.getAllMarkets(session);
                        out.println("S:Markets:" + markets);
                    }

                    if (inputLine.split(":")[1].equals("popularTags")) {
                        int contador = 0;
                        String popularTags = "";
                        List<String> listPopularTags = tagDAO.getTagsName(session);;
                        for (String aPTags : listPopularTags) {
                            if (contador < 3) {
                                popularTags += aPTags + ":";
                                contador++;
                            }
                        }

                        out.println("S:PopularTags:" + popularTags);
                    }

                    if (inputLine.split(":")[1].equals("tagsName")) {
                        String tags = "";
                        List<String> listPopularTags = tagDAO.getTagsName(session);;
                        for (String aTags : listPopularTags) {
                            tags += aTags + ":";

                        }
                        out.println("S:tagsName:" + tags);
                    }

                    if (inputLine.split(":")[1].equals("relationTags")) {
                        String userTag = inputLine.split(":")[2];
                        String relationTags = tagDAO.getRelationTags(session, userTag);

                        out.println("S:relationTags:" + relationTags);
                    }

                    if (inputLine.split(":")[1].equals("showOffer")) {
                        Double latitud = Double.parseDouble(inputLine.split(":")[2]);
                        Double longitud = Double.parseDouble(inputLine.split(":")[3]);

                        String offers = ofertaDAO.showOffer(session, latitud, longitud);

                        out.println("S:showOffer:" + offers);
                    }

                    if (inputLine.split(":")[1].equals("img")) {
                        String fileName = inputLine.split(":")[2];
                        byte[] filebyte = new byte[Integer.valueOf(inputLine.split(":")[3])];

                        File userImages = new File("images/" + username);
                        if (!userImages.exists()) {
                            System.out.println("Creando carpeta de imágenes para el usuario " + username);
                            userImages.mkdirs();
                        }

                        String path = "images\\" + username + "\\" + fileName + ".png";
                        FileOutputStream fos = new FileOutputStream(path);
                        ofertaDAO.imageInformation(session, path);

                        InputStream is = clientSocket.getInputStream();

                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        int numeroBytesLeidos = 0;

                        while (numeroBytesLeidos != filebyte.length) {
                            numeroBytesLeidos += is.read(filebyte, 0, filebyte.length);
                            bos.write(filebyte, 0, numeroBytesLeidos);
                        }

                        bos.close();
                        fos.close();

                        out.println("");
                    }

                    if (inputLine.split(":")[1].equals("getUser")) {
                        out.println("S:getUser:" + username);
                    }

                    if (inputLine.split(":")[1].equals("AddOffer")) {
                        String[] tagsList = inputLine.split(":")[2].split(",");
                        Float precio = Float.parseFloat(inputLine.split(":")[3]);
                        String precioUnidad = inputLine.split(":")[4];
                        String unidad = inputLine.split(":")[5];
                        String imagen = inputLine.split(":")[6];
                        String nombreSupermercado = inputLine.split(":")[7];
                        String direccion = inputLine.split(":")[8];
                        Float latitud = Float.parseFloat(inputLine.split(":")[9]);
                        Float longitud = Float.parseFloat(inputLine.split(":")[10]);

                        ofertaDAO.addOffer(session, username, tagsList, precio, precioUnidad, unidad, imagen, nombreSupermercado, direccion, longitud, latitud);
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
}
