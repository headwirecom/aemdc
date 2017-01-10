package com.headwire.aemdc.gui;

import ch.qos.logback.classic.Level;
import com.headwire.aemdc.command.HelpCommand;
import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Reflection;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.companion.RunnableCompanion;
import com.headwire.aemdc.runner.BasisRunner;
import com.headwire.aemdc.runner.HelpRunner;
import com.headwire.aemdc.util.Help;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainApp extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(MainApp.class);
    private static final ch.qos.logback.classic.Logger ROOT_LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory
            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

    // data storage for this app
    private Model model = new Model();

    // web view and browser used to show help texts
    private final WebView browser = new WebView();
    private final WebEngine webEngine = browser.getEngine();

    // cache for all the nodes used to display the panels of each command
    private final HashMap<String, Node> nodeCache = new HashMap<>();

    // tree items used for the left side of the UI
    private final TreeItem<String> rootNode  = new TreeItem<>("commands");;

    // main container, we will replace the center with the selection from the tree
    private BorderPane container = new BorderPane();

    // label for the bottom showing last log message
    private Label lastLogMessage = new Label("last log message: ");

    // launch of the application
    public static void main(String[] args) {
        // set default INFO log level to avoid logging from ConfigUtil
        ROOT_LOGGER.setLevel(Level.INFO);

        launch(args);
    }

    // hook to execute aemdc
    private void performAction(ArrayList<String> parameters) {
        System.out.println(parameters);
        Serializer serializer = new Persister();
        try {
            serializer.write(model, new File("aemdcgui.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            RunnableCompanion.main(parameters.toArray(new String[parameters.size()]));
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // setup of the UI
    @Override
    public void start(Stage stage) {

        Serializer serializer = new Persister();
        try {
            File aemdcguiConfigFile = new File("aemdcgui.xml");
            if(aemdcguiConfigFile.exists()) {
                model = serializer.read(Model.class, aemdcguiConfigFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final TreeView tree = setupCommandTree();

        // show the panel that will be used on the root node of the tree even if there is no selection
        showPanelForPath("commands");

        stage.setTitle("AEM Developer Companion");
        stage.setWidth(800);
        stage.setHeight(600);

        container.setLeft(tree);

        Tab workspace = createWorkspaceTab();
        Tab logView = createLogViewTab();

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(workspace, logView);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tabPane);
        borderPane.setBottom(lastLogMessage);

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();
    }

    private Tab createLogViewTab() {
        Tab logView = new Tab("log");

        TextArea ta = new TextArea();
        ta.setWrapText(true);

        Console console = new Console(ta, lastLogMessage);
        PrintStream ps = new PrintStream(console, true);
        System.setOut(ps);
        System.setErr(ps);
        logView.setContent(ta);

        logView.setClosable(false);
        return logView;
    }

    private Tab createWorkspaceTab() {
        Tab workspace = new Tab("workspace");
        workspace.setClosable(false);
        workspace.setContent(container);
        return workspace;
    }

    private TreeView setupCommandTree() {

        makeTreeItems();
        return makeTreeView();
    }

    private void makeTreeItems() {
        rootNode.setExpanded(true);
        model.getTypes().forEach(name -> {
            final TreeItem type = new TreeItem<>(name);
            type.setExpanded(true);
            rootNode.getChildren().add(type);
            model.getTemplatesForType(name).forEach(templateName -> {
                TreeItem template = new TreeItem<>(templateName);
                type.getChildren().add(template);
            });
        });
    }

    private TreeView makeTreeView() {
        final TreeView tree = new TreeView(rootNode);

        tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(tree.getSelectionModel().getSelectedIndex() < 0) return;
                TreeItem<String> item = (TreeItem<String>) newValue;
                showPanelForPath(treeNodeToPath(item));
            }
        });
        return tree;
    }

    private String treeNodeToPath(TreeItem<String> item) {
        TreeItem<String> parent = item;
        String path = "";
        while(parent != null) {
            if(parent != item) {
                path = ":" + path;
            }
            path = parent.getValue() + path;
            parent = parent.getParent();
        }
        return path;
    }

    private void showPanelForPath(String path) {

        String[] p = path.split(":");

        Node node = nodeCache.get(path);
        if(node == null) {
            if(p.length > 2) {
                node = getPaneForPath(p[1], p[2]);
                if(node != null) {
                    nodeCache.put(path, node);
                }
            } else if(p.length == 2) {
                node = getHelpFor(p[1]);
            } else {
                node = intro();
            }
        }

        container.setCenter(node);
    }

    private Node getHelpFor(String command) {
        webEngine.loadContent(model.getHelpTextForType(command));
        return browser;
    }

    private Node intro() {
        String path;
        path = MainApp.class.getResource("/index.html").toExternalForm();
        System.out.println("loading "+path);
        webEngine.load(path);
        return browser;
    }

    private Region getPaneForPath(String type, String template) {
        List<String> placeholders = model.getPlaceHoldersForName(type, template);
        final GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setPadding(new Insets(15, 5, 5, 15));

        int row = 0;

        Label help = new Label(model.getHelpTextForTemplate(type, template));
        grid.add(help, 0, row, 2, 1);

        row++;

        Label targetName = new Label("Target Name:");
        grid.add(targetName, 0, row);

        TextField targetNameTextField = new TextField();
        targetNameTextField.setMinWidth(200);
        targetNameTextField.setUserData("targetName");
        setValue(targetNameTextField, type, template);
        grid.add(targetNameTextField, 1, row);
        row++;

        for (String placeholder: placeholders
                ) {

            if(placeholder.startsWith("ph_")) {
                Label label = new Label(placeholder);
                grid.add(label, 0, row, 2, 1);
                row++;
                grid.add((new TableLikeGrid()).getTableLikeGrid(this, type, template, placeholder), 0, row, 2, 1);

            } else {
                Label label = new Label(placeholder+":");
                grid.add(label, 0, row);

                TextField field = new TextField();
                field.setUserData(placeholder);
                field.setMinWidth(200);
                setValue(field, type, template);
                grid.add(field, 1, row);

            }
            row++;
        }

        Button btn = new Button("create");
        btn.setOnAction((ActionEvent e) -> {
            model.reset(type, template);
            ArrayList<String> params = collectParameters(type, template, grid);
            if(params.size() < 3) {
                System.out.println("not enough parameters to run the command");
            } else if(!params.get(2).startsWith("targetName=")) {
                System.out.println("no target name specified but required");
            } else {
                params.set(2, params.get(2).substring("targetName=".length()));
                performAction(params);
            }
        });

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, row);

        return new ScrollPane(grid);

    }

    public void setValue(TextField field, String type, String template) {
        String value = model.getValue(type, template, field.getUserData().toString());
        field.setText(value);
    }

    private ArrayList<String> collectParameters(String type, String template, GridPane grid) {
        ArrayList<String> params = new ArrayList<String>();
        ArrayList<Node> visitor = new ArrayList<Node>();

        params.add(type);
        params.add(template);

        visitor.addAll(grid.getChildren());
        for (int i = 0; i < visitor.size(); i++)
        {
            Node child = visitor.get(i);
            if(child instanceof TextField) {
                TextField tf = (TextField) child;
                Object userData = tf.getUserData();
                if(!tf.getText().equals("")) {
                    if (userData instanceof TextField) {
                        TextField name = (TextField) userData;
                        if (!name.getText().equals("")) {
                            String paramName = name.getUserData() + ":" + name.getText();
                            String paramValue = tf.getText();
                            model.setValue(type, template, paramName, paramValue);
                            params.add(paramName+"="+paramValue);
                        }
                    } else if (userData != null) {
                        String userDataValue = userData.toString();
                        if (!userDataValue.startsWith("ph_")) {
                            String paramName = userDataValue;
                            String paramValue = tf.getText();
                            model.setValue(type, template, paramName, paramValue);
                            params.add(paramName+"="+paramValue);
                        }
                    }
                }
            } else if(child instanceof Pane) {
                visitor.addAll(((Pane)child).getChildren());
            }
        }
        return params;
    }

    public HashMap<String, String> getPlaceholders(String type, String template, String phName) {
        return model.getPlaceHolders(type, template, phName);
    }
}

class Console extends OutputStream
{
    private final Label lastLogMessage;
    private TextArea    output;
    private String line = "";

    public Console(TextArea ta, Label lastLogMessage)
    {
        this.output = ta;
        this.lastLogMessage = lastLogMessage;
    }

    @Override
    public void write(int i) throws IOException
    {
        String ch = String.valueOf((char) i);
        if(i >= 32) {
            line += ch;
        } else if((i == 10 || i == 13) && line.length() > 0) {
            lastLogMessage.setText("last log message: "+line);
            line = "";
        }
        output.appendText(ch);
    }

}