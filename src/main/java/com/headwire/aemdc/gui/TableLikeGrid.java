package com.headwire.aemdc.gui;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;

/**
 * Created by rr on 12/29/2016.
 */
public class TableLikeGrid {

    private int index = 1;

    public Node getTableLikeGrid(MainApp app, String type, String template, String phName) {

        VBox panel = new VBox();

        final GridPane grid = new GridPane();

        Label name = new Label("Name");
        grid.add(name, 0, 0);

        Label value = new Label("Value");
        grid.add(value, 1, 0);

        Label placeholder = new Label("");
        grid.add(placeholder , 2, 0);

        HashMap<String, String> placeholders = app.getPlaceholders(type, template, phName);

        for ( String key: placeholders.keySet()
             ) {
            addRow(app, type, template, grid, phName, key, placeholders.get(key));
        }
        addRow(app, type, template, grid, phName, "", "");

        Button add = new Button("+");
        add.setOnAction((ActionEvent e) -> {
            addRow(app, type, template, grid, phName, "", "");
        });

        panel.getChildren().addAll(grid, add);

        return panel;
    }

    private void addRow(MainApp app, String type, String template, GridPane grid, String name, String nameValue, String value) {
        TextField tfName = new TextField();
        tfName.setUserData(name);
        tfName.setText(nameValue);

        TextField tfValue = new TextField();
        tfValue.setUserData(tfName);
        tfValue.setText(value);

        Button btnDelete = new Button("x");
        btnDelete.setOnAction((ActionEvent e) -> {
            int pos = grid.getChildren().indexOf(e.getSource());
            grid.getChildren().remove(pos-2,pos+1);
        });
        grid.addRow(index++, tfName, tfValue, btnDelete);
    }

}
