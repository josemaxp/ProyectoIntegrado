/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
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

        String markets = "";

        List<String> listMarkets = query.list();
        
        return listMarkets;
    }
}
