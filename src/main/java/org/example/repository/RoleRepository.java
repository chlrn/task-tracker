package org.example.repository;

import org.example.entity.Role;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.RolesRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.generated.Tables.ROLES;

@Repository
public class RoleRepository {

    private final DSLContext dsl;

    public RoleRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Role save(Role role) {
        RolesRecord roleRecord = dsl.insertInto(ROLES)
                .set(ROLES.NAME, role.getName())
                .returning()
                .fetchOne();

        return mapRecordToRole(roleRecord);
    }

    public Optional<Role> findById(Long roleId) {
        RolesRecord roleRecord = dsl.selectFrom(ROLES)
                .where(ROLES.ID.eq(Long.valueOf(Math.toIntExact(roleId))))
                .fetchOne();

        return roleRecord != null ? Optional.of(mapRecordToRole(roleRecord)) : Optional.empty();
    }

    public Optional<Role> findByName(String roleName) {
        RolesRecord roleRecord = dsl.selectFrom(ROLES)
                .where(ROLES.NAME.eq(roleName))
                .fetchOne();

        return roleRecord != null ? Optional.of(mapRecordToRole(roleRecord)) : Optional.empty();
    }

    public List<Role> findAll() {
        return dsl.selectFrom(ROLES)
                .fetch()
                .map(this::mapRecordToRole);
    }

    private Role mapRecordToRole(RolesRecord record) {
        Role role = new Role(null, "ROLE_MANAGER");
        role.setId(Long.valueOf(record.getId()));
        role.setName(record.getName());
        return role;
    }
}
