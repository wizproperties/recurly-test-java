package recurlytest;

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
import com.ning.billing.recurly.model.Subscriptions;

public class SimpleTest2 {

    public static final String RECURLY_PAGE_SIZE = "100";
    public static final String RECURLY_API_KEY = "9dc5244d81c04c848dd23748e5e073c7";
    public static final String RECURLY_SUBDOMAIN = "";
    public static final String RECURLY_DEFAULT_CURRENCY_KEY = "thisdoesntmatter";
	private static final String MY_PLAN_CODE = null;

    // Default to USD for all tests, which is expected to be supported by Recurly by default
    private static final String CURRENCY = System.getProperty(RECURLY_DEFAULT_CURRENCY_KEY, "USD");

    private RecurlyClient recurlyClient;

    @BeforeClass(groups = {"integration", "enterprise"})
    public void setUp() throws Exception {
        final String apiKey = System.getProperty(RECURLY_API_KEY);
        String subDomainTemp = System.getProperty(RECURLY_SUBDOMAIN);

        if (apiKey == null) {
            Assert.fail("You need to set your Recurly api key to run integration tests:" +
                        " -Dkillbill.payment.recurly.apiKey=...");
        }
        
        if (subDomainTemp == null) {
          subDomainTemp = "api";
        }
        
        final String subDomain = subDomainTemp;

        recurlyClient = new RecurlyClient(apiKey, subDomain);
        recurlyClient.open();
    }

    @AfterClass(groups = {"integration", "enterprise"})
    public void tearDown() throws Exception {
        recurlyClient.close();
    }

    @Test(groups = "integration")
    public void testUnauthorizedException() throws Exception {
        final String subdomain = System.getProperty(RECURLY_SUBDOMAIN);
        RecurlyClient unauthorizedRecurlyClient = new RecurlyClient("invalid-api-key", subdomain);
        unauthorizedRecurlyClient.open();

        try {
            unauthorizedRecurlyClient.getAccounts();
            Assert.fail("getAccounts call should not succeed with invalid credentials.");
        } catch (RecurlyAPIException expected) {
            Assert.assertEquals(expected.getRecurlyError().getSymbol(), "unauthorized");
        }
    }

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

    @Test(groups = "integration")
    public void testGetSiteSubscriptions() throws Exception {
        final Account accountData = TestUtils.createAccount1();
        final BillingInfo billingInfoData = TestUtils.createRandomBillingInfo();
        final Plan planData = recurlyClient.getPlan(MY_PLAN_CODE);

        try {
            final Account account = recurlyClient.createAccount(accountData);
            billingInfoData.setAccount(account);
            final BillingInfo billingInfo = recurlyClient.createOrUpdateBillingInfo(billingInfoData);
            final Plan plan = recurlyClient.createPlan(planData);

            final Subscription subscriptionData = new Subscription();
            subscriptionData.setPlanCode(plan.getPlanCode());
            subscriptionData.setAccount(accountData);
            subscriptionData.setCurrency(CURRENCY);
            subscriptionData.setUnitAmountInCents(1242);
            subscriptionData.setRemainingBillingCycles(1);

            // makes sure we have at least one subscription
            recurlyClient.createSubscription(subscriptionData);

            // make sure we return more than one subscription
            Assert.assertTrue(recurlyClient.getSubscriptions().size() > 0);
        } finally {
            // Close the account
            recurlyClient.closeAccount(accountData.getAccountCode());
        }
    }
	
}
