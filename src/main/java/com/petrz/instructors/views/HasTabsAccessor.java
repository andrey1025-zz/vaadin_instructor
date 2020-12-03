package com.petrz.instructors.views;

import com.petrz.instructors.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.Optional;

public interface HasTabsAccessor {
    default Tabs getTabs(Component component) {
        Optional<Component> parent = component.getParent();
        Tabs menu = null;
        while (parent.isPresent()) {
            Component p = parent.get();
            if (p instanceof MainView) {
                MainView main = (MainView) p;
                menu = main.getMenu();
            }
            parent = p.getParent();
        }
        return menu;
    }
}
