package luvegroup.sdc;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.RowData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ContactManager extends AppCompatActivity {

    private static Context context;
    private static final String TAG = "ContactManager";

    public ContactManager(Context context) {
        this.context = context;
    }




    public static void addContactToBook(Contact contact, long rawId) {
        //removeContactByRawContactId(context, rawId);

        String name = contact.getName();
        String surname = contact.getSurname();
        String[] numbers = contact.getPhones();

        // Create a new Raw Contact
        ContentValues rawContactValues = new ContentValues();
        rawContactValues.put(ContactsContract.RawContacts._ID, rawId);
        rawContactValues.put(ContactsContract.RawContacts.ACCOUNT_TYPE, (String) null);
        rawContactValues.put(ContactsContract.RawContacts.ACCOUNT_NAME, (String) null);
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, rawContactValues);

        // Insert the display name
        ContentValues nameValues = new ContentValues();
        nameValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawId);
        nameValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        nameValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        nameValues.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, surname);
        nameValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name + " " + surname);
        nameValues.put(ContactsContract.CommonDataKinds.Note.NOTE, "SEST-LUVE");
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, nameValues);

        int index = 0;
        for(String number : numbers) {
            int phoneType = index == 0 ? 2 : 7;

            if (number != null) {
                ContentValues phoneNumberValues = new ContentValues();
                phoneNumberValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawId);
                phoneNumberValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                phoneNumberValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, numbers[index]);
                phoneNumberValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, phoneType);
                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, phoneNumberValues);
            }

            index++;
        }
    }

    public static void addContactsToBook(List<Contact> contacts, Context context) {
        for (Contact contact : contacts) {
            String name = contact.getName();
            String surname = contact.getSurname();
            String[] numbers = contact.getPhones();

            ArrayList<ContentProviderOperation> operations = new ArrayList<>();

            // Create a new Raw Contact
            operations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, (String) null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, (String) null)
                    .build());

            // Insert the display name
            operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, surname)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name + " " + surname)
                    .withValue(ContactsContract.CommonDataKinds.Note.NOTE, "SEST-LUVE")
                    .build());

            int index = 0;
            for (String number : numbers) {
                int phoneType = index == 0 ? 2 : 7;

                if (number != null) {
                    operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phoneType)
                            .build());
                }

                index++;
            }

            try {
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    /*
    public static void addContactToBook(Context context, ArrayList<Contact> contacts) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        int rawId = getFreeContactID();
        for (Contact contact : contacts) {
            // Create a new Raw Contact
            operations.add(
                    ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts._ID, rawId)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, (String) null)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, (String) null)
                            .build());

            String name = contact.getName();
            String surname = contact.getSurname();
            String[] numbers = contact.getPhones();

            // Insert the display name
            operations.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawId)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, surname)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name + " " + surname)
                            .withValue(ContactsContract.CommonDataKinds.Note.NOTE, "SEST-LUVE")
                            .build());

            int index = 0;
            for (String number : numbers) {
                int phoneType = index == 0 ? 2 : 7;

                if (number != null) {
                    operations.add(
                            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawId)
                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phoneType)
                                    .build());
                }

                index++;
            }

            rawId++;
        }

        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

     */







/*
    public static void addContactsToBookInstantly(ArrayList<Contact> contacts) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        for (Contact contact : contacts) {
            long rawId = getFreeContactID();

            // Create a new Raw Contact
            operations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts._ID, rawId)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            // Insert the display name
            operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawId)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.getName())
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contact.getSurname())
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName() + " " + contact.getSurname())
                    .withValue(ContactsContract.CommonDataKinds.Note.NOTE, "SEST-LUVE")
                    .build());

            int index = 0;
            for (String number : contact.getPhones()) {
                int phoneType = index == 0 ? 2 : 7;

                if (number != null) {
                    // Insert phone numbers
                    operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawId)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phoneType)
                            .build());
                }
                index++;
            }
        }

        // Execute the batch of operations
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error applying batch operation", e);
        }
    }
    */




    public static int getFreeContactID() {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID},
                null,
                null,
                ContactsContract.RawContacts._ID + " ASC");

        int currentID = 1;
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndex(ContactsContract.RawContacts._ID);
                    int rawContactId = cursor.getInt(columnIndex);
                    if (rawContactId > currentID) {
                        break;
                    } else {
                        currentID = rawContactId + 1;
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return currentID;
    }

    public static ArrayList<Contact> fetchAll(Context context) {
        ArrayList<Contact> contactList = new ArrayList<>();
        try {
            Connection connection = DatabaseConnection.getConnection(ManagementActivity.dbPassword);

            CompletableFuture<QueryResult> future = connection.sendPreparedStatement("select * from sdc");

            QueryResult queryResult = future.get();

            ResultSet rs = queryResult.getRows();


            for (RowData rowData : rs) {
                // Retrieve data from the result set
                String name = rowData.getString("name");
                String surname = rowData.getString("surname");
                String phone = rowData.getString("phone");
                String phone2 = rowData.getString("phone2");
                String phone3 = rowData.getString("phone3");
                String phone4 = rowData.getString("phone4");
                String phone5 = rowData.getString("phone5");

                //int rawId = rowData.getInt("id");

                contactList.add(new Contact(name, surname, phone, phone2, phone3, phone4, phone5));
            }

            connection.disconnect().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return contactList;
    }

    public static void addContactToDatabase(Contact contact) {
        try {
            String name = contact.getName();
            String surname = contact.getSurname();
            String[] numbers = contact.getPhones();

            Connection connection = DatabaseConnection.getConnection(ManagementActivity.dbPassword);

            String insertQuery = "INSERT INTO sdc (name, surname, phone, phone2, phone3, phone4, phone5) VALUES (?, ?, ?, ?, ?, ?, ?)";
            ArrayList<Object> queryParams = new ArrayList<>();
            queryParams.add(name);
            queryParams.add(surname);

            for (int i = 0; i < 5; i++) {
                if (i < numbers.length) {
                    queryParams.add(numbers[i]);
                } else {
                    queryParams.add(null);
                }
            }

            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(insertQuery, queryParams);
            future.get();

            connection.disconnect().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    public static void removeContactFromDatabase(String name, String surname) {
        try {
            Connection connection = DatabaseConnection.getConnection(ManagementActivity.dbPassword);

            String deleteQuery = "DELETE FROM sdc WHERE name = ? AND surname = ?";
            ArrayList<Object> queryParams = new ArrayList<>();
            queryParams.add(name);
            queryParams.add(surname);

            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(deleteQuery, queryParams);
            future.get();

            connection.disconnect().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    public static void updateContactInDatabase(Contact oldContact, Contact newContact) {
        try {
            Connection connection = DatabaseConnection.getConnection(ManagementActivity.dbPassword);

            String updateQuery = "UPDATE sdc SET name = ?, surname = ?, phone = ?, phone2 = ?, phone3 = ?, phone4 = ?, phone5 = ? WHERE name = ? AND surname = ? AND phone = ? AND phone2 = ? AND phone3 = ? AND phone4 = ? AND phone5 = ?";

            ArrayList<Object> queryParams = new ArrayList<>();

            queryParams.add(newContact.getName());
            queryParams.add(newContact.getSurname());

            String[] newNumbers = newContact.getPhones();
            for (int i = 0; i < 5; i++) {
                if (i < newNumbers.length) {
                    queryParams.add(newNumbers[i]);
                } else {
                    queryParams.add("");
                }
            }



            queryParams.add(oldContact.getName());
            queryParams.add(oldContact.getSurname());

            String[] oldNumbers = oldContact.getPhones();
            for (int i = 0; i < 5; i++) {
                if (i < oldNumbers.length) {
                    queryParams.add(oldNumbers[i]);
                } else {
                    queryParams.add("");
                }
            }


            CompletableFuture<QueryResult> future = connection.sendPreparedStatement(updateQuery, queryParams);
            future.get();

            connection.disconnect().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    public static void synchronizeContacts(Context context) {
        try {
            ArrayList<Contact> contacts = fetchAll(context);
            removeAllContactsWithLabel(context, "SEST-LUVE");
            //removeAllBusinessContacts(context);

            int batchSize = 20;
            for (int i = 0; i < contacts.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, contacts.size());
                List<Contact> batchContacts = contacts.subList(i, endIndex);
                addContactsToBook(batchContacts, context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void removeContactByRawContactId(Context context, long rawContactId) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri rawContactUri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactId);

        Cursor cursor = contentResolver.query(rawContactUri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            contentResolver.delete(rawContactUri, null, null);
        }

        if (cursor != null) {
            cursor.close();
        }
    }


    public static void removeAllContacts(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID},
                null,
                null,
                null
        );

        if (cursor != null) {
            List<Long> rawContactIds = new ArrayList<>();
            try {
                while (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndex(ContactsContract.RawContacts._ID);
                    if (columnIndex >= 0) {
                        long rawContactId = cursor.getLong(columnIndex);
                        rawContactIds.add(rawContactId);
                    } else {
                        Log.e(TAG, "getColumnIndex returned a value less than 0");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error collecting raw contact IDs", e);
            } finally {
                cursor.close();
            }

            // Now delete the contacts using the collected raw contact IDs
            for (Long rawContactId : rawContactIds) {
                //Log.d(TAG, "Deleting contact with ID: " + rawContactId);
                removeContactByRawContactId(context, rawContactId);
            }
        }
    }

    public static void removeAllContactsWithLabel(Context context, String label) {
        ContentResolver contentResolver = context.getContentResolver();

        // Query raw contact IDs that have a note with the specified label
        String selection = ContactsContract.CommonDataKinds.Note.NOTE + " = ?";
        String[] selectionArgs = {label};

        Cursor cursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                selection,
                selectionArgs,
                null
        );

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);
                    if (columnIndex >= 0) {
                        long rawContactId = cursor.getLong(columnIndex);

                        // Delete the contact with the specified raw contact ID
                        Log.d(TAG, "Deleting contact with ID: " + rawContactId);
                        removeContactByRawContactId(context, rawContactId);
                    } else {
                        Log.e(TAG, "getColumnIndex returned a value less than 0");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error collecting raw contact IDs", e);
            } finally {
                cursor.close();
            }
        }
    }





}
