package me.williandrade.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.URI;

import me.williandrade.R;
import me.williandrade.activity.IndexActivity;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[];
    private int mIcons[];

    private String name;

    private URI image;
    private String creditis;

    private Context myContext;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        TextView textView;
        ImageView image;
        TextView Name;
        TextView credits;
        LinearLayout containerRow;


        public ViewHolder(View itemView, int ViewType) {
            super(itemView);


            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                containerRow = (LinearLayout) itemView.findViewById(R.id.containerRow);
//                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                Holderid = 1;
            } else {


                Name = (TextView) itemView.findViewById(R.id.name);
                credits = (TextView) itemView.findViewById(R.id.credits);
                image = (ImageView) itemView.findViewById(R.id.circleView);
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }
        }


    }


    public MenuAdapter(String Titles[], String Name, Integer credits, URI image, Context myContext) {
        this.mNavTitles = Titles;
        this.name = Name;
        this.creditis = credits + " Credits";
        this.image = image;
        this.myContext = myContext;

    }


    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);

            ViewHolder vhItem = new ViewHolder(v, viewType);

            return vhItem;


        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(v, viewType);

            return vhHeader;


        }
        return null;

    }

    @Override
    public void onBindViewHolder(MenuAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == 1) {
            holder.textView.setText(mNavTitles[position - 1]);

            if (mNavTitles[position - 1] == "Sign Out") {

                holder.containerRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((IndexActivity) myContext).stopButtonClicked();
                    }
                });
            }
//            holder.imageView.setImageResource(mIcons[position -1]);
        } else {

//            holder.image.setImageResource(image);           // Similarly we set the resources for header view
            holder.Name.setText(name);
            holder.credits.setText(creditis);
        }
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length + 1;
    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}
