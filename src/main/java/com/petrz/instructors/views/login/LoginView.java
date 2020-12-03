package com.petrz.instructors.views.login;

import com.petrz.instructors.data.entity.Admin;
import com.petrz.instructors.data.entity.Instructor;
import com.petrz.instructors.data.entity.UserData;
import com.petrz.instructors.data.service.AdminService;
import com.petrz.instructors.data.service.InstructorService;
import com.petrz.instructors.util.EmailService;
import com.petrz.instructors.views.HasMainViewAccessor;
import com.petrz.instructors.views.HasTabsAccessor;
import com.petrz.instructors.views.account.AccountView;
import com.petrz.instructors.views.grading.GradingView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.petrz.instructors.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.Random;

@Route(value = "login", layout = MainView.class)
@PageTitle("Login")
@CssImport("styles/views/login/login-view.css")
@RouteAlias(value = "", layout = MainView.class)
public class LoginView extends Div implements HasTabsAccessor, HasMainViewAccessor {

    private EmailService emailService;
    private InstructorService instructorService;
    private AdminService adminService;
    private RadioButtonGroup<String> loginType = new RadioButtonGroup<>();
    private EmailField email = new EmailField("Email");
    private PasswordField password = new PasswordField("Password");
    private Button submit = new Button("Log in");
    private String otp = "";

    public LoginView(@Autowired EmailService emailService, @Autowired InstructorService instructorService, @Autowired AdminService adminService) {
        setId("login-view");
        this.emailService = emailService;
        this.instructorService = instructorService;
        this.adminService = adminService;


        adminService.initData("norbware@gmail.com", "p", "Norbware Dev");

        add(createTitle());
        add(createLayout());
        add(createButtonLayout());

        submit.addClickListener(e -> {
            if( email.getValue().trim().equals("") ) {
                Notification.show("Error: missing email");
                email.focus();
            } else {
                if (!password.isVisible()) {
                    // login type: instructor , login phase1
                    otp = generateOTP();
                    sendEmail(email.getValue(), otp);
                    //Notification.show("OTP sent to your email, type it as password:" + otp);
                    Notification.show("OTP sent to your email, type it as password");
                    password.setVisible(true);
                    password.focus();
                } else if (!otp.equals("") ) {
                    // login type: instructor , login phase2
                    if(!otp.equals(password.getValue())) {
                        Notification.show("Error: wrong otp");
                    } else {
                        authenticateInstructor();
                    }
                } else if( password.getValue().trim().equals("")) {
                    // login type: admin , missing pwd
                    Notification.show("Error: missing password");
                    password.focus();
                } else {
                    // login type: admin
                    authenticateAdmin();
                }
            }
        });
    }

    private void doLoginSuccess(UserData loggedInUser) {

        // store logged in user data in ui session
        UI.getCurrent().getSession().setAttribute("loggedInUser", loggedInUser);

        Tabs menu = getTabs(this);
        menu.setVisible(true);
        MainView mainView = getMainView(this);
        Assert.notNull(mainView,"mainView must not be null");
        mainView.setUserNameTitle(loggedInUser.getName());
        mainView.setEnabledMenuTabs(loggedInUser.getAdmin());

        if( loggedInUser.getAdmin() ) {
            UI.getCurrent().navigate(AccountView.class);
        } else {
            UI.getCurrent().navigate(GradingView.class);
        }
        Notification.show("Welcome, "+loggedInUser.getName()+"!");
    }

    private void authenticateInstructor() {
        // check email in instructors table
        Optional<Instructor> instructor = instructorService.findByEmail(email.getValue());
        if( !instructor.isPresent()) {
            Notification.show("Error: invalid email");
        } else {
            UserData loggedInUser = new UserData();
            loggedInUser.setId(instructor.get().getId());
            loggedInUser.setAdmin(false);
            loggedInUser.setEmail(instructor.get().getEmail());
            loggedInUser.setName(instructor.get().getName());
            doLoginSuccess(loggedInUser);
        }
    }

    private void authenticateAdmin() {
        // check email and pwd in administrator table
        Optional<Admin> admin = adminService.findByEmailAndPwd(email.getValue(),password.getValue());
        if( !admin.isPresent()) {
            Notification.show("Error: invalid email or password");
        } else {
            UserData loggedInUser = new UserData();
            loggedInUser.setId(admin.get().getId());
            loggedInUser.setAdmin(true);
            loggedInUser.setEmail(admin.get().getEmail());
            loggedInUser.setName(admin.get().getName());
            doLoginSuccess(loggedInUser);
        }
    }

    private String generateOTP() {
        Random rnd = new Random();
        return String.valueOf(100000+rnd.nextInt(899999));
    }

    private void sendEmail(String emailTo, String otp) {
        emailService.sendSimpleMessage(emailTo,"your otp code", "Your otp code is: "+otp);
    }

    private Component createTitle() {
        return new H3("Login");
    }

    private Component createLayout() {
        //FormLayout formLayout = new FormLayout();
        VerticalLayout layout = new VerticalLayout();

        loginType.setItems("Instructor", "Administrator");
        loginType.addValueChangeListener(event -> {
            boolean adminLogin = !event.getValue().equals("Instructor");
            password.setVisible(adminLogin);
            if( adminLogin ) { otp = ""; }
        });
        loginType.setValue("Instructor");

        layout.add(loginType, email, password);
        return layout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        email.setErrorMessage("Please enter a valid email address");
        password.setRequired(true);
        password.setVisible(false);
        buttonLayout.add(submit);
        return buttonLayout;
    }

}
