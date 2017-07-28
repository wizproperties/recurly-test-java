package recurlytest;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ning.billing.recurly.RecurlyAPIException;
import com.ning.billing.recurly.RecurlyClient;
import com.ning.billing.recurly.model.Account;
import com.ning.billing.recurly.model.BillingInfo;
import com.ning.billing.recurly.model.Plan;
import com.ning.billing.recurly.model.Subscription;

public class ActualRecurlyTest {

	public static final String RECURLY_PAGE_SIZE = "100";
//  public static final String RECURLY_API_KEY = "9dc5244d81c04c848dd23748e5e073c7"; //default
//  public static final String RECURLY_API_KEY = "ewr1-aKa0ZpgJgzHUVSMDbqdnXf"; //public
  public static final String RECURLY_API_KEY = "b7ae615dc08e4c2398947d305def6ed3"; //private
  
  public static final String RECURLY_SUBDOMAIN = "wizproperties.recurly.com";
//  public static final String RECURLY_DEFAULT_CURRENCY_KEY = "thisdoesntmatter";
	private static final String MY_PLAN_CODE = "shiny";

  // Default to USD for all tests, which is expected to be supported by Recurly by default
  private static final String CURRENCY = "USD";

  private RecurlyClient recurlyClient;

  /**
   * Sets up the recurlyClient for all API calls
   * @throws Exception
   */
	@BeforeClass
  public void setUp() throws Exception {

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

	/**
	 * Closes the recurlyClient
	 * @throws Exception
	 */
	@AfterClass
  public void tearDown() throws Exception {
      recurlyClient.close();
  }
  
  /**
   * This sets up a subscription through the API and then tests that it is actually created on the site (via the API).
   * On cleanup, it will then try to delete the sub and the account. 
   * @throws Exception if there are no subscriptions or there is a Recruly API exception
   */
  @Test(groups = "test1")
  public void testSetSiteSubscriptions() throws Exception {
      final Account accountData = TestUtils2.createAccount1();
      final Subscription subscriptionData = new Subscription();
      final BillingInfo billingInfoData = TestUtils2.createRandomBillingInfo();
      final Plan planData = recurlyClient.getPlan(MY_PLAN_CODE);

      try {
          final Account account = recurlyClient.createAccount(accountData);
          billingInfoData.setAccount(account);

          subscriptionData.setPlanCode(MY_PLAN_CODE);
          subscriptionData.setAccount(accountData);
          subscriptionData.setCurrency(CURRENCY);
//          subscriptionData.setUnitAmountInCents(1242); //these are not mandatory, removing for now
//          subscriptionData.setRemainingBillingCycles(1); // not mandatory

          // makes sure we have at least one subscription
          recurlyClient.createSubscription(subscriptionData);


          if (recurlyClient == null) {
          		Assert.fail("recurlyClient is null");
          }
          else if(recurlyClient.getSubscriptions() == null) {
          		Assert.fail("recurlyClient subscriptions is null");
          }
          
      } 
      catch (RecurlyAPIException exception) {
      		System.out.println("Recurly API exception" + exception.getLocalizedMessage());
      		Assert.fail("exception: " + exception.getLocalizedMessage());
      } catch (Exception exception) {
          		exception.printStackTrace();
          		Assert.fail("exception: " + exception.getLocalizedMessage());
          
      }finally {
          // Close the account and sub
      		recurlyClient.cancelSubscription(subscriptionData);
          recurlyClient.closeAccount(accountData.getAccountCode());
      }
  }
	
}
