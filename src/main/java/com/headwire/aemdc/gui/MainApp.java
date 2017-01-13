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
import org.apache.commons.io.FileUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

    // main tab pane containing the tree and command panel
    private TabPane tabPane;

    // label for the bottom showing last log message
    private Label lastLogMessage = new Label("last log message: ");
    private Console console;

    // launch of the application
    public static void main(String[] args) {
        // set default INFO log level to avoid logging from ConfigUtil
        ROOT_LOGGER.setLevel(Level.INFO);

        launch(args);
    }

    // hook to execute aemdc
    private void performAction(ArrayList<String> parameters, boolean preview) {

        console.clear();

        tabPane.getSelectionModel().select(1);

        Serializer serializer = new Persister();
        try {
            serializer.write(model, new File("aemdcgui.xml"));
        } catch (Exception e) {
            LOG.error("failed to write current state of UI to aemdcgui.xml",e);
        }
        try {

            LOG.info("aemdc "+String.join(" ", parameters));
            RunnableCompanion.main(parameters.toArray(new String[parameters.size()]));
            LOG.info("aemdc completed");
        } catch(IOException ioe) {
            LOG.error("failed to perform aemdc command", ioe);
        }

        if(preview) {
            hidePreviewPane();
            showPreviewPane();
        } else {
            hidePreviewPane();
        }
    }

    // show the preview panel (file tree with a textare displaying the selected file)
    private void showPreviewPane() {
        BorderPane preview = new BorderPane();
        TextArea ta = new TextArea();
        ta.setWrapText(false);
        ta.setStyle("-fx-font-family: Monospaced;");
        preview.setCenter(ta);

        TreeItem root = new TreeItem("root");
        root.setExpanded(true);

        TreeItem firstFile = makePreviewTree(root);

        TreeView tree = new TreeView(root);
        tree.setShowRoot(false);
        tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(tree.getSelectionModel().getSelectedIndex() < 0) return;
                TreeItemFileWrapper item = (TreeItemFileWrapper) ((TreeItem) newValue).getValue();
                if(!item.getFile().isDirectory()) {
                    showFileInTextArea(item.getFile(), ta);
                }
            }
        });
        if(firstFile != null) {
            tree.getSelectionModel().select(firstFile);
        }
        preview.setLeft(tree);

        Tab tab = new Tab("preview");
        tab.setContent(preview);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    // load file into a textarea
    private void showFileInTextArea(File file, TextArea ta) {
        final StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while(line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            br.close();
        } catch(Exception e) {
            LOG.error("problem reading file for preview pane {}", file);
        }
        ta.setText(sb.toString());
    }

    // make a preview tree of all files in the temp/aemdc folder
    private TreeItem makePreviewTree(TreeItem root) {

        final File tempDir = new File(System.getProperty("java.io.tmpdir"));
        final File tempAemDCFolder = new File(tempDir, "aemdc");

        final TreeItem firstFile = makePreviewTreeFromFile(root, tempAemDCFolder);

        return firstFile;
    }

    // create treeitems from file structure
    private TreeItem makePreviewTreeFromFile(TreeItem node, File folder) {
        TreeItem firstFile = null;
        for (File file: folder.listFiles()
             ) {
            TreeItem child = new TreeItem(new TreeItemFileWrapper(file));
            node.getChildren().add(child);
            if(file.isDirectory()) {
                child.setExpanded(true);
                TreeItem first = makePreviewTreeFromFile(child, file);
                if(firstFile == null) {
                    firstFile = first;
                }
            } else {
                if(firstFile == null) {
                    firstFile = child;
                }
            }
        }
        return firstFile;
    }

    // wrapper class for tree item to handle a file
    static class TreeItemFileWrapper {
        private File file;
        public TreeItemFileWrapper(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        public String toString() {
            return file.getName();
        }
    }

    // remove the preview pane from the tabs
    private void hidePreviewPane() {
        if(tabPane.getTabs().size() > 2) {
            tabPane.getTabs().remove(2, 3);
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
            LOG.info("encountered a problem while reading the state of the UI from aemdcgui.xml", e);
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

        tabPane = new TabPane();
        tabPane.getTabs().addAll(workspace, logView);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tabPane);
        borderPane.setBottom(lastLogMessage);

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();
    }

    // creates a log viewer wrapping system.out and system.err
    private Tab createLogViewTab() {
        Tab logView = new Tab("log");

        TextArea ta = new TextArea();
        ta.setWrapText(false);

        console = new Console(ta, lastLogMessage);
        ta.setStyle("-fx-font-family: Monospaced;");
        PrintStream ps = new PrintStream(console, true);
        System.setOut(ps);
        System.setErr(ps);
        logView.setContent(ta);

        logView.setClosable(false);
        return logView;
    }

    // create the basic main tab
    private Tab createWorkspaceTab() {
        Tab workspace = new Tab("workspace");
        workspace.setClosable(false);
        workspace.setContent(container);
        return workspace;
    }

    // create the command tree
    private TreeView setupCommandTree() {

        makeTreeItems();
        return makeTreeView();
    }

    // make tree items for command tree from the model
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

    // create the actual tree view for the commands
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

    // convert a tree item to a path separated by ':'
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

    // shows a panel for a given path (commands:<type>:<command>) (uses a cache)
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

    // show the help text for a given command (commands[:<type>[:<template>]]
    private Node getHelpFor(String command) {
        webEngine.loadContent(model.getHelpTextForType(command));
        return browser;
    }

    // load the intro text
    private Node intro() {
        String path;
        path = MainApp.class.getResource("/index.html").toExternalForm();
        webEngine.load(path);
        return browser;
    }

    // create the pane for a given type/template
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
            collectAndCreate(type, template, grid, false);
        });

        Button clear = new Button("clear");
        clear.setOnAction((ActionEvent e) -> {
            nodeCache.remove("commands:"+type+":"+template);
            model.reset(type, template);
            Object obj = ((TreeView)container.getLeft()).getSelectionModel().getSelectedItem();
            ((TreeView)container.getLeft()).getSelectionModel().select(null);
            ((TreeView)container.getLeft()).getSelectionModel().select(obj);
        });

        Button preview = new Button("preview");
        preview.setOnAction((ActionEvent e) -> {
            collectAndCreate(type, template, grid, true);
        });

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(clear);
        hbBtn.getChildren().add(preview);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, row);

        return new ScrollPane(grid);

    }

    // collect the values entered on the UI for the given type/template and execute aemdc if appropriate
    private void collectAndCreate(String type, String template, GridPane grid, boolean preview) {
        model.reset(type, template);
        ArrayList<String> params = collectParameters(type, template, grid, false);
        if(params.size() < 3) {
            LOG.info("not enough parameters to run the command");
        } else if(!params.get(2).startsWith("targetName=")) {
            LOG.info("no target name specified but required");
        } else {
            params.set(2, params.get(2).substring("targetName=".length()));
            if(preview) {
                File tempDir = new File(System.getProperty("java.io.tmpdir"));
                File tempAemDCFolder = new File(tempDir, "aemdc");
                tempAemDCFolder.mkdirs();
                if(tempAemDCFolder.exists()) {
                    try {
                        FileUtils.deleteDirectory(tempAemDCFolder);
                    } catch (IOException e) {
                        LOG.error("not able to remove temp file folder for preview", e);
                    } finally {
                        params.add(0, "-temp="+tempAemDCFolder.getAbsolutePath());
                        performAction(params, preview);
                    }
                }
            }
            else {
                performAction(params, preview);
            }
        }
    }

    // set the stored content based on what placeholder this field is linked to
    public void setValue(TextField field, String type, String template) {
        String value = model.getValue(type, template, field.getUserData().toString());
        field.setText(value);
    }

    // collect the parameters entered and create the arguments to call aemdc from it
    private ArrayList<String> collectParameters(String type, String template, GridPane grid, boolean clear) {
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

    // get a list of all the placeholders for a given type/template
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

    public void clear() {
        output.clear();
    }

}