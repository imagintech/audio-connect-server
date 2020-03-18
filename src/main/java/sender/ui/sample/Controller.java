package sender.ui.sample;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import sender.core.CodeDriver;

public class Controller implements Initializable {

    @FXML
    private Button startServer;

    @FXML
    private Button stopServer;

    @FXML
    private Hyperlink audioConnectWebPageLink;

    @FXML
    private Label serverStatusDisplay;

    @FXML
    private ComboBox<String> mixers;

    @FXML
    private ComboBox<String> dataLines;

    @FXML
    private ComboBox<String> formats;

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
        updateMixers();
        updateLines();
        updateFormats();

        mixers.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov,
                                final Number oldValue, final Number newValue)
            {
                codeDriver.selectMixer(newValue.intValue());
                updateLines();
                updateFormats();
            }
        });

        dataLines.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov,
                                final Number oldValue, final Number newValue)
            {
                if(newValue.intValue() >= 0) {
                    codeDriver.selectLine(newValue.intValue());
                    updateFormats();
                }
            }
        });

        formats.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov,
                                final Number oldValue, final Number newValue)
            {
                if(newValue.intValue() >= 0) {
                    codeDriver.selectFormat(newValue.intValue());
                }
            }
        });

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

    private void updateMixers() {
        mixers.getItems().setAll(codeDriver.getMixers());
        mixers.setValue(codeDriver.getMixer());
    }

    private void updateLines() {
        dataLines.getItems().setAll(codeDriver.getLines());
        dataLines.setValue(codeDriver.getLine());
    }

    private void updateFormats() {
        formats.getItems().setAll(codeDriver.getFormats());
        formats.setValue(codeDriver.getFormat());
    }
    /* getters and setters */
    public HostServices getHostServices() { return hostServices ; }
    public void setHostServices(HostServices hostServices) { this.hostServices = hostServices ; }
}
