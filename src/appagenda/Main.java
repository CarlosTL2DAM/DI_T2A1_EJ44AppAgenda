/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author usuario
 */
public class Main extends Application {
    //Atributos
    private EntityManagerFactory emf;
    private EntityManager em;
    
    
    @Override
    public void start(Stage primaryStage) throws IOException{
        StackPane rootMain = new StackPane();
        //Obtenemos una referencia al archivo FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AgendaView.fxml"));
        
        //Su contenido será el elemento, ahora no
        //Parent root = fxmlLoader.load();

        //Agregamos el rootAgendaView a un Pane, porque es generico y puede contener todo tipo de Layouts
        Pane rootAgendaView = fxmlLoader.load();
        //Añadimos como hijo la estructura del AgendaView.fxml
        rootMain.getChildren().add(rootAgendaView);
        
        //Conexion a la BD creando los objetos Entity Manager y EntityManagerFactory
        emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        em = emf.createEntityManager();
        
        //Instanciamos AgendaViewController, que es el controlador de el archivo FXML
        //Esta clase realizará funciones de control de la ventana
        AgendaViewController agendaViewController = (AgendaViewController)fxmlLoader.getController();
        //Pasamos el objeto em a la clase controladora
        agendaViewController.setEntityManager(em);
        //Cargamos todas las personas
        agendaViewController.cargarTodasPersonas();
        
        Scene scene = new Scene(rootMain,600,400);
        primaryStage.setTitle("App Agenda");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //Método heredado de la clase Application, lo sobreescribimos cerrando las clases
    @Override
    public void stop() throws Exception {
        em.close();
        emf.close();
        try{
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
        } catch(SQLException ex){}
    }
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
