package recurlytest;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ning.billing.recurly.RecurlyAPIException;
import com.ning.billing.recurly.RecurlyClient;
import com.ning.billing.recurly.model.Account;
import com.ning.billing.recurly.model.BillingInfo;
import com.ning.billing.recurly.model.Plan;
import com.ning.billing.recurly.model.Subscription;

public class Simple2Test {

    public static final String RECURLY_PAGE_SIZE = "100";
//    public static final String RECURLY_API_KEY = "9dc5244d81c04c848dd23748e5e073c7"; //default
//    public static final String RECURLY_API_KEY = "ewr1-aKa0ZpgJgzHUVSMDbqdnXf"; //public
    public static final String RECURLY_API_KEY = "b7ae615dc08e4c2398947d305def6ed3"; //private
    
    public static final String RECURLY_SUBDOMAIN = "wizproperties.recurly.com";
//    public static final String RECURLY_DEFAULT_CURRENCY_KEY = "thisdoesntmatter";
	private static final String MY_PLAN_CODE = "shiny";

    // Default to USD for all tests, which is expected to be supported by Recurly by default
    private static final String CURRENCY = "USD";

    private RecurlyClient recurlyClient;

    @BeforeMethod
	@BeforeClass(groups = "myintegration")
    public void setUp() throws Exception {
        //final String apiKey = System.getProperty(RECURLY_API_KEY);
        //String subDomainTemp = System.getProperty(RECURLY_SUBDOMAIN);

    	
    	
    		final String apiKey = RECURLY_API_KEY;
    		String subDomainTemp = RECURLY_SUBDOMAIN;
    	
        if (apiKey == null) {
            AssertJUnit.fail("You need to set your Recurly api key to run integration tests:" +
                        " -Dkillbill.payment.recurly.apiKey=...");
        }
        
        if (subDomainTemp == null) {
          subDomainTemp = "api";
        }
        
        final String subDomain = subDomainTemp;

        recurlyClient = new RecurlyClient(apiKey, subDomain);
        recurlyClient.open();
    		
        System.out.println("Setup complete");
    }

    @AfterMethod
	@AfterClass(groups = "myintegration")
    public void tearDown() throws Exception {
        recurlyClient.close();
    }

    @Test(groups = "myintegration")
    public void testUnauthorizedException() throws Exception {
        //final String subdomain = System.getProperty(RECURLY_SUBDOMAIN);
    		final String subdomain = RECURLY_SUBDOMAIN;	
        RecurlyClient unauthorizedRecurlyClient = new RecurlyClient("invalid-api-key", subdomain);
        unauthorizedRecurlyClient.open();

        try {
            unauthorizedRecurlyClient.getAccounts();
            AssertJUnit.fail("getAccounts call should not succeed with invalid credentials.");
        } catch (RecurlyAPIException expected) {
            AssertJUnit.assertEquals(expected.getRecurlyError().getSymbol(), "unauthorized");
        }
    }
/*
    @Test(groups = "integration", description = "See https://github.com/killbilling/recurly-java-library/issues/21")
    public void testGetEmptySubscriptions() throws Exception {
        final Account accountData = TestUtils.createAccount1();
        final BillingInfo billingInfoData = TestUtils.createRandomBillingInfo();

        try {
            // Create a user
            final Account account = recurlyClient.createAccount(accountData);

            // Create BillingInfo
            billingInfoData.setAccount(account);
            final BillingInfo billingInfo = recurlyClient.createOrUpdateBillingInfo(billingInfoData);
            Assert.assertNotNull(billingInfo);
            final BillingInfo retrievedBillingInfo = recurlyClient.getBillingInfo(account.getAccountCode());
            Assert.assertNotNull(retrievedBillingInfo);

            final Subscriptions subs = recurlyClient.getAccountSubscriptions(accountData.getAccountCode(), "active");
            Assert.assertEquals(subs.size(), 0);
        } finally {
            // Close the account
            recurlyClient.closeAccount(accountData.getAccountCode());
        }
    }
*/
    
    @Test(groups = "simpleSetAccounts")
    public void testSetAccounts() throws Exception {
    		
    	try {
    		Account accountData = TestUtils2.createAccount1();
    	
    		Account account = recurlyClient.createAccount(accountData);
    		System.out.println("Account created");
    		
    		recurlyClient.getAccounts();
    		Assert.assertNotEquals(recurlyClient.getAccounts(), null);
    	}
    	catch (Exception e) {
    		System.out.println(e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    @Test(groups = "myintegration")
    public void testGetSiteSubscriptions() throws Exception {
        final Account accountData = TestUtils2.createAccount1();
        final BillingInfo billingInfoData = TestUtils2.createRandomBillingInfo();
        final Plan planData = recurlyClient.getPlan(MY_PLAN_CODE);

        try {
            final Account account = recurlyClient.createAccount(accountData);
            billingInfoData.setAccount(account);
//            final BillingInfo billingInfo = recurlyClient.createOrUpdateBillingInfo(billingInfoData);
            //final Plan plan = planData;

            Subscription subscriptionData = new Subscription();
//            subscriptionData.setPlanCode(plan.getPlanCode());
            subscriptionData.setPlanCode(MY_PLAN_CODE);
            subscriptionData.setAccount(accountData);
            subscriptionData.setCurrency(CURRENCY);
//            subscriptionData.setUnitAmountInCents(1242);
//            subscriptionData.setRemainingBillingCycles(1);

            // makes sure we have at least one subscription
            recurlyClient.createSubscription(subscriptionData);


            if (recurlyClient == null) {
            		AssertJUnit.fail("recurlyClient is null");
            }
            else if(recurlyClient.getSubscriptions() == null) {
            		AssertJUnit.fail("recurlyClient subscriptions is null");
            }
            else {
	            System.out.println("Sub size: " + recurlyClient.getSubscriptions().size());
	            // make sure we return more than one subscription
	            AssertJUnit.assertTrue(recurlyClient.getSubscriptions().size() > 0);
            }
            
        } 
        catch (RecurlyAPIException exception) {
        		System.out.println("exception: " + exception.getLocalizedMessage());
        		AssertJUnit.fail("exception: " + exception.getLocalizedMessage());
        } catch (Exception exception) {
            		exception.printStackTrace();
            		AssertJUnit.fail("exception: " + exception.getLocalizedMessage());
            
        }finally {
            // Close the account
//            recurlyClient.closeAccount(accountData.getAccountCode());
        }
    }
	
}
