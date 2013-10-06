package org.solovyev.android.messenger.users;

import com.google.inject.Inject;
import org.junit.Test;
import org.solovyev.android.db.Dao;
import org.solovyev.android.messenger.DefaultDaoTest;
import org.solovyev.android.messenger.accounts.TestAccount;
import org.solovyev.android.properties.AProperty;
import org.solovyev.android.properties.MutableAProperties;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.solovyev.android.messenger.users.Users.newNeverSyncedUserSyncData;
import static org.solovyev.android.messenger.users.Users.newUser;
import static org.solovyev.android.properties.Properties.newProperties;

public class UserDaoTest extends DefaultDaoTest<User> {

	@Inject
	@Nonnull
	private UserDao dao;

	@Nonnull
	@Override
	protected Dao<User> getDao() {
		return dao;
	}

	@Test
	public void testShouldReadAllEntities() throws Exception {
		testShouldReadAllEntitiesForAccount(getAccount1(), ACCOUNT_1_USER_COUNT, getUsers1());
		testShouldReadAllEntitiesForAccount(getAccount2(), ACCOUNT_2_USER_COUNT, getUsers2());
		testShouldReadAllEntitiesForAccount(getAccount3(), ACCOUNT_3_USER_COUNT, getUsers3());

	}

	private void testShouldReadAllEntitiesForAccount(@Nonnull TestAccount account, int count, @Nonnull List<User> users) {
		assertTrue(UsersTest.areSame(account.getUser(), dao.read(account.getUser().getId())));
		for(int i = 0; i < count; i++) {
			final User user = dao.read(getEntityForUser(account, i).getEntityId());
			assertTrue(UsersTest.areSame(users.get(1 + i), user));
		}
	}

	@Nonnull
	@Override
	protected Collection<User> populateEntities(@Nonnull Dao<User> dao) {
		final List<User> users = new ArrayList<User>();
		users.addAll(getUsers1());
		users.addAll(getUsers2());
		users.addAll(getUsers3());
		return users;
	}

	@Nonnull
	@Override
	protected Entity<User> newInsertEntity() {
		final MutableAProperties properties = newProperties(Collections.<String, AProperty>emptyMap());
		final org.solovyev.android.messenger.entities.Entity entity = getAccount1().newUserEntity("test");
		final User user = newUser(entity, newNeverSyncedUserSyncData(), properties);
		return newEntity(user);
	}

	@Override
	protected boolean areSame(@Nonnull User e1, @Nonnull User e2) {
		return UsersTest.areSame(e1, e2);
	}

	@Nonnull
	@Override
	protected User changeEntity(@Nonnull User user) {
		return user;
	}
}