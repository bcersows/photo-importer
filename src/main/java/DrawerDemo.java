
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;
import com.jfoenix.controls.JFXDrawersStack;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DrawerDemo extends Application {

    private static final String LEFT = "LEFT";
    private static final String TOP = "TOP";
    private static final String RIGHT = "RIGHT";
    private static final String BOTTOM = "BOTTOM";

    @Override
    public void start(final Stage stage) {
        final FlowPane content = new FlowPane();
        final JFXButton leftButton = new JFXButton(LEFT);
        final JFXButton topButton = new JFXButton(TOP);
        final JFXButton rightButton = new JFXButton(RIGHT);
        final JFXButton bottomButton = new JFXButton(BOTTOM);
        content.getChildren().addAll(leftButton, topButton, rightButton, bottomButton);
        content.setMaxSize(200, 200);

        final JFXDrawer leftDrawer = new JFXDrawer();
        final StackPane leftDrawerPane = new StackPane();
        leftDrawerPane.getStyleClass().add("red-400");
        leftDrawerPane.getChildren().add(new JFXButton("Left Content"));
        leftDrawer.setSidePane(leftDrawerPane);
        leftDrawer.setDefaultDrawerSize(150);
        leftDrawer.setResizeContent(true);
        leftDrawer.setOverLayVisible(false);
        leftDrawer.setResizableOnDrag(true);

        final JFXDrawer bottomDrawer = new JFXDrawer();
        final StackPane bottomDrawerPane = new StackPane();
        bottomDrawerPane.getStyleClass().add("deep-purple-400");
        bottomDrawerPane.getChildren().add(new JFXButton("Bottom Content"));
        bottomDrawer.setDefaultDrawerSize(150);
        bottomDrawer.setDirection(DrawerDirection.BOTTOM);
        bottomDrawer.setSidePane(bottomDrawerPane);
        bottomDrawer.setResizeContent(true);
        bottomDrawer.setOverLayVisible(false);
        bottomDrawer.setResizableOnDrag(true);

        final JFXDrawer rightDrawer = new JFXDrawer();
        final StackPane rightDrawerPane = new StackPane();
        rightDrawerPane.getStyleClass().add("blue-400");
        rightDrawerPane.getChildren().add(new JFXButton("Right Content"));
        rightDrawer.setDirection(DrawerDirection.RIGHT);
        rightDrawer.setDefaultDrawerSize(150);
        rightDrawer.setSidePane(rightDrawerPane);
        rightDrawer.setOverLayVisible(false);
        rightDrawer.setResizableOnDrag(true);

        final JFXDrawer topDrawer = new JFXDrawer();
        final StackPane topDrawerPane = new StackPane();
        topDrawerPane.getStyleClass().add("green-400");
        topDrawerPane.getChildren().add(new JFXButton("Top Content"));
        topDrawer.setDirection(DrawerDirection.TOP);
        topDrawer.setDefaultDrawerSize(150);
        topDrawer.setSidePane(topDrawerPane);
        topDrawer.setOverLayVisible(false);
        topDrawer.setResizableOnDrag(true);

        final JFXDrawersStack drawersStack = new JFXDrawersStack();
        drawersStack.setContent(content);

        leftDrawer.setId(LEFT);
        rightDrawer.setId(RIGHT);
        bottomDrawer.setId(BOTTOM);
        topDrawer.setId(TOP);

        leftButton.addEventHandler(MOUSE_PRESSED, e -> drawersStack.toggle(leftDrawer));
        bottomButton.addEventHandler(MOUSE_PRESSED, e -> drawersStack.toggle(bottomDrawer));
        rightButton.addEventHandler(MOUSE_PRESSED, e -> drawersStack.toggle(rightDrawer));
        topButton.addEventHandler(MOUSE_PRESSED, e -> drawersStack.toggle(topDrawer));

        final Scene scene = new Scene(drawersStack, 800, 800);

        stage.setTitle("JFX Drawer Demo");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }

}
