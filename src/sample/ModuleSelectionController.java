package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.*;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuleSelectionController {

    //variables
    String moduleCode, moduleName;
    int moduleCredits;
    boolean mandatory;
    Schedule delivery;
    private int creditOne, creditTwo;

    //observable list for storing modules
    private ObservableList<Module> list = FXCollections.observableArrayList();
    private ObservableList<Module> finalUnselectedModules1 = FXCollections.observableArrayList();
    private ObservableList<Module> finalUnselectedModules2 = FXCollections.observableArrayList();
    private ObservableList<Module> finalSelectedModules1 = FXCollections.observableArrayList();
    private ObservableList<Module> finalSelectedModules2 = FXCollections.observableArrayList();

    //object instantiation
    private StudentProfile model = new StudentProfile();
    private Module module;

    @FXML
    private TextArea profileDisplay, selectedModulesDisplay, reservedModulesDisplay;

    @FXML
    private Accordion accordion;

    @FXML
    private TitledPane t1, t2;

    @FXML
    private ListView<Module> unselectedTerm1Modules, reservedTerm1Modules, unselectedTerm2Modules, reservedTerm2Modules,
            unselectedTerm1, unselectedTerm2, selectedYearLong, selectedTerm1, selectedTerm2;

    @FXML
    private Button addButton1, removeButton1, confirmButton1, addButton2, removeButton2, confirmButton2, btnCreateProfile,
            resetBtn;

    @FXML
    private TabPane tabPane;

    @FXML
    private MenuItem loadItem, saveItem, exitItem, aboutItem;

    @FXML
    private ComboBox<Course> cboCourses;

    @FXML
    private TextField txtPnumber, txtFirstName, txtSurname, txtEmail, term1CreditField, term2CreditField;

    @FXML
    private DatePicker inputDate;

    @FXML
    public void initialize() {
        module = new Module(moduleCode, moduleName, moduleCredits, mandatory, delivery);
        addCoursesToComboBox(generateAndReturnCourses());

        saveItem.setOnAction(event -> System.out.println("Save Item"));

        loadItem.setOnAction(event -> System.out.println("Load Item"));

        exitItem.setOnAction(event -> System.exit(0));

        aboutItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About Module Selection Tool");
            alert.setHeaderText(null);
            alert.setContentText("************* Module Selection Tool *************\n" +
                    "\nModule Selection Tool is a tool used for selecting\n" +
                    "the various modules in a course for both terms");
            alert.showAndWait();
        });

        resetBtn.setDisable(true);

        btnCreateProfile.setOnAction(event -> {
            changeTab(1);

            model.setStudentName(getStudentName());
            model.setStudentPnumber(getStudentPnumber());
            model.setStudentEmail(getStudentEmail());
            model.setSubmissionDate(getStudentDate());
            model.setStudentCourse(getSelectedCourse());

            String selectedCourse = String.valueOf(getSelectedCourse());
            if (selectedCourse.equals("Computer Science")) {
                for (Module module : getSelectedCourse().getAllModulesOnCourse()) {
                    if ((module.getDelivery().equals(Schedule.TERM_1))) {
                        populateTextView(module);
                        populateListView(module);
                        if (!(module.isMandatory())) {
                            finalUnselectedModules1.addAll(module);
                        } else {
                            finalSelectedModules1.addAll(module);
                        }
                    }
                    if (module.getDelivery().equals(Schedule.TERM_2)) {
                        populateTextView(module);
                        populateListView(module);
                        if (!(module.isMandatory())) {
                            finalUnselectedModules2.addAll(module);
                        } else {
                            finalSelectedModules2.addAll(module);
                        }
                    }
                    if (module.getDelivery().equals(Schedule.YEAR_LONG)) {
                        populateListView(module);
                        finalSelectedModules1.addAll(module);
                    }
                }

            } else {
                for (Module module : getSelectedCourse().getAllModulesOnCourse()) {
                    populateTextView(module);
                    if (module.getDelivery().equals(Schedule.TERM_1)) {
                        populateListView(module);
                        if (!(module.isMandatory())) {
                            finalUnselectedModules1.addAll(module);
                        } else {
                            finalSelectedModules1.addAll(module);
                        }
                    }
                    if (module.getDelivery().equals(Schedule.TERM_2)) {
                        populateListView(module);
                        if (!(module.isMandatory())) {
                            finalUnselectedModules2.addAll(module);
                        } else {
                            finalSelectedModules2.addAll(module);
                        }
                    }
                    if (module.getDelivery().equals(Schedule.YEAR_LONG)) {
                        populateListView(module);
                    }
                }
            }
            resetBtn.setDisable(false);

        });

        accordion.setExpandedPane(t1);

        confirmButton2.setOnAction(event -> {
            changeTab(3);

            populateProfileTextArea(model);
            setSelectedModules(module);
            populateSelectedModuleTextArea();
            setReservedModules(module);
            populateReservedModuleTextArea();
        });

    }

    public int getCreditOne() {
        return creditOne;
    }

    public void setCreditOne(int creditOne) {
        this.creditOne = creditOne;
    }

    public int getCreditTwo() {
        return creditTwo;
    }

    public void setCreditTwo(int creditTwo) {
        this.creditTwo = creditTwo;
    }

    //method to change tabs
    public void changeTab(int index) {
        tabPane.getSelectionModel().select(index);
    }

    //method to allow the controller to add courses to the combo box
    public void addCoursesToComboBox(Course[] courses) {
        cboCourses.getItems().addAll(courses);
        cboCourses.getSelectionModel().select(0); //select first course by default
    }

    //methods to retrieve the form selection/input
    public Course getSelectedCourse() {
        return cboCourses.getSelectionModel().getSelectedItem();
    }

    public String getStudentPnumber() {
        return txtPnumber.getText();
    }

    public Name getStudentName() {
        return new Name(txtFirstName.getText(), txtSurname.getText());
    }

    public String getStudentEmail() {
        return txtEmail.getText();
    }

    public LocalDate getStudentDate() {
        return inputDate.getValue();
    }

    //populate the list views
    public void populateListView(Module module) {

        if (module.getDelivery().equals(Schedule.TERM_1)) {
            if (!(module.isMandatory())) {
                unselectedTerm1.getItems().addAll(module);
            }
        }
        if (module.getDelivery().equals(Schedule.TERM_2)) {
            if (!(module.isMandatory())) {
                unselectedTerm2.getItems().addAll(module);
            }
        }
        if (module.getDelivery().equals(Schedule.YEAR_LONG)) {
            selectedYearLong.getItems().addAll(module);
        }
        if ((module.getDelivery().equals(Schedule.TERM_1)) && (module.isMandatory())) {
            selectedTerm1.getItems().addAll(module);
        }
        if ((module.getDelivery().equals(Schedule.TERM_2)) && (module.isMandatory())) {
            selectedTerm2.getItems().addAll(module);
        }
    }

    //populate the text views for the credit with initial values
    public void populateTextView(Module mod) {
        int credit1 = 15, credit2 = 15;
        if ((mod.getDelivery().equals(Schedule.TERM_1)) && (mod.isMandatory())) {
            credit1 += mod.getModuleCredits();
            term1CreditField.setText(String.valueOf(credit1));
            setCreditOne(credit1);
            System.out.println(getCreditOne());
        }
        if ((getSelectedCourse().toString().equals("Computer Science")) && selectedTerm2.getItems().isEmpty()) {
            term2CreditField.setText(String.valueOf(credit2));
            setCreditTwo(credit2);
            System.out.println(getCreditTwo());
        } else if ((getSelectedCourse().toString().equals("Software Engineering")) && (mod.getDelivery().equals(Schedule.TERM_2)) ) {
            if (mod.isMandatory()) {
                credit2 += mod.getModuleCredits();
                setCreditTwo(credit2);
                term2CreditField.setText(String.valueOf(credit2));
            }

        }
    }

    //add method to remove modules from unselected term 1 view and add to selected term 1 view
    public void addModule1() {
        int index = unselectedTerm1.getSelectionModel().getSelectedIndex();
        list.add(unselectedTerm1.getSelectionModel().getSelectedItem());
        finalUnselectedModules1.remove(unselectedTerm1.getSelectionModel().getSelectedItem());
        finalSelectedModules1.add(unselectedTerm1.getSelectionModel().getSelectedItem());
        unselectedTerm1.getItems().remove(index);
        selectedTerm1.getItems().addAll(list);
        list.clear();
    }

    //add method to remove modules from selected term 1 view and add to unselected term 1 view
    public void removeModule1() {
        int index = selectedTerm1.getSelectionModel().getSelectedIndex();
        list.add(selectedTerm1.getSelectionModel().getSelectedItem());
        finalUnselectedModules1.add(selectedTerm1.getSelectionModel().getSelectedItem());
        finalSelectedModules1.remove(selectedTerm1.getSelectionModel().getSelectedItem());
        selectedTerm1.getItems().remove(index);
        unselectedTerm1.getItems().addAll(list);
        list.clear();
    }

    //add method to remove modules from unselected term 2 view and add to selected term 2 view
    public void addModule2() {
        int index = unselectedTerm2.getSelectionModel().getSelectedIndex();
        list.add(unselectedTerm2.getSelectionModel().getSelectedItem());
        finalUnselectedModules2.remove(unselectedTerm2.getSelectionModel().getSelectedItem());
        finalSelectedModules2.add(unselectedTerm2.getSelectionModel().getSelectedItem());
        unselectedTerm2.getItems().remove(index);
        selectedTerm2.getItems().addAll(list);
        list.clear();
    }

    //add method to remove modules from selected term 2 view and add to unselected term 2 view
    public void removeModule2() {
        int index = selectedTerm2.getSelectionModel().getSelectedIndex();
        list.add(selectedTerm2.getSelectionModel().getSelectedItem());
        finalUnselectedModules2.add(selectedTerm2.getSelectionModel().getSelectedItem());
        finalSelectedModules2.remove(selectedTerm2.getSelectionModel().getSelectedItem());
        selectedTerm2.getItems().remove(index);
        unselectedTerm2.getItems().addAll(list);
        list.clear();
    }

    public void resetView() {
        unselectedTerm1.getItems().clear();
        unselectedTerm2.getItems().clear();
        selectedYearLong.getItems().clear();
        selectedTerm1.getItems().clear();
        selectedTerm2.getItems().clear();
        finalUnselectedModules1.clear();
        finalUnselectedModules2.clear();

        String selectedCourse = String.valueOf(getSelectedCourse());
        if (selectedCourse.equals("Computer Science")) {
            for (Module module : getSelectedCourse().getAllModulesOnCourse()) {
                populateTextView(module);
                if ((module.getDelivery().equals(Schedule.TERM_1))) {
                    populateListView(module);
                    if (!(module.isMandatory())) {
                        finalUnselectedModules1.addAll(module);
                    } else {
                        finalSelectedModules1.addAll(module);
                    }
                }
                if (module.getDelivery().equals(Schedule.TERM_2)) {
                    populateListView(module);
                    if (!(module.isMandatory())) {
                        finalUnselectedModules2.addAll(module);
                    } else {
                        finalSelectedModules2.addAll(module);
                    }
                }
                if (module.getDelivery().equals(Schedule.YEAR_LONG)) {
                    populateListView(module);
                    finalSelectedModules1.addAll(module);
                }
            }

        } else {
            for (Module module : getSelectedCourse().getAllModulesOnCourse()) {
                populateTextView(module);
                if (module.getDelivery().equals(Schedule.TERM_1)) {
                    populateListView(module);
                    if (!(module.isMandatory())) {
                        finalUnselectedModules1.addAll(module);
                    } else {
                        finalSelectedModules1.addAll(module);
                    }
                }
                if (module.getDelivery().equals(Schedule.TERM_2)) {
                    populateListView(module);
                    if (!(module.isMandatory())) {
                        finalUnselectedModules2.addAll(module);
                    } else {
                        finalSelectedModules2.addAll(module);
                    }
                }
                if (module.getDelivery().equals(Schedule.YEAR_LONG)) {
                    populateListView(module);
                }
            }
        }
    }

    public void submitUnselected() {
        changeTab(2);

        String selectedCourse = String.valueOf(getSelectedCourse());
        if (selectedCourse.equals("Computer Science")) {
            for (Module module : getSelectedCourse().getAllModulesOnCourse()) {
                if (module.getDelivery().equals(Schedule.TERM_1)) {
                    populateReservedTab(module);
                }
                if (module.getDelivery().equals(Schedule.TERM_2)) {
                    populateReservedTab(module);
                }
            }
        } else {
            for (Module module : getSelectedCourse().getAllModulesOnCourse()) {
                if (module.getDelivery().equals(Schedule.TERM_1)) {
                    populateReservedTab(module);
                }
                if (module.getDelivery().equals(Schedule.TERM_2)) {
                    populateReservedTab(module);
                }
            }
        }
    }

    //method to populate the reserved modules view
    public void populateReservedTab(Module module) {
        if (module.getDelivery().equals(Schedule.TERM_1)) {
            unselectedTerm1Modules.setItems(finalUnselectedModules1);
            addButton1.setDisable(false);
            removeButton1.setDisable(false);
            confirmButton1.setDisable(false);
        }
        if (module.getDelivery().equals(Schedule.TERM_2)) {
            unselectedTerm2Modules.setItems(finalUnselectedModules2);
            addButton2.setDisable(false);
            removeButton2.setDisable(false);
            confirmButton2.setDisable(false);

        }
    }

    public void addToReservedModule1() {
        int index = unselectedTerm1Modules.getSelectionModel().getSelectedIndex();
        list.add(unselectedTerm1Modules.getSelectionModel().getSelectedItem());
        unselectedTerm1Modules.getItems().remove(index);
        reservedTerm1Modules.getItems().addAll(list);
        list.clear();
    }

    public void addToReservedModule2() {
        int index = unselectedTerm2Modules.getSelectionModel().getSelectedIndex();
        list.add(unselectedTerm2Modules.getSelectionModel().getSelectedItem());
        unselectedTerm2Modules.getItems().remove(index);
        reservedTerm2Modules.getItems().addAll(list);
        list.clear();
    }

    public void removeFromReservedModule1() {
        int index = reservedTerm1Modules.getSelectionModel().getSelectedIndex();
        list.add(reservedTerm1Modules.getSelectionModel().getSelectedItem());
        reservedTerm1Modules.getItems().remove(index);
        unselectedTerm1Modules.getItems().addAll(list);
        list.clear();
    }

    public void removeFromReservedModule2() {
        int index = reservedTerm2Modules.getSelectionModel().getSelectedIndex();
        list.add(reservedTerm2Modules.getSelectionModel().getSelectedItem());
        reservedTerm2Modules.getItems().remove(index);
        unselectedTerm2Modules.getItems().addAll(list);
        list.clear();
    }

    public void confirmModule1() {
        t2.setDisable(false);
        t2.setExpanded(true);
        t1.setDisable(true);
    }

    public void populateProfileTextArea(StudentProfile studentProfile) {
        profileDisplay.appendText("Name:    " + studentProfile.getStudentName().getFullName() + "\n");
        profileDisplay.appendText("PNo.:      " + studentProfile.getStudentPnumber() + "\n");
        profileDisplay.appendText("Email:     " + studentProfile.getStudentEmail() + "\n");
        profileDisplay.appendText("Date:      " + studentProfile.getSubmissionDate().toString() + "\n");
        profileDisplay.appendText("Course:  " + studentProfile.getStudentCourse().toString() + "\n");
    }

    public void populateSelectedModuleTextArea() {
        selectedModulesDisplay.appendText("Selected modules:\n============\n");
        for (Module mod : finalSelectedModules1) {
            selectedModulesDisplay.appendText("Module code: " + mod.getModuleCode() + ", Module name: ");
            selectedModulesDisplay.appendText(mod.getModuleName() + ", Credits: " + mod.getModuleCredits());
            selectedModulesDisplay.appendText(", Mandatory on your course?: " + (mod.isMandatory() ? "Yes" : "No"));
            selectedModulesDisplay.appendText(", Delivery: " + mod.getDelivery() + "\n\n");
        }

        for (Module mod : finalSelectedModules2) {
            selectedModulesDisplay.appendText("Module code: " + mod.getModuleCode() + ", Module name: ");
            selectedModulesDisplay.appendText(mod.getModuleName() + ", Credits: " + mod.getModuleCredits());
            selectedModulesDisplay.appendText(", Mandatory on your course?: " + (mod.isMandatory() ? "Yes" : "No"));
            selectedModulesDisplay.appendText(", Delivery: " + mod.getDelivery() + "\n\n");
        }

    }

    public void populateReservedModuleTextArea() {
        reservedModulesDisplay.appendText("Reserved modules:\n============\n");
        for (Module mod : reservedTerm1Modules.getItems()) {
            reservedModulesDisplay.appendText("Module code: " + mod.getModuleCode() + ", Module name: ");
            reservedModulesDisplay.appendText(mod.getModuleName() + ", Credits: " + mod.getModuleCredits());
            reservedModulesDisplay.appendText(", Delivery: " + mod.getDelivery() + "\n\n");
        }

        for (Module mod : reservedTerm2Modules.getItems()) {
            reservedModulesDisplay.appendText("Module code: " + mod.getModuleCode() + ", Module name: ");
            reservedModulesDisplay.appendText(mod.getModuleName() + ", Credits: " + mod.getModuleCredits());
            reservedModulesDisplay.appendText(", Delivery: " + mod.getDelivery() + "\n\n");
        }

    }

    public void setReservedModules(Module mods) {
        for (Module mod : reservedTerm1Modules.getItems()) {
            mods.setModuleCode(mod.getModuleCode());
            mods.setModuleName(mod.getModuleName());
            mods.setModuleCredits(mod.getModuleCredits());
            mods.setDelivery(mod.getDelivery());
        }

        for (Module mod : reservedTerm2Modules.getItems()) {
            mods.setModuleCode(mod.getModuleCode());
            mods.setModuleName(mod.getModuleName());
            mods.setModuleCredits(mod.getModuleCredits());
            mods.setDelivery(mod.getDelivery());
        }
    }

    public void setSelectedModules(Module mods) {
        for (Module mod : finalSelectedModules1) {
            mods.setModuleCode(mod.getModuleCode());
            mods.setModuleName(mod.getModuleName());
            mods.setModuleCredits(mod.getModuleCredits());
            mods.setMandatory(mod.isMandatory());
            mods.setDelivery(mod.getDelivery());
        }

        for (Module mod : finalSelectedModules2) {
            mods.setModuleCode(mod.getModuleCode());
            mods.setModuleName(mod.getModuleName());
            mods.setModuleCredits(mod.getModuleCredits());
            mods.setMandatory(mod.isMandatory());
            mods.setDelivery(mod.getDelivery());
        }
    }

