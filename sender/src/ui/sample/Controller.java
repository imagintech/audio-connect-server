package ui.sample;

import core.CodeDriver;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

public class Controller implements Initializable {

    @FXML
    private Button startServer;

    @FXML
    private Button stopServer;

    @FXML
    private Hyperlink audioConnectWebPageLink;

    @FXML
    private Label serverStatusDisplay;

    private CodeDriver codeDriver;

    private HostServices hostServices;

    /* constants */
    private static final String STATUS_IDLE = "IDLE";
    private static final String STATUS_RUNNING = "RUNNING ...";

    public Controller()
    {
        codeDriver = new CodeDriver();
    }

    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        stopServer.setDisable(true);
    }

    public void OnClickStartServer(ActionEvent event)
    {
        if(codeDriver.startServer())
        {
            startServer.setDisable(true);
            stopServer.setDisable(false);
            serverStatusDisplay.setText(STATUS_RUNNING);
        }
    }

    public void OnClickStopServer(ActionEvent event)
    {
        if(codeDriver.stopServer())
        {
            stopServer.setDisable(true);
            startServer.setDisable(false);
            serverStatusDisplay.setText(STATUS_IDLE);
        }
    }

    public void OnClickDismiss(ActionEvent event)
    {
        while(!codeDriver.stopServer());
        serverStatusDisplay.setText(STATUS_IDLE);
        Platform.exit();
    }

    public void OnClickAudioConnectWebPageLink(ActionEvent event)
    {
        hostServices.showDocument(audioConnectWebPageLink.getText());
    }

    /* getters and setters */
    public HostServices getHostServices() { return hostServices ; }
    public void setHostServices(HostServices hostServices) { this.hostServices = hostServices ; }
}
