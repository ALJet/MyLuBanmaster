package indi.aljet.myluban_master.example;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import indi.aljet.myluban_master.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter
        .ImageHolder> {


    private Context mContext;
    private List<ImageBean> mImageList;

    public ImageAdapter(List<ImageBean> imageList) {
        this.mImageList = imageList == null ?
        mImageList : imageList;
    }


    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ImageHolder(LayoutInflater.from(mContext)
        .inflate(R.layout.item_image,parent,false));
    }


    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        ImageBean image = mImageList.get(position);
        holder.originArg.setText(image.getOriginArg());
        holder.thumbArg.setText(image.getThumbArg());
        Glide.with(mContext)
                .load(image.getImage())
                .into(holder.image);

    }

    class ImageHolder extends RecyclerView.ViewHolder{


        private TextView originArg;
        private TextView thumbArg;
        private ImageView image;

        public ImageHolder(View itemView) {
            super(itemView);
            originArg = itemView.findViewById(R.id.origin_arg);
            thumbArg = itemView.findViewById(R.id.thumb_arg);
            image = itemView.findViewById(R.id.image);
        }
    }
}
