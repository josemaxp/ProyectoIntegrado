/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author josem
 */
public class OfertaDAO {

    public List<String> showOffer(Session session, Double latitud, Double longitud) {
        String hql = "from Oferta";
        Query query = session.createQuery(hql);
        String offer = "";
        List<String> allOffers = new ArrayList<>();

        List<Oferta> listOffer = query.list();
        for (Oferta aOffer : listOffer) {
            //Obtener el usuario que ha subido la oferta
            hql = "select username from Usuario where id = :id";
            query = session.createQuery(hql);
            query.setParameter("id", aOffer.getIdUsuario());

            List<String> offerUsername = query.list();

            //Obtener la lista de etiquetas que tiene la oferta
            hql = "from Tener";
            query = session.createQuery(hql);

            List<Tener> listTener = query.list();
            List<Integer> listIDTags = new ArrayList<>();

            for (Tener aTener : listTener) {
                //Añado en una lista todas los id de las etiquetas que se encuentran en esa oferta
                if (aTener.getId().getIdOferta() == aOffer.getId()) {
                    listIDTags.add(aTener.getId().getIdEtiqueta());
                }
            }

            //obtengo si la oferta es una oferta aprovada o no
            boolean approvedOffer = approvedOffer(session, aOffer, listIDTags, listTener);

            //Obtengo los nombres de esas etiquetas
            String tagName = "";
            for (int i = 0; i < listIDTags.size(); i++) {
                hql = "select nombre from Etiqueta where id = :id";
                query = session.createQuery(hql);
                query.setParameter("id", listIDTags.get(i));

                List<String> listTagName = query.list();
                tagName += listTagName.get(0) + "_";
            }

            //Obtener el supermercado que tiene la oferta
            hql = "from Estar";
            query = session.createQuery(hql);

            List<Estar> listEstar = query.list();
            int marketId = -1;

            for (Estar aEstar : listEstar) {
                if (aEstar.getId().getIdOferta() == aOffer.getId()) {
                    marketId = aEstar.getId().getIdSupermercado();
                }
            }

            if (marketId != -1) {
                hql = "from Supermercado where id = :id";
                query = session.createQuery(hql);
                query.setParameter("id", marketId);

                List<Supermercado> listMarket = query.list();
                double distancia = distanciaCoord(latitud, longitud, listMarket.get(0).getLatitud(), listMarket.get(0).getLongitud());

                offer += offerUsername.get(0) + "_" + aOffer.getPrecio() + "_" + aOffer.getPrecioUnidad() + "_" + listMarket.get(0).getNombre() + "_" + distancia + "_" + aOffer.getImagen() + "_" + approvedOffer + "_" + aOffer.getId() + "_" + listMarket.get(0).getLatitud() + "_" + listMarket.get(0).getLongitud() + "_" + tagName + ":";
                allOffers.add(offer);
                offer = "";
            }
        }

        return allOffers;
    }

