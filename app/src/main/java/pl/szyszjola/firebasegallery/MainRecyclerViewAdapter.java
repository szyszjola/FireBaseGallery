package pl.szyszjola.firebasegallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {

    private List<Picture.SinglePicture> singlePictureArrayList;
    private Context mContext;
    private FireBaseStorageConector conector = new FireBaseStorageConector();

    MainRecyclerViewAdapter(List<Picture.SinglePicture> singlePictureArrayList, Context mContext) {
        this.singlePictureArrayList = singlePictureArrayList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.activity_main_card_view, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picture.SinglePicture picture = singlePictureArrayList.get(position);
        holder.title.setText(picture.getTitle());
        conector.firebaseDownload(holder.image, picture.getImage());
        holder.description.setText(picture.getDescription());
    }

    @Override
    public int getItemCount() {
        return singlePictureArrayList.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ImageView image;
        TextView title;
        TextView description;
        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
