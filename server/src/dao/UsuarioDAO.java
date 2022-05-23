/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Usuario;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.SHA;

/**
 *
 * @author josem
 */
public class UsuarioDAO {

    public int registerUser(Session session, String username, String password, String mail) {
        Transaction tx = null;
        Integer userID = 0;
        String passwordHashed = SHA.generate512(password);

        try {
            tx = session.beginTransaction();
            Usuario user = new Usuario(mail, username, passwordHashed, 0);

            String hql = "from Usuario where username like :keyword";
            Query query = session.createQuery(hql);
            query.setParameter("keyword", username);

            List<Usuario> listUsername = query.list();

            hql = "from Usuario where correo like :keyword";
            query = session.createQuery(hql);
            query.setParameter("keyword", mail);

            List<Usuario> listMail = query.list();

            if (listUsername.size() == 0 && listMail.size() == 0) {
                userID = (Integer) session.save(user);
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }

        return userID;
    }

    public boolean updateUser(Session session, String username, String newUsername, String password, String mail) {
        Transaction tx = null;
        boolean resultado = false;
        List<Usuario> currentUserInfo = userInfo(username, session);
        String passwordHashed = "";
        int contador = 0;
        if(!password.equals("")){
            passwordHashed = SHA.generate512(password);
        }

        try {
            tx = session.beginTransaction();
            Usuario usuario = (Usuario) session.get(Usuario.class, currentUserInfo.get(0).getId());

            if (!currentUserInfo.get(0).getUsername().equals(newUsername)) {
                usuario.setUsername(newUsername);
                contador++;
            }

            if (!currentUserInfo.get(0).getCorreo().equals(mail)) {
                usuario.setCorreo(mail);
                contador++;
            }

            if (!passwordHashed.equals(currentUserInfo.get(0).getPassword()) && !password.equals("")) {
                usuario.setPassword(passwordHashed);
                contador++;
            }

            if (contador > 0) {
                session.update(usuario);
                resultado = true;
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }

        return resultado;
    }

    public boolean checkUser(String username, String password, Session session) {
        String passwordHashed = SHA.generate512(password);

        String hql = "from Usuario where username like :keyword";
        Query query = session.createQuery(hql);
        query.setParameter("keyword", username);

        String passwordDB = "";

        List<Usuario> listUser = query.list();
        for (Usuario aUser : listUser) {
            passwordDB = aUser.getPassword();
        }

        return passwordDB.equals(passwordHashed);
    }

    public List<Usuario> userInfo(String username, Session session) {

        String hql = "from Usuario where username like :keyword";
        Query query = session.createQuery(hql);
        query.setParameter("keyword", username);

        List<Usuario> listUser = query.list();

        return listUser;
    }
}
