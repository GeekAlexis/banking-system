package cs174a;                                             // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// You may have as many imports as you need.
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.lang.Exception;
import java.sql.Statement;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;

import java.util.TreeMap;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Comparator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;


/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable
{
	private OracleConnection _connection;                   // Example connection object to your DB.
	private ATMApp atmApp;
	private BankTeller bankTeller;
	private String taxId;

	/**
	 * Default constructor.
	 * DO NOT REMOVE.
	 */
	App()
	{
		atmApp = new ATMApp(this);
		bankTeller = new BankTeller(this);
		setUpEncryption();
	}

	/**
	 * This is an example access operation to the DB.
	 */
	void exampleAccessToDB()
	{
		// Statement and ResultSet are AutoCloseable and closed automatically.
		try( Statement statement = _connection.createStatement() )
		{
			try( ResultSet resultSet = statement.executeQuery( "select * from user_tables" ) )
			{
				while( resultSet.next() )
					System.out.println( resultSet.getString( 1 ) + " " + resultSet.getString( 2 ) + " " );
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
		}
	}

	////////////////////////////// Implement all of the methods given in the interface /////////////////////////////////
	// Check the Testable.java interface for the function signatures and descriptions.

	@Override
	public String initializeSystem()
	{
		// Some constants to connect to your DB.
		final String DB_URL = "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";
		final String DB_USER = "c##shidasheng";
		final String DB_PASSWORD = "4050415";

		// Initialize your system.  Probably setting up the DB connection.
		Properties info = new Properties();
		info.put( OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER );
		info.put( OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD );
		info.put( OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20" );

		try
		{
			OracleDataSource ods = new OracleDataSource();
			ods.setURL( DB_URL );
			ods.setConnectionProperties( info );
			_connection = (OracleConnection) ods.getConnection();

			// Get the JDBC driver name and version.
			DatabaseMetaData dbmd = _connection.getMetaData();
			System.out.println( "Driver Name: " + dbmd.getDriverName() );
			System.out.println( "Driver Version: " + dbmd.getDriverVersion() );

			// Print some connection properties.
			System.out.println( "Default Row Prefetch Value is: " + _connection.getDefaultRowPrefetch() );
			System.out.println( "Database Username is: " + _connection.getUserName() );
			System.out.println();

			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	public void setUpUI() {
		Scanner s = new Scanner(System.in);
		String choice = "";

		while(!choice.equals("3")) {
			System.out.println("Welcome to your virtual account management system.\n");
			System.out.println("0: ATM App\n1: Bank Teller\n2: Set Date\n3: Set New Interest Rate\n4: Exit");
			System.out.println("Enter the number associated with the action you'd like to take: ");
			choice = s.nextLine();

			if(choice.equals("0")) {
				if(checkDate() == false) {
					System.out.println("No date set yet. Please set a date first.");
					continue;
				}
				atmApp.displayUI(s, _connection);
			} else if (choice.equals("1")) {
				if(checkDate() == false) {
					System.out.println("No date set yet. Please set a date first.");
					continue;
				}
				bankTeller.displayUI(s);
			} else if (choice.equals("2")){
				System.out.println("Setting a New Date\n");
				System.out.println("Enter the year: ");
				String year = s.nextLine();
				System.out.println("Enter the month: ");
				String month = s.nextLine();
				System.out.println("Enter the day: ");
				String day = s.nextLine();
				setDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
			} else if (choice.equals("3")){
				System.out.println("Setting a New Interest Rate\n");
				System.out.println("Enter the new rate: ");
				String rate = s.nextLine();
				double interest = Double.parseDouble(rate);
				System.out.println("Which type of account would you like to set the new interest rate for?");
				System.out.println("0: Student Checking\n1: Interest Checking\n2: Savings\n3: Pocket");
				String accChoice = s.nextLine();
				switch(accChoice) {
					case "0":
						setNewInterest(interest, "student");
						break;
					case "1":
						setNewInterest(interest, "interest");
						break;
					case "2":
						setNewInterest(interest, "savings");
						break;
					case "3":
						setNewInterest(interest, "pocket");
						break;
					default:
						System.out.println("Not a valid choice.");
						break;
				}
			} else {
				break;
			}
		}
		s.close();
	}

	@Override
	public String dropTables()
	{
		try( Statement statement = _connection.createStatement() )
		{
			statement.executeUpdate( "DROP TABLE BankDate" );
			statement.executeUpdate( "DROP TABLE Involves" );
			statement.executeUpdate( "DROP TABLE Owners" );
			statement.executeUpdate( "DROP TABLE Primary" );
			statement.executeUpdate( "DROP TABLE LinkedTo" );
			statement.executeUpdate( "DROP TABLE Accounts" );
			statement.executeUpdate( "DROP TABLE Customers" );
			statement.executeUpdate( "DROP TABLE Transactions" );
			statement.executeUpdate( "DROP SEQUENCE transId_seq" );
			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	@Override
	public String createTables()
	{
		String createDate = "CREATE TABLE BankDate(" +
							"d DATE, " +
							"PRIMARY KEY(d))";

		String createAccounts = "CREATE TABLE Accounts (" +
								"aid VARCHAR(5), " +
								"branch VARCHAR(15) DEFAULT \'Los Angeles\', " +
								"init_balance DECIMAL(15, 2), " +
								"curr_balance DECIMAL(15, 2), " +
								"interest DECIMAL(4, 2), " +
								"active INTEGER, " +
								"type VARCHAR(15), " +
								"PRIMARY KEY(aid) )";

		String createCustomers = "CREATE TABLE Customers (" +
								"tax_id VARCHAR(9), " +
								"aid VARCHAR(5), " +
								"name VARCHAR(20), " +
								"address VARCHAR(50), " +
								"pin VARCHAR(64) DEFAULT '33dc0ba86008f4434bd43d050df9022209367483c1eef5280b25da861c32f6ad'," +
								"PRIMARY KEY (tax_id) )";

		String createTransactions = "CREATE TABLE Transactions (" +
									"tid INTEGER, " +
									"d DATE, " +
									"amount DECIMAL(15,2), " +
									"type VARCHAR(20), " +
									"fee DECIMAL(15,2), " +
									"check_no INTEGER DEFAULT 0, " +
									"avg_daily_balance DECIMAL(15,2), " +
									"PRIMARY KEY (tid) )";

		String createInvolves = "CREATE TABLE Involves (" +
								"tid INTEGER, " +
								"aid_to VARCHAR(5), " +
								"aid_from VARCHAR(5), " +
								"PRIMARY KEY (tid, aid_to, aid_from), " +
								"FOREIGN KEY (tid) REFERENCES Transactions(tid) " +
								"ON DELETE CASCADE )";

		String createOwners = "CREATE TABLE Owners (" +
								"tax_id VARCHAR(9), " +
								"aid VARCHAR(5), " +
								"PRIMARY KEY (tax_id, aid), " +
								"FOREIGN KEY (tax_id) REFERENCES Customers(tax_id) " +
								"ON DELETE CASCADE, " +
								"FOREIGN KEY (aid) REFERENCES Accounts(aid) " +
								"ON DELETE CASCADE )";

		String createPrimary = "CREATE TABLE Primary (" +
								"tax_id VARCHAR(9), " +
								"aid VARCHAR(5), " +
								"PRIMARY KEY (tax_id, aid), " +
								"FOREIGN KEY (aid) REFERENCES Accounts(aid) " +
								"ON DELETE CASCADE, " +
								"FOREIGN KEY (tax_id) REFERENCES Customers(tax_id) " +
								"ON DELETE CASCADE )";

		String createLinkedTo = "CREATE TABLE LinkedTo (" +
								"aid_main VARCHAR(5), " +
								"aid_pocket VARCHAR(5), " +
								"PRIMARY KEY (aid_main, aid_pocket), " +
								"FOREIGN KEY (aid_main) REFERENCES Accounts(aid) " +
								"ON DELETE CASCADE, " +
								"FOREIGN KEY (aid_pocket) REFERENCES Accounts(aid) " +
								"ON DELETE CASCADE )";

		String createSequence = "CREATE SEQUENCE transId_seq "+
								"START WITH 1 " +
								"INCREMENT BY 1";

		try( Statement statement = _connection.createStatement() )
		{
			statement.executeUpdate( createDate );
			statement.executeUpdate( createAccounts );
			statement.executeUpdate( createCustomers );
			statement.executeUpdate( createTransactions );
			statement.executeUpdate( createInvolves );
			statement.executeUpdate( createOwners );
			statement.executeUpdate( createPrimary );
			statement.executeUpdate( createLinkedTo );
			statement.executeUpdate( createSequence );
			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	@Override
	public String setDate( int year, int month, int day )
	{
		Calendar c = Calendar.getInstance();
		String newDate = Integer.toString(year) + "-" + Integer.toString(month)  + "-" + Integer.toString(day);
		c.setLenient(false);
		c.set(year, month-1, day);
		if(Integer.toString(year).length() > 4) {
			System.out.println("Invalid date entered");
			return "1 "+newDate;
		}

		try {
			c.getTime();
		}
		catch (Exception e) {
			System.out.println("Invalid date entered");
			return "1 "+newDate;
		}

		try( Statement statement = _connection.createStatement())
		{
			statement.executeUpdate("DELETE FROM BankDate");
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1 "+newDate;
		}

		String query = "INSERT INTO BankDate(d) " +
						"VALUES (TO_DATE(?, \'YYYY-MM-DD\')) ";
		try( PreparedStatement prepStatement = _connection.prepareStatement(query))
		{
			prepStatement.setString(1, newDate);
			prepStatement.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1 "+newDate;
		}

		return "0 "+newDate;
	}

	public String getDateInfo() {
		String bankDate = "";
		try (Statement statement = _connection.createStatement()) {
			ResultSet rs = statement.executeQuery("SELECT D.d FROM BankDate D");
			while(rs.next()) {
				bankDate = rs.getString(1);
				String splitDateTime[] = bankDate.split(" ");
				return splitDateTime[0];
			}
		} catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error with getting date.");
			return "1";
		}
		return bankDate;
	}

	public boolean checkDate() {
		String query = "SELECT COUNT(*) FROM BankDate";
		try (Statement s = _connection.createStatement() ) {
			ResultSet rs = s.executeQuery(query);
			int count = -1;
			while(rs.next()) {
				count = rs.getInt(1);
			}
			if(count == 0) {
				return false;
			} else if (count == 1) {
				return true;
			}
		} catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return false;
		}
		return false;
	}

	@Override
	public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
	{
		if (Double.compare(initialBalance, 1000) < 0 || Double.compare(initialBalance, 0) < 0) {
			return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
		} else {
			String createAccount = "INSERT INTO Accounts(aid, init_balance, curr_balance, interest, active, type) " +
									"VALUES (?, ?, ?, ?, ?, ?) ";
			try( PreparedStatement prepStatement2 = _connection.prepareStatement(createAccount))
			{
				prepStatement2.setString(1, id);
				prepStatement2.setDouble(2, initialBalance);
				prepStatement2.setDouble(3, 0);
				if(accountType == AccountType.STUDENT_CHECKING) {
					prepStatement2.setDouble(4, 0.00);
					prepStatement2.setString(6, "student");
				} else if (accountType == AccountType.INTEREST_CHECKING) {
					prepStatement2.setDouble(4, 3.00);
					prepStatement2.setString(6, "interest");
				} else {
					prepStatement2.setDouble(4, 4.80);
					prepStatement2.setString(6, "savings");
				}
				prepStatement2.setInt(5, 1);
				prepStatement2.executeUpdate();
			}
			catch( SQLException e )
			{
				System.err.println( e.getMessage() );
				return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
			}

			// Check if exists
			String checkError = "0";
			String checkCust = checkCustomerExists(tin);
			if(checkCust.equals("0")) {
				checkError = createCustomer(id, tin, name, address);
			}

			if(checkError.equals("1")) {
				return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
			}

			if(checkCust.equals("-1")) {
				return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
			}

			// ONLY DO THIS IF NOT NEW CUSTOMER (or else will be double createOwners in createCustomer and here)
			String checkOwnership = checkIfOwnerExists(tin, id);
			if(checkOwnership.equals("0")) {
				String check = createOwners(tin, id);
				if(check.equals("1")) {
					return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
				}
			}
			if(checkOwnership.equals("-1")) {
				return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
			}

			checkError = createPrimary(tin, id);

			if(checkError.equals("1")) {
				return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
			}

			String checkTrans = createTransaction("deposit", initialBalance, 0.00, 0, 0.00, id, id, initialBalance, initialBalance);
			if(checkTrans.equals("1")) {
				return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
			}
			return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
		}
	}

	@Override
	public String createPocketAccount( String id, String linkedId, double initialTopUp, String tin )
	{
		if(initialTopUp <= 0.01) {
			System.out.println("Pocket account must have at least $0.02. Pocket account creation failed");
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
		}

		boolean checkAccount = checkClosed(linkedId);
		if(checkAccount == true) {
			System.out.println("Attempting to link pocket account to a closed account. Pocket account creation failed.");
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
		}

		String checkCust = checkCustomerExists(tin);
			if(checkCust.equals("0")) {
				System.out.println("Customer with the given tax ID does not exist.");
				return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
			}

		// Check if customer already has the savings or checkings account
		ArrayList<String> ownedAccs = new ArrayList<String>();
		String findCustomer = "SELECT O.aid FROM Owners O WHERE O.tax_id = ?";
		try( PreparedStatement prepStatement3 = _connection.prepareStatement(findCustomer))
		{
			prepStatement3.setString(1, tin);
			ResultSet rs = prepStatement3.executeQuery();
			while(rs.next()) {
				ownedAccs.add(rs.getString(1));
			}
			if (ownedAccs.contains(linkedId) == false) {
				System.out.println("The given account is not owned by the customer with the given ID.");
				return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
		}

		double linkedBalance = getBalance(linkedId);
		if(Double.compare(linkedBalance, initialTopUp+5.02) < 0) {
			System.out.println("Linked account does not have sufficient funds to create pocket account.");
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
		}

		String createAccount = "INSERT INTO Accounts(aid, init_balance, curr_balance, interest, active, type) " +
								"VALUES (?, ?, ?, ?, ?, ?) ";
		try( PreparedStatement prepStatement2 = _connection.prepareStatement(createAccount))
		{
			prepStatement2.setString(1, id);
			prepStatement2.setDouble(2, initialTopUp); // OR SHOULD WE SET IT AS 0 AND ADD STATEMENT TO UPDATE LIKE CURR BALANCE
			prepStatement2.setDouble(3, 0.00);
			prepStatement2.setDouble(4, 0.00);
			prepStatement2.setInt(5, 1);
			prepStatement2.setString(6, "pocket");
			prepStatement2.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
		}

		String createLinkedTo = "INSERT INTO LinkedTo(aid_main, aid_pocket) " +
								"VALUES (?, ?) ";
		try( PreparedStatement prepStatement = _connection.prepareStatement(createLinkedTo))
		{
			prepStatement.setString(1, linkedId);
			prepStatement.setString(2, id);
			prepStatement.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
		}

		String checkError = createPrimary(tin, id);

		if(checkError.equals("1")) {
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
		}

		checkError = createOwners(tin, id);

		if(checkError.equals("1")) {
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
		}

		checkError = topUp(id, initialTopUp);
		if(Character.toString(checkError.charAt(0)).equals("1")) {
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
		}

		return "0 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
	}

	@Override
	public String createCustomer( String accountId, String tin, String name, String address )
	{
		boolean checkAccount = checkClosed(accountId);
		if(checkAccount == true) {
			System.out.println("Attempting to link customer to a closed account. Customer creation failed.");
			return "1";
		}

		String checkForCust = checkCustomerExists(tin);
		if(checkForCust.equals("1")) {
			System.out.println("Customer with given tax ID already exists.");
			return "1";
		}

		String checkForAcc = "SELECT COUNT(*) FROM Accounts A WHERE A.aid = ?";
		try( PreparedStatement checkAccExists = _connection.prepareStatement(checkForAcc))
		{
			checkAccExists.setString(1, accountId);
			ResultSet rs2 = checkAccExists.executeQuery();
			while(rs2.next()) {
				if(rs2.getInt(1) == 0) {
					System.out.println("Account doesn't exist.");
					return "1";
				}
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		String createCustomer = "INSERT INTO Customers(tax_id, aid, name, address, pin) " +
								"VALUES (?, ?, ?, ?, ?) ";
		try( PreparedStatement prepStatement = _connection.prepareStatement(createCustomer))
		{
			prepStatement.setString(1, tin);
			prepStatement.setString(2, accountId);
			prepStatement.setString(3, name);
			prepStatement.setString(4, address);
			String pin = encrypt("1717");
			prepStatement.setString(5, pin);
			prepStatement.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		String check = createOwners(tin, accountId);
		if(check.equals("1")) {
			return "1";
		}

		return "0";
	}

	public String createOwners(String tin, String accountId) {
		// Check if trying to add owner to a pocket account
		String checkPrimary = "SELECT COUNT(*) FROM Owners O WHERE O.aid = ? AND O.aid IN " +
								"(SELECT A.aid FROM Accounts A WHERE A.type = \'pocket\')";
		try( PreparedStatement prepStatement = _connection.prepareStatement(checkPrimary))
		{
			prepStatement.setString(1, accountId);
			ResultSet rs = prepStatement.executeQuery();
			while(rs.next()) {
				if(rs.getInt(1) > 0) {
					System.out.println("Pocket account cannot have more than one owner.");
					return "1";
				}
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not create primary owner.");
			return "1";
		}

		String createOwners = "INSERT INTO Owners(tax_id, aid) " +
							"VALUES (?, ?) ";
		try( PreparedStatement prepStatement2 = _connection.prepareStatement(createOwners))
		{
			prepStatement2.setString(1, tin);
			prepStatement2.setString(2, accountId);
			prepStatement2.executeUpdate();
			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not create owner.");
			return "1";
		}
	}

	public String createPrimary(String tin, String id) {
		String createPrimary = "INSERT INTO Primary(tax_id, aid) " +
								"VALUES (?, ?) ";
		try( PreparedStatement prepStatement3 = _connection.prepareStatement(createPrimary))
		{
			prepStatement3.setString(1, tin);
			prepStatement3.setString(2, id);
			prepStatement3.executeUpdate();
			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not create primary owner.");
			return "1";
		}
	}

	public String createTransaction( String type, double amount, double fee, int check_no, double avg_daily_balance, String aid_to, String aid_from, double newTo, double newFrom) {
		String dateInfo = getDateInfo();

		boolean checkClosed = checkClosed(aid_to);
		if(checkClosed) {
			System.out.println("An account involved in the transaction is already closed. Transaction failed.");
			return "1";
		}
		checkClosed = checkClosed(aid_from);
		if(checkClosed) {
			System.out.println("An account involved in the transaction is already closed. Transaction failed.");
			return "1";
		}

		String createTransactions = "INSERT INTO Transactions(tid, d, amount, type, fee, check_no, avg_daily_balance) " +
									"VALUES (transId_seq.nextval, TO_DATE(?, \'YYYY-MM-DD\'), ?, ?, ?, ?, ?) ";
		try( PreparedStatement prepStatement3 = _connection.prepareStatement(createTransactions))
		{
			prepStatement3.setString(1, dateInfo);
			prepStatement3.setDouble(2, amount);
			prepStatement3.setString(3, type);
			prepStatement3.setDouble(4, fee);
			prepStatement3.setInt(5, check_no);
			prepStatement3.setDouble(6, avg_daily_balance);
			prepStatement3.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		if(type.equals("addInterest")) {
			return "0";
		}

		String createInvolves = "INSERT INTO Involves(aid_to, aid_from, tid) " +
								"VALUES (?, ?, transId_seq.currval) ";
		try( PreparedStatement prepStatement3 = _connection.prepareStatement(createInvolves))
		{
			prepStatement3.setString(1, aid_to);
			prepStatement3.setString(2, aid_from);
			prepStatement3.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		String check = updateBalance(newTo, aid_to);

		if(check.equals("1")) {
			return "1";
		}

		// Check if account should be closed
		boolean shouldToClose = checkAccountBalance(getBalance(aid_to));

		if(shouldToClose == true) {
			closeAccount(aid_to);
		}

		if(aid_from.equals(aid_to) == false) {
			String check2 = updateBalance(newFrom, aid_from);
			if(check2.equals("1")) {
				return "1";
			}

			boolean shouldFromClose = checkAccountBalance(getBalance(aid_from));

			if(shouldFromClose == true) {
				closeAccount(aid_from);
			}
		}

		return "0";
	}

	public String chargeFee(String id, double amount) {
		double balance = 0.00;
		balance = getBalance(id);

		balance = balance - amount;

		if(Double.compare(balance, 0.00) >= 0) {
			String isValid = createTransaction("fee", amount, 0.00, 0, 0.00, id, id, balance, balance);
			if(isValid.equals("1")) {
				System.out.println("Charging fee failed.");
				return "1";
			}
			return "0";
		} else {
			System.out.println("Error with charge fee");
			return "1";
		}
	}

	@Override
	public String deposit( String accountId, double amount )
	{
		double oldBalance = 0;
		double newBalance = 0;

		boolean checkType = isCheckingOrSavings(accountId);

		if (checkType == false) {
			System.out.println("The involved account must be a checking/savings account.");
			return "1 " + oldBalance + " " + newBalance;
		}

		oldBalance = getBalance(accountId);
		if(Double.compare(oldBalance, -1.00) == 0) {
			return "1 " + oldBalance + " " + newBalance;
		}

		newBalance = oldBalance + amount;

		if(Double.compare(newBalance, 0.00) >= 0) {
			String isValid = createTransaction("deposit", amount, 0.00, 0, 0.00, accountId, accountId, newBalance, newBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + oldBalance + " " + newBalance;
			}
		} else {
			return "1 " + oldBalance + " " + newBalance;
		}

		return "0 " + oldBalance + " " + newBalance;
	}

	@Override
	public String showBalance( String accountId )
	{
		double balance = 0;
		// check if account exists
		String checkAcc = "SELECT COUNT(*) FROM Accounts A WHERE A.aid = ?";
		try(PreparedStatement statement = _connection.prepareStatement(checkAcc)) {
			statement.setString(1, accountId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				if(rs.getInt(1) == 0) {
					System.out.println("Could not retrieve balance");
					return "1 " + balance;
				}
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not retrieve balance");
			return "1 " + balance;
		}

		balance = getBalance(accountId);
		if(Double.compare(balance, -1.00) == 0) {
			System.out.println("Could not retrieve balance.");
			return "1 " + balance;
		} else {
			System.out.println(balance);
			return "0 " + balance;
		}
	}

	public double getBalance(String accountId) {
		String getBalance = "SELECT A.curr_balance FROM Accounts A WHERE A.aid = ?";
		double balance = 0.00;
		try(PreparedStatement statement = _connection.prepareStatement(getBalance)) {
			statement.setString(1, accountId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				balance = rs.getDouble(1);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not retrieve balance");
			return -1.00;
		}
		return balance;
	}

	public String updateBalance(double balance, String aid) {
		String updBalance = "UPDATE Accounts SET curr_balance = ? WHERE aid = ?";
		try(PreparedStatement updateBalance = _connection.prepareStatement(updBalance)) {
			updateBalance.setDouble(1, balance);
			updateBalance.setString(2, aid);
			updateBalance.executeUpdate();
			return "0";
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not update balance.");
			return "1";
		}
	}

	@Override
	public String topUp( String accountId, double amount )
	{
		double pocketOldBalance = 0;
		double mainOldBalance = 0;
		String main_id = "";
		double linkedNewBalance = 0;
		double pocketNewBalance = 0;

		// check if first transaction of the month for the pocket account
		String isFirst = checkFirstOfMonth(accountId);
		if(isFirst.equals("-1")) {
			return "1 " + linkedNewBalance + " " + pocketNewBalance;
		}

		String getMain = "SELECT L.aid_main FROM LinkedTo L WHERE L.aid_pocket = ?";
		try( PreparedStatement mainStatement = _connection.prepareStatement(getMain) ) {
			mainStatement.setString(1, accountId);
			ResultSet rs_main = mainStatement.executeQuery();
			while(rs_main.next()) {
				main_id = rs_main.getString(1);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not find linked account ID.");
			return "1 " + linkedNewBalance + " " + pocketNewBalance;
		}

		boolean checkType = checkType(accountId, "pocket");
		boolean checkType2 = isCheckingOrSavings(main_id);

		if (checkType == false || checkType2 == false) {
			System.out.println("The involved accounts must be a pocket account and a checking/savings account.");
			return "1 " + linkedNewBalance + " " + pocketNewBalance;
		}

		pocketOldBalance = getBalance(accountId);
		mainOldBalance = getBalance(main_id);
		if(Double.compare(pocketOldBalance, -1.00) == 0 || Double.compare(mainOldBalance, -1.00) == 0) {
			return "1 " + linkedNewBalance + " " + pocketNewBalance;
		}

		linkedNewBalance = mainOldBalance - amount;
		pocketNewBalance = pocketOldBalance + amount;

		if(Double.compare(linkedNewBalance, 0.00) >= 0 && Double.compare(pocketNewBalance, 0.00) >= 0) {
			String isValid = "";
			String check = "";
			String checkFunds = "";
			if(isFirst.equals("1")) {
				checkFunds = checkSufficientFunds(main_id, linkedNewBalance, 5);
				if(checkFunds.equals("1")) {
					return "1 " + linkedNewBalance + " " + pocketNewBalance;
				}
				isValid = createTransaction("topUp", amount, 5.00, 0, 0.00, accountId, main_id, pocketNewBalance, linkedNewBalance);
				check = chargeFee(main_id, 5.00);
				if(check.equals("1")) {
					return "1 " + linkedNewBalance + " " + pocketNewBalance;
				}
			} else {
				isValid = createTransaction("topUp", amount, 0.00, 0, 0.00, accountId, main_id, pocketNewBalance, linkedNewBalance);
			}
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + linkedNewBalance + " " + pocketNewBalance;
			}
		} else {
			System.out.println("Amount requested is larger than the available balance.");
			return "1 " + linkedNewBalance + " " + pocketNewBalance;
		}

		return "0 " + linkedNewBalance + " " + pocketNewBalance;
	}

	@Override
	public String payFriend( String from, String to, double amount )
	{
		double fromOldBalance = 0;
		double toOldBalance = 0;
		double fromNewBalance = 0;
		double toNewBalance = 0;
		boolean checkType = checkType(from, "pocket");
		boolean checkType2 = checkType(to, "pocket");
		String isFirst = checkFirstOfMonth(from);
		String isFirst2 = checkFirstOfMonth(to);
		if(isFirst.equals("-1") || isFirst2.equals("-1")) {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		if (checkType == false || checkType2 == false) {
			System.out.println("One or more involved accounts are not pocket accounts.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		String checkSameOwner = checkSameOwner(from, to);
		if(checkSameOwner.equals("1")) {
			System.out.println("Cannot pay money to an account with a mutual owner.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}
		if(checkSameOwner.equals("-1")) {
			System.out.println("Error in checking if accounts have a mutual owner.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		fromOldBalance = getBalance(from);
		toOldBalance = getBalance(to);

		if(Double.compare(fromOldBalance, -1.00) == 0 || Double.compare(toOldBalance, -1.00) == 0) {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		fromNewBalance = fromOldBalance - amount;
		toNewBalance = toOldBalance + amount;

		if(Double.compare(fromNewBalance, 0.00) >= 0 && Double.compare(toNewBalance, 0.00) >= 0) {
			String isValid = "";
			String checkFunds = "";
			if(isFirst.equals("1") || isFirst2.equals("1")) {
				String check = "";
				if(isFirst.equals("1")) {
					checkFunds = checkSufficientFunds(from, fromNewBalance, 5.00);
					if(checkFunds.equals("1")) {
						return "1 " + fromNewBalance + " " + toNewBalance;
					}
					check = chargeFee(from, 5.00);
				}
				if(check.equals("1")) {
					return "1 " + fromNewBalance + " " + toNewBalance;
				}
				if(isFirst2.equals("1")) {
					checkFunds = checkSufficientFunds(to, toNewBalance, 5.00);
					if(checkFunds.equals("1")) {
						return "1 " + fromNewBalance + " " + toNewBalance;
					}
					check = chargeFee(to, 5.00);
				}
				if(check.equals("1")) {
					return "1 " + fromNewBalance + " " + toNewBalance;
				}
				isValid = createTransaction("payFriend", amount, 5.00, 0, 0.00, to, from, toNewBalance, fromNewBalance);

			} else {
				isValid = createTransaction("payFriend", amount, 0.00, 0, 0.00, to, from, toNewBalance, fromNewBalance);
			}
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + fromNewBalance + " " + toNewBalance;
			}
		} else {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		return "0 " + fromNewBalance + " " + toNewBalance;
	}

	@Override
	public String listClosedAccounts()
	{
		String getClosed = "SELECT A.aid FROM Accounts A WHERE A.active = 0";
		try(Statement statement = _connection.createStatement()) {
			ResultSet rs = statement.executeQuery(getClosed);
			ArrayList<String> closedIds = new ArrayList<String>();
			while(rs.next()) {
				closedIds.add(rs.getString("aid"));
			}
			String closedAccs = "";
			String printClosed = "";
			if(closedIds.size() == 0) {
				System.out.println("No accounts are closed.");
				return "0" + closedAccs;
			}
			for(int i = 0; i < closedIds.size(); i++) {
				closedAccs = closedAccs + " " + closedIds.get(i);
				printClosed = printClosed + closedIds.get(i) + "\n";
			}
			System.out.println("Closed Accounts:");
			System.out.print(printClosed);

			return "0" + closedAccs;
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	public String checkSufficientFunds(String accountId, double balance, double fee) {
		double newVal = balance - fee;
		if(Double.compare(newVal, 0.00) >= 0) {
			return "0";
		} else {
			System.out.println("Insufficient funds to complete transaction.");
			return "1";
		}
	}

	public boolean checkType(String accountId, String type) {
		String typeCheck = "SELECT A.type FROM Accounts A WHERE A.aid = ?";
		try(PreparedStatement statement = _connection.prepareStatement(typeCheck)) {
			statement.setString(1, accountId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				if((rs.getString(1)).equals(type)) {
					return true;
				}
			}
			return false;
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return false;
		}
	}

	public String checkFirstOfMonth(String accountId) {
		String checkTransactions = "SELECT COUNT(*) FROM Involves I WHERE I.aid_to = ? OR I.aid_from = ?";
		try( PreparedStatement check = _connection.prepareStatement(checkTransactions) ) {
			check.setString(1, accountId);
			check.setString(2, accountId);
			ResultSet rs = check.executeQuery();
			while(rs.next()) {
				if(rs.getInt(1) == 0) {
					return "1";
				}
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error in checking if first transaction of the month.");
			return "-1";
		}
		return "0";
	}

	public String checkIfOwnerExists(String tin, String id) {
		String findOwner = "SELECT * FROM Owners O WHERE O.tax_id = ? AND O.aid = ?";
		try( PreparedStatement prepStatement = _connection.prepareStatement(findOwner))
		{
			prepStatement.setString(1, tin);
			prepStatement.setString(2, id);
			ResultSet rs = prepStatement.executeQuery();
			// if owner doesn't exist yet
			if(rs.next() == false) {
				return "0";
			} else {
				return "1";
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error with checking owner existence.");
			return "-1";
		}
	}

	public boolean isOwner(String accountId) {
		String checkOwner = "";
		String check = "SELECT O.tax_id FROM Owners O WHERE O.aid = ?";
		try(PreparedStatement statement = _connection.prepareStatement(check)) {
			statement.setString(1, accountId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				checkOwner = rs.getString("tax_id");
				if(checkOwner.equals(this.taxId)) {
					return true;
				}
			}
			return false;
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return false;
		}
	}

	public String checkCustomerExists(String tin) {
		String findCustomer = "SELECT * FROM Customers C WHERE C.tax_id = ?";
		try( PreparedStatement prepStatement = _connection.prepareStatement(findCustomer))
		{
			prepStatement.setString(1, tin);
			ResultSet rs = prepStatement.executeQuery();
			// if customer doesn't exist yet
			if(rs.next() == false) {
				return "0";
			} else {
				return "1";
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error with checking customer existence.");
			return "-1";
		}
	}

	public boolean isCheckingOrSavings(String accountId) {
		String typeCheck = "SELECT A.type FROM Accounts A WHERE A.aid = ?";
		try(PreparedStatement statement = _connection.prepareStatement(typeCheck)) {
			statement.setString(1, accountId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				if((rs.getString(1)).equals("pocket") == false) {
					return true;
				}
			}
			return false;
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return false;
		}
	}

	// use to check if performing transaction on a closed account
	public boolean checkClosed(String accountId) {
		String getClosed = "SELECT A.aid FROM Accounts A WHERE A.active = 0";
		try(Statement statement = _connection.createStatement()) {
			ResultSet rs = statement.executeQuery(getClosed);
			ArrayList<String> closedIds = new ArrayList<String>();
			while(rs.next()) {
				if(accountId.equals(rs.getString(1))) {
					return true;
				}
			}
			return false;
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return true;
		}
	}

	// return true if needs to be closed; false if okay
	public boolean checkAccountBalance(double balance) {
		if(Double.compare(balance, 0.01) == 0 || Double.compare(balance, 0.00) == 0) {
			System.out.println("Account balance less than $0.02. Account should close.");
			return true;
		} else {
			return false;
		}
	}

	public void closePockets(String accountId) {
		ArrayList<String> pocketAccs = new ArrayList<String>();

		String getPockets = "SELECT L.aid_pocket FROM LinkedTo L WHERE L.aid_main = ?";
		try(PreparedStatement pocketStatement = _connection.prepareStatement(getPockets)) {
			pocketStatement.setString(1, accountId);
			ResultSet rs = pocketStatement.executeQuery();
			while(rs.next()) {
				pocketAccs.add(rs.getString(1));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not find pocket accounts.");
		}

		// If no pocket accounts are linked
		if(pocketAccs.size() == 0) {
			return;
		}

		String pocketList = makeQueryList(pocketAccs);

		String updActive = "UPDATE Accounts SET active = 0 WHERE aid IN "+pocketList;
		try(Statement updateActive = _connection.createStatement()) {
			updateActive.executeUpdate(updActive);
			System.out.println("Closed pocket account.");
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not close pocket account.");
		}
	}

	public void closeAccount(String accountId) {
		String updActive = "UPDATE Accounts SET active = 0 WHERE aid = ?";
		try(PreparedStatement updateActive = _connection.prepareStatement(updActive)) {
			updateActive.setString(1, accountId);
			updateActive.executeUpdate();
			System.out.println("Closed account.");
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not close account.");
		}
		closePockets(accountId);
	}

	public String withdrawal(String accountId, double amount) {
		double oldBalance = 0;
		double newBalance = 0;
		boolean shouldClose = false;

		boolean checkType = isCheckingOrSavings(accountId);

		if (checkType == false) {
			System.out.println("The involved account must be a checking/savings account.");
			return "1 " + oldBalance + " " + newBalance;
		}

		oldBalance = getBalance(accountId);
		if(Double.compare(oldBalance, -1.00) == 0) {
			return "1 " + oldBalance + " " + newBalance;
		}

		newBalance = oldBalance - amount;

		if(Double.compare(newBalance, 0.00) >= 0) {
			String isValid = createTransaction("withdrawal", amount, 0.00, 0, 0.00, accountId, accountId, newBalance, newBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + oldBalance + " " + newBalance;
			}
		} else {
			System.out.println("Amount requested is larger than the available balance.");
			return "1 " + oldBalance + " " + newBalance;
		}

		return "0 " + oldBalance + " " + newBalance;
	}

	public String purchase(String accountId, double amount) {
		double oldBalance = 0;
		double newBalance = 0;
		boolean shouldClose = false;
		String isFirst = checkFirstOfMonth(accountId);
		if(isFirst.equals("-1")) {
			return "1 " + oldBalance + " " + newBalance;
		}

		boolean checkType = checkType(accountId, "pocket");

		if (checkType == false) {
			System.out.println("The involved account must be a pocket account.");
			return "1 " + oldBalance + " " + newBalance;
		}

		oldBalance = getBalance(accountId);
		if(Double.compare(oldBalance, -1.00) == 0) {
			return "1 " + oldBalance + " " + newBalance;
		}

		newBalance = oldBalance - amount;

		if(Double.compare(newBalance, 0.00) >= 0) {
			String isValid = "";
			String checkFunds = "";
			String check = "";
			if(isFirst.equals("1")) {
				checkFunds = checkSufficientFunds(accountId, newBalance, 5.00);
				if(checkFunds.equals("1")) {
					return "1 " + oldBalance + " " + newBalance;
				}
				isValid = createTransaction("purchase", amount, 5.00, 0, 0.00, accountId, accountId, newBalance, newBalance);
				check = chargeFee(accountId, 5.00);
				if(check.equals("1")) {
					return "1 " + oldBalance + " " + newBalance;
				}
			} else {
				isValid = createTransaction("purchase", amount, 0.00, 0, 0.00, accountId, accountId, newBalance, newBalance);
			}
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + oldBalance + " " + newBalance;
			}
		} else {
			System.out.println("Amount requested is larger than the available balance.");
			return "1 " + oldBalance + " " + newBalance;
		}

		return "0 " + oldBalance + " " + newBalance;
	}

	public String transfer(String from, String to, double amount ) {
		double fromOldBalance = 0;
		double toOldBalance = 0;
		double fromNewBalance = 0;
		double toNewBalance = 0;

		if(Double.compare(amount, 2000) > 0) {
			System.out.println("Transfer amount is too large");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		if(from.equals(to)) {
			System.out.println("Cannot transfer funds to the same account.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		boolean check = isOwner(from);
		if(check == false) {
			System.out.println("Customer initiating transfer is not an owner of one or more of the accounts involved.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		check = isOwner(to);
		if(check == false) {
			System.out.println("Customer initiating transfer is not an owner of one or more of the accounts involved.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		boolean checkType = isCheckingOrSavings(from);
		boolean checkType2 = isCheckingOrSavings(to);

		if (checkType == false || checkType2 == false) {
			System.out.println("The involved accounts must be checking/savings accounts.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		fromOldBalance = getBalance(from);
		toOldBalance = getBalance(to);

		if(Double.compare(fromOldBalance, -1.00) == 0 || Double.compare(toOldBalance, -1.00) == 0) {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		fromNewBalance = fromOldBalance - amount;
		toNewBalance = toOldBalance + amount;

		if(Double.compare(fromNewBalance, 0.00) >= 0 && Double.compare(toNewBalance, 0.00) >= 0) {
			String isValid = createTransaction("transfer", amount, 0.00, 0, 0.00, to, from, toNewBalance, fromNewBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + fromNewBalance + " " + toNewBalance;
			}
		} else {
			System.out.println("Amount requested is larger than the available balance.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		return "0 "+ fromNewBalance + " " + toNewBalance;
	}

	public String collect(String pocketId, String linkedId, double amount) {
		double fee = amount * 0.03;
		double pocketNewBalance = 0.00;
		double linkedNewBalance = 0.00;

		String isFirst = checkFirstOfMonth(pocketId);
		if(isFirst.equals("-1")) {
			return "1 " + pocketNewBalance + " " + linkedNewBalance;
		}

		boolean checkType = checkType(pocketId, "pocket");
		boolean checkType2 = isCheckingOrSavings(linkedId);

		if (checkType == false || checkType2 == false) {
			System.out.println("The involved accounts must be a pocket account and a checking/savings account.");
			return "1 " + pocketNewBalance + " " + linkedNewBalance;
		}

		double pocketOldBalance = getBalance(pocketId);
		double linkedOldBalance = getBalance(linkedId);

		if(Double.compare(pocketOldBalance, -1.00) == 0 || Double.compare(linkedOldBalance, -1.00) == 0) {
			return "1 " + pocketNewBalance + " " + linkedNewBalance;
		}

		pocketNewBalance = pocketOldBalance - amount;
		linkedNewBalance = linkedOldBalance + amount;

		if(Double.compare(pocketNewBalance, 0.00) >= 0 && Double.compare(linkedNewBalance, 0.00) >= 0) {
			String isValid = "";
			String checkFunds = "";
			String check = "";
			if(isFirst.equals("1")) {
				checkFunds = checkSufficientFunds(pocketId, pocketNewBalance, fee+5);
				if(checkFunds.equals("1")) {
					return "1 " + pocketNewBalance + " " + linkedNewBalance;
				}
				isValid = createTransaction("collect", amount, fee+5, 0, 0.00, linkedId, pocketId, linkedNewBalance, pocketNewBalance);
				check = chargeFee(pocketId, fee+5.00);
				if(check.equals("1")) {
					return "1 " + pocketNewBalance + " " + linkedNewBalance;
				}
			} else {
				checkFunds = checkSufficientFunds(pocketId, pocketNewBalance, fee);
				if(checkFunds.equals("1")) {
					return "1 " + pocketNewBalance + " " + linkedNewBalance;
				}
				isValid = createTransaction("collect", amount, fee, 0, 0.00, linkedId, pocketId, linkedNewBalance, pocketNewBalance);
			}
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + pocketNewBalance + " " + linkedNewBalance;
			}
		} else {
			System.out.println("Amount requested is larger than the available balance.");
			return "1 " + pocketNewBalance + " " + linkedNewBalance;
		}

		String check = chargeFee(pocketId, fee);
		if(check.equals("1")) {
			return "1 " + pocketNewBalance + " " + linkedNewBalance;
		}

		return "0 " + pocketNewBalance + " " + linkedNewBalance;
	}

	public String checkSameOwner(String from, String to) {
		String findFromOwner = "SELECT O.tax_id FROM Owners O WHERE O.aid = ?";
		String tin = "";
		try(PreparedStatement fromStatement = _connection.prepareStatement(findFromOwner)) {
			fromStatement.setString(1, from);
			ResultSet rs_from = fromStatement.executeQuery();
			while(rs_from.next()) {
				tin = rs_from.getString("tax_id");
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "-1";
		}
		String findToOwner = "SELECT O.tax_id FROM Owners O WHERE O.aid = ?";
		try(PreparedStatement toStatement = _connection.prepareStatement(findToOwner)) {
			toStatement.setString(1, to);
			ResultSet rs_to = toStatement.executeQuery();
			while(rs_to.next()) {
				String temp = rs_to.getString("tax_id");
				if(temp.equals(tin)) {
					return "1";
				}
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "-1";
		}
		return "0";

	}

	public String wire( String from, String to, double amount)
	{
		double fromOldBalance = 0;
		double toOldBalance = 0;
		double fromNewBalance = 0;
		double toNewBalance = 0;
		double fee = amount * 0.02;

		boolean checkOwner = isOwner(from);
		if(checkOwner == false) {
			System.out.println("Customer initiating wire is not the owner of the account.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		String checkSameOwner = checkSameOwner(from, to);
		if(checkSameOwner.equals("1")) {
			System.out.println("Cannot wire money between accounts with a mutual owner.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}
		if(checkSameOwner.equals("-1")) {
			System.out.println("Error in checking if accounts have a mutual owner.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		boolean checkType = isCheckingOrSavings(from);
		boolean checkType2 = isCheckingOrSavings(to);

		if (checkType == false || checkType2 == false) {
			System.out.println("The involved accounts must be checking/savings accounts.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		fromOldBalance = getBalance(from);
		toOldBalance = getBalance(to);

		if(Double.compare(fromOldBalance, -1.00) == 0 || Double.compare(toOldBalance, -1.00) == 0) {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		fromNewBalance = fromOldBalance - amount;
		toNewBalance = toOldBalance + amount;

		if(Double.compare(fromNewBalance, 0.00) >= 0 && Double.compare(toNewBalance, 0.00) >= 0) {
			String checkFunds = checkSufficientFunds(from, fromNewBalance, fee);
			if(checkFunds.equals("1")) {
				return "1 " + fromNewBalance + " " + toNewBalance;
			}
			String isValid = createTransaction("wire", amount, fee, 0, 0.00, to, from, toNewBalance, fromNewBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + fromNewBalance + " " + toNewBalance;
			}
		} else {
			System.out.println("Amount requested is larger than the available balance.");
			return "1 " + fromNewBalance + " " + toNewBalance;
		}
		String check = chargeFee(from, fee);
		if(check.equals("1")) {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		return "0 " + fromNewBalance + " " + toNewBalance;
	}

	public int getCheckNo() {
		int check_no = 0;
		try (Statement getCheckNo = _connection.createStatement()) {
			ResultSet rs = getCheckNo.executeQuery("SELECT MAX(T.check_no) FROM Transactions T");
			while(rs.next()) {
				check_no = rs.getInt(1);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return -1;
		}
		return check_no;
	}

	public String writeCheck(String accountId, double amount) {
		double oldBalance = 0;
		double newBalance = 0;
		boolean shouldClose = false;
		int check_no = getCheckNo();
		if (check_no == -1) {
			System.out.println("Error generating check number.");
			return "1 " + oldBalance + " " + newBalance;
		}

		check_no = check_no + 1;

		boolean checkType = checkType(accountId, "student");
		boolean checkType2 = checkType(accountId, "interest");

		if (checkType == false && checkType2 == false) {
			System.out.println("The involved accounts must be a checking account.");
			return "1 " + oldBalance + " " + newBalance;
		}

		oldBalance = getBalance(accountId);
		if(Double.compare(oldBalance, -1.00) == 0) {
			return "1 " + oldBalance + " " + newBalance;
		}

		newBalance = oldBalance - amount;

		if(Double.compare(newBalance, 0.00) >= 0) {
			String isValid = createTransaction("writeCheck", amount, 0.00, check_no, 0.00, accountId, accountId, newBalance, newBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + oldBalance + " " + newBalance;
			}
		} else {
			return "1 " + oldBalance + " " + newBalance;
		}

		return "0 " + oldBalance + " " + newBalance;
	}

	public double getNumDays() {
		String bankDate = getDateInfo();
		String getMonth = "SELECT EXTRACT( MONTH FROM TO_DATE(?, \'YYYY-MM-DD\') ) MONTH FROM DUAL";
		int month = 0;
		try(PreparedStatement monthStatement = _connection.prepareStatement(getMonth)) {
			monthStatement.setString(1, bankDate);
			ResultSet rs = monthStatement.executeQuery();
			while(rs.next()) {
				month = rs.getInt(1);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return -1;
		}

		if (month == 2) {
			return 28.0;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			return 30.0;
		} else {
			return 31.0;
		}
	}

	public double calculateAverage(String accountId, double initBalance, double numDays) {
		ArrayList<String> tid_tos = new ArrayList<String>();
		ArrayList<String> tid_froms = new ArrayList<String>();
		TreeMap<Integer, ArrayList<Double>> transactions = new TreeMap<Integer, ArrayList<Double>>();

		String getTidsTo = "SELECT I.tid FROM Involves I WHERE I.aid_to = ?";
		try(PreparedStatement tidToStatement = _connection.prepareStatement(getTidsTo)) {
			tidToStatement.setString(1, accountId);
			ResultSet rs = tidToStatement.executeQuery();
			while(rs.next()) {
				tid_tos.add(rs.getString("tid"));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return -1.0;
		}

		String getTidsFrom = "SELECT I.tid FROM Involves I WHERE I.aid_from = ?";
		try(PreparedStatement tidFromStatement = _connection.prepareStatement(getTidsFrom)) {
			tidFromStatement.setString(1, accountId);
			ResultSet rs2 = tidFromStatement.executeQuery();
			while(rs2.next()) {
				tid_froms.add(rs2.getString("tid"));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return -1.0;
		}

		String tidList = makeQueryList(tid_tos);
		int day = 0;

		String getPosInfo = "SELECT T.amount, T.d FROM Transactions T WHERE " +
						"(T.type=\'deposit\' OR T.type=\'transfer\' OR T.type=\'collect\' OR T.type=\'wire\') AND T.tid IN "+tidList;
		try(Statement posStatement = _connection.createStatement()) {
			ResultSet rs_pos = posStatement.executeQuery(getPosInfo);
			while(rs_pos.next()) {
				Date d = rs_pos.getDate("d");
				Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				day = cal.get(Calendar.DAY_OF_MONTH);
				if(transactions.containsKey(day)) {
					transactions.get(day).add(rs_pos.getDouble(1));
				} else {
					ArrayList<Double> tmp = new ArrayList<Double>();
					tmp.add(rs_pos.getDouble(1));
					transactions.put(day, tmp);
				}
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return -1.0;
		}

		tidList = makeQueryList(tid_froms);

		String getNegInfo = "SELECT T.amount, T.d FROM Transactions T WHERE " +
						"(T.type=\'topUp\' OR T.type=\'withdrawal\' OR T.type=\'transfer\' " +
						"OR T.type=\'wire\' OR T.type=\'fee\' OR T.type=\'writeCheck\') AND T.tid IN "+tidList;
		try(Statement negStatement = _connection.createStatement()) {
			ResultSet rs_neg = negStatement.executeQuery(getNegInfo);
			while(rs_neg.next()) {
				Date d = rs_neg.getDate("d");
				Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				day = cal.get(Calendar.DAY_OF_MONTH);
				if(transactions.containsKey(day)) {
					transactions.get(day).add((rs_neg.getDouble(1)*-1));
				} else {
					ArrayList<Double> tmp = new ArrayList<Double>();
					tmp.add((rs_neg.getDouble(1)*-1));
					transactions.put(day, tmp);
				}
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return -1.0;
		}

		int lastDay = 1;
		double avg_daily_balance = 0;
		double tempBal = 0;
		for(Integer i : transactions.keySet()) {
			avg_daily_balance += tempBal*(i-lastDay);
			lastDay = i;

			for(int j = 0; j < transactions.get(i).size(); j++) {
				tempBal += transactions.get(i).get(j);
			}
			if((double)i == numDays) {
				avg_daily_balance += tempBal*(i-lastDay+1);
			}
		}
		if((double)transactions.lastKey() != numDays) {
			avg_daily_balance += tempBal*(numDays-lastDay+1);
		}

		avg_daily_balance = avg_daily_balance/numDays;

		return avg_daily_balance;
	}

	public String accrueInterest(String accountId) {
		boolean checkType = isCheckingOrSavings(accountId);
		if (checkType == false) {
			System.out.println("The involved accounts must be a savings/checking account.");
			return "1";
		}

		boolean checkClosed = checkClosed(accountId);
		if (checkClosed == true) {
			System.out.println("Cannot accrue interest for a closed account.");
			return "1";
		}

		double numDays = getNumDays();

		double initBalance = 0;
		double currBalance = 0;
		double rate = 0;

		String getInitial = "SELECT A.init_balance, A.interest, A.curr_balance FROM Accounts A WHERE A.aid = ?";
		try(PreparedStatement initStatement = _connection.prepareStatement(getInitial)) {
			initStatement.setString(1, accountId);
			ResultSet rs_init = initStatement.executeQuery();
			while(rs_init.next()) {
				initBalance = rs_init.getDouble(1);
				rate = rs_init.getDouble(2);
				currBalance = rs_init.getDouble(3);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		double avg_daily_balance = calculateAverage(accountId, initBalance, numDays);
		double interest = avg_daily_balance*(rate/100);

		String isValid = createTransaction("accrueInterest", interest, 0.00, 0, avg_daily_balance, accountId, accountId, currBalance + interest, currBalance + interest);
		if(isValid.equals("1")) {
			System.out.println("Accrue interest transaction failed.");
			return "1";
		}

		return "0";
	}

	public String checkInsurance(String tin) {
		ArrayList<String> primaryAcc = new ArrayList<String>();
		double sum = 0;

		String getAcc = "SELECT P.aid FROM Primary P WHERE P.tax_id = ?";
		try(PreparedStatement accStatement = _connection.prepareStatement(getAcc)) {
			accStatement.setString(1, tin);
			ResultSet rs = accStatement.executeQuery();
			while(rs.next()) {
				primaryAcc.add(rs.getString(1));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error retrieving accounts where customer is the primary owner.");
			return "1";
		}

		for(int i = 0; i < primaryAcc.size(); i++) {
			String getBalance = "SELECT A.curr_balance FROM Accounts A WHERE A.aid = ?";
			try(PreparedStatement balStatement = _connection.prepareStatement(getBalance)) {
				balStatement.setString(1, primaryAcc.get(i));
				ResultSet rs3 = balStatement.executeQuery();
				while(rs3.next()) {
					sum = sum + rs3.getDouble(1);
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				System.out.println("Error retrieving balance from accounts where customer is the primary owner.");
				return "1";
			}
		}

		if(sum > 100000) {
			System.out.println("WARNING: Limit of the insurance has been reached.");
			return "1";
		}
		return "0";
	}

	public String getMonth(String bankDate) {
		String getMonth = "SELECT EXTRACT( MONTH FROM TO_DATE(?, \'YYYY-MM-DD\') ) MONTH FROM DUAL";
		int month = 0;
		try(PreparedStatement monthStatement = _connection.prepareStatement(getMonth)) {
			monthStatement.setString(1, bankDate);
			ResultSet rs = monthStatement.executeQuery();
			while(rs.next()) {
				month = rs.getInt(1);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "-1";
		}
		switch(month) {
			case 1:
				return "January";
			case 2:
				return "February";
			case 3:
				return "March";
			case 4:
				return "April";
			case 5:
				return "May";
			case 6:
				return "June";
			case 7:
				return "July";
			case 8:
				return "August";
			case 9:
				return "September";
			case 10:
				return "October";
			case 11:
				return "November";
			case 12:
				return "December";
			default:
				System.out.println("Error in retrieving month.");
				return "-1";
		}
	}

	public String getYear(String bankDate) {
		String getYear = "SELECT EXTRACT( YEAR FROM TO_DATE(?, \'YYYY-MM-DD\') ) YEAR FROM DUAL";
		int year = 0;
		try(PreparedStatement yearStatement = _connection.prepareStatement(getYear)) {
			yearStatement.setString(1, bankDate);
			ResultSet rs = yearStatement.executeQuery();
			while(rs.next()) {
				year = rs.getInt(1);
			}
			return Integer.toString(year);
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "-1";
		}
	}

	public String generateMonthly(String tin) {
		String bankDate = getDateInfo();
		// Pull month from BankDate. Use MONTH() to make a query to pull all transactions with this month for the given customer
		ArrayList<String> ownedAccounts = new ArrayList<String>();
		boolean isEnd = checkEndOfMonth();
		if(isEnd == false) {
			System.out.println("Cannot generate monthly statements until the end of the month");
			return "1";
		}

		String checkPrimary = "SELECT COUNT(*) FROM Primary P WHERE P.tax_id = ?";
		try(PreparedStatement pStatement = _connection.prepareStatement(checkPrimary)) {
			pStatement.setString(1, tin);
			ResultSet rs = pStatement.executeQuery();
			while(rs.next()) {
				if(rs.getInt(1) == 0) {
					System.out.println("Customer with tax ID is not a primary owner of any account.");
					return "1";
				}
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		// Get all accounts
		String getAcc = "SELECT O.aid FROM Owners O WHERE O.tax_id = ?";
		try(PreparedStatement accStatement = _connection.prepareStatement(getAcc)) {
			accStatement.setString(1, tin);
			ResultSet rs = accStatement.executeQuery();
			while(rs.next()) {
				ownedAccounts.add(rs.getString(1));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		String month = getMonth(bankDate);
		String year = getYear(bankDate);

		for(int i = 0; i < ownedAccounts.size(); i++) {
			System.out.println("------"+month+" "+year+"\'s Transactions for Account "+ownedAccounts.get(i)+"------");

			String getBalance = "SELECT A.init_balance, A.curr_balance FROM Accounts A WHERE A.aid = ?";
			try(PreparedStatement balStatement = _connection.prepareStatement(getBalance)) {
				balStatement.setString(1, ownedAccounts.get(i));
				ResultSet rs3 = balStatement.executeQuery();
				while(rs3.next()) {
					System.out.println("Initial Balance: $"+rs3.getString(1)+" Final Balance: $"+rs3.getString(2));
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				return "1";
			}

			TreeMap<String, ArrayList<String>> transactionInfo = new TreeMap<String, ArrayList<String>>(new CompKeyStr());

			String getTInfo = "SELECT I.tid, I.aid_to, I.aid_from FROM Involves I WHERE I.aid_to = ? OR I.aid_from = ?";
			try(PreparedStatement tStatement = _connection.prepareStatement(getTInfo)) {
				tStatement.setString(1, ownedAccounts.get(i));
				tStatement.setString(2, ownedAccounts.get(i));
				ResultSet rs4 = tStatement.executeQuery();
				while(rs4.next()) {
					ArrayList<String> tmp = new ArrayList<String>();
					tmp.add(rs4.getString(2));
					tmp.add(rs4.getString(3));
					transactionInfo.put(rs4.getString(1), tmp);
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				return "1";
			}

			// get names and addresses of all owners
			System.out.println("------------------------ Owners ------------------------");
			System.out.println(String.format("%-20s %-50s", "Name", "Address"));
			System.out.println(String.format("%-20s %-50s", "----", "-------"));
			String getName = "SELECT C.name, C.address FROM Customers C WHERE C.tax_id IN (SELECT O.tax_id FROM Owners O WHERE O.aid = ?)";
			try(PreparedStatement nameStatement = _connection.prepareStatement(getName)) {
				nameStatement.setString(1, ownedAccounts.get(i));
				ResultSet rs_c = nameStatement.executeQuery();
				while(rs_c.next()) {
					String tmp = String.format("%-20s %-50s", rs_c.getString(1), rs_c.getString(2));
					System.out.println(tmp);
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				return "1";
			}

			// get amount and type for each transaction
			System.out.println("--------------------- Transactions ---------------------");
			for(String j : transactionInfo.keySet()) {
				String getTInfo2 = "SELECT T.amount, T.type FROM Transactions T WHERE T.tid = ?";
				try(PreparedStatement tStatement2 = _connection.prepareStatement(getTInfo2)) {
					tStatement2.setString(1, j);
					ResultSet rs5 = tStatement2.executeQuery();
					while(rs5.next()) {
						(transactionInfo.get(j)).add(rs5.getString(1));
						(transactionInfo.get(j)).add(rs5.getString(2));
					}
				} catch ( SQLException e )
				{
					System.err.println( e.getMessage() );
					return "1";
				}

				// transaction info: transaction id, aid_to, aid_from, amount, type
				if(transactionInfo.get(j).get(3).equals("fee")) {
					System.out.println(transactionInfo.get(j).get(3)+" $"+transactionInfo.get(j).get(2)+" from "+transactionInfo.get(j).get(1)+" to bank");
				} else if(transactionInfo.get(j).get(3).equals("deposit")) {
					System.out.println(transactionInfo.get(j).get(3)+" $"+transactionInfo.get(j).get(2)+" to "+transactionInfo.get(j).get(0));
				} else if(transactionInfo.get(j).get(3).equals("withdrawal") || transactionInfo.get(j).get(3).equals("writeCheck")) {
					System.out.println(transactionInfo.get(j).get(3)+" $"+transactionInfo.get(j).get(2)+" from "+transactionInfo.get(j).get(1));
				} else {
					System.out.println(transactionInfo.get(j).get(3)+" $"+transactionInfo.get(j).get(2)+" from "+transactionInfo.get(j).get(1)+" to "+transactionInfo.get(j).get(0));
				}
			}
			System.out.println("");
		}

		checkInsurance(tin);

		return "0";
	}

	public String generateDTER() {
		boolean isEnd = checkEndOfMonth();
		if(isEnd == false) {
			System.out.println("Cannot generate monthly statements until the end of the month");
			return "1";
		}

		ArrayList<String> customers = new ArrayList<String>();
		try(Statement custStatement = _connection.createStatement()) {
			ResultSet rs = custStatement.executeQuery("SELECT C.tax_id FROM Customers C");
			while(rs.next()) {
				customers.add(rs.getString("tax_id"));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		System.out.println("--- Government Drug and Tax Evasion Report ---");

		for(int i = 0; i < customers.size(); i++) {
			double sum = 0;

			// get all accounts the customer owns
			ArrayList<String> ownedAccs = new ArrayList<String>();

			String getAcc = "SELECT O.aid FROM Owners O WHERE O.tax_id = ?";
			try(PreparedStatement accStatement = _connection.prepareStatement(getAcc)) {
				accStatement.setString(1, customers.get(i));
				ResultSet rs = accStatement.executeQuery();
				while(rs.next()) {
					ownedAccs.add(rs.getString(1));
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				return "1";
			}

			String listOfAccs = makeQueryList(ownedAccs);
			ArrayList<String> tempTransactions = new ArrayList<String>();

			// deposits and transfers
			String getTInfo = "SELECT I.tid FROM Involves I WHERE I.aid_from IN "+listOfAccs+" AND I.aid_to IN "+listOfAccs;
			try(Statement tStatement = _connection.createStatement()) {
				ResultSet rs2 = tStatement.executeQuery(getTInfo);
				while(rs2.next()) {
					tempTransactions.add(rs2.getString(1));
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				return "1";
			}

			for(int j = 0; j < tempTransactions.size(); j++) {
				String getAmt = "SELECT T.amount FROM Transactions T WHERE T.tid = ? AND (T.type = \'deposit\' OR T.type = \'transfer\')";
				try(PreparedStatement amtStatement = _connection.prepareStatement(getAmt)) {
					amtStatement.setString(1, tempTransactions.get(j));
					ResultSet rs3 = amtStatement.executeQuery();
					while(rs3.next()) {
						sum = sum + rs3.getDouble("amount");
					}
				} catch ( SQLException e )
				{
					System.err.println( e.getMessage() );
					return "1";
				}
			}

			tempTransactions.clear();

			// wires
			String getTInfo2 = "SELECT I.tid FROM Involves I WHERE I.aid_to IN "+listOfAccs;
			try(Statement tStatement = _connection.createStatement()) {
				ResultSet rs4 = tStatement.executeQuery(getTInfo2);
				while(rs4.next()) {
					tempTransactions.add(rs4.getString(1));
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				return "1";
			}

			for(int j = 0; j < tempTransactions.size(); j++) {
				String getAmt2 = "SELECT T.amount FROM Transactions T WHERE T.tid = ? AND T.type = \'wire\'";
				try(PreparedStatement amtStatement2 = _connection.prepareStatement(getAmt2)) {
					amtStatement2.setString(1, tempTransactions.get(j));
					ResultSet rs5 = amtStatement2.executeQuery();
					while(rs5.next()) {
						sum = sum + rs5.getDouble("amount");
					}
				} catch ( SQLException e )
				{
					System.err.println( e.getMessage() );
					return "1";
				}
			}

			if(sum > 10000) {
				System.out.println(customers.get(i));
			}
		}

		return "0";
	}

	public String generateCustomerReport(String tin) {
		// check if valid tax ID
		String isValid = checkCustomerExists(tin);
		if(isValid.equals("0")) {
			System.out.println("No customer exists with the given tax ID.");
			return "1";
		}

		String findAccounts = "SELECT O.aid FROM Owners O WHERE O.tax_id = ?";
		ArrayList<String> accounts = new ArrayList<String>();
		try(PreparedStatement accStatement = _connection.prepareStatement(findAccounts)) {
			accStatement.setString(1, tin);
			ResultSet rs = accStatement.executeQuery();

			while(rs.next()) {
				accounts.add(rs.getString("aid"));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		System.out.println("Accounts for Customer with Tax ID "+tin);
		System.out.println("----------------------");

		String findAccountInfo = "SELECT * FROM Accounts WHERE aid IN (";
		try(Statement infoStatement = _connection.createStatement()) {
			for(int i = 0; i < accounts.size(); i++) {
				findAccountInfo = findAccountInfo + "\'" + accounts.get(i) + "\',";
			}
			if(findAccountInfo.length() > 1) {
                findAccountInfo = findAccountInfo.substring(0, findAccountInfo.length()-1);
			}
			findAccountInfo = findAccountInfo + ")";
			ResultSet rs2 = infoStatement.executeQuery(findAccountInfo);

			while(rs2.next()) {
				if(rs2.getString("active").equals("1")) {
					System.out.println(rs2.getString("aid")+ "\t" + "active");
				} else {
					System.out.println(rs2.getString("aid")+ "\t" + "inactive");
				}
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		return "0";
	}

	public String addInterest() {
		// Select all aids for accounts with active = 1 and checking or savingsand put in arraylist. iterate through arraylist and call accrueinterest
		// find a way to note that this has been done for the month
		boolean isEnd = checkEndOfMonth();
		if(isEnd == false) {
			System.out.println("Cannot generate monthly statements until the end of the month");
			return "1";
		}

		try(Statement checkDone = _connection.createStatement()) {
			ResultSet rs = checkDone.executeQuery("SELECT COUNT(*) FROM Transactions T WHERE T.type = \'addInterest\'");
			while(rs.next()) {
				if(rs.getInt(1) > 0) {
					System.out.println("Interest has already been added for the month.");
					return "1";
				}
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		ArrayList<String> openAccs = new ArrayList<String>();

		try(Statement getAccs = _connection.createStatement()) {
			ResultSet rs = getAccs.executeQuery("SELECT A.aid FROM Accounts A WHERE A.active = 1 AND (A.type = \'student\' OR A.type = \'interest\' OR A.type = \'savings\')");
			while(rs.next()) {
				openAccs.add(rs.getString(1));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		String check = "";
		for(int i = 0; i < openAccs.size(); i++) {
			check = accrueInterest(openAccs.get(i));
			if(check.equals("1")) {
				break;
			}
		}

		if(check.equals("1") == false) {
			createTransaction("addInterest", 0.00, 0.00, 0, 0.00, "0", "0", 0.00, 0.00);
		}

		return "0";
	}

	public boolean checkEndOfMonth() {
		double numDays = getNumDays();
		String bankDate = getDateInfo();
		String getDay = "SELECT EXTRACT( DAY FROM TO_DATE(?, \'YYYY-MM-DD\') ) DAY FROM DUAL";
		int day = 0;
		try(PreparedStatement monthStatement = _connection.prepareStatement(getDay)) {
			monthStatement.setString(1, bankDate);
			ResultSet rs = monthStatement.executeQuery();
			while(rs.next()) {
				day = rs.getInt(1);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
		}
		if(numDays == day) {
			return true;
		} else {
			return false;
		}
	}

	public String makeQueryList(ArrayList<String> arr) {
		String list = "(";
		for(int i = 0; i < arr.size(); i++) {
			list = list + "\'" + arr.get(i) + "\',";
		}
		if(list.length() > 1) {
			list = list.substring(0, list.length()-1);
		}
		list = list + ")";
		return list;
	}

	public String deleteClosed() {
		boolean isEnd = checkEndOfMonth();
		if(isEnd == false) {
			System.out.println("Cannot delete closed accounts and customers until the end of the month");
			return "1";
		}

		ArrayList<String> closedAccs = new ArrayList<String>();

		try(Statement getInactive = _connection.createStatement()) {
			ResultSet rs = getInactive.executeQuery("SELECT A.aid FROM Accounts A WHERE A.active = 0");
			while(rs.next()) {
				closedAccs.add(rs.getString(1));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		ArrayList<String> ownClosed = new ArrayList<String>();

		String tempAcc = makeQueryList(closedAccs);
		String findOwners = "SELECT O.tax_id FROM Owners O WHERE O.aid IN "+tempAcc;

		try(Statement getOwners = _connection.createStatement()) {
			ResultSet rs2 = getOwners.executeQuery(findOwners);
			while(rs2.next()) {
				ownClosed.add(rs2.getString(1));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		String ownList = makeQueryList(ownClosed);

		String deletion = "DELETE FROM Owners O WHERE O.tax_id IN "+ownList+" AND O.aid IN "+tempAcc;

		try(Statement delOwners = _connection.createStatement()) {
			delOwners.executeUpdate(deletion);
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error deleting owner.");
			return "1";
		}

		String checkIfOwnsStill = "SELECT O.tax_id FROM Owners O WHERE O.tax_id IN "+ownList;

		try(Statement stillOwners = _connection.createStatement()) {
			ResultSet rs3 = stillOwners.executeQuery(checkIfOwnsStill);
			while(rs3.next()) {
				String temp = rs3.getString(1);
				ownClosed.remove(temp);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		if(ownClosed.size() > 0) {
			ownList = makeQueryList(ownClosed);
			String deletion_cust = "DELETE FROM Customers C WHERE C.tax_id IN "+ownList;

			try(Statement delCustomers = _connection.createStatement()) {
				delCustomers.executeUpdate(deletion_cust);
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				System.out.println("Error deleting customer.");
				return "1";
			}
		}

		String deletion_acct= "DELETE FROM Accounts A WHERE A.active = 0";

		try(Statement delAccounts = _connection.createStatement()) {
			delAccounts.executeUpdate(deletion_acct);
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error deleting owner.");
			return "1";
		}
		return "0";
	}

	public String deleteTransactions() {
		boolean isEnd = checkEndOfMonth();
		if(isEnd == false) {
			System.out.println("Cannot delete transactions until the end of the month");
			return "1";
		}
		try(Statement infoStatement = _connection.createStatement()) {
			infoStatement.executeUpdate("DELETE FROM Transactions");
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
		System.out.println("Successfully deleted all transactions for the month.");
		return "0";
	}

	public String setNewInterest(double interest, String type) {
		String updAccounts = "UPDATE Accounts SET interest = ? WHERE type = ?";
		try(PreparedStatement interestStatement = _connection.prepareStatement(updAccounts)) {
			interestStatement.setDouble(1, interest);
			interestStatement.setString(2, type);
			interestStatement.executeUpdate();
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
		return "0";
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public boolean verifyPIN(String pin) {
		String encrypted = hash(pin);
		String userPin = "";

		String checkPin = "SELECT C.pin FROM Customers C WHERE C.tax_id = ?";
		try(PreparedStatement statement = _connection.prepareStatement(checkPin)) {
			statement.setString(1, this.taxId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				userPin = rs.getString(1);
			}
			if(encrypted.equals(userPin)) {
				return true;
			} else {
				return false;
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return false;
		}
	}

	public void setPIN(String OldPIN, String NewPIN) {
		String checkPin = "SELECT C.pin FROM Customers C WHERE C.tax_id = ?"; // NEED TO DISTNGUISH THE CUSTOMER bc some may have same pin
		String pin = "";
		String encryptedOld = hash(OldPIN);

		try(PreparedStatement statement = _connection.prepareStatement(checkPin)) {
			statement.setString(1, this.taxId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				pin = rs.getString(1);
			}
			if(pin.equals(encryptedOld) == false) {
				System.out.println("Incorrect old PIN.");
				return;
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error getting customer with old pin number.");
		}

		String encryptedNew = hash(NewPIN);

		String setNewPin = "UPDATE Customers SET pin = ? WHERE tax_id = ?";
		try(PreparedStatement statement2 = _connection.prepareStatement(setNewPin)) {
			statement2.setString(1, encryptedNew);
			statement2.setString(2, this.taxId);
			statement2.executeUpdate();
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error setting new pin number.");
		}
	}

	private static String hash(String input) {
    String output = "";
    try {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      byte hash_bytes[] = sha256.digest(input.getBytes());
      BigInteger hash_num = new BigInteger(1, hash_bytes);
      output = hash_num.toString(16);
    }
    catch( NoSuchAlgorithmException e ) {
      System.err.println( e.getMessage() );
    }
    return output;
  }
}

class CompKeyStr implements Comparator<String>{

    @Override
    public int compare(String str1, String str2) {
        return Integer.valueOf(str1).compareTo(Integer.valueOf(str2));
    }

}
