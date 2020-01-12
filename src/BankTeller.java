package cs174a;

import cs174a.Testable.*;
import java.util.Scanner;

public class BankTeller {
    private App app;

    BankTeller(App app) {
        this.app = app;
    }

    public void displayUI(Scanner s) {
        System.out.println("\nWelcome to the Bank Teller Interface, through which customer accounts are managed. These are your options for action:");
        
        String choice = "";
        while(choice.equals("9") == false){
            System.out.println("\n---Teller Options---");
            System.out.println("0: Enter Check Transaction");
            System.out.println("1: Generate Monthly Statement");
            System.out.println("2: List Closed Accounts");
            System.out.println("3: Generate Government Drug and Tax Evasion Report");
            System.out.println("4: Generate Customer Report");
            System.out.println("5: Add Interest");
            System.out.println("6: Create Account");
            System.out.println("7: Delete Closed Accounts and Customers");
            System.out.println("8: Delete Transactions");
            System.out.println("9: Exit Teller Interface");
            System.out.println("Enter the number associated with the action you'd like to take: ");
            choice = s.nextLine();

            String tax_id = "";
            String response = "";
            String strAmount = "";
            switch(choice) {
                case "0": //Enter Check Transaction
                    System.out.println("Enter the account that the check is written from:");
                    String aid_check = s.nextLine();
                    System.out.println("Enter the amount that the check is written for:");
                    strAmount = s.nextLine();
                    while(checkAmountInput(strAmount) == false){
                        System.out.println("Not a valid amount. Please re-enter:");
                        strAmount = s.nextLine();
                    }
                    response = app.writeCheck(aid_check, Double.parseDouble(strAmount));
                    if(response.charAt(0) == '0'){
                        System.out.println("\nCheck succesfully written!");
                    }
                    break;
                case "1": //Generate Monthly Statement\
                    System.out.println("Enter the tax id that the monthly statement should be generated for:");
                    tax_id = s.nextLine();
                    app.generateMonthly(tax_id);
                    break;
                case "2": //List Closed Accounts 
                    app.listClosedAccounts();
                    break;  
                case "3": //Generate Government Drug and Tax Evasion Report (Generate appropriate output if no deposits > 10000)
                    app.generateDTER();
                    break;
                case "4": //Generate Customer Report 
                    System.out.println("Enter the tax ID you would like to generate a report for: ");
                    String tin = s.nextLine();
                    app.generateCustomerReport(tin);
                    break;
                case "5": //Add Interest
                    response = app.addInterest();
                    if(response.charAt(0) == '0'){
                        System.out.println("\nInterest added!");
                    }
                    break;
                case "6": //Create Account
                    System.out.println("\nCreating account for:\n0: Returning Customer\n1: New Customer");
                    String createCus = s.nextLine();
                    while(createCus.equals("0") == false && createCus.equals("1") == false){
                        System.out.println("Not a valid option. Enter 0 for returning customers or 1 for new customers:");
                        createCus = s.nextLine();
                    }
                    String createAcc = "";
                    String aid = "";
                    if(createCus.equals("0")) { //Returning Customer
                        System.out.println("\nWhat type of account would you like to create?");
                        System.out.println("0: Checking (Interest)\n1: Checking (Student)\n2: Savings\n3: Pocket\n4: Cancel");
                        createAcc = s.nextLine();
                        while(checkIntegerInput(createAcc) == false){
                            System.out.println("Not a valid amount. Please re-enter:");
                            createAcc = s.nextLine();
                        }
                        while(Integer.parseInt(createAcc) > 4 || Integer.parseInt(createAcc) < 0){
                            System.out.println("Not a valid Option. Please try again.");
                            System.out.println("0: Checking (Interest)\n1: Checking (Student)\n2: Savings\n3: Pocket\n4: Cancel");
                            createAcc = s.nextLine();
                        }

                        if(createAcc.equals("4")){ //Cancel
                            break;
                        }

                        System.out.println("What is the tax id associated with the new account?");
                        tax_id = s.nextLine();
                        System.out.println("Give the newly created account a 5-digit id:");
                        aid = s.nextLine();

                        if(createAcc.equals("0")){ //Interest Checking
                            System.out.println("Give the newly created account an initial starting balance (greater than or equal to $1000):");
                            strAmount = s.nextLine();
                            while(checkAmountInput(strAmount) == false || Double.parseDouble(strAmount) < 1000){
                                System.out.println("Not a valid amount. Please re-enter:");
                                strAmount = s.nextLine();
                            }
                            double amount = Double.parseDouble(strAmount);
                            System.out.println("What is the name associated with the new account?");
                            String name = s.nextLine();
                            System.out.println("What is the address of the customer associated with the new account?");
                            String address = s.nextLine();
                            String r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING , aid, amount, tax_id, name, address);
                            if(r.charAt(0) == '0'){
                                System.out.println("Checking account created successfully!");
                            }
                        } else if (createAcc.equals("1")){ //Student Checking
                            System.out.println("Give the newly created account an initial starting balance (greater than or equal to $1000):");
                            strAmount = s.nextLine();
                            while(checkAmountInput(strAmount) == false || Double.parseDouble(strAmount) < 1000){
                                System.out.println("Not a valid amount. Please re-enter:");
                                strAmount = s.nextLine();
                            }
                            double amount = Double.parseDouble(strAmount);
                            System.out.println("What is the name associated with the new account?");
                            String name = s.nextLine();
                            System.out.println("What is the address of the customer associated with the new account?");
                            String address = s.nextLine();
                            String r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING , aid, amount, tax_id, name, address);
                            if(r.charAt(0) == '0'){
                                System.out.println("Checking account created successfully!");
                            }
                        } else if (createAcc.equals("2")){ //Savings
                            System.out.println("Give the newly created account an initial starting balance (greater than or equal to $1000):");
                            strAmount = s.nextLine();
                            while(checkAmountInput(strAmount) == false || Double.parseDouble(strAmount) < 1000){
                                System.out.println("Not a valid amount. Please re-enter:");
                                strAmount = s.nextLine();
                            }
                            double amount = Double.parseDouble(strAmount);
                            System.out.println("What is the name associated with the new account?");
                            String name = s.nextLine();
                            System.out.println("What is the address of the customer associated with the new account?");
                            String address = s.nextLine();
                            String r = app.createCheckingSavingsAccount( AccountType.SAVINGS , aid, amount, tax_id, name, address);
                            if(r.charAt(0) == '0'){
                                System.out.println("Savings account created successfully!");
                            }
                        } else if (createAcc.equals("3")){ //Pocket
                            System.out.println("What is the checking/savings account that the new pocket account is linked to?");
                            String chSavAcc = s.nextLine();
                            System.out.println("Give the newly created account an initial starting balance (greater than or equal to $1):");
                            strAmount = s.nextLine();
                            while(checkAmountInput(strAmount) == false || Double.parseDouble(strAmount) < 1){
                                System.out.println("Not a valid amount. Please re-enter:");
                                strAmount = s.nextLine();
                            }
                            double amount = Double.parseDouble(strAmount);
                            String r = app.createPocketAccount(aid, chSavAcc, amount, tax_id);
                            if(r.charAt(0) == '0'){
                                System.out.println("Pocket account created successfully!");
                            }
                        }
                    }
                    else { //New Customer (Creates Checking Interest by deafault)
                        System.out.println("\nWhat type of account would you like to create?");
                        System.out.println("0: Checking (Interest)\n1: Checking (Student)\n2: Savings\n3: Cancel");
                        createAcc = s.nextLine();
                        while(checkIntegerInput(createAcc) == false){
                            System.out.println("Not a valid amount. Please re-enter:");
                            createAcc = s.nextLine();
                        }
                        while(Integer.parseInt(createAcc) > 3 || Integer.parseInt(createAcc) < 0){
                            System.out.println("Not a valid Option. Please try again.");
                            System.out.println("0: Checking (Interest)\n1: Checking (Student)\n2: Savings\n3: Cancel");
                            createAcc = s.nextLine();
                        }
                        if(createAcc.equals("3")){ //Cancel
                            break;
                        }

                        System.out.println("Enter the customer's tax id:");
                        tax_id = s.nextLine();
                        System.out.println("Give the newly created account a 5-digit id:");
                        aid = s.nextLine();
                        System.out.println("Give the newly created account an initial starting balance (greater than or equal to $1000):");
                        strAmount = s.nextLine();
                        while(checkAmountInput(strAmount) == false || Double.parseDouble(strAmount) < 1000){
                            System.out.println("Not a valid amount. Please re-enter:");
                            strAmount = s.nextLine();
                        }
                        double amount = Double.parseDouble(strAmount);
                        System.out.println("What is the name associated with the new account?");
                        String name = s.nextLine();
                        System.out.println("What is the address of the customer associated with the new account?");
                        String address = s.nextLine();
                        String r = "";
                        if(createAcc.equals("0")){
                            r = app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING, aid, amount, tax_id, name, address);
                        } else if (createAcc.equals("1")) {
                            r = app.createCheckingSavingsAccount(AccountType.STUDENT_CHECKING, aid, amount, tax_id, name, address);
                        } else {
                            r = app.createCheckingSavingsAccount(AccountType.SAVINGS, aid, amount, tax_id, name, address);
                        }

                        if(r.charAt(0) == '0'){
                            System.out.println("New customer created successfully!");
                        }
                    }
                    break;
                case "7": //Delete Closed Accounts and Customers (Problems: bug, check below; on complete, generate success message, ORA if no closed)
                    app.deleteClosed(); 
                    break;
                    //BUG: SOMETIMES DELETES CUSTOMERS/OWNERS EVEN IF AN ACCOUNT IS STILL ACTIVE:
                    //Example: create 1. a checking interest account (Primary) 2. a savings account 3. a pocket account linked to #1
                    //         wire all money from checking interest to savings (#1 will close)
                    //         collect all money from pocket to savings (#3 will close)
                    //         delete all closed accounts (#1 and #3 should be deleted)
                    //         savings (#2) still active in accounts, but is not listed under owners as customer has been deleted
                    //         customer and owners tables are empty    
                case "8": //Delete Transactions 
                    app.deleteTransactions();
                    break;
                case "9": //Exit Teller App
                    
                    break;
                default:
                    System.out.println("Not a valid transaction number.");
                    break;
            }
        }
    }

        //check to see if inputted amount is valid
    public boolean checkAmountInput(String strAmount){
        try { 
            double d = Double.parseDouble(strAmount); 
            if(d < 0){
                return false;
            }
            return true;
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean checkIntegerInput(String input){
        try { 
            int i = Integer.parseInt(input); 
            if(i < 0){
                return false;
            }
            return true;
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}