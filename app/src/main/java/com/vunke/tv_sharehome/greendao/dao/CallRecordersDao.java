package com.vunke.tv_sharehome.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.vunke.tv_sharehome.greendao.dao.bean.CallRecorders;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CALL_RECORDERS".
*/
public class CallRecordersDao extends AbstractDao<CallRecorders, Long> {

    public static final String TABLENAME = "CALL_RECORDERS";

    /**
     * Properties of entity CallRecorders.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property CallId = new Property(0, Long.class, "callId", true, "CALL_ID");
        public final static Property ContactName = new Property(1, String.class, "contactName", false, "CONTACT_NAME");
        public final static Property CreateTime = new Property(2, java.util.Date.class, "createTime", false, "CREATE_TIME");
        public final static Property CallTime = new Property(3, String.class, "callTime", false, "CALL_TIME");
        public final static Property CallType = new Property(4, String.class, "callType", false, "CALL_TYPE");
        public final static Property CallRecordersPhone = new Property(5, String.class, "callRecordersPhone", false, "CALL_RECORDERS_PHONE");
    };


    public CallRecordersDao(DaoConfig config) {
        super(config);
    }
    
    public CallRecordersDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CALL_RECORDERS\" (" + //
                "\"CALL_ID\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: callId
                "\"CONTACT_NAME\" TEXT," + // 1: contactName
                "\"CREATE_TIME\" INTEGER," + // 2: createTime
                "\"CALL_TIME\" TEXT," + // 3: callTime
                "\"CALL_TYPE\" TEXT," + // 4: callType
                "\"CALL_RECORDERS_PHONE\" TEXT);"); // 5: callRecordersPhone
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CALL_RECORDERS\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CallRecorders entity) {
        stmt.clearBindings();
 
        Long callId = entity.getCallId();
        if (callId != null) {
            stmt.bindLong(1, callId);
        }
 
        String contactName = entity.getContactName();
        if (contactName != null) {
            stmt.bindString(2, contactName);
        }
 
        java.util.Date createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(3, createTime.getTime());
        }
 
        String callTime = entity.getCallTime();
        if (callTime != null) {
            stmt.bindString(4, callTime);
        }
 
        String callType = entity.getCallType();
        if (callType != null) {
            stmt.bindString(5, callType);
        }
 
        String callRecordersPhone = entity.getCallRecordersPhone();
        if (callRecordersPhone != null) {
            stmt.bindString(6, callRecordersPhone);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CallRecorders readEntity(Cursor cursor, int offset) {
        CallRecorders entity = new CallRecorders( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // callId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // contactName
            cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)), // createTime
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // callTime
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // callType
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // callRecordersPhone
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CallRecorders entity, int offset) {
        entity.setCallId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setContactName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCreateTime(cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)));
        entity.setCallTime(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCallType(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCallRecordersPhone(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CallRecorders entity, long rowId) {
        entity.setCallId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(CallRecorders entity) {
        if(entity != null) {
            return entity.getCallId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
