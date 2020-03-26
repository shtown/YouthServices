package gov.townofsouthamptonny.android.youthservices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gov.townofsouthamptonny.android.youthservices.database.ServicesCursorWrapper;
import gov.townofsouthamptonny.android.youthservices.database.YSDbSchema.YSTable;


/**
 * Created by JDaly on 2/25/2016.
 */
public class ServicesGenerator {

    private static ServicesGenerator sServicesGenerator;
    public static List<ServicesItem> mServicesItems;
    private Context mContext;
    private static SQLiteDatabase mDatabase;

    public static void addServiceItem(ServicesItem si)  {
        ContentValues values = getContentValues(si);
        mDatabase.insert(YSTable.NAME, null, values);
    }

    public static void addList(List<ServicesItem> list) {
        mServicesItems = list;
    }

    public static ServicesGenerator get(Context context) {
        if (sServicesGenerator == null) sServicesGenerator = new ServicesGenerator(context);
        return sServicesGenerator;
    }

    private ServicesGenerator(Context context) {
        mServicesItems  = new ArrayList<>();
    }

    /*
    private ServicesGenerator(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new YSBaseHelper(mContext).getWritableDatabase();
        //mServicesItems = new ArrayList<>();
    }
    */

    public static List<ServicesItem> getServices() {
        return mServicesItems;
    }

    /*
    public List<ServicesItem> getServices() {
        return new ArrayList<>();;
    }
    */
    /*
    public List<ServicesItem> getServices() {
        List<ServicesItem> services = new ArrayList<>();

        ServicesCursorWrapper cursor = queryServices(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                services.add(cursor.getServicesItem());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return services;
    }
    */

    public static ServicesItem getService(UUID id) {
        for(ServicesItem service : mServicesItems)  {
            if(service.getUUID().equals(id))  {
                return service;
            }
        }
        return null;
    }
/*
    public static ServicesItem getService(UUID id) {
        ServicesCursorWrapper cursor = queryServices(YSTable.Cols.UUID + " = ? ", new String[]{id.toString()});

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getServicesItem();
        } finally {
            cursor.close();
            ;
        }
    }
    */

    public void updateService(ServicesItem servicesItem) {
        String uuidString = servicesItem.getUUID().toString();
        ContentValues values = getContentValues(servicesItem);

        mDatabase.update(YSTable.NAME, values, YSTable.Cols.UUID + " = ?", new String[] {uuidString});
    }


    private static ContentValues getContentValues(ServicesItem servicesItem)  {
        ContentValues values = new ContentValues();
        values.put(YSTable.Cols.UUID, servicesItem.getUUID().toString());
        values.put(YSTable.Cols.ID, servicesItem.getID());
        values.put(YSTable.Cols.LOC_ID, servicesItem.getLoc_ID());
        values.put(YSTable.Cols.F_NAME, servicesItem.getF_Name());
        values.put(YSTable.Cols.ADDRESS, servicesItem.getAddress());
        values.put(YSTable.Cols.CIVIC, servicesItem.getCivic());
        values.put(YSTable.Cols.ADDRESSLINE1, servicesItem.getAddressLine1());
        values.put(YSTable.Cols.ADDRESSLINE2, servicesItem.getAddressLine2());
        values.put(YSTable.Cols.ADDRESSLINE3, servicesItem.getAddressLine3());
        values.put(YSTable.Cols.ZIP, servicesItem.getZip());
        values.put(YSTable.Cols.FEE, servicesItem.getFee());
        values.put(YSTable.Cols.CONTACT, servicesItem.getContact());
        values.put(YSTable.Cols.TITLE, servicesItem.getTitle());
        values.put(YSTable.Cols.EMAIL, servicesItem.getEmail());
        values.put(YSTable.Cols.PHONE1, servicesItem.getPhone1());
        values.put(YSTable.Cols.PHONE1EXT, servicesItem.getPhone1Ext());
        values.put(YSTable.Cols.PHONE2, servicesItem.getPhone2());
        values.put(YSTable.Cols.PHONE2EXT, servicesItem.getPhone2xt());
        values.put(YSTable.Cols.FAX, servicesItem.getFax());
        values.put(YSTable.Cols.WEBLINK, servicesItem.getWebLink());
        values.put(YSTable.Cols.IP_ADDRESS, servicesItem.getIP_Address());
        values.put(YSTable.Cols.MAPPED, servicesItem.getMapped());
        values.put(YSTable.Cols.SUBMISSIONDATE, servicesItem.getSubmissionDate());
        values.put(YSTable.Cols.CATEGORY, servicesItem.getCategory());
        values.put(YSTable.Cols.XCOORD, servicesItem.getxCoord());
        values.put(YSTable.Cols.YCOORD, servicesItem.getyCoord());
        values.put(YSTable.Cols.LAT, servicesItem.getLat());
        values.put(YSTable.Cols.LON, servicesItem.getLon());
        values.put(YSTable.Cols.DESC, servicesItem.getDesc());

        return values;
    }

    private static ServicesCursorWrapper queryServices(String whereClause, String[] whereArgs)  {
        Cursor cursor = mDatabase.query(
                YSTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        //cursor.close();
        return new ServicesCursorWrapper(cursor);
    }

}
