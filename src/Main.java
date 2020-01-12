package cs174a;                         // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// DO NOT REMOVE THIS IMPORT.
import cs174a.Testable.*;
import java.util.Scanner;
/**
 * This is the class that launches your application.
 * DO NOT CHANGE ITS NAME.
 * DO NOT MOVE TO ANY OTHER (SUB)PACKAGE.
 * There's only one "main" method, it should be defined within this Main class, and its signature should not be changed.
 */
public class Main
{
	/**
	 * Program entry point.
	 * DO NOT CHANGE ITS NAME.
	 * DON'T CHANGE THE //!### TAGS EITHER.  If you delete them your program won't run our tests.
	 * No other function should be enclosed by the //!### tags.
	 */
	//!### COMENZAMOS
	public static void main( String[] args )
	{
		App app = new App();                        // We need the default constructor of your App implementation.  Make sure such
													// constructor exists.
		String r = app.initializeSystem();          // We'll always call this function before testing your system.

		if( r.equals( "0" ) )
		{
			Scanner s = new Scanner(System.in);
			System.out.println("-----------------\n0: Reset DB\n1: Run App\n2: Example Test\n3: Accrue Interest Test\n4: Actual Sample DB Test\n5: Testing Testable\n6: Example Test (Long)\n-----------------");
			String input = s.nextLine();
			if(input.equals("0")){
				//app.dropTables();
				app.createTables();
			} else if (input.equals("1")){
				app.setUpUI();
			} else if (input.equals("2")){
				r = app.setDate(2019, 11, 30);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "12345", 1000, "123456789", "John Smith", "100 Main St" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("54321", "12345", 100, "123456789");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "13579", 1000, "123456789", "John Smith", "100 Main St" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("97531", "13579", 50, "123456789");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "55555", 1000, "555555555", "John Smith", "555 Main St" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("55557", "55555", 30, "555555555");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "55556", 1000, "555555555", "John Smith", "100 Main St" );
				System.out.println( r );

				System.out.println("----------------------------");
			} else if( input.equals("3") ) {
				r = app.setDate(2019, 11, 5);
				System.out.println( r );

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "17431", 1000, "344151573", "Joe Pepsi", "3210 State St" );
				System.out.println( r );

				app.setTaxId("344151573");

				r = app.createCheckingSavingsAccount(AccountType.SAVINGS, "12345", 5000, "122219876", "Elizabeth Sailor", "4321 State St");
				System.out.println( r );

				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "41725", 15000, "201674933", "George Brush", "5346 Foothill Av" );
				System.out.println( r );

				r = app.createOwners("344151573", "41725");
				System.out.println( r );

				r = app.setDate(2019, 11, 10);
				System.out.println( r );

				r = app.transfer("41725", "17431", 25);
				System.out.println( r );

				r = app.withdrawal("17431", 100);
				System.out.println( r );

				r = app.setDate(2019, 11, 15);
				System.out.println( r );

				r = app.writeCheck("17431", 200);
				System.out.println( r );

				r = app.setDate(2019, 11, 25);
				System.out.println( r );

				app.setTaxId("344151573");

				r = app.wire("17431", "12345", 50);
				System.out.println( r );

				r = app.deposit("17431", 300);
				System.out.println( r );

				r = app.setDate(2019, 11, 30);
				System.out.println( r );

				r = app.addInterest();
				System.out.println( r );

