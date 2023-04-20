package luvegroup.sdc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ContactsListAdapter extends ArrayAdapter<Contact> {

    private Context mContext;
    Activity activity;

    public ContactsListAdapter(Context context, Activity activity) {
        super(context, R.layout.row_layout);
        mContext = context;
        this.activity = activity;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.row_layout, null);
        }

        TextView itemTextView = view.findViewById(R.id.itemTextView);

        Contact contact = getItem(position);
        String nameSurname = contact.getName() + " " + contact.getSurname();
        String phones = Arrays.stream(contact.getPhones())
                .filter(Objects::nonNull)
                .filter(phone -> !phone.isEmpty())
                .collect(Collectors.joining(", "));
        String formattedContact = nameSurname + " (" + phones + ")";
        itemTextView.setText(formattedContact);

        ImageButton deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Explain why the permissions are needed
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Potwierdź usunięcie!");
                builder.setMessage("Czy na pewno chcesz usunąć ten kontakt?");
                builder.setPositiveButton("Potwierdź", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Contact contact = getItem(position);
                        String name = contact.getName();
                        String surname = contact.getSurname();
                        ContactManager.removeContactFromDatabase(name, surname);
                        remove(contact);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Broń Boże, nie usuwaj!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the application
                        //activity.finish();
                    }
                });
                builder.show();



            }
        });

        ImageButton editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact newContact = getItem(position);

                // Step 3: Create an instance of the Dialog class
                Dialog dialog = new Dialog(getContext());

                // Step 3: Set the content view of the dialog
                dialog.setContentView(R.layout.manage_contact_modal);
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setLayout(width, height);

                // Step 4: Customize the dialog as desired
                Button btnClose = dialog.findViewById(R.id.modal_cancel_button);
                Button btnSave = dialog.findViewById(R.id.modal_login_button);


                TextView modalTitle = dialog.findViewById(R.id.modal_title);
                modalTitle.setText("Edytuj kontakt");

                EditText editName = dialog.findViewById(R.id.name_input);
                EditText editSurname = dialog.findViewById(R.id.surname_input);
                EditText editPhone1 = dialog.findViewById(R.id.phone_input_1);
                EditText editPhone2 = dialog.findViewById(R.id.phone_input_2);
                EditText editPhone3 = dialog.findViewById(R.id.phone_input_3);
                EditText editPhone4 = dialog.findViewById(R.id.phone_input_4);
                EditText editPhone5 = dialog.findViewById(R.id.phone_input_5);

                editName.setText(newContact.getName());
                editSurname.setText(newContact.getSurname());
                String[] oldPhones = newContact.getPhones();
                editPhone1.setText(oldPhones[0]);
                editPhone2.setText(oldPhones[1]);
                editPhone3.setText(oldPhones[2]);
                editPhone4.setText(oldPhones[3]);
                editPhone5.setText(oldPhones[4]);

                Contact oldContact = new Contact(newContact.getName(), newContact.getSurname(), oldPhones);

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss(); // Close the dialog
                    }
                });
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if((!editName.getText().toString().isEmpty() || !editSurname.getText().toString().isEmpty()) && !editPhone1.getText().toString().isEmpty()) {
                            newContact.setName(editName.getText().toString());
                            newContact.setSurname(editSurname.getText().toString());

                            String[] phones = new String[5];
                            phones[0] = editPhone1.getText().toString();
                            phones[1] = editPhone2.getText().toString();
                            phones[2] = editPhone3.getText().toString();
                            phones[3] = editPhone4.getText().toString();
                            phones[4] = editPhone5.getText().toString();

                            newContact.setPhones(phones);
                            ContactManager.updateContactInDatabase(oldContact, newContact);
                            notifyDataSetChanged();


                            dialog.dismiss(); // Close the dialog
                        }
                    }
                });

                // Step 5: Show the dialog to the user
                dialog.show();
            }
        });

        return view;
    }
}