    public void addOffer(Session session, String username, String[] tagsList, Float precio, String precioUnidad, String unidad, String imagen, String nombreSupermercado, String direccion, Float longitud, Float latitud) {
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

        hql = "update Usuario set puntos = puntos+100 where id like :id";
        query = session.createQuery(hql);
        query.setParameter("id", userID);

        query.executeUpdate();

        try {
            tx = session.beginTransaction();
            Oferta oferta = new Oferta(precio, precioUnidad + "€/" + unidad, imagen, userID);
            ofertaID = (Integer) session.save(oferta);

            hql = "from Supermercado";
            query = session.createQuery(hql);

            List<Supermercado> listMarkets = query.list();
            int marketID = -1;
            for (Supermercado aMarket : listMarkets) {
                double distancia = distanciaCoord(latitud, longitud, aMarket.getLatitud(), aMarket.getLongitud());

                //Si la distancia es menor a 100 metros y el nombre es igual
                if (distancia < 0.1 && aMarket.getNombre().equals(nombreSupermercado)) {
                    marketID = aMarket.getId();
                }
            }

            if (marketID == -1) {
                Supermercado supermercado = new Supermercado(nombreSupermercado, longitud, latitud, direccion);
                supermercadoID = (Integer) session.save(supermercado);

                EstarId estarId = new EstarId(supermercadoID, ofertaID);
                Estar estar = new Estar(estarId);
                session.save(estar);
            } else {
                EstarId estarId = new EstarId(marketID, ofertaID);
                Estar estar = new Estar(estarId);
                session.save(estar);
            }

            PublicarId publicarId = new PublicarId(userID, ofertaID);
            Publicar publicar = new Publicar(publicarId);
            session.save(publicar);

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

    public void imageInformation(Session session, String path) {
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            String hql = "select id from Oferta order by id desc";
            Query query = session.createQuery(hql);
            List<Integer> idOffer = query.list();

            hql = "update Oferta set imagen = :path where id = :id";
            query = session.createQuery(hql);
            query.setParameter("path", path);
            query.setParameter("id", idOffer.get(0));

            query.executeUpdate();

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    //Calcular distancia entre dos puntos geográficos
    private double distanciaCoord(double lat1, double lng1, double lat2, double lng2) {
        double radioTierra = 6371;//en kilómetros  
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double distancia = radioTierra * va2;

        return distancia;
    }

    public List<String> myOffers(Session session, String username, Double latitud, Double longitud) {
        //Obtener el id del usuario
        String hql = "select id from Usuario where username = :username";
        Query query = session.createQuery(hql);
        query.setParameter("username", username);

        List<Integer> userID = query.list();

        //Obtener las ofertas de únicamente ese usuario
        hql = "from Oferta where idUsuario = :idUsuario";
        query = session.createQuery(hql);
        query.setParameter("idUsuario", userID.get(0));

        System.out.println(userID.get(0));

        String offer = "";
        List<String> allOffers = new ArrayList<>();

        List<Oferta> listOffer = query.list();
        for (Oferta aOffer : listOffer) {
            //Obtener la lista de etiquetas que tiene la oferta
            hql = "from Tener";
            query = session.createQuery(hql);

            List<Tener> listTener = query.list();
            List<Integer> listIDTags = new ArrayList<>();

            for (Tener aTener : listTener) {
                //Añado en una lista todas los id de las etiquetas que se encuentran en esa oferta
                if (aTener.getId().getIdOferta() == aOffer.getId()) {
                    listIDTags.add(aTener.getId().getIdEtiqueta());
                }
            }

            //obtengo si la oferta es una oferta aprovada o no
            boolean approvedOffer = approvedOffer(session, aOffer, listIDTags, listTener);

            //Obtengo los nombres de esas etiquetas
            String tagName = "";
            for (int i = 0; i < listIDTags.size(); i++) {
                hql = "select nombre from Etiqueta where id = :id";
                query = session.createQuery(hql);
                query.setParameter("id", listIDTags.get(i));

                List<String> listTagName = query.list();
                tagName += listTagName.get(0) + "_";
            }

            //Obtener el supermercado que tiene la oferta
            hql = "from Estar";
            query = session.createQuery(hql);

            List<Estar> listEstar = query.list();
            int marketId = -1;

            for (Estar aEstar : listEstar) {
                if (aEstar.getId().getIdOferta() == aOffer.getId()) {
                    marketId = aEstar.getId().getIdSupermercado();
                }
            }

            if (marketId != -1) {
                hql = "from Supermercado where id = :id";
                query = session.createQuery(hql);
                query.setParameter("id", marketId);

                List<Supermercado> listMarket = query.list();
                double distancia = distanciaCoord(latitud, longitud, listMarket.get(0).getLatitud(), listMarket.get(0).getLongitud());

                offer += username + "_" + aOffer.getPrecio() + "_" + aOffer.getPrecioUnidad() + "_" + listMarket.get(0).getNombre() + "_" + distancia + "_" + aOffer.getImagen() + "_" + approvedOffer + "_" + aOffer.getId() + "_" + listMarket.get(0).getLatitud() + "_" + listMarket.get(0).getLongitud() + "_" + tagName + ":";
                allOffers.add(offer);
                offer = "";
            }
        }

        return allOffers;
    }

    private boolean approvedOffer(Session session, Oferta oferta, List<Integer> listIDTags, List<Tener> listTener) {
        int tagsCount = 0;
        int idCompareMarket = -1;
        int idCurrentMarket = -1;
        boolean approvedOffer = false;

        String hql = "from Oferta";
        Query query = session.createQuery(hql);

        List<Oferta> allOffers = query.list();

        hql = "from Estar";
        query = session.createQuery(hql);

        List<Estar> market_offer_relation = query.list();

        //Recorro todas las ofertas que existen
        for (Oferta offer : allOffers) {
            Set compareListIDTags = new HashSet();

            //Si la oferta es distinta a la que le paso y el usuario es distinto entonces entra
            if (offer.getId() != oferta.getId() && offer.getIdUsuario() != oferta.getIdUsuario()) {

                //Compruebo si las ofertas están en el mismo supermercado
                for (Estar aEstar : market_offer_relation) {
                    if (aEstar.getId().getIdOferta() == offer.getId()) {
                        idCompareMarket = aEstar.getId().getIdSupermercado();
                    } else if (aEstar.getId().getIdOferta() == oferta.getId()) {
                        idCurrentMarket = aEstar.getId().getIdSupermercado();
                    }
                }

                //Si están en el mismo, procedo a comprobar las etiquetas que tienen en común
                if (idCompareMarket == idCurrentMarket) {
                    //recorro toda la tabla 'Tener' y compruebo que el id de la oferta que está en el bucle ahora mismo es el mismo que el que ha encontrado en la tabla tener.
                    //Si es el mismo, miro las etiquetas que tiene y las comparo con las de la oferta actual. Si hay más de 3 iguales entonces es una oferta aprobada.
                    for (Tener aTener : listTener) {
                        if (aTener.getId().getIdOferta() == offer.getId()) {
                            compareListIDTags.add(aTener.getId().getIdEtiqueta());
                        }
                    }

                    //Ahora compruebo las etiquetas compartidas
                    for (int i = 0; i < listIDTags.size(); i++) {
                        if (compareListIDTags.contains(listIDTags.get(i))) {
                            tagsCount++;
                        }
                    }

                    if (tagsCount >= 3) {
                        approvedOffer = true;
                    }
                }
            }
        }

        return approvedOffer;
    }

    public void deleteOffer(Session session, int id) {

        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            Oferta oferta = (Oferta) session.get(Oferta.class, id);
            session.delete(oferta);

            String hql = "from Estar";
            Query query = session.createQuery(hql);

            List<Estar> offersInMarkets = query.list();

            for (Estar aEstar : offersInMarkets) {
                if (aEstar.getId().getIdOferta() == id) {
                    hql = "delete Estar where id = :id";
                    query = session.createQuery(hql);
                    query.setParameter("id", aEstar.getId());

                    query.executeUpdate();
                }
            }
            
            hql = "from Publicar";
            query = session.createQuery(hql);

            List<Publicar> offersUsers = query.list();

            for (Publicar aPublicar : offersUsers) {
                if (aPublicar.getId().getIdOferta() == id) {
                    hql = "delete Publicar where id = :id";
                    query = session.createQuery(hql);
                    query.setParameter("id", aPublicar.getId());

                    query.executeUpdate();
                }
            }
            
            hql = "from Tener";
            query = session.createQuery(hql);

            List<Tener> offersTags = query.list();

            for (Tener aTener : offersTags) {
                if (aTener.getId().getIdOferta() == id) {
                    hql = "delete Tener where id = :id";
                    query = session.createQuery(hql);
                    query.setParameter("id", aTener.getId());

                    query.executeUpdate();
                }
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

}
