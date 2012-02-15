package net.trajano.gasprices.test;

import java.util.Date;

import junit.framework.TestCase;
import net.trajano.gasprices.PreferenceAdaptor;
import android.text.format.Time;

/**
 * Test scenarios to ensure that update checks work.
 * 
 * @author Archimedes Trajano (developer@trajano.net)
 * 
 */
public class UpdateCheckTest extends TestCase {
	/**
	 * If I last updated at 8:01pm, the next scheduled update from that date is
	 * at midnight the next day.
	 */
	public void testAfter8pm() {
		final Date lastUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.hour = 20;
			t.minute = 0;
			t.second = 1;
			lastUpdate = new Date(t.normalize(false));
		}

		final Date nextExpectedUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.monthDay += 1;
			t.hour = 0;
			t.minute = 0;
			t.second = 0;
			nextExpectedUpdate = new Date(t.normalize(false));
		}
		assertEquals(nextExpectedUpdate,
				PreferenceAdaptor.nextUpdateDate(lastUpdate));
	}

	public void testAt5pm() {
		final Date lastUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.hour = 17;
			t.minute = 0;
			t.second = 0;
			lastUpdate = new Date(t.normalize(false));
		}

		final Date nextExpectedUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.hour = 20;
			t.minute = 0;
			t.second = 0;
			nextExpectedUpdate = new Date(t.normalize(false));
		}
		assertEquals(nextExpectedUpdate,
				PreferenceAdaptor.nextUpdateDate(lastUpdate));
	}

	public void testAt8pm() {
		final Date lastUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.hour = 20;
			t.minute = 0;
			t.second = 0;
			lastUpdate = new Date(t.normalize(false));
		}

		final Date nextExpectedUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.monthDay += 1;
			t.hour = 0;
			t.minute = 0;
			t.second = 0;
			nextExpectedUpdate = new Date(t.normalize(false));
		}
		assertEquals(nextExpectedUpdate,
				PreferenceAdaptor.nextUpdateDate(lastUpdate));
	}

	public void testAtMidnight() {
		final Date lastUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.hour = 0;
			t.minute = 0;
			t.second = 0;
			lastUpdate = new Date(t.normalize(false));
		}

		final Date nextExpectedUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.hour = 17;
			t.minute = 0;
			t.second = 0;
			nextExpectedUpdate = new Date(t.normalize(false));
		}
		assertEquals(nextExpectedUpdate,
				PreferenceAdaptor.nextUpdateDate(lastUpdate));
	}

	/**
	 * Assuming the last update date was 8am today, the next one should be at
	 * 5pm today.
	 */
	public void testEarlyInTheMorning() {
		final Date lastUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.hour = 8;
			t.minute = 0;
			lastUpdate = new Date(t.normalize(false));
		}

		final Date nextExpectedUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.hour = 17;
			t.minute = 0;
			t.second = 0;
			nextExpectedUpdate = new Date(t.normalize(false));
		}
		assertEquals(nextExpectedUpdate,
				PreferenceAdaptor.nextUpdateDate(lastUpdate));
	}

	public void testOneSecondTo5pm() {
		final Date lastUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.hour = 16;
			t.minute = 59;
			t.second = 59;
			lastUpdate = new Date(t.normalize(false));
		}

		final Date nextExpectedUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.hour = 17;
			t.minute = 0;
			t.second = 0;
			nextExpectedUpdate = new Date(t.normalize(false));
		}
		assertEquals(nextExpectedUpdate,
				PreferenceAdaptor.nextUpdateDate(lastUpdate));
	}

	public void testSystemTimeVsNewDate() {
		assertEquals(System.currentTimeMillis(), new Date().getTime());
	}

	/**
	 * If it was last updated two days ago at 1pm, then the next update date
	 * should be two days ago at 5pm.
	 */
	public void testTwoDaysAgo() {
		final Date lastUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.monthDay -= 2;
			t.hour = 13;
			t.minute = 0;
			t.second = 0;
			lastUpdate = new Date(t.normalize(false));
		}

		final Date nextExpectedUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.monthDay -= 2;
			t.hour = 17;
			t.minute = 0;
			t.second = 0;
			nextExpectedUpdate = new Date(t.normalize(false));
		}
		assertEquals(nextExpectedUpdate,
				PreferenceAdaptor.nextUpdateDate(lastUpdate));
	}

	/**
	 * If it was last updated yesterday at 1pm, then the next update date should
	 * be yesterday at 5pm.
	 */
	public void testYesterday() {
		final Date lastUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.monthDay -= 1;
			t.hour = 13;
			t.minute = 0;
			t.second = 0;
			lastUpdate = new Date(t.normalize(false));
		}

		final Date nextExpectedUpdate;
		{
			final Time t = new Time();
			t.setToNow();
			t.monthDay -= 1;
			t.hour = 17;
			t.minute = 0;
			t.second = 0;
			nextExpectedUpdate = new Date(t.normalize(false));
		}
		assertEquals(nextExpectedUpdate,
				PreferenceAdaptor.nextUpdateDate(lastUpdate));
	}
}
