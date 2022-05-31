/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Producto;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author josem
 */
public class ProductoDAO {

    public List<String> getProductsName(Session session) {
        String hql = "select distinct nombre from Producto";
        Query query = session.createQuery(hql);

        List<String> listProductsName = query.list();

        return listProductsName;
    }

    public void addProduct(Session session, List<String> names) {
        Transaction tx = null;
        List<String> allProductsName = getProductsName(session);

        try {
            tx = session.beginTransaction();

            for (int i = 0; i < names.size(); i++) {
                if (!allProductsName.contains(names.get(i))) {
                    Producto newProduct = new Producto(names.get(i));
                    session.save(newProduct);
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
