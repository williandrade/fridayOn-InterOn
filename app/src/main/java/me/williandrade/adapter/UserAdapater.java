package me.williandrade.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import me.williandrade.R;
import me.williandrade.activity.IndexActivity;
import me.williandrade.dto.UserDTO;

public class UserAdapater extends RecyclerView.Adapter<UserAdapater.ViewHolder> {
    private List<UserDTO> mDataset;
    private Context contextComing;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView userCard;
        public ImageView userCardPic;
        public TextView userCardName;
        public TextView userCardDoing;

        public ViewHolder(View itemView) {
            super(itemView);
            userCard = (CardView) itemView.findViewById(R.id.userCard);
            userCardPic = (ImageView) itemView.findViewById(R.id.userCardPic);
            userCardName = (TextView) itemView.findViewById(R.id.userCardName);
            userCardDoing = (TextView) itemView.findViewById(R.id.userCardDoing);
        }
    }

    public UserAdapater(String reference, Context context) {
        this.mDataset = new ArrayList<>();
        this.contextComing = context;

        runGetData(reference);
    }

    @Override
    public UserAdapater.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_cards, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String uid = mDataset.get(position).getUid();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (mDataset.get(position).getPhoto() != null) {
            Bitmap image = loadBitmap(mDataset.get(position).getPhoto());
            holder.userCardPic.setImageBitmap(image);
        }

        holder.userCardName.setText(mDataset.get(position).getEmail());
        holder.userCardDoing.setText(mDataset.get(position).getDoing());

        holder.userCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((IndexActivity) contextComing).callButtonClicked(uid);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void runGetData(String reference) {
        final Gson gson = new Gson();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(reference);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserDTO> result = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String json = ds.getValue(String.class);
                    Log.d("UserDAO", json);
                    result.add(gson.fromJson(json, UserDTO.class));
                }

                changeList(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UserDAO", databaseError.getMessage());
            }
        });

    }

    public void changeList(List<UserDTO> result) {
        this.mDataset = result;
        this.notifyDataSetChanged();
    }

    public Bitmap loadBitmap(String url) {
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }
}