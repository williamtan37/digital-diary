import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.text.*;
import java.io.*;
import javax.swing.text.DefaultCaret;

public class Diary {
    private LinkedList<User> userDatabase = new LinkedList();
    private LinkedList<Contact> contactDatabase = new LinkedList();
    private LinkedList<Reminder> reminderDatabase = new LinkedList();

    private final String USER_LIST_TITLE = "FIRST NAME | LAST NAME | AGE | GENDER | EMAIL | PASSWORD | CONTACT LIST | REMINDER LIST | NOTES LIST";
    private final String CONTACT_LIST_TITLE = "FIRST NAME | LAST NAME | EMAIL | AGE | GENDER | PHONE NUMBER | PICTURE DIRECTORY";
    private final String REMINDER_LIST_TITLE = "TITLE | MESSAGE | LOCATION | ALARM MESSAGE | START DATE | END DATE";
    private final File USER_LIST_PATH = new File("/Users/SuchenTan/Desktop/Digital Diary/TextFiles/UserList.txt");

    private BufferedReader textInputStream;
    private final String TITLE = "Diary";
    private final ImageIcon LOGO = new ImageIcon("/Users/SuchenTan/Desktop/Digital Diary/Images/finaldiarylogo.png");
    private final int PORT = 6000;

    private final int WINDOW_X = 820;
    private final int WINDOW_Y = 600;

    private JFrame frame = new JFrame();

    private JLabel logolabel = new JLabel(LOGO);

    private JTextField emailLabel = new JTextField("Email", 25);
    private JPasswordField passwordLabel = new JPasswordField("Password", 25);
    private JButton signInButton = new JButton("Sign In");
    private JButton newAccountButton = new JButton("New Account");

    private char originalEchoChar = passwordLabel.getEchoChar();

    private final Color bluebackgroundColor = new Color(0, 175, 240),
    greyTextColor = new Color(160, 160, 200), 
    lightBlueFocusLostTextFieldColor = new Color(204, 239, 252);

    private Font font = new Font(emailLabel.getName(), Font.PLAIN, 14);

    private String email, password, firstName, lastName, comfirmPassword,dataReceived;
    private boolean accessGranted;

    Contact selectedContact;
    String contactImgDirectory, reminderImgDirectory;
 
    String notesInformation;

    Icon transparentImg = new ImageIcon("/Users/SuchenTan/Desktop/Digital Diary/Images/transparent");
    
    JScrollPane contactsScrollPane;
    JList contactsList;
    JButton contactsButton;
    JTextArea notesTextArea;
    JButton contactsOptionsButton;
    JPanel panel1,panel2;
        
    File CONTACT_LIST_PATH;
    File REMINDER_LIST_PATH;
    File NOTES_LIST_PATH;
    
    JButton saveNotesButton;
    JButton remindersButton;
    JButton remindersOptionsButton;
    
    JList reminderList;
    JScrollPane reminderScrollPane;
    Reminder selectedReminder;
    JScrollPane noteScrollPane;
    
    public Diary() {
        frame.getContentPane().setBackground(bluebackgroundColor);

        frame.setSize(WINDOW_X, WINDOW_Y);
        frame.setTitle(TITLE);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

    }

    public void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
        | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(frame);

