package org.example.repository;

import org.example.entity.User;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.UsersRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.generated.Tables.USERS;

@Repository
public class UserRepository {

    private final DSLContext dsl;

    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public User save(User user) {
        UsersRecord userRecord = dsl.insertInto(USERS)
                .set(USERS.EMAIL, user.getEmail())
                .set(USERS.NAME, user.getName())
                // предполагается, что у вас есть связь с таблицей ролей
                .returning()
                .fetchOne();

        return mapRecordToUser(userRecord);
    }

    public Optional<User> findById(Long userId) {
        UsersRecord userRecord = dsl.selectFrom(USERS)
                .where(USERS.ID.eq(Long.valueOf(Math.toIntExact(userId))))
                .fetchOne();

        return userRecord != null ? Optional.of(mapRecordToUser(userRecord)) : Optional.empty();
    }

    public Optional<User> findByEmail(String email) {
        UsersRecord userRecord = dsl.selectFrom(USERS)
                .where(USERS.EMAIL.eq(email))
                .fetchOne();

        return userRecord != null ? Optional.of(mapRecordToUser(userRecord)) : Optional.empty();
    }

    public List<User> findAll() {
        return dsl.selectFrom(USERS)
                .fetch()
                .map(this::mapRecordToUser);
    }

    private User mapRecordToUser(UsersRecord record) {
        User user = new User();
        user.setId(Long.valueOf(record.getId()));
        user.setEmail(record.getEmail());
        user.setName(record.getName());
        return user;
    }
}
