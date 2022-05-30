/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Estar;
import entity.Oferta;
import entity.Producto;
import entity.Receta;
import entity.Supermercado;
import entity.Tener;
import entity.Tenerproducto;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

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
                query.setParameter("id", listProducts.get(i).getId());

                List<Producto> listProductName = query.list();
                productName += listProductName.get(0).getNombre() + ": " + listProducts.get(i).getCantidad() + " " + listProducts.get(i).getUnidadmedida() + "_";
            }

            recipes += aRecipe.getId() + "_" + recipeUsername.get(0) + "_" + aRecipe.getNombre() + "_" + aRecipe.getComensales() + "_" + aRecipe.getPasos() + "_" + aRecipe.getLikes() + "_" + aRecipe.getUtensilios() + "_" + aRecipe.getImagen() + "_" + aRecipe.getTiempo() + "_" + productName + ":";
            allRecipes.add(recipes);
            recipes = "";

        }

        return allRecipes;
    }
}