//    public boolean validatePnumber() {
//
//    }

    public boolean validateFirstName() {
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(txtFirstName.getText());
        if (matcher.find() && matcher.group().equals(txtFirstName.getText())) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validate First name");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid first name");
            alert.showAndWait();

            return false;
        }
    }

    public boolean validateLastName() {
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(txtSurname.getText());
        if (matcher.find() && matcher.group().equals(txtSurname.getText())) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validate Surname");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid Surname");
            alert.showAndWait();

            return false;
        }

    }

    public boolean validateEmail() {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z]+)+");
        Matcher matcher = pattern.matcher(txtEmail.getText());
        if (matcher.find() && matcher.group().equals(txtEmail.getText())) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validate Email");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid email");
            alert.showAndWait();

            return false;
        }
    }



    private Course[] generateAndReturnCourses() {
        Module imat3423 = new Module("IMAT3423", "Systems Building: Methods", 15, true, Schedule.TERM_1);
        Module ctec3451 = new Module("CTEC3451", "Development Project", 30, true, Schedule.YEAR_LONG);
        Module ctec3902_SoftEng = new Module("CTEC3902", "Rigorous Systems", 15, true, Schedule.TERM_2);
        Module ctec3902_CompSci = new Module("CTEC3902", "Rigorous Systems", 15, false, Schedule.TERM_2);
        Module ctec3110 = new Module("CTEC3110", "Secure Web Application Development", 15, false, Schedule.TERM_1);
        Module ctec3605 = new Module("CTEC3605", "Multi-service Networks 1", 15, false, Schedule.TERM_1);
        Module ctec3606 = new Module("CTEC3606", "Multi-service Networks 2", 15, false, Schedule.TERM_2);
        Module ctec3410 = new Module("CTEC3410", "Web Application Penetration Testing", 15, false, Schedule.TERM_2);
        Module ctec3904 = new Module("CTEC3904", "Functional Software Development", 15, false, Schedule.TERM_2);
        Module ctec3905 = new Module("CTEC3905", "Front-End Web Development", 15, false, Schedule.TERM_2);
        Module ctec3906 = new Module("CTEC3906", "Interaction Design", 15, false, Schedule.TERM_1);
        Module ctec3911 = new Module("CTEC3911", "Mobile Application Development", 15, false, Schedule.TERM_1);
        Module imat3410 = new Module("IMAT3104", "Database Management and Programming", 15, false, Schedule.TERM_2);
        Module imat3406 = new Module("IMAT3406", "Fuzzy Logic and Knowledge Based Systems", 15, false, Schedule.TERM_1);
        Module imat3611 = new Module("IMAT3611", "Computer Ethics and Privacy", 15, false, Schedule.TERM_1);
        Module imat3613 = new Module("IMAT3613", "Data Mining", 15, false, Schedule.TERM_1);
        Module imat3614 = new Module("IMAT3614", "Big Data and Business Models", 15, false, Schedule.TERM_2);
        Module imat3428_CompSci = new Module("IMAT3428", "Information Technology Services Practice", 15, false, Schedule.TERM_2);


        Course compSci = new Course("Computer Science");
        compSci.addModuleToCourse(imat3423);
        compSci.addModuleToCourse(ctec3451);
        compSci.addModuleToCourse(ctec3902_CompSci);
        compSci.addModuleToCourse(ctec3110);
        compSci.addModuleToCourse(ctec3605);
        compSci.addModuleToCourse(ctec3606);
        compSci.addModuleToCourse(ctec3410);
        compSci.addModuleToCourse(ctec3904);
        compSci.addModuleToCourse(ctec3905);
        compSci.addModuleToCourse(ctec3906);
        compSci.addModuleToCourse(ctec3911);
        compSci.addModuleToCourse(imat3410);
        compSci.addModuleToCourse(imat3406);
        compSci.addModuleToCourse(imat3611);
        compSci.addModuleToCourse(imat3613);
        compSci.addModuleToCourse(imat3614);
        compSci.addModuleToCourse(imat3428_CompSci);

        Course softEng = new Course("Software Engineering");
        softEng.addModuleToCourse(imat3423);
        softEng.addModuleToCourse(ctec3451);
        softEng.addModuleToCourse(ctec3902_SoftEng);
        softEng.addModuleToCourse(ctec3110);
        softEng.addModuleToCourse(ctec3605);
        softEng.addModuleToCourse(ctec3606);
        softEng.addModuleToCourse(ctec3410);
        softEng.addModuleToCourse(ctec3904);
        softEng.addModuleToCourse(ctec3905);
        softEng.addModuleToCourse(ctec3906);
        softEng.addModuleToCourse(ctec3911);
        softEng.addModuleToCourse(imat3410);
        softEng.addModuleToCourse(imat3406);
        softEng.addModuleToCourse(imat3611);
        softEng.addModuleToCourse(imat3613);
        softEng.addModuleToCourse(imat3614);

        Course[] courses = new Course[2];
        courses[0] = compSci;
        courses[1] = softEng;

        return courses;
    }

}
