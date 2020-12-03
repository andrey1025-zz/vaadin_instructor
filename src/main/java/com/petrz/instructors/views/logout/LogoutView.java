package com.petrz.instructors.views.logout;

import com.petrz.instructors.views.HasMainViewAccessor;
import com.petrz.instructors.views.login.LoginView;
import com.petrz.instructors.views.main.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.util.Assert;

@Route(value = "logout", layout = MainView.class)
@PageTitle("Logout")
public class LogoutView extends Div implements HasMainViewAccessor {
    public LogoutView() {
        setId("logout-view");
/*
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        Button logoutButton = new Button("Logout");
        logoutButton.addClickListener( e-> {
            UI.getCurrent().getSession().setAttribute("loggedInUser", null);
            MainView mainView = getMainView(this);
            Assert.notNull(mainView,"mainView must not be null");
            mainView.setUserNameTitle("");
            UI.getCurrent().navigate(LoginView.class);
        });
        layout.add(logoutButton);
        add(layout);*/
    }
}

