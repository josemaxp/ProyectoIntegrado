/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Relacionetiqueta;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author josem
 */
public class EtiquetaDAO {

    /**
     * Get top 3 popular tags.
     */
    public List<String> getTagsName(Session session) {
        String hql = "select nombre from Etiqueta order by contador desc";
        Query query = session.createQuery(hql);

        List<String> listPopularTags = query.list();

        return listPopularTags;
    }

    /**
     * Get top 3 relation tags.
     */
    public List<String> getRelationTags(Session session, String etiqueta) {
        String hql = "select id from Etiqueta where nombre like :nombre";
        Query query = session.createQuery(hql);
        query.setParameter("nombre", etiqueta);
        List<String> tagNames = new ArrayList<>();

        //Obtengo el id de la etiqueta escrita por el usuario
        List<Integer> tag = query.list();
        //Compruebo que exista
        if (tag.size() > 0) {
            //Ordeno la tabla de las relaciones entre etiquetas a partir de la tabla contador de mayor a menor para así directamente al obtener los 3 primeros, que estos sean los que más se repiten
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

                tagNames.add(tagName.get(0));
            }
        }
        return tagNames;
    }
}