				// should not go through
				r = app.addInterest();
				System.out.println( r );
			} else if (input.equals("4")) {
				r = app.setDate(2011, 3, 1);
				System.out.println( r );

				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "17431", 1200, "344151573", "Joe Pepsi", "3210 State St" );
				System.out.println( r );
				r = app.createCustomer("17431", "322175130", "Ivan Lendme", "1235 Johnson Dr");
				System.out.println( r );
				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "54321", 21000, "212431965", "Hurryson Ford", "678 State St" );
				System.out.println( r );
				r = app.createCustomer("54321", "122219876", "Elizabeth Sailor", "4321 State St");
				System.out.println( r );
				r = app.createCustomer("54321", "203491209", "Nam-Hoi Chung", "1997 People\'s St HK");
				System.out.println( r );
				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "12121", 1200, "207843218", "David Copperfill", "1357 State St" );
				System.out.println( r );
				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "41725", 15000, "201674933", "George Brush", "5346 Foothill Av" );
				System.out.println( r );
				r = app.createCustomer("41725", "231403227", "Billy Clinton", "5777 Hollister");
				System.out.println( r );
				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "93156", 2000000, "209378521", "Kelvin Costner", "Santa Cruz #3579" );
				System.out.println( r );
				r = app.createOwners("122219876", "93156");
				System.out.println( r );
				r = app.createOwners("203491209", "93156");
				System.out.println( r );
				r = app.createCustomer("93156", "210389768", "Olive Stoner", "6689 El Colegio #151");
				System.out.println( r );
				r = app.createPocketAccount("53027", "12121", 50, "207843218");
				System.out.println( r );
				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "43942", 1289, "361721022", "Alfred Hitchcock", "6667 El Colegio #40" );
				System.out.println( r );
				r = app.createOwners("212431965", "43942");
				System.out.println( r );
				r = app.createOwners("322175130", "43942");
				System.out.println( r );
				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "29107", 34000, "209378521", "Kelvin Costner", "Santa Cruz #3579" );
				System.out.println( r );
				r = app.createOwners("210389768", "29107");
				System.out.println( r );
				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "19023", 2300, "412231856", "Cindy Laugher", "7000 Hollister" );
				System.out.println( r );
				r = app.createOwners("412231856", "17431");
				System.out.println( r );
				r = app.createOwners("412231856", "54321");
				System.out.println( r );
				r = app.createOwners("201674933", "19023");
				System.out.println( r );
				r = app.createCustomer("43942", "400651982", "Pit Wilson", "911 State St");
				System.out.println( r );
				r = app.createPocketAccount("60413", "43942", 20, "400651982");
				System.out.println( r );
				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "32156", 1000, "188212217", "Michael Jordon", "3852 Court Rd" );
				System.out.println( r );
				r = app.createOwners("188212217", "93156");
				System.out.println( r ); 
				r = app.createOwners("344151573", "32156");
				System.out.println( r ); 
				r = app.createOwners("207843218", "32156");
				System.out.println( r ); 
				r = app.createOwners("122219876", "32156");
				System.out.println( r );
				r = app.createOwners("203491209", "32156");
				System.out.println( r );
				r = app.createOwners("210389768", "32156");
				System.out.println( r );
				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "76543", 8456, "212116070", "Li Kung", "2 People\'s Rd Beijing" );
				System.out.println( r );
				r = app.createOwners("188212217", "76543");
				System.out.println( r ); 
				r = app.createOwners("212116070", "29107");
				System.out.println( r );
				r = app.createPocketAccount("43947", "29107", 30, "212116070");
				System.out.println( r );
				r = app.createCustomer("19023", "401605312", "Fatal Castro", "3756 La Cumbre Plaza");
				System.out.println( r );
				r = app.createOwners("401605312", "41725");
				System.out.println( r );
				r = app.createPocketAccount("67521", "19023", 100, "401605312");
				System.out.println( r );

				r = app.setDate(2011, 3, 2);
				System.out.println( r );

				r = app.deposit("17431", 8800);
				System.out.println( r );

				r = app.setDate(2011, 3, 3);
				System.out.println( r );

				app.setTaxId("122219876");
				r = app.withdrawal("54321", 3000);
				System.out.println( r );

				r = app.setDate(2011, 3, 5);
				System.out.println( r );

				app.setTaxId("212116070");
				r = app.withdrawal("76543", 2000);
				System.out.println( r );
				app.setTaxId("207843218");
				r = app.purchase("53027", 5);
				System.out.println( r );

				r = app.setDate(2011, 3, 6);
				System.out.println( r );

				app.setTaxId("188212217");
				r = app.withdrawal("93156", 1000000);
				System.out.println( r );
				r = app.writeCheck("93156", 950000);
				System.out.println( r );
				app.setTaxId("212116070");
				r = app.withdrawal("29107", 4000);
				System.out.println( r );
				r = app.collect("43947", "29107", 10);
				System.out.println( r );
				r = app.topUp("43947", 30);
				System.out.println( r );

				r = app.setDate(2011, 3, 7);
				System.out.println( r );

				app.setTaxId("322175130");
				r = app.transfer("43942", "17431", 289);
				System.out.println( r );
				app.setTaxId("400651982");
				r = app.withdrawal("43942", 289);
				System.out.println( r );

				r = app.setDate(2011, 3, 8);
				System.out.println( r );

				r = app.payFriend("60413", "67521", 10);
				System.out.println( r );
				r = app.deposit("93156", 50000);
				System.out.println( r );
				r = app.writeCheck("12121", 200);
				System.out.println( r );
				app.setTaxId("201674933");
				r = app.transfer("41725", "19023", 1000);
				System.out.println( r );

				r = app.setDate(2011, 3, 9);
				System.out.println( r );

				app.setTaxId("401605312");
				r = app.wire("41725", "32156", 4000);
				System.out.println( r );
				r = app.payFriend("53027", "60413", 10);
				System.out.println( r );

				r = app.setDate(2011, 3, 10);
				System.out.println( r );

				app.setTaxId("400651982");
				r = app.purchase("60413", 15);
				System.out.println( r );

				r = app.setDate(2011, 3, 12);
				System.out.println( r );

				app.setTaxId("203491209");
				r = app.withdrawal("93156", 20000);
				System.out.println( r );
				r = app.writeCheck("76543", 456);
				System.out.println( r );
				r = app.topUp("67521", 50);
				System.out.println( r );

				r = app.setDate(2011, 3, 14);
				System.out.println( r );

				r = app.payFriend("67521", "53027", 20);
				System.out.println( r );
				app.setTaxId("212116070");
				r = app.collect("43947", "29107", 15);
				System.out.println( r );

			} else if (input.equals("5")) {
				r = app.setDate(2008, 11, 10);
				System.out.println( r );

				// simple case of making student checking
				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "17431", 1000, "344151573", "Joe Pepsi", "3210 State St" );
				System.out.println( r );
				// simple case of making interest checking
				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "41725", 15000, "201674933", "George Brush", "5346 Foothill Av" );
				System.out.println( r );
				// simple case of making savings
				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "29107", 34000, "209378521", "Kelvin Costner", "Santa Cruz #3579" );
				System.out.println( r );
				// simple case of making pocket account
				r = app.createPocketAccount("43947", "29107", 50, "209378521");
				System.out.println( r );
				r = app.createPocketAccount("76543", "41725", 20, "201674933");
				System.out.println( r );
				r = app.createPocketAccount("65656", "17431", 30, "344151573");
				System.out.println( r );
				// simple case of making customer
				r = app.createCustomer("29107", "400651982", "Pit Wilson", "911 State St");
				System.out.println( r );
				// simple case of showing interest balance
				r = app.showBalance("41725");
				System.out.println( r );
				// simple case of showing savings balance
				r = app.showBalance("29107");
				System.out.println( r );
				// simple case of showing pocket balance
				r = app.showBalance("43947");
				System.out.println( r );
				// simple case of showing student balance
				r = app.showBalance("17431");
				System.out.println( r );
				// simple case of listing closed (when no closed)
				r = app.listClosedAccounts();
				System.out.println( r );
				// simple case of regular deposit
				r = app.deposit("41725", 100);
				System.out.println( r );
				// simple case of pay friend
				r = app.payFriend("43947", "76543", 10);
				System.out.println( r );

				// case of making checking/savings with existing account id
				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "17431", 1500, "123456789", "Trial Run", "123 Fail St" );
				System.out.println( r );
				// case of making checking/savings with < 1000
				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "11111", 900, "123456789", "Trial Run", "123 Fail St" );
				System.out.println( r );
				// case of making checking/savings with < 0
				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "22222", -1, "123456789", "Trial Run", "123 Fail St" );
				System.out.println( r );
				// case of making checking/savings with existing customer
				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "54321", 1000, "344151573", "Joe Pepsi", "3210 State St" );
				System.out.println( r );

				// case of making pocket with non-existent parent account
				r = app.createPocketAccount("76543", "34567", 20, "201674933");
				System.out.println( r );
				// case of making pocket with account that the customer doesn't own
				r = app.createPocketAccount("76543", "29107", 20, "201674933");
				System.out.println( r );
				// case of making pocket with non-existent customer
				r = app.createPocketAccount("76543", "41725", 20, "999999999");
				System.out.println( r );
				// close account and then list closed accounts
				r = app.topUp("76543", 15075);
				System.out.println( r );
				r = app.listClosedAccounts();
				System.out.println( r );
				// case of making pocket with closed account
				r = app.createPocketAccount("00000", "41725", 20, "201674933");
				System.out.println( r );
				// case of making pocket with < 0.02
				r = app.createPocketAccount("33333", "29107", 0.01, "209378521");
				System.out.println( r );
				// case of making pocket with < 0
				r = app.createPocketAccount("12121", "29107", -0.02, "209378521");
				System.out.println( r );

				// case of creating a customer with existing customer id
				r = app.createCustomer("17431", "400651982", "Tommy Hilfiger", "555 Brand St");
				System.out.println( r );
				// case of creating a customer with non-existent account
				r = app.createCustomer("66666", "876543210", "Tommy Hilfiger", "555 Brand St");
				System.out.println( r );

				// case of top-up (on existing pocket)
				r = app.topUp("43947", 1000);
				System.out.println( r );
				// case of top-up with invalid pocket id
				r = app.topUp("88888", 1000);
				System.out.println( r );
				// case of top-up where parent account funds insufficient
				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "77777", 5000, "987654321", "For Pocket", "111 Oops St" );
				System.out.println( r );
				r = app.createPocketAccount("10101", "77777", 5000, "987654321");
				System.out.println( r );
				// case of top-up where not a pocket account
				r = app.topUp("29107", 1000);
				System.out.println( r );

				// case of showing balance for non-existent account
				r = app.showBalance("01010");
				System.out.println( r );
				// case of showing balance for a closed account
				r = app.showBalance("41725");
				System.out.println( r );

				// case of deposit with a negative amount
				r = app.deposit("41725", -100);
				System.out.println( r );
				// case of deposit with non-existent account
				r = app.deposit("41414", 100);
				System.out.println( r );
				// case of deposit on closed account
				r = app.deposit("41725", 100);
				System.out.println( r );
				// case of deposit from pocket
				r = app.deposit("43947", 100);
				System.out.println( r );

				// case of pay friend when "to" is not a pocket account
				r = app.payFriend("43947", "77777", 10);
				System.out.println( r );
				// case of pay friend when "from" is not a pocket account
				r = app.payFriend("77777", "43947", 10);
				System.out.println( r );
				// case of pay friend when both are not pocket accounts
				r = app.payFriend("77777", "29107", 10);
				System.out.println( r );
				// case of pay friend when insufficient funds
				r = app.payFriend("43947", "65656", 5000);
				System.out.println( r );

				// case of invalid day
				r = app.setDate(2008, 11, 50);
				System.out.println( r );
				// case of invalid month
				r = app.setDate(2008, 15, 10);
				System.out.println( r );
				// case of invalid year
				r = app.setDate(25032, 11, 10);
				System.out.println( r );

			} else {
				//app.exampleAccessToDB();                // Example on how to connect to the DB.

				// Example tests.  We'll overwrite your Main.main() function with our final tests.

				// Another example test.
				r = app.setDate(2019, 11, 30);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "17431", 1000, "344151573", "Joe Pepsi", "3210 State St" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "54321", 21000, "212431965", "Hurryson Ford", "678 State St" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "12121", 1200, "207843218", "David Copperfill", "1357 State St" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "41725", 15000, "201674933", "George Brush", "5346 Foothill Av" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "76543", 8456, "212116070", "Li Kung", "2 People's Rd Beijing" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "93156", 2000000, "209378521", "Kelvin Costner", "Santa Cruz #3579" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "43942", 1289, "361721022", "Alfred Hitchcock", "6667 El Colegio #40" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "29107", 34000, "209378521", "Kelvin Costner", "Santa Cruz #3579" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "19023", 2300, "412231856", "Cindy Laugher", "7000 Hollister" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCustomer("43942", "400651982", "Pit Wilson", "911 State St");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("60413", "43942", 20, "400651982");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "32156", 1000, "188212217", "Michael Jordon", "3852 Court Rd" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createOwners("212116070", "29107");
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.createOwners("212116070", "60413");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.listClosedAccounts();
				System.out.println( r );

				System.out.println("----------------------------");
				
				r = app.showBalance("19023");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("43947", "29107", 30, "212116070");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "34567", 20.75, "123456789", "Tester McTesting", "6565 Segovia" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "23456", 1120.75, "123456789", "Tester McTesting", "6565 Segovia" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCustomer("54321", "122219876", "Elizabeth Sailor", "4321 State St");
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.createCustomer("54321", "122219876", "Elizabeth Sailor", "4321 State St");
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.createCustomer("00000", "987654321", "Just Testing", "4321 Testable St");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount(AccountType.SAVINGS, "12345", 5000, "122219876", "Elizabeth Sailor", "4321 State St");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.withdrawal("54321", 3000);
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.purchase("54321", 3000);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.purchase("43947", 10);
				System.out.println( r );

				System.out.println("----------------------------");

				app.setTaxId("212116070");

				r = app.wire("76543", "12345", 7309.80);
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.wire("76543", "29107", 5);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.showBalance("76543");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.transfer("76543", "29107", 999.99);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.payFriend("43947", "60413", 5);
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.payFriend("43947", "43947", 5);
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.payFriend("43942", "17431", 289);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.listClosedAccounts();
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.writeCheck("93156", 1000000);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.showBalance("93156");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.showBalance("29107");
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail, insufficient funds
				r = app.wire("29107", "93156", 34964.99);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.setDate(10, 21, 5);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.generateCustomerReport("212116070");
				System.out.println( r );

				System.out.println("----------------------------");

				boolean check = app.verifyPIN("1717");
				System.out.println( check );

				System.out.println("----------------------------");

				double num = app.getNumDays();
				System.out.println(Double.toString(num));

				System.out.println("----------------------------");

				boolean end = app.checkEndOfMonth();
				System.out.println(end);

				System.out.println("----------------------------");

				r = app.setNewInterest(2.5, "student");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.withdrawal("19023", 2300);
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.withdrawal("29107", 10000000);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.writeCheck("17431", 10);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.generateMonthly("212116070");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.generateMonthly("209378521");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.generateDTER();
				System.out.println( r );

				System.out.println("----------------------------");

				//r = app.deleteClosed();
				//System.out.println( r );

				System.out.println("----------------------------");

				//r = app.deleteTransactions();
				//System.out.println( r );

				System.out.println("----------------------------");

				r = app.generateCustomerReport("212116070");
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.generateCustomerReport("000000000");
				System.out.println( r );

				System.out.println("----------------------------");

				app.setPIN("1717", "9999");

				System.out.println("----------------------------");

				r = app.setDate(2019, 12, 31);
				System.out.println( r );

				//double num2 = app.calculateAverage("67890", 1500.75, 30.0);
				//System.out.println(Double.toString(num2));
			}
			s.close();
		}
	}
	//!### FINALIZAMOS
}
