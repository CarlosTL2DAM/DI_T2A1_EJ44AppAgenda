/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

import appagenda.entidades.Provincia;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author usuario
 */
public class ConsultaProvincias {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Para conectar la aplicación con una base de datos indicando en el parámetro la unidad de 
        //persistencia (se puede ver en el archivo persistence.xml)
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        //Clase para poder llevar a cabo operaciones con la base de datos
        EntityManager em = emf.createEntityManager();
        //para realizar una consulta se busca crear un objeto de tipo Query
        Query queryProvincias = em.createNamedQuery("Provincia.findAll");
        List<Provincia> listProvincias = queryProvincias.getResultList();
        
        //Como funciona este bucle?
        for(Provincia provincia : listProvincias) {
            System.out.println(provincia.getNombre());
        }
        
        //Tambien se podría hacer mediante el siguiente bucle
        // for(int i = 0 ; i < listProvincias.size(); i++)
        //  Provincia provincia = listProvincias.get(i);
        //  System.out.println(provincia.getNombre());
        
        
        //Vamos a obtener las lineas que contengan como nombre Cádiz
        Query queryProvinciaCadiz = em.createNamedQuery("Provincia.findByNombre");
        queryProvinciaCadiz.setParameter("nombre", "Cádiz");
        List<Provincia> listProvinciasCadiz = queryProvinciaCadiz.getResultList();
        for(Provincia provinciaCadiz : listProvinciasCadiz)
        {
            System.out.print(provinciaCadiz.getId() + ":");
            System.out.println(provinciaCadiz.getNombre());
        }
        
        //Vamos a obtener la provincia que tenga id 2
        //Buscamos la provincia conn find
        Provincia provinciaId2 = em.find(Provincia.class,2);
        //En caso de que lo encuentre
        if(provinciaId2 != null){
            System.out.print(provinciaId2.getId() + ":");
            System.out.println(provinciaId2.getNombre());
        }
        //En caso de que no lo encuentre
        else {
            System.out.println("No hay ninguna provincia con Id = 2");
        }
        
        //Modificación de objetos se lleva a cabo con el método merge
        //En primer lugar vamos a buscar si existe la provincia con nombre Cadiz
        queryProvinciaCadiz = em.createNamedQuery("Provincia.findByNombre");
        //Introducimos los parametros
        queryProvinciaCadiz.setParameter("nombre", "Cádiz");
        //Hacemos una lista con los resultados
        listProvinciasCadiz = queryProvinciaCadiz.getResultList();
        //Comenzamos una transacción
        em.getTransaction().begin();
        
        //Realizamos un bucle que recorra toda la lista y cada uno de los resultados
        //los vaya incluyendo en la instacia "provinciaCadiz" en la cual introduciremos el codigo
        //Realizamos un merge para incluirlo en la base de datos
        for(Provincia provinciaCadiz : listProvinciasCadiz) {
            provinciaCadiz.setCodigo("CA");
            em.merge(provinciaCadiz);
        }
        
        //Realizamos un commit
        em.getTransaction().commit();
        
        //ELIMINACION DE UN DETERMINADO OBJETO
        //Vamos a eliminar la provincia con ID= 15, primero buscamos la provincia
        Provincia provinciaId15 = em.find(Provincia.class, 15);
        //Comenzamos la transacción
        em.getTransaction().begin();
        //Caso de que exista
        if(provinciaId15 != null){
            em.remove(provinciaId15);
        }
        //Caso de que no exista
        else {
            System.out.println("No hay ninguna provincia con ID = 15");
        }
        
        //Hacemos un commit
        em.getTransaction().commit();
        
        
       
        
    }
    
}
