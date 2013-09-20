package org.solovyev.android.messenger;

import org.solovyev.android.messenger.realms.Account;
import org.solovyev.android.messenger.realms.RealmConnectionException;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 7/25/12
 * Time: 5:52 PM
 */

/**
 * Connection to remote realm.
 * <p/>
 * This class is often used in background and listens to remote events (e.g. implementing long polling).
 * To start listening one must call {@link RealmConnection#start()} method,
 * to finish listening - {@link RealmConnection#stop()}.
 * <p/>
 * Application can toggle state quite often (due, for example, to connectivity problems).
 */
public interface RealmConnection {

	@Nonnull
	Account getRealm();

	/**
	 * Method starts listening to remote realm events
	 */
	void start() throws RealmConnectionException;

	/**
	 * Method stops listening to remove realm events
	 */
	void stop();

	boolean isStopped();
}
