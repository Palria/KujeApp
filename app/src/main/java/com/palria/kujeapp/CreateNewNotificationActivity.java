package com.palria.kujeapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.palria.kujeapp.GlobalValue;

import java.util.HashMap;

public class CreateNewNotificationActivity extends AppCompatActivity {

    TextInputEditText titleTextEditText;
    TextInputEditText messageEditText;
    String title;
    String message;
    String notificationId;
    Button notifyActionButton;
    AlertDialog alertProgressDialog;
    AlertDialog successDialog;
    AlertDialog confirmNotificationDialog;
    ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_notification);
        titleTextEditText = findViewById(R.id.titleEditTextId);
        messageEditText = findViewById(R.id.messageEditTextId);
        backButton = findViewById(R.id.backButton);
        notifyActionButton = findViewById(R.id.notifyActionButtonId);
        notifyActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmNotificationDialog.show();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               CreateNewNotificationActivity.super.onBackPressed();
            }
        });
        alertProgressDialog = new AlertDialog.Builder(CreateNewNotificationActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();
        createSuccessDialog();
        initConfirmOrderDialog(false,"");
    }

void notifyCustomer() {

    title = titleTextEditText.getText() + "";
    message = messageEditText.getText() + "";
    notificationId = GlobalValue.getRandomString(60);
    if (!(message.trim().isEmpty())){
    if (!(title.trim().isEmpty())){
        toggleProgress(true);
        HashMap<String, Object> notDetails = new HashMap<>();
        notDetails.put(GlobalValue.NOTIFICATION_ID, notificationId);
        notDetails.put(GlobalValue.NOTIFICATION_TITLE, title);
        notDetails.put(GlobalValue.NOTIFICATION_MESSAGE, message);
        notDetails.put(GlobalValue.DATE_NOTIFIED_TIME_STAMP, FieldValue.serverTimestamp());
        GlobalValue.getFirebaseFirestoreInstance()
                .collection(GlobalValue.PLATFORM_NOTIFICATIONS)
                .document(notificationId)
                .set(notDetails, SetOptions.merge())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toggleProgress(false);
                        initConfirmOrderDialog(true,e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                toggleProgress(false);
                successDialog.show();

            }
        });
    }else{
        titleTextEditText.setError("Provide title");
        GlobalValue.createSnackBar(CreateNewNotificationActivity.this,notifyActionButton,"Error: You need to provide notification title in the text field", Snackbar.LENGTH_INDEFINITE);
    }
    }else{
        messageEditText.setError("Provide message");
        GlobalValue.createSnackBar(CreateNewNotificationActivity.this,notifyActionButton,"Error: You need to provide notification message in the text field", Snackbar.LENGTH_INDEFINITE);
    }
}

    private void toggleProgress(boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    alertProgressDialog.show();
                }else{
                    alertProgressDialog.cancel();
                }
            }
        });

    }
    public void createSuccessDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);

        builder.setTitle("Successfully Notified!");
        builder.setMessage("what next?");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_success_circle_outline_24);
        builder
//                .setPositiveButton("My page", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                PostNewUpdateActivity.this.finish();
//                startActivity(new Intent(getApplicationContext(),OwnerSinglePageActivity.class));
//            }
//        })
                .setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //  successDialog.cancel();
                        //openGallery(/*unnecessary view*/productCollectionNameEditText);
                        CreateNewNotificationActivity.this.finish();
                    }
                }).setNeutralButton("Notify new", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                titleTextEditText.setText("");
                messageEditText.setText("");
            }
        });
        successDialog =builder.create();

        // successDialog.show();

    }
    public void initConfirmOrderDialog(boolean isRetry, String errorMessage){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        String button = "Confirm";
        String message = "Confirm to notify";
        if(isRetry){
            button = "Retry";
            message = "Your Notification Failed,"+errorMessage+" Please try again";
        }

        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyCustomer();
//                        notifyActionButton.setEnabled(false);

                    }
                });
            }
        }).setNeutralButton("Not yet",null);
        confirmNotificationDialog =builder.create();
        if(isRetry){
            confirmNotificationDialog.show();
        }


    }


}