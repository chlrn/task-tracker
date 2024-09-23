/*
 * This file is generated by jOOQ.
 */
package org.jooq.generated.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Check;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function2;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.generated.Keys;
import org.jooq.generated.Public;
import org.jooq.generated.tables.records.TeamWorkflowRecord;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TeamWorkflow extends TableImpl<TeamWorkflowRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.team_workflow</code>
     */
    public static final TeamWorkflow TEAM_WORKFLOW = new TeamWorkflow();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TeamWorkflowRecord> getRecordType() {
        return TeamWorkflowRecord.class;
    }

    /**
     * The column <code>public.team_workflow.team_id</code>.
     */
    public final TableField<TeamWorkflowRecord, Long> TEAM_ID = createField(DSL.name("team_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.team_workflow.workflow</code>.
     */
    public final TableField<TeamWorkflowRecord, String> WORKFLOW = createField(DSL.name("workflow"), SQLDataType.VARCHAR(255), this, "");

    private TeamWorkflow(Name alias, Table<TeamWorkflowRecord> aliased) {
        this(alias, aliased, null);
    }

    private TeamWorkflow(Name alias, Table<TeamWorkflowRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.team_workflow</code> table reference
     */
    public TeamWorkflow(String alias) {
        this(DSL.name(alias), TEAM_WORKFLOW);
    }

    /**
     * Create an aliased <code>public.team_workflow</code> table reference
     */
    public TeamWorkflow(Name alias) {
        this(alias, TEAM_WORKFLOW);
    }

    /**
     * Create a <code>public.team_workflow</code> table reference
     */
    public TeamWorkflow() {
        this(DSL.name("team_workflow"), null);
    }

    public <O extends Record> TeamWorkflow(Table<O> child, ForeignKey<O, TeamWorkflowRecord> key) {
        super(child, key, TEAM_WORKFLOW);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public List<ForeignKey<TeamWorkflowRecord, ?>> getReferences() {
        return Arrays.asList(Keys.TEAM_WORKFLOW__FKM7I4QNPXG1KNQ2EMP3IUY82AM);
    }

    private transient Team _team;

    /**
     * Get the implicit join path to the <code>public.team</code> table.
     */
    public Team team() {
        if (_team == null)
            _team = new Team(this, Keys.TEAM_WORKFLOW__FKM7I4QNPXG1KNQ2EMP3IUY82AM);

        return _team;
    }

    @Override
    public List<Check<TeamWorkflowRecord>> getChecks() {
        return Arrays.asList(
            Internal.createCheck(this, DSL.name("team_workflow_workflow_check"), "(((workflow)::text = ANY ((ARRAY['TODO'::character varying, 'IN_PROGRESS'::character varying, 'REVIEW'::character varying, 'TEST'::character varying, 'DONE'::character varying])::text[])))", true)
        );
    }

    @Override
    public TeamWorkflow as(String alias) {
        return new TeamWorkflow(DSL.name(alias), this);
    }

    @Override
    public TeamWorkflow as(Name alias) {
        return new TeamWorkflow(alias, this);
    }

    @Override
    public TeamWorkflow as(Table<?> alias) {
        return new TeamWorkflow(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TeamWorkflow rename(String name) {
        return new TeamWorkflow(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TeamWorkflow rename(Name name) {
        return new TeamWorkflow(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TeamWorkflow rename(Table<?> name) {
        return new TeamWorkflow(name.getQualifiedName(), null);
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
