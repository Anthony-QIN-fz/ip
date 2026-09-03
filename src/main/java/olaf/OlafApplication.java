package olaf;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Configures and displays Olaf's JavaFX user interface.
 */
public class OlafApplication extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final String STYLESHEET = "/view/Olaf.css";
    private static final double WINDOW_WIDTH = 520.0;
    private static final double WINDOW_HEIGHT = 640.0;
    private static final double MINIMUM_WINDOW_WIDTH = 400.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 480.0;
    private static final long EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS = 2L;

    private ExecutorService commandExecutor;
    private Olaf olaf;
    private String startupError;

    /** {@inheritDoc} */
    @Override
    public void init() {
        commandExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "olaf-command-worker");
            thread.setDaemon(true);
            return thread;
        });

        try {
            olaf = Olaf.createDefault();
        } catch (StorageException exception) {
            startupError = ResponseFormatter.formatError(exception.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        MainWindow mainWindow;
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(requireResource(MAIN_WINDOW_FXML));
            root = loader.load();
            mainWindow = loader.getController();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Olaf's main window.", exception);
        }

        mainWindow.setCommandExecutor(commandExecutor);
        if (olaf == null) {
            mainWindow.showStartupError(startupError);
        } else {
            mainWindow.setOlaf(olaf);
        }

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(requireResource(STYLESHEET).toExternalForm());
        stage.setTitle("Olaf");
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();

        Platform.runLater(mainWindow::requestInputFocus);
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        if (commandExecutor != null) {
            commandExecutor.shutdown();
            try {
                if (!commandExecutor.awaitTermination(
                        EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    commandExecutor.shutdownNow();
                }
            } catch (InterruptedException exception) {
                commandExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private URL requireResource(String path) {
        URL resource = OlafApplication.class.getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Required GUI resource is missing: " + path);
        }
        return resource;
    }
}
