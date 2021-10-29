/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

import appagenda.entidades.Persona;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * FXML Controller class
 *
 * @author usuario
 */
public class AgendaViewController implements Initializable {

    //Atributos
    private EntityManager entityManager;
    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private TableColumn<Persona, String> columnNombre;
    @FXML
    private TableColumn<Persona, String> columnApellidos;
    @FXML
    private TableColumn<Persona, String> columnEmail;
    @FXML
    private TableColumn<Persona, String> columnProvincia;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    private Persona personaSeleccionada;
    @FXML
    private AnchorPane rootAgendaView;

    //Método para pasar el objeto EntityManager a la clase controladora
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void cargarTodasPersonas() {
        //Hacemos una consulta de todas las personas
        Query queryPersonaFindAll = entityManager.createNamedQuery("Persona.findAll");
        //Almacenamos los resultados de la consulta en una lista de tipo persona
        List<Persona> listPersonas = queryPersonaFindAll.getResultList();
        //Introducimos las personas en la tabla, cambiamos a un observableArrayList 
        //nuestra lista de personas para poder añadirlos
        tableViewAgenda.setItems(FXCollections.observableArrayList(listPersonas));

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Enlazamos las columnas con propiedades de la clase Persona, columnNombre con nombre
        //columnApellidos con apellidos y columnemail con email
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        //Añadimos la columna provincia, como aquí la clase persona tiene asociada
        //una clase Provincia, hay que hacerlo de manera distinta, ya que solo queremos
        //mostrar el nombre de la provincia
        //Obtenemos en primer lugar la información de la celda
        columnProvincia.setCellValueFactory(cellData -> {
            //Creamos una instancia de SimpleStringProperty
            SimpleStringProperty property = new SimpleStringProperty();
            //En el caso de que exxista un valor de pronicina para la celda 
            //la introducimos en la instancia de SimpleStringProperty obteniendo el nombre 
            //de la obtención de la provincia de la obtención de los datos de la celda
            if (cellData.getValue().getProvincia() != null) {
                property.setValue(cellData.getValue().getProvincia().getNombre());
            }
            //Devolvemos property
            return property;
        });
        //Evento que almacena la persona seleccionada en el tableViewAgenda y 
        //la almacena el atributo de clase persona personaSeleccionada
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    personaSeleccionada = newValue;
                    if (personaSeleccionada != null) {
                        textFieldNombre.setText(personaSeleccionada.getNombre());
                        textFieldApellidos.setText(personaSeleccionada.getApellidos());
                    } else {
                        textFieldNombre.setText("");
                        textFieldApellidos.setText("");
                    }
                });
    }

    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        //Realizaremos la accion, siempre y cuando se haya seleccionado previamente algun campo
        if (personaSeleccionada != null) {
            //Almacenamos el indice de la fila que se ha seleccionado
            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();

            //Introducimos en persona seleccionada los nuevos nombre y apellidos
            personaSeleccionada.setNombre(textFieldNombre.getText());
            personaSeleccionada.setApellidos(textFieldApellidos.getText());
            //Comenzamos una transaccción para realizar el merge de la persona seleccionada, cambiando el valor
            entityManager.getTransaction().begin();
            entityManager.merge(personaSeleccionada);
            entityManager.getTransaction().commit();

            //Introducimos los cambios en el indice de la tabla que se ha almacenado anteriormente.
            tableViewAgenda.getItems().set(numFilaSeleccionada, personaSeleccionada);

            //Quitamos el foco del botón y se lo devolvemos al TableView para que el 
            //usuario pueda moverse entre las filas con el teclado
            TablePosition pos = new TablePosition(tableViewAgenda, numFilaSeleccionada, null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }
    }

    @FXML
    private void onActionButtonNuevo(ActionEvent event) {
        try {
            //Cargar la vista de detalle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();
            //Ocultar la vista de la lista
            rootAgendaView.setVisible(false);

            //Le decimos quien es la raiz a la clase PersonaDetalleViewController para que pueda usar la root principal
            PersonaDetalleViewController personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);

            //Añadir la vista detalle al StackPane principal para que se muestre
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);

            //Intercambio de datos funcionales con el detalle
            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
            
            //Intercambiamos datos de Persona
            personaSeleccionada = new Persona();
            personaDetalleViewController.setPersona(entityManager, personaSeleccionada, Boolean.TRUE);
            
            //Mostramos los datos en la vista de detalle
            personaDetalleViewController.mostrarDatos();
            
        } catch (IOException ex) {
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    @FXML
    private void onActionButtonEditar(ActionEvent event) {

        try {
            //Cargar la vista de detalle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();
            //Ocultar la vista de la lista
            rootAgendaView.setVisible(false);

            //Le decimos quien es la raiz a la clase PersonaDetalleViewController para que pueda usar la root principal
            PersonaDetalleViewController personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);

            //Añadir la vista detalle al StackPane principal para que se muestre
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);
            
            //Intercambio de datos funcionales con el detalle
            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);

            //Intercambiamos datos de Persona
            personaDetalleViewController.setPersona(entityManager, personaSeleccionada, Boolean.FALSE);
            
            
            //Mostramos los datos en la vista de detalle
            personaDetalleViewController.mostrarDatos();
            
        } catch (IOException ex) {
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onActionButtonSuprimir(ActionEvent event) {
        //Creamos una alerta de tipo confirmacion
        Alert alert = new Alert(AlertType.CONFIRMATION);
        //Le damos un titulo
        alert.setTitle("Confirmar");
        //Le damos una cabecera
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        //Introducimos el contenido, en este caso nombre y apellido de persona a borrar
        alert.setContentText(personaSeleccionada.getNombre() + " " 
            + personaSeleccionada.getApellidos());
        //Creamos una clase Optional, de tipo ButtonType, para comprobar si 
        //Cuando se pulsa es sobre el botón de confirmación
        Optional<ButtonType> result = alert.showAndWait();
        //En caso de que sea aceptar
        if(result.get() == ButtonType.OK){
            //Acciones a realizar si el usuario  acepta
            //Comenzamos la transaccion
            entityManager.getTransaction().begin();
            //Este merge, es para asegurarnos de que se gestiona la Persona a eliminar
            entityManager.merge(personaSeleccionada);
            //Eliminamos la persona
            entityManager.remove(personaSeleccionada);
            //Realizamos la transaccion
            entityManager.getTransaction().commit();
            
            tableViewAgenda.getItems().remove(personaSeleccionada);
            tableViewAgenda.getFocusModel().focus(null);
            tableViewAgenda.requestFocus();
        } 
        //En caso de que se pulse cancelar
        else{
            //Acciones a realizar si el usuario cancela, devolvemos el focus al campo seleccionado
            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada, personaSeleccionada);
            TablePosition pos = new TablePosition(tableViewAgenda, numFilaSeleccionada, null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }
    }

}
