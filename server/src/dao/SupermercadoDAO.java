/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Supermercado;
import java.util.List;
import java.util.Objects;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author josem
 */
public class SupermercadoDAO {

    public List<String> getAllMarkets(Session session) {
        String hql = "select distinct nombre from Supermercado";
        Query query = session.createQuery(hql);

        List<String> listMarkets = query.list();

        return listMarkets;
    }

    public List<String> getComunidadesAutonomas(Session session) {
        String hql = "select distinct comunidadAutonoma from Supermercado";
        Query query = session.createQuery(hql);

        List<String> listComunidadesAutonomas = query.list();

        return listComunidadesAutonomas;
    }

    public List<String> getProvincias(Session session, String CCAA) {
        String hql = "select distinct provincia from Supermercado where comunidadAutonoma = :CCAA";
        Query query = session.createQuery(hql);
        query.setParameter("CCAA", CCAA);

        List<String> listProvincias = query.list();

        return listProvincias;
    }

    public List<String> getPoblacion(Session session, String provincia) {
        String hql = "select distinct poblacion from Supermercado where provincia = :provincia";
        Query query = session.createQuery(hql);
        query.setParameter("provincia", provincia);

        List<String> listPoblaciones = query.list();

        return listPoblaciones;
    }

    public Supermercado getMarket(Session session, String nombre, Float latitud, Float longitud) {
        String hql = "from Supermercado where nombre = :nombre";
        Query query = session.createQuery(hql);
        query.setParameter("nombre", nombre);

        List<Supermercado> allMarkets = query.list();
        Supermercado market = null;

        for (Supermercado aMarket : allMarkets) {
            if (Objects.equals(aMarket.getLatitud(), latitud) && Objects.equals(aMarket.getLongitud(), longitud)) {                
                market = aMarket;
            }
        }

        return market;
    }
}
