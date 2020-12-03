package com.petrz.instructors.views;

import com.petrz.instructors.views.main.MainView;
import com.vaadin.flow.component.Component;

import java.util.Optional;

public interface HasMainViewAccessor {
        default MainView getMainView(Component component) {
            Optional<Component> parent = component.getParent();
            MainView mainView = null;
            while (parent.isPresent()) {
                Component p = parent.get();
                if (p instanceof MainView) {
                    mainView = (MainView) p;
                }
                parent = p.getParent();
            }
            return mainView;
        }

    }
