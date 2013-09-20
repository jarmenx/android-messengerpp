package org.solovyev.android.messenger.realms.sms;

import org.solovyev.android.captcha.ResolvedCaptcha;
import org.solovyev.android.messenger.realms.*;
import org.solovyev.android.messenger.security.InvalidCredentialsException;
import org.solovyev.android.messenger.users.User;
import org.solovyev.android.messenger.users.Users;
import org.solovyev.android.properties.AProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

/**
 * User: serso
 * Date: 5/27/13
 * Time: 8:47 PM
 */
final class SmsRealmBuilder extends AbstractRealmBuilder<SmsAccountConfiguration> {

	SmsRealmBuilder(@Nonnull RealmDef realmDef, @Nullable Account editedAccount, @Nonnull SmsAccountConfiguration configuration) {
		super(realmDef, configuration, editedAccount);
	}

	@Nonnull
	@Override
	protected User getRealmUser(@Nonnull String realmId) {
		return Users.newUser(realmId, SmsRealmDef.USER_ID, Users.newNeverSyncedUserSyncData(), Collections.<AProperty>emptyList());
	}

	@Nonnull
	@Override
	protected Account newRealm(@Nonnull String id, @Nonnull User user, @Nonnull AccountState state) {
		return new SmsAccount(id, getRealmDef(), user, getConfiguration(), state);
	}

	@Override
	public void connect() throws ConnectionException {

	}

	@Override
	public void disconnect() throws ConnectionException {

	}

	@Override
	public void loginUser(@Nullable ResolvedCaptcha resolvedCaptcha) throws InvalidCredentialsException {
	}
}
