package com.petrz.instructors.views.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.petrz.instructors.data.entity.UserData;
import com.petrz.instructors.views.instructor.report.ReportView;
import com.petrz.instructors.views.login.LoginView;
import com.petrz.instructors.views.logout.LogoutView;
import com.petrz.instructors.views.grading.GradingView;
import com.petrz.instructors.views.sections.SectionsView;
import com.petrz.instructors.views.semesters.SemestersView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.petrz.instructors.views.account.AccountView;
import com.petrz.instructors.views.admins.AdminsView;
import com.petrz.instructors.views.instructors.InstructorsView;
import com.petrz.instructors.views.courses.CoursesView;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.util.Assert;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@CssImport("./styles/views/main/main-view.css")
@PWA(name = "Instructors", shortName = "Instructors", enableInstallPrompt = false)
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainView extends AppLayout implements BeforeEnterObserver {

    private final Tabs menu;
    private final List<Tab> adminTabs;
    private final List<Tab> instructorTabs;
    private H1 viewTitle;
    private H3 userNameTitle;
    private Button logoutButton;

    public MainView() {
        adminTabs = new ArrayList<>();
        instructorTabs = new ArrayList<>();
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
        menu.setVisible(false);
    }

    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());
        viewTitle = new H1();
        layout.add(viewTitle);
        userNameTitle = new H3();
        HorizontalLayout userLayout = new HorizontalLayout();
        userLayout.setId("header-user");
        userLayout.setAlignItems(FlexComponent.Alignment.END);
        userLayout.add(userNameTitle);
        //userLayout.add(new Image("images/user.svg", "Avatar"));
        logoutButton = new Button("Logout");
        logoutButton.setVisible(false);
        logoutButton.addClickListener(e->{
            logoutButton.getUI().ifPresent(ui ->
                    ui.navigate(LogoutView.class));
        });
        userLayout.add(logoutButton);
        layout.add(userLayout);
        return layout;
    }

    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(new Image("images/logo.png", "Instructors logo"));
        logoLayout.add(new H1("Instructors"));
        layout.add(logoLayout, menu);
        return layout;
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        createMenuItems();
        adminTabs.forEach(t->tabs.add(t));
        instructorTabs.forEach(t->tabs.add(t));
        return tabs;
    }

    private void createMenuItems() {
        adminTabs.add(createTab("Account", AccountView.class));
        adminTabs.add(createTab("Admins", AdminsView.class));
        adminTabs.add(createTab("Instructors", InstructorsView.class));
        adminTabs.add(createTab("Courses", CoursesView.class));
        adminTabs.add(createTab("Sections", SectionsView.class));
        //adminTabs.add(createTab("Semesters", SemestersView.class));
        adminTabs.add(createTab("Reports", ReportView.class));
        instructorTabs.add(createTab("Grading", GradingView.class));
        instructorTabs.add(createTab("Reports", ReportView.class));
    }


    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        //System.out.println("MainView::afterNavigation getContent():"+getContent());
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
        viewTitle.setText(getCurrentPageTitle());
        if( UI.getCurrent().getSession().getAttribute("loggedInUser") == null ) {
            // no user logged in : hide menu
            menu.setVisible(false);
        } else {
            // user logged in
            menu.setVisible(true);
            logoutButton.setVisible(true);
            if( GradingView.class.equals(getContent().getClass()) ) {
                ((GradingView)getContent()).setInstructorId(((UserData)UI.getCurrent().getSession().getAttribute("loggedInUser")).getId());
            }
            if( ReportView.class.equals(getContent().getClass()) ) {
                Integer instructorId = ((UserData)UI.getCurrent().getSession().getAttribute("loggedInUser")).getId();
                ((ReportView)getContent()).setInstructorId(
                        ((UserData)UI.getCurrent().getSession().getAttribute("loggedInUser")).getAdmin() ? null : instructorId);
            }
            if( LogoutView.class.equals(getContent().getClass())) {
                // do logout and then show the login view
                UI.getCurrent().getSession().setAttribute("loggedInUser", null);
                setUserNameTitle("");
                menu.setVisible(false);
                logoutButton.setVisible(false);
                UI.getCurrent().navigate(LoginView.class);
            }
        }
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren()
                .filter(tab -> ComponentUtil.getData(tab, Class.class)
                        .equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }

    public Tabs getMenu() {
        return menu;
    }

    public void setUserNameTitle(String userName) {
        userNameTitle.setText(userName);
    }

    public void setEnabledMenuTabs(boolean userIsAdmin) {
        adminTabs.forEach(t->t.setVisible(userIsAdmin));
        instructorTabs.forEach(t->t.setVisible(!userIsAdmin));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (!beforeEnterEvent.getNavigationTarget().equals(LoginView.class)) {
            if (UI.getCurrent().getSession().getAttribute("loggedInUser") == null) {
                menu.setVisible(false);
                beforeEnterEvent.rerouteTo(LoginView.class);
            } else {
            }
        } else {
            //menu.setVisible(false);
        }
    }

}
