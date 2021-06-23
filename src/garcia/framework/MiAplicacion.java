package garcia.framework;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.*;

public class MiAplicacion {

    private List<Accion> acciones = new ArrayList<>();

    public MiAplicacion() {

    }

    public void init() {

        //escanear proyecto y retorna las clases que implementen la interfaz Accion. Devuelve clases no instancias
        Set<Class<? extends Accion>> clases = new Reflections("").getSubTypesOf(Accion.class);
        clases.forEach(clase -> {
            try {
                acciones.add(clase.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException("");
            }
        });
        ordenarListaAccionesPorNombreDeClase();
        mostrarMenuLanterna();

    }

    private void ordenarListaAccionesPorNombreDeClase() {
        List<String> list = new ArrayList<>();
        List<Accion> listAccionesOrdenadas = new ArrayList<>();
        for (Accion accion : acciones) {
            list.add(accion.getClass().getName());
        }
        Collections.sort(list);
        for (String string : list) {
            for (Accion accion : acciones) {
                if (string.equals(accion.getClass().getName())) {
                    listAccionesOrdenadas.add(accion);
                }
            }
        }
        this.acciones = listAccionesOrdenadas;
    }

    private void mostrarMenuLanterna() {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;

        try {

            screen = terminalFactory.createScreen();
            screen.startScreen();


            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);


            final Window window = new BasicWindow("Framework made in Arg ndeah");

            Panel contentPanel = new Panel(new GridLayout(2));

            GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
            gridLayout.setHorizontalSpacing(3);


            Label title = new Label("Seleccione una opcion a ejecutar:");
            title.setLayoutData(GridLayout.createLayoutData(
                    GridLayout.Alignment.BEGINNING, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
                    GridLayout.Alignment.BEGINNING, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
                    true,       // Give the component extra horizontal space if available
                    false,        // Give the component extra vertical space if available
                    2,                  // Horizontal span
                    1));                  // Vertical span
            contentPanel.addComponent(title);

            ComboBox<Accion> comboBox = new ComboBox<Accion>(acciones);
            contentPanel.addComponent(new Label("Acciones"));
            contentPanel.addComponent(comboBox.setReadOnly(false).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)));

            contentPanel.addComponent(new Label("Seleccione el boton continuar para ejecutar la accion"));
            contentPanel.addComponent(new Button("Continuar", () -> {
                comboBox.getSelectedItem().ejecutar();
                MessageDialog.showMessageDialog(textGUI, "Se ejecuto", "Ejecucion finalizada con exito", MessageDialogButton.OK);
            }
            ).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));

            contentPanel.addComponent(
                    new EmptySpace()
                            .setLayoutData(
                                    GridLayout.createHorizontallyFilledLayoutData(2)));
            contentPanel.addComponent(
                    new Separator(Direction.HORIZONTAL)
                            .setLayoutData(
                                    GridLayout.createHorizontallyFilledLayoutData(2)));
            contentPanel.addComponent(
                    new Button("Salir", window::close).setLayoutData(
                            GridLayout.createHorizontallyEndAlignedLayoutData(2)));


            window.setComponent(contentPanel);

            textGUI.addWindowAndWait(window);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (screen != null) {
                try {
                    screen.stopScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

