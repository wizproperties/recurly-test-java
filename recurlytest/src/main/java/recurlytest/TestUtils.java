package recurlytest;

import com.ning.billing.recurly.model.Account;
import com.ning.billing.recurly.model.Address;
import com.ning.billing.recurly.model.BillingInfo;

public class TestUtils {

	

    public static Account createAccount1() {
        final Account account = new Account();

        account.setAcceptLanguage("en_US");
        account.setAccountCode("1234567890");
        account.setCompanyName("SomeCompany");
        account.setEmail("someemail" + "@test.com");
        account.setFirstName("Joe");
        account.setLastName("Test");
        account.setAddress(createAddressNumeric(1234));
        account.setVatNumber("123456789012345");

        return account;
    }
    
    public static BillingInfo createRandomBillingInfo() {
        final BillingInfo info = new BillingInfo();

        info.setAccount(createAccount1());
        info.setFirstName("Joe");
        info.setLastName("Test");
        info.setCompany("SomeCompany");
        
        info.setAddress1("4321" + " Fake St");
        info.setAddress2("");
        info.setCity("Boulder, CO");
        info.setState("CO");
        info.setZip("80305");
        info.setCountry("US");
        info.setPhone("8881231234");
        
        info.setVatNumber("123456789012345");
        info.setYear(createTestCCYear());
        info.setMonth(createTestCCMonth());
        info.setNumber(createTestCCNumber());
        info.setVerificationValue(createTestCCVerificationNumber());

        return info;
    }
    
    public static Address createAddressNumeric(final int seed) {
        final Address address = new Address();

        address.setAddress1(seed + " Fake St");
        address.setAddress2("");
        address.setCity("Boulder, CO");
        address.setState("CO");
        address.setZip("80305");
        address.setCountry("US");
        address.setPhone("8881231234");

        return address;
    }
    
    public static String createTestCCNumber() {
        return "4111-1111-1111-1111";
    }

    public static String createTestCCVerificationNumber() {
        return "123";
    }

    public static String createTestCCMonth() {
        return "11";
    }

    public static String createTestCCYear() {
        return "2020";
    }
}
