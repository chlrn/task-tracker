/*
 * This file is generated by jOOQ.
 */
package org.jooq.generated.tables;


import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function2;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.generated.Keys;
import org.jooq.generated.Public;
import org.jooq.generated.tables.records.TeamRecord;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Team extends TableImpl<TeamRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.team</code>
     */
    public static final Team TEAM = new Team();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TeamRecord> getRecordType() {
        return TeamRecord.class;
    }

    /**
     * The column <code>public.team.id</code>.
     */
    public final TableField<TeamRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.team.name</code>.
     */
    public final TableField<TeamRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(255), this, "");

    private Team(Name alias, Table<TeamRecord> aliased) {
        this(alias, aliased, null);
    }

    private Team(Name alias, Table<TeamRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.team</code> table reference
     */
    public Team(String alias) {
        this(DSL.name(alias), TEAM);
    }

    /**
     * Create an aliased <code>public.team</code> table reference
     */
    public Team(Name alias) {
        this(alias, TEAM);
    }

    /**
     * Create a <code>public.team</code> table reference
     */
    public Team() {
        this(DSL.name("team"), null);
    }

    public <O extends Record> Team(Table<O> child, ForeignKey<O, TeamRecord> key) {
        super(child, key, TEAM);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<TeamRecord, Long> getIdentity() {
        return (Identity<TeamRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<TeamRecord> getPrimaryKey() {
        return Keys.TEAM_PKEY;
    }

    @Override
    public Team as(String alias) {
        return new Team(DSL.name(alias), this);
    }

    @Override
    public Team as(Name alias) {
        return new Team(alias, this);
    }

    @Override
    public Team as(Table<?> alias) {
        return new Team(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Team rename(String name) {
        return new Team(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Team rename(Name name) {
        return new Team(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Team rename(Table<?> name) {
        return new Team(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function2<? super Long, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function2<? super Long, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
