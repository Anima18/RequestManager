package com.example.chirs.rxsimpledemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.requestmanager.NetworkRequest;
import com.example.requestmanager.callBack.BitmapCallBack;
import com.trello.rxlifecycle.LifecycleProvider;

import java.util.List;

/**
 * Created by jianjianhong on 2016/6/13.
 */
public class BitmapAdapter extends BaseAdapter {

    private Context context;
    private List<String> urlList;

    public BitmapAdapter(Context context, List<String> urlList) {
        this.context = context;
        this.urlList = urlList;
    }

    @Override
    public int getCount() {
        return urlList.size();
    }

    @Override
    public Object getItem(int position) {
        return urlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Nullable
    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_bitmap_item, parent, false);
            holder.image = (ImageView)convertView.findViewById(R.id.bitlv_im);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
            /*BitmapDrawable dd = (BitmapDrawable) (holder.image).getDrawable();

            if(dd != null) {
                Bitmap bm =dd.getBitmap();
                if(bm != null) {
                    Log.d("BitmapAdapter", "bm is not null");
                    bm.recycle();
                    bm = null;
                }
            }*/
        }

        String url = urlList.get(position);
        Log.d("WebService", url);
        /*WebService.getBitMap(context, url, new BitmapCallBack() {
            @Override
            public void onSuccess(String url, Bitmap bitmap) {
                if(bitmap == null) {
                    holder.image.setImageResource(R.drawable.not_found);
                }else {
                    holder.image.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onFailure(int code, String message) {
                holder.image.setImageResource(R.drawable.not_found);
            }

            @Override
            public void onCompleted() {

            }
        });*/
        new NetworkRequest.Builder((LifecycleProvider) context)
                .url(url)
                .tag(context)
                .getBitMap(new BitmapCallBack(){
                    @Override
                    public void onSuccess(String url, @Nullable Bitmap bitmap) {
                        if(bitmap == null) {
                            holder.image.setImageResource(R.drawable.not_found);
                        }else {
                            holder.image.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        holder.image.setImageResource(R.drawable.not_found);
                    }

                    @Override
                    public void onCompleted() {

                    }
                });

        return convertView;
    }

    class ViewHolder{
        public ImageView image;
    }
}