        updateUserDatabase();
        logIn();      
        frame.setVisible(true);
    }

    public void logIn() {
        emailLabel.setForeground(greyTextColor);
        passwordLabel.setForeground(greyTextColor);
        emailLabel.setBackground(lightBlueFocusLostTextFieldColor);
        passwordLabel.setBackground(lightBlueFocusLostTextFieldColor);

        emailLabel.setFont(font);
        passwordLabel.setFont(font);

        //***************************************************************************************************
        // Logo
        //***************************************************************************************************
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 40, 0);
        frame.add(logolabel, c);

        //***************************************************************************************************
        // Email Field
        //***************************************************************************************************
        c.gridy++;
        c.ipady = 15;
        c.insets = new Insets(0, 0, 12, 0);
        frame.add(emailLabel, c);
        emailLabel.addFocusListener(new FocusListener() {

                @Override
                public void focusLost(FocusEvent arg0) {
                    if (emailLabel.getText().equals("")) {
                        emailLabel.setForeground(greyTextColor);
                        emailLabel.setText("Email");
                    }
                    emailLabel.setBackground(lightBlueFocusLostTextFieldColor);
                }

                @Override
                public void focusGained(FocusEvent arg0) {

                    if (emailLabel.getText().equals("Email")) {
                        emailLabel.setText(null);
                        emailLabel.setForeground(Color.BLACK);
                    }
                    emailLabel.setBackground(Color.WHITE);

                }
            });

        //***************************************************************************************************
        // Password Field
        //***************************************************************************************************   
        passwordLabel.setEchoChar((char) 0);
        c.gridy++;
        frame.add(passwordLabel, c);
        passwordLabel.addFocusListener(new FocusListener() {
                public void focusLost(FocusEvent arg0) {
                    if (passwordLabel.getText().equals("")) {
                        passwordLabel.setEchoChar((char) 0);
                        passwordLabel.setForeground(greyTextColor);
                        passwordLabel.setText("Password");
                    }
                    passwordLabel.setBackground(lightBlueFocusLostTextFieldColor);
                }           

                public void focusGained(FocusEvent arg0) {
                    if (passwordLabel.getText().equals("Password")) {
                        passwordLabel.setEchoChar(originalEchoChar);
                        passwordLabel.setText("");
                        passwordLabel.setForeground(Color.BLACK);
                    }
                    passwordLabel.setBackground(Color.WHITE);
                }
            });

        //***************************************************************************************************
        // SignIn button
        //***************************************************************************************************
        c.gridy++;
        frame.add(signInButton, c);

        signInButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    email = emailLabel.getText();
                    password = passwordLabel.getText();

                    validateUser(email, password);
                }
            });        
    }

    public void validateUser(String email, String password)
    {
        User targetUser = null;
        boolean isFound = false;
        for(int i = 0; i < userDatabase.size(); i++)
        {
            if(email.equalsIgnoreCase(userDatabase.get(i).getEmail()) && password.equals(userDatabase.get(i).getPassword()))
            {
                targetUser = userDatabase.get(i);
                isFound = true;
                break;
            }                    
        }

        if(isFound == true)
        {
            userInterface(targetUser);
        }

        else
            JOptionPane.showMessageDialog(frame,"Invalid E-mail or Password!");
    }

    public void userInterface(User user)
    {
        CONTACT_LIST_PATH = new File("/Users/SuchenTan/Desktop/Digital Diary/TextFiles/" +user.getContactListLocation());
        REMINDER_LIST_PATH = new File("/Users/SuchenTan/Desktop/Digital Diary/TextFiles/" + user.getReminderListLocation());
        NOTES_LIST_PATH = new File("/Users/SuchenTan/Desktop/Digital Diary/TextFiles/" + user.getNotesListLocation());

        updateContactDatabase(CONTACT_LIST_PATH);
        updateReminderDatabase(REMINDER_LIST_PATH);
        updateNotesDatabase(NOTES_LIST_PATH);
        frame.remove(logolabel);
        frame.remove(emailLabel);
        frame.remove(passwordLabel);
        frame.remove(signInButton);

        loadingAnimation();

           Timer timer = new Timer();

        timer.schedule( new TimerTask(){
          public void run(){
            frame.remove(loadingLabel);
        loadUI();
        }
        },1000);
    }

    ImageIcon loadingIcon;
    JLabel loadingLabel;
    public void loadingAnimation()
    {
        loadingIcon = new ImageIcon("/Users/SuchenTan/Desktop/Digital Diary/Images/7.gif");
        loadingLabel = new JLabel(loadingIcon, JLabel.CENTER);

        frame.add(loadingLabel);
        validateAndRepaint();
    }

    public void loadUI()
    {            
        GridBagConstraints c = new GridBagConstraints();
        panel1 = new JPanel(new GridBagLayout());
        panel2 = new JPanel(new GridBagLayout());
        panel1.setOpaque(false);
        panel2.setOpaque(false);

        

        addNotesUI();
        addContactsUI();
        addReminderUI();
        addListeners();

        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0,0,50,50);
        
        frame.add(panel1,c);
        c.gridx = 1;
        frame.add(panel2,c);

        
        validateAndRepaint();
    }
    
    public void validateAndRepaint(){
        frame.validate();
        frame.repaint();
    }
    
    public void addReminderUI()
    {
        remindersOptionsButton = new JButton("Reminder Options");
        remindersButton = new JButton("Please Select a Reminder");
        remindersButton.setPreferredSize(new Dimension(465,40));
        remindersOptionsButton.setPreferredSize(new Dimension(465,40));
        GridBagConstraints c = new GridBagConstraints();
        reminderScrollPane = new JScrollPane();        
        reminderList = new JList(reminderDatabase.toArray());
        
        reminderList.setVisibleRowCount(6);
        reminderList.setFont(font);
        reminderList.setBackground(lightBlueFocusLostTextFieldColor);
        reminderList.setFixedCellHeight(24);
        reminderList.setFixedCellWidth(442);
        DefaultListCellRenderer renderer =  (DefaultListCellRenderer)reminderList.getCellRenderer();  
        renderer.setHorizontalAlignment(JLabel.CENTER); 
        reminderScrollPane.setViewportView(reminderList);
        
        c.gridx = 0;
        c.gridy = 2;
        panel2.add(remindersButton,c);
        c.gridy = 3;
        panel2.add(reminderScrollPane,c);
        c.gridy = 4;
        panel2.add(remindersOptionsButton,c);
        
    }

    public void addNotesUI(){
        GridBagConstraints c = new GridBagConstraints();
        
        saveNotesButton = new JButton("Save Notes");
        notesTextArea = new JTextArea(9,34);
        notesTextArea.append(notesInformation);
        
       noteScrollPane = new JScrollPane(notesTextArea);
        saveNotesButton.setPreferredSize(new Dimension(465,40));
        notesTextArea.setBackground(lightBlueFocusLostTextFieldColor);
        notesTextArea.setFont(font);
        
        notesTextArea.setCaretPosition(0);
        c.gridx = 0;
        c.gridy = 0;
        panel2.add(saveNotesButton,c);
        c.gridy = 1;
        panel2.add(noteScrollPane,c);
        
    }
    
    public void addContactsUI(){
        contactsButton = new JButton("Please Select a Contact");
        contactsOptionsButton = new JButton("Contact Options");
        contactsButton.setPreferredSize(new Dimension(203,40));
        contactsOptionsButton.setPreferredSize(new Dimension(203,40));
        contactsScrollPane = new JScrollPane();        
        contactsList = new JList(contactDatabase.toArray());

        contactsList.setVisibleRowCount(14);
        contactsList.setFont(font);
        contactsList.setBackground(lightBlueFocusLostTextFieldColor);
        contactsList.setFixedCellHeight(24);
        contactsList.setFixedCellWidth(180);
        DefaultListCellRenderer renderer =  (DefaultListCellRenderer)contactsList.getCellRenderer();  
        renderer.setHorizontalAlignment(JLabel.CENTER); 
        contactsScrollPane.setViewportView(contactsList);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        panel1.add(contactsButton,c);
        c.gridy = 1;
        panel1.add(contactsScrollPane,c);
        c.gridy = 2;
        panel1.add(contactsOptionsButton,c);        
    }

    public void addListeners(){
        contactsList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent listSelectionEvent) {
                    JList list = (JList) listSelectionEvent.getSource();
                    Object selectedObject = list.getSelectedValue();

                    if(selectedObject != null){
                        selectedContact = (Contact)selectedObject;
                        contactsButton.setText(selectedContact.getFirstName() + " " +selectedContact.getLastName());
                        contactImgDirectory = selectedContact.getImgDirectory();
                    }
                    else{      
                        contactsButton.setText("Please Select a Contact");
                        selectedContact = null;
                    }
                }
            });

        contactsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(selectedContact == null){
                        JOptionPane.showMessageDialog(frame,"Please select a contact.");
                    }
                    else{
                        Icon img = new ImageIcon("/Users/SuchenTan/Desktop/Digital Diary/Images/" + contactImgDirectory);
                        JOptionPane.showMessageDialog(frame,selectedContact.toStringGUI(),selectedContact.toString(),1,img);
                    }
                }
            });  

        contactsOptionsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String[] buttons = {"Sort Conacts", "Remove Contact", "Add Contact"};    
                    int returnValue = JOptionPane.showOptionDialog(null, "                              What would you like to do?", "Options",JOptionPane.DEFAULT_OPTION, 0, transparentImg, buttons, null);
                    contactOptions(returnValue);        
                }
            }); 

        saveNotesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try{
                        notesInformation = notesTextArea.getText();
                        BufferedWriter output = new BufferedWriter(new FileWriter(NOTES_LIST_PATH, false));
                        output.write(notesInformation);
                        output.flush();
                        output.close();
                        JOptionPane.showMessageDialog(frame, "Notes saved successfully!");
                    }catch(IOException ee){
                        System.out.println("something went wrong");
                    }

                }
            }); 
            
            remindersButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(selectedReminder == null){
                        JOptionPane.showMessageDialog(frame,"Please select a reminder first.");
                    }
                    else{
                        Icon img = new ImageIcon("/Users/SuchenTan/Desktop/Digital Diary/Images/" + reminderImgDirectory);
                        JOptionPane.showMessageDialog(frame,selectedReminder.toStringGUI(),selectedReminder.toString(),1,img);
                    }
                }
            });  
            
            remindersOptionsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String[] buttons = {"Remove Reminder","Add Reminder"};    
                    int returnValue = JOptionPane.showOptionDialog(null, "                              What would you like to do?", "Options",JOptionPane.DEFAULT_OPTION, 0, transparentImg, buttons, null);
                    reminderOptions(returnValue);  
                }
            });
            reminderList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent listSelectionEvent) {
                    JList list = (JList) listSelectionEvent.getSource();
                    Object selectedObject = list.getSelectedValue();

                    if(selectedObject != null){
                        selectedReminder = (Reminder)selectedObject;
                        remindersButton.setText(selectedReminder.getTitle());
                        reminderImgDirectory = selectedReminder.getImgDirectory();
                    }
                    else{      
                        remindersButton.setText("Please Select a Reminder!");
                        selectedReminder = null;
                    }
                }
            });
    }

    public void reminderOptions(int optionValue)
    {
        String targetDeleteReminder;
        if(optionValue == 1){
        }
        
        else if(optionValue == 0){
            targetDeleteReminder = JOptionPane.showInputDialog("Which reminder do you want to delete?");
            deleteReminder(targetDeleteReminder);
        }
    }
    public void contactOptions(int optionValue)
    {
        String targetDeleteName;
        String firstName, lastName, email, age, gender, phoneNumber;
        Contact newContact = null;

        if(optionValue == 0){
            sortContacts();
        }
        else if(optionValue == 1){
            targetDeleteName = JOptionPane.showInputDialog("What is the name of the person you want to delete?");
            deleteContact(targetDeleteName);
        }
        else if(optionValue == 2){
            firstName = JOptionPane.showInputDialog("What is your first name?");
            lastName = JOptionPane.showInputDialog("What is you last name?");
            email = JOptionPane.showInputDialog("What is your E-mail address?");
            age = JOptionPane.showInputDialog("What is your age?");
            gender = JOptionPane.showInputDialog("Are you Male or Female?");
            phoneNumber = JOptionPane.showInputDialog("What is your phone number?");

            newContact = new Contact(firstName, lastName, email, age, gender, phoneNumber, "temp");
            addContact(newContact);
        }

    } 

    public void sortContacts()
    {

        String[] buttons =  {"Gender (Male-Female)","Age (Low-High)","Last Name (A-Z)", "First Name (A-Z)"};    
        int returnValue = JOptionPane.showOptionDialog(null, "                                                   What do you want to sort your contacts by?", "Sort Contacts",JOptionPane.DEFAULT_OPTION, 0, transparentImg, buttons, null);
        if(returnValue == 3){
            Collections.sort(contactDatabase, new Comparator<Contact>() {
                    public int compare(Contact contact1, Contact contact2){
                        String firstContact1 = contact1.getFirstName();
                        String firstContact2 = contact2.getFirstName();

                        if(firstContact1.compareTo(firstContact2) > 0)
                            return 1;
                        else if(firstContact1.compareTo(firstContact2) < 0)
                            return -1;
                        else return 0;
                    }
                });
        }

        else if(returnValue == 2){
            Collections.sort(contactDatabase, new Comparator<Contact>() {
                    public int compare(Contact contact1, Contact contact2){
                        String lastContact1 = contact1.getLastName();
                        String lastContact2 = contact2.getLastName();

                        if(lastContact1.compareTo(lastContact2) > 0)
                            return 1;
                        else if(lastContact1.compareTo(lastContact2) < 0)
                            return -1;
                        else return 0;
                    }
                });
        }

        else if(returnValue == 1){
            Collections.sort(contactDatabase, new Comparator<Contact>() {
                    public int compare(Contact contact1, Contact contact2){
                        int ageContact1 = Integer.parseInt(contact1.getAge());
                        int ageContact2 = Integer.parseInt(contact2.getAge());

                        if(ageContact1 > ageContact2)
                            return 1;
                        else if(ageContact1 < ageContact2)
                            return -1;
                        else 
                            return 0;
                    }
                });

        }
        else if(returnValue == 0){
            Collections.sort(contactDatabase, new Comparator<Contact>() {
                    public int compare(Contact contact1, Contact contact2){
                        String genderContact1 = contact1.getGender();
                        String genderContact2 = contact2.getGender();

                        if(genderContact1.compareTo(genderContact2) < 0)
                            return 1;
                        else if(genderContact1.compareTo(genderContact2) > 0)
                            return -1;
                        else 
                            return 0;
                    }
                });

        }

        contactsList.setListData(contactDatabase.toArray());
        validateAndRepaint();
    }

    public void addContact(Contact newContact)
    {
        contactDatabase.add(newContact);
        contactsList.setListData(contactDatabase.toArray());
        validateAndRepaint();
    }

    public void deleteContact(String fullName)
    {        
        boolean isFound = false;
        for(int i = 0; i < contactDatabase.size();i++){
            if(fullName.equals(contactDatabase.get(i).getFirstName() + " " + contactDatabase.get(i).getLastName())){                
                contactDatabase.remove(i);
                isFound = true;
                break;
            }
        }

        if(isFound == false){
            JOptionPane.showMessageDialog(frame, "Please enter a valid name!");
        }

        else{
            contactsList.setListData(contactDatabase.toArray());
            validateAndRepaint();
        }

    }
    
    public void deleteReminder(String reminder)
    {        
        boolean isFound = false;
        for(int i = 0; i < reminderDatabase.size();i++){
            if(reminder.equals(reminderDatabase.get(i).getTitle())){                
                reminderDatabase.remove(i);
                isFound = true;
                break;
            }
        }

        if(isFound == false){
            JOptionPane.showMessageDialog(frame, "Please enter a valid reminder!");
        }

        else{
            reminderList.setListData(reminderDatabase.toArray());
            validateAndRepaint();
        }

    }

    public void updateContactDatabase(File file)
    {
        String line;
        String[] updateContactsElements;
        Contact newContact;
        boolean error = false;

        contactDatabase.clear();

        try {
            textInputStream = new BufferedReader(new FileReader(file));

            while ((line = textInputStream.readLine()) != null) {
                try {
                    if (!line.equals(CONTACT_LIST_TITLE)) {
                        updateContactsElements = line.split("\\|");

                        newContact = new Contact(updateContactsElements[0],updateContactsElements[1],updateContactsElements[2],
                            updateContactsElements[3],updateContactsElements[4],updateContactsElements[5], updateContactsElements[6]);
                        contactDatabase.add(newContact);
                    }
                } catch (Exception e) {
                    System.out.println("Something went wrong with reading the file. Missing args?");
                    e.printStackTrace();
                    error = true;
                }
            }

        } catch (IOException e) {
            System.out.println("Something went wrong with creating the textInputStream.");
            e.printStackTrace();
            error = true;
        }

        if (error == false) {
            // System.out.println("Contacts Database Successfully Loaded!");
        }
    }

    public void updateReminderDatabase(File file)
    {
        String line;
        String[] updateReminderElements;
        Reminder newReminder;
        boolean error = false;

        DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);

        reminderDatabase.clear();

        try {
            textInputStream = new BufferedReader(new FileReader(file));

            while ((line = textInputStream.readLine()) != null) {
                try {
                    if (!line.equals(REMINDER_LIST_TITLE)) {
                        updateReminderElements = line.split("\\|");

                        newReminder = new Reminder(updateReminderElements[0],updateReminderElements[1],updateReminderElements[2],
                            updateReminderElements[3],df.parse(updateReminderElements[4]), df.parse(updateReminderElements[5]),updateReminderElements[6]);
                        reminderDatabase.add(newReminder);
                    }
                } catch (Exception e) {
                    System.out.println("Something went wrong with reading the file. Missing args?");
                    e.printStackTrace();
                    error = true;
                }
            }

        } catch (IOException e) {
            System.out.println("Something went wrong with creating the textInputStream.");
            e.printStackTrace();
            error = true;
        }

        if (error == false) {
            // System.out.println("Contacts Database Successfully Loaded!");
        }
    }

    public void updateUserDatabase()
    {
        String line;
        String[] updateUserElements;
        User newUser;
        boolean error = false;

        userDatabase.clear();
        try {
            textInputStream = new BufferedReader(new FileReader(USER_LIST_PATH));

            while ((line = textInputStream.readLine()) != null) {
                try {
                    if (!line.equals(USER_LIST_TITLE)) {
                        updateUserElements = line.split("\\|");

                        newUser = new User(updateUserElements[0],updateUserElements[1],updateUserElements[2],
                            updateUserElements[3],updateUserElements[4],updateUserElements[5],updateUserElements[6]
                        ,updateUserElements[7], updateUserElements[8]);
                        userDatabase.add(newUser);
                    }
                } catch (Exception e) {
                    System.out.println("Something went wrong with reading the file. Missing args?");
                    e.printStackTrace();
                    error = true;
                }
            }

        } catch (IOException e) {
            System.out.println("Something went wrong with creating the textInputStream.");
            e.printStackTrace();
            error = true;
        }

        if (error == false) {
            // System.out.println("User Database Successfully Loaded!");
        }
    }

    public void updateNotesDatabase(File file)
    {
        String line;
        notesInformation = "";
        try {
            textInputStream = new BufferedReader(new FileReader(file));

            while ((line = textInputStream.readLine()) != null) {
                try {
                    notesInformation += line + "\n";
                } catch (Exception e) {
                    System.out.println("Something went wrong with reading the file. Missing args?");
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.out.println("Something went wrong with creating the textInputStream.");
            e.printStackTrace();
        }
    }
}