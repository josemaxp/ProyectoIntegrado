/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.DenunciasofertasId;
import entity.Denunciasrecetas;
import entity.DenunciasrecetasId;
import entity.Estar;
import entity.Oferta;
import entity.Producto;
import entity.Receta;
import entity.Tener;
import entity.Tenerproducto;
import entity.TenerproductoId;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author josem
 */
public class RecetaDAO {

    public List<String> showRecipe(Session session) {
        String hql = "from Receta";
        Query query = session.createQuery(hql);

        String recipes = "";
        List<String> allRecipes = new ArrayList<>();

        List<Receta> listRecipe = query.list();
        for (Receta aRecipe : listRecipe) {
            //Obtener el usuario que ha subido la receta
            hql = "select username from Usuario where id = :id";
            query = session.createQuery(hql);
            query.setParameter("id", aRecipe.getIdUsuario());

            List<String> recipeUsername = query.list();

            //Obtener la lista de productos que tiene la receta
            hql = "from Tenerproducto";
            query = session.createQuery(hql);

            List<Tenerproducto> listTener = query.list();
            List<Tenerproducto> listProducts = new ArrayList<>();

            for (Tenerproducto aTener : listTener) {
                //Añado en una lista todas los id de los productos que se encuentran en esa receta
                if (aTener.getId().getIdReceta() == aRecipe.getId()) {
                    listProducts.add(aTener);
                }
            }

            //Obtengo la información de esos productos
            String productName = "";
            for (int i = 0; i < listProducts.size(); i++) {
                hql = "from Producto where id = :id";
                query = session.createQuery(hql);
                query.setParameter("id", listProducts.get(i).getId().getIdProducto());

                List<Producto> listProductName = query.list();
                productName += listProductName.get(0).getNombre() + "|" + listProducts.get(i).getCantidad() + "|" + listProducts.get(i).getUnidadmedida() + "_";
            }

            if (productName.equals("")) {
                productName = "null";
            }

            recipes += aRecipe.getId() + "_" + recipeUsername.get(0) + "_" + aRecipe.getNombre() + "_" + aRecipe.getComensales() + "_" + aRecipe.getPasos() + "_" + aRecipe.getLikes() + "_" + aRecipe.getUtensilios() + "_" + aRecipe.getImagen() + "_" + aRecipe.getTiempo() + "_" + productName + ":";
            allRecipes.add(recipes);
            recipes = "";

        }

        return allRecipes;
    }

    public void addRecipe(Session session, String username, String recipeName, String steps, String cookware, int people, String time, List<String> products) {
        Transaction tx = null;
        List<String> productsName = new ArrayList();
        List<String> productsQuantity = new ArrayList();
        List<String> productsMeasure = new ArrayList();
        Integer recipeID = -1;

        for (int i = 0; i < products.size(); i++) {
            String[] separatedProducts = products.get(i).split("_");//[0] =  nombre, [1] = cantidad, [2] = unidad de media
            productsName.add(separatedProducts[0].toLowerCase());
            productsQuantity.add(separatedProducts[1]);
            productsMeasure.add(separatedProducts[2]);
        }

        ProductoDAO productoDAO = new ProductoDAO();
        productoDAO.addProduct(session, productsName);

        String hql = "select id from Usuario where username = :username";
        Query query = session.createQuery(hql);
        query.setParameter("username", username);

        List<Integer> userID = query.list();

        try {
            tx = session.beginTransaction();

            Receta newRecipe = new Receta(recipeName, time, people, steps, cookware, 0, "", userID.get(0));
            recipeID = (Integer) session.save(newRecipe);

            for (int i = 0; i < productsName.size(); i++) {

                hql = "select id from Producto where nombre = :nombre";
                query = session.createQuery(hql);
                query.setParameter("nombre", productsName.get(i));

                List<Integer> productID = query.list();

                TenerproductoId tenerproductoID = new TenerproductoId(productID.get(0), recipeID);
                Tenerproducto tenerproducto = new Tenerproducto(tenerproductoID, Double.parseDouble(productsQuantity.get(i)), productsMeasure.get(i));

                session.save(tenerproducto);
            }

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
            String hql = "select id from Receta order by id desc";
            Query query = session.createQuery(hql);
            List<Integer> idOffer = query.list();

            hql = "update Receta set imagen = :path where id = :id";
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

    public void deleteRecipe(Session session, int recipeID) {
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            Receta recipe = (Receta) session.get(Receta.class, recipeID);
            session.delete(recipe);

            String hql = "from Tenerproducto";
            Query query = session.createQuery(hql);

            List<Tenerproducto> recipesProduct = query.list();

            for (Tenerproducto aTenerProducto : recipesProduct) {
                if (aTenerProducto.getId().getIdReceta() == recipeID) {
                    hql = "delete Tenerproducto where id = :id";
                    query = session.createQuery(hql);
                    query.setParameter("id", aTenerProducto.getId());

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

    public void reportRecipe(Session session, int recipeID, String username) {
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            boolean comprobarDenuncia = false;
            int contadorDenuncias = 0;

            String hql = "select id from Usuario where username = :username";
            Query query = session.createQuery(hql);
            query.setParameter("username", username);

            List<Integer> userID = query.list();

            hql = "from Denunciasrecetas";
            query = session.createQuery(hql);

            List<Denunciasrecetas> denunciasRecipeList = query.list();

            for (Denunciasrecetas aDenuncia : denunciasRecipeList) {
                if (aDenuncia.getId().getIdReceta() == recipeID && aDenuncia.getId().getIdUsuario() == userID.get(0)) {
                    comprobarDenuncia = true;
                }

                if (aDenuncia.getId().getIdReceta() == recipeID) {
                    contadorDenuncias++;
                }
            }

            if (!comprobarDenuncia) {
                DenunciasrecetasId denunciasRecipesId = new DenunciasrecetasId(userID.get(0), recipeID);
                Denunciasrecetas denunciasrecipes = new Denunciasrecetas(denunciasRecipesId);

                session.save(denunciasrecipes);

                //Lo hago de nuevo para contar la nueva denuncia
                hql = "from Denunciasrecetas";
                query = session.createQuery(hql);

                denunciasRecipeList = query.list();

                for (Denunciasrecetas aDenuncia : denunciasRecipeList) {
                    if (aDenuncia.getId().getIdReceta() == recipeID) {
                        contadorDenuncias++;
                    }
                }

                if (contadorDenuncias == 10) {
                    deleteRecipe(session, recipeID);
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
