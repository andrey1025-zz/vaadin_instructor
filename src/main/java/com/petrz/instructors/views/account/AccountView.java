package com.petrz.instructors.views.account;

import com.petrz.instructors.data.entity.Admin;
import com.petrz.instructors.data.entity.UserData;
import com.petrz.instructors.data.service.AdminService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.petrz.instructors.views.main.MainView;
import org.springframework.util.Assert;

import java.util.Optional;

@Route(value = "account", layout = MainView.class)
@PageTitle("Account")
@CssImport("./styles/views/account/account-view.css")
public class AccountView extends Div {

    private AdminService adminService;
    private EmailField email = new EmailField("Email");
    private PasswordField pwd = new PasswordField("Password");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<Admin> binder = new Binder(Admin.class);

    public AccountView(AdminService adminService) {
        setId("account-view");
        this.adminService = adminService;
        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        binder.bindInstanceFields(this);
        initForm();

        cancel.addClickListener(
                e -> initForm()
        );
        save.addClickListener(e -> {
            adminService.update(binder.getBean());
            Notification.show("Account details stored.");
        });
    }

    private void initForm() {
        UserData userData = (UserData)UI.getCurrent().getSession().getAttribute("loggedInUser");
        Assert.notNull(userData,"userData must be not null");
        Assert.notNull(adminService,"adminService must be not null");
        Assert.notNull(userData.getEmail(),"userData.getEmail() must be not null");
        Optional<Admin> admin = adminService.findByEmail(userData.getEmail());
        assert( admin.isPresent() );
        binder.setBean(admin.get());
    }

    private void clearForm() {
        binder.setBean(new Admin());
    }

    private Component createTitle() {
        return new H3("My account information");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        email.setErrorMessage("Please enter a valid email address");
        formLayout.add(email, pwd);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

}
