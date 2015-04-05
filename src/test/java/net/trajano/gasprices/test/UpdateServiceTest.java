package net.trajano.gasprices.test;

import net.trajano.gasprices.GasPricesUpdateService_;
import android.content.Intent;
import android.test.ServiceTestCase;

public class UpdateServiceTest extends ServiceTestCase<GasPricesUpdateService_> {
	/**
	 * Must set up a default constructor where the class is passed to the super
	 * class constructor.
	 */
	public UpdateServiceTest() {
		super(GasPricesUpdateService_.class);
	}

	/**
	 * Prove the test framework works.
	 */
	public void testNothing() {

	}

	/**
	 * This tests starting the service.
	 */
	public void testStartService() {
		getService();
	}
}
