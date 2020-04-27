package org.x42bn6.nopassword.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.x42bn6.nopassword.Service;
import org.x42bn6.nopassword.SubService;

/**
 * A {@code ServiceView} is a view class on top of a {@link org.x42bn6.nopassword.Service} object for easy addition to
 * the UI.
 */
public class ServiceView extends GridPane {
    /**
     * The underlying {@link Service}.
     */
    private final Service service;

    public ServiceView(Service service) {
        this.service = service;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        Text serviceDescription = new Text(service.getName());
        add(serviceDescription, 0, 0, 2, 1);
        int i = 1;
        for (SubService subService : service.getSubServices()) {
            add(new Text(subService.getClass().getSimpleName()), 0, i);
            add(new Text(subService.getDescription()), 1, i);
            i++;
        }
    }
}
