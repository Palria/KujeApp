package com.palria.kujeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.R;
import com.palria.kujeapp.SingleJobActivity;
import com.palria.kujeapp.SingleProductActivity;
import com.palria.kujeapp.models.PersonalizedNotificationDataModel;

import java.util.ArrayList;
import java.util.HashMap;


public class PersonalizedNotificationAdapter extends RecyclerView.Adapter<PersonalizedNotificationAdapter.ViewHolder> {

    ArrayList<PersonalizedNotificationDataModel> notificationDataModelArrayList;
    Context context;


    public PersonalizedNotificationAdapter(Context context, ArrayList<PersonalizedNotificationDataModel> notificationDataModelArrayList) {
        this.notificationDataModelArrayList = notificationDataModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.notification_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonalizedNotificationDataModel notificationDataModel = notificationDataModelArrayList.get(position);

        holder.notificationTitleTextView.setText(notificationDataModel.getTitle());
        holder.dateNotifiedTextView.setText(notificationDataModel.getDateNotified());
        holder.notificationMessageTextView.setText(notificationDataModel.getMessage());


        if(notificationDataModel.isSeen()){
            holder.notificationMessageTextView.setTextColor(context.getResources().getColor(R.color.light_dark,context.getTheme()));
//        holder.notificationTitleTextView.setTextColor(context.getResources().getColor(R.color.light_dark,context.getTheme()));
            holder.dateNotifiedTextView.setTextColor(context.getResources().getColor(R.color.light_dark,context.getTheme()));

        }
        else{
            markAsSeen(notificationDataModel);
            holder.notificationMessageTextView.setTextColor(context.getResources().getColor(R.color.black,context.getTheme()));
//        holder.notificationTitleTextView.setTextColor(context.getResources().getColor(R.color.black,context.getTheme()));
            holder.dateNotifiedTextView.setTextColor(context.getResources().getColor(R.color.black,context.getTheme()));
//        holder.itemView.setBackgroundResource(R.color.success_green);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(notificationDataModel.getType()){
                case GlobalValue.NOTIFICATION_TYPE_PRODUCT_ORDERED:
                 String productId = notificationDataModel.getNotification_model_info_list().get(0);
                 Intent intent = new Intent(context, SingleProductActivity.class);
                 intent.putExtra(GlobalValue.PRODUCT_ID,productId);
//                 intent.putExtra(GlobalValue.IS_LOAD_FROM_ONLINE,true);
                 context.startActivity(intent);
                        break;
                case GlobalValue.NOTIFICATION_TYPE_ADVERT_SUBMITTED:
                    context.startActivity( GlobalValue.getHostActivityIntent(context,null,GlobalValue.APPROVE_ADVERTS_FRAGMENT_TYPE,null));
                    break;
                    case GlobalValue.NOTIFICATION_TYPE_JOB_POSTED:
                        String jobId = notificationDataModel.getNotification_model_info_list().get(0);
                        Intent intent1 = new Intent(context, SingleJobActivity.class);
                        intent1.putExtra(GlobalValue.JOB_ID,jobId);
//                 intent.putExtra(GlobalValue.IS_LOAD_FROM_ONLINE,true);
                        context.startActivity(intent1);
                        break;
                    case GlobalValue.NOTIFICATION_TYPE_PRODUCT_POSTED:
                        String productId1 = notificationDataModel.getNotification_model_info_list().get(0);
                        Intent intent2 = new Intent(context, SingleProductActivity.class);
                        intent2.putExtra(GlobalValue.PRODUCT_ID,productId1);
//                 intent.putExtra(GlobalValue.IS_LOAD_FROM_ONLINE,true);
                        context.startActivity(intent2);
                        break;
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return notificationDataModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView notificationTitleTextView;
        public TextView dateNotifiedTextView;
        public TextView notificationMessageTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.notificationTitleTextView =  itemView.findViewById(R.id.notificationTitleTextViewId);
            this.dateNotifiedTextView =  itemView.findViewById(R.id.dateNotifiedTextViewId);
            this.notificationMessageTextView =  itemView.findViewById(R.id.notificationMessageTextViewId);

        }
    }

    void markAsSeen(PersonalizedNotificationDataModel notificationDataModel ){
        WriteBatch writeBatch = GlobalValue.getFirebaseFirestoreInstance().batch();

        DocumentReference notifyDocumentReference =  GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId()).collection(GlobalValue.PERSONALIZED_NOTIFICATIONS).document(notificationDataModel.getNotificationId());
        HashMap<String, Object> notifyDetails = new HashMap<>();
        notifyDetails.put(GlobalValue.DATE_SEEN_LAST_TIME_STAMP, FieldValue.serverTimestamp());
        notifyDetails.put(GlobalValue.IS_SEEN, true);
        writeBatch.set(notifyDocumentReference,notifyDetails, SetOptions.merge());
        writeBatch.commit();
    }
}

