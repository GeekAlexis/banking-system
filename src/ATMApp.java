package cs174a;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.lang.Exception;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;

import java.util.Scanner;

import cs174a.App;

public class ATMApp {
    private App app;

    ATMApp(App app) {
        this.app = app;
    }

    public void displayUI(Scanner s, OracleConnection _connection) {
        //Scanner s = new Scanner(System.in);
        // CHECK IF CUSTOMER EXISTS YET OR NOT (EITHER HERE OR IN APP) OR ELSE NO CUSTOMER WILL BE FOUND
        boolean customerExists = false;
        String taxId = "";
        while (customerExists == false) {
            System.out.println("Welcome to the ATM App Interface. Please enter your tax ID: ");
            taxId = s.nextLine();
            if ((app.checkCustomerExists(taxId)).equals("0")) {
                System.out.println("No customer exists with the given tax ID. Try again.");
            } else {
                customerExists = true;
                app.setTaxId(taxId);
            }
        }
        System.out.println("Please enter your PIN: ");
        String pin = s.nextLine();
        
        // implement encryption before entering the pin
        boolean pinVerified = app.verifyPIN(pin);

        //Login Successful
        String getAccounts = "SELECT aid FROM Owners WHERE tax_id = ?";
        ArrayList<String> aidList = new ArrayList<>();
        try(PreparedStatement statement = _connection.prepareStatement(getAccounts)){
            statement.setString(1, taxId);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                aidList.add(rs.getString(1));
            }
        } catch(SQLException e) {
            System.err.println(e.getMessage());
            System.out.println("Could not retreive owner accounts.");
        }
        
        if(pinVerified == true) {
            String choice = "";
            while(choice.equals("9") == false){
                System.out.println("\n---Transaction Options---");
                System.out.println("0: Deposit");
                System.out.println("1: Top-Up");
                System.out.println("2: Withdrawal");
                System.out.println("3: Purchase");
                System.out.println("4: Transfer");
                System.out.println("5: Collect");
                System.out.println("6: Wire");
                System.out.println("7: Pay Friend");
                System.out.println("8: Set PIN\n");
                System.out.println("9: Exit ATM\n");
                System.out.println("Enter the number associated with the transaction type you'd like to make: ");
                choice = s.nextLine();

                String aid = "", response = "", strAmount = "";
                double amount = 0;
                switch(choice) {
                    case "0": //deposit
                        System.out.println("Enter your account ID: ");
                        aid = s.nextLine();
                        while(checkOwnerAccounts(aidList, aid) == false){
                            System.out.println("Not a valid account id. Please re-enter:");
                            aid = s.nextLine();
                        }
                        
                        System.out.println("Enter the amount you'd like to deposit: ");
                        strAmount = s.nextLine();
                        while(checkAmountInput(strAmount) == false){
                            System.out.println("Not a valid amount. Please re-enter:");
                            strAmount = s.nextLine();
                        }
                        amount = Double.parseDouble(strAmount); 

                        response = app.deposit(aid, amount);
                        if(response.charAt(0) == '0'){
                            System.out.println("\nYour deposit was successful!");
                        }
                        break;
                    case "1": //top up 
                        System.out.println("Enter your pocket account ID: ");
                        aid = s.nextLine();
                        while(checkOwnerAccounts(aidList, aid) == false){
                            System.out.println("Not a valid account id. Please re-enter:");
                            aid = s.nextLine();
                        }

                        System.out.println("Enter the amount you would like to move to your pocket account: ");
                        strAmount = s.nextLine();
                        while(checkAmountInput(strAmount) == false){
                            System.out.println("Not a valid amount. Please re-enter:");
                            strAmount = s.nextLine();
                        }
                        amount = Double.parseDouble(strAmount); 

                        response = app.topUp(aid, amount);
                        if(response.charAt(0) == '0'){
                            System.out.println("\nSuccessfully moved to pocket account!");
                        }
                        break;
                    case "2": //withdrawal 
                        System.out.println("Enter your account ID: ");
                        aid = s.nextLine();
                        while(checkOwnerAccounts(aidList, aid) == false){
                            System.out.println("Not a valid account id. Please re-enter:");
                            aid = s.nextLine();
                        }

                        System.out.println("Enter the amount you'd like to withdraw: ");
                        strAmount = s.nextLine();
                        while(checkAmountInput(strAmount) == false){
                            System.out.println("Not a valid amount. Please re-enter:");
                            strAmount = s.nextLine();
                        }
                        amount = Double.parseDouble(strAmount); 
                        
                        response = app.withdrawal(aid, amount);
                        if(response.charAt(0) == '0'){
                            System.out.println("\nWithdrawal successful!");
                        }
                        break;  
                    case "3": //purchase 
                        System.out.println("Enter your pocket account ID: ");
                        aid = s.nextLine();
                        while(checkOwnerAccounts(aidList, aid) == false){
                            System.out.println("Not a valid account id. Please re-enter:");
                            aid = s.nextLine();
                        }

                        System.out.println("Enter the amount you like to spend on purchases: ");
                        strAmount = s.nextLine();
                        while(checkAmountInput(strAmount) == false){
                            System.out.println("Not a valid amount. Please re-enter:");
                            strAmount = s.nextLine();
                        }
                        amount = Double.parseDouble(strAmount); 

                        response = app.purchase(aid, amount);
                        if(response.charAt(0) == '0'){
                            System.out.println("\nSuccessfully purchased!");
                        }
                        break;
                    case "4": //transfer 
                        System.out.println("Enter your account ID: ");
                        aid = s.nextLine();
                        while(checkOwnerAccounts(aidList, aid) == false){
                            System.out.println("Not a valid account id. Please re-enter:");
                            aid = s.nextLine();
                        }

                        System.out.println("Enter the account ID that you would like to transfer to: ");
                        String aid_to = s.nextLine();
                        while(checkOwnerAccounts(aidList, aid_to) == false){
                            System.out.println("Not a valid account id. Please re-enter:");
                            aid_to = s.nextLine();
                        }

                        System.out.println("Enter the amount you'd like to tranfer: ");
                        strAmount = s.nextLine();
                        while(checkAmountInput(strAmount) == false){
                            System.out.println("Not a valid amount. Please re-enter:");
                            strAmount = s.nextLine();
                        }
                        amount = Double.parseDouble(strAmount); 
                        
                        response = app.transfer(aid, aid_to, amount);
                        if(response.charAt(0) == '0'){
                            System.out.println("\nTransfer successful!");
                        }
                        break;
                    case "5": //collect
                        System.out.println("Enter your account ID: ");
                        aid = s.nextLine();
                        while(checkOwnerAccounts(aidList, aid) == false){
                            System.out.println("Not a valid account id. Please re-enter:");
                            aid = s.nextLine();
                        }

                        System.out.println("Enter your pocket account ID: ");
                        String aid_poc = s.nextLine();
                        while(checkOwnerAccounts(aidList, aid_poc) == false){
                            System.out.println("Not a valid account id. Please re-enter:");
                            aid_poc = s.nextLine();
                        }

                        System.out.println("Enter the amount you'd like to collect from your pocket account: ");
                        strAmount = s.nextLine();
                        while(checkAmountInput(strAmount) == false){
                            System.out.println("Not a valid amount. Please re-enter:");
                            strAmount = s.nextLine();
                        }
                        amount = Double.parseDouble(strAmount); 

                        response = app.collect(aid_poc, aid, amount);
                        if(response.charAt(0) == '0'){
                            System.out.println("\nCollection successful!");
                        }

                        break;
                    case "6": //wire 
                        System.out.println("Enter your account ID: ");
                        aid = s.nextLine();
                        while(checkOwnerAccounts(aidList, aid) == false){
                            System.out.println("Not a valid account id. Please re-enter:");
                            aid = s.nextLine();
                        }

                        System.out.println("Enter the account ID of the account you are wiring to: ");
                        String aid_wire = s.nextLine();

                        System.out.println("Enter the amount you'd like to wire: ");
                        strAmount = s.nextLine();
                        while(checkAmountInput(strAmount) == false){
                            System.out.println("Not a valid amount. Please re-enter:");
                            strAmount = s.nextLine();
                        }
                        amount = Double.parseDouble(strAmount); 

                        response = app.wire(aid, aid_wire, amount);
                        if(response.charAt(0) == '0'){
                            System.out.println("\nMoney wire successful!");
                        }
                        break;
                    case "7": //payfriend 
                        System.out.println("Enter your pocket account ID: ");
                        aid = s.nextLine();
                        while(checkOwnerAccounts(aidList, aid) == false){
                            System.out.println("Not a valid account id. Please re-enter:");
                            aid = s.nextLine();
                        }

                        System.out.println("Enter the pocket account that you would like to pay to:");
                        String aid_payfriend = s.nextLine();

                        System.out.println("Enter the amount you like to pay: ");
                        strAmount = s.nextLine();
                        while(checkAmountInput(strAmount) == false){
                            System.out.println("Not a valid amount. Please re-enter:");
                            strAmount = s.nextLine();
                        }
                        amount = Double.parseDouble(strAmount); 

                        response = app.payFriend(aid, aid_payfriend, amount);
                        if(response.charAt(0) == '0'){
                            System.out.println("\nPaid succesfully!");
                        }
                        break;
                    case "8": //setPin 
                        System.out.println("Setting a new PIN");
                        System.out.print("Enter your old PIN: ");
                        String oldPin = s.nextLine();
                        System.out.print("Enter your new PIN: ");
                        String newPin = s.nextLine();
                        app.setPIN(oldPin, newPin);
                        break;
                    case "9": //exit
                        break;
                    default:
                        System.out.println("Not a valid transaction number.");
                        break;
                }
            }
        } else {
            System.out.println("Incorrect PIN.");
        }
    }

    //check to see if aid is within the queried list of account ids under the user
    public boolean checkOwnerAccounts(ArrayList<String> aidList, String aid){
        for(String str : aidList){
            if(aid.equals(str)){
                return true;
            }
        }
        return false;
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
}