package luvegroup.sdc;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ManagementActivity extends AppCompatActivity {
    public PermissionManager permissionManager;
    public static ContactManager contactManager;
    public static ListView listView;
    static ContactsListAdapter adapter = null;
    public static String dbPassword = "Daisy#2022";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_management, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        setTitle(R.string.management_activity_title);

        listView = findViewById(R.id.contactsView);

        adapter = new ContactsListAdapter(this, this);

        listView.setAdapter(adapter);
        adapter.addAll(ContactManager.fetchAll(this));

        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_contact) {
            if (adapter != null) {
                // Step 3: Create an instance of the Dialog class
                Dialog dialog = new Dialog(this);

                // Step 3: Set the content view of the dialog
                dialog.setContentView(R.layout.manage_contact_modal);

                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setLayout(width, height);

                // Step 4: Customize the dialog as desired
                Button btnClose = dialog.findViewById(R.id.modal_cancel_button);
                Button btnSave = dialog.findViewById(R.id.modal_login_button);

                TextView modalTitle = dialog.findViewById(R.id.modal_title);
                modalTitle.setText("Dodaj kontakt");

                EditText editName = dialog.findViewById(R.id.name_input);
                EditText editSurname = dialog.findViewById(R.id.surname_input);
                EditText editPhone1 = dialog.findViewById(R.id.phone_input_1);
                EditText editPhone2 = dialog.findViewById(R.id.phone_input_2);
                EditText editPhone3 = dialog.findViewById(R.id.phone_input_3);
                EditText editPhone4 = dialog.findViewById(R.id.phone_input_4);
                EditText editPhone5 = dialog.findViewById(R.id.phone_input_5);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss(); // Close the dialog
                    }
                });
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!editName.getText().toString().isEmpty() && !editSurname.getText().toString().isEmpty() && !editPhone1.getText().toString().isEmpty()) {
                            Contact contact = new Contact(editName.getText().toString(), editSurname.getText().toString(), editPhone1.getText().toString(), editPhone2.getText().toString(), editPhone3.getText().toString(), editPhone4.getText().toString(), editPhone5.getText().toString());
                            ContactManager.addContactToDatabase(contact);
                            adapter.add(contact);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss(); // Close the dialog
                        }
                    }
                });

                // Step 5: Show the dialog to the user
                dialog.show();

                return true;
            }

            return super.onOptionsItemSelected(item);
        }
        return false;
    }





}