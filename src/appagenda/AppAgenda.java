/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

import appagenda.entidades.Provincia;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author usuario
 */
public class AppAgenda {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Para conectar la aplicación con una base de datos indicando en el parámetro la unidad de 
        //persistencia (se puede ver en el archivo persistence.xml)
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        //Clase para poder llevar a cabo operaciones con la base de datos
        EntityManager em = emf.createEntityManager();
        
        //Iniciamos una transacción sobre EntityManager
        //em.getTransaction().begin();
        
        //Si queremos que se realicen las transacciones haremos commite sino rollback
        //em.getTransaction().commit();
        //em.getTransaction().rollback();
        
        Provincia provinciaCadiz = new Provincia(2,"Cádiz");
        Provincia provinciaSevilla = new Provincia();
        provinciaSevilla.setNombre("Sevilla");
        
        em.getTransaction().begin();
        //Añadimos las provincias de cadiz y sevilla a la base de datos
        em.persist(provinciaCadiz);
        em.persist(provinciaSevilla);
        em.getTransaction().commit();
        
        
        
        em.close();
        emf.close();
        try{
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
        }catch (SQLException ex){}
        
    }
    
}
