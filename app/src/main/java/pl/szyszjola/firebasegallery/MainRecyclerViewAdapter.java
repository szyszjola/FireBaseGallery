package pl.szyszjola.firebasegallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {

    private List<Picture.SinglePicture> singlePictureArrayList;
    private Context mContext;
    private FireBaseStorageConector conector = new FireBaseStorageConector(mContext);
    private static final int FOOTER_VIEW = 1;
    public static final String PATH_KEY = "PATH";

    MainRecyclerViewAdapter(List<Picture.SinglePicture> singlePictureArrayList, Context mContext) {
        this.singlePictureArrayList = singlePictureArrayList;
        this.mContext = mContext;
    }

    public class FooterViewHolder extends ViewHolder {
        FooterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "FOOTER", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class NormalViewHolder extends ViewHolder {
        NormalViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "NORMAL", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == singlePictureArrayList.size()) {
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView;
        if (viewType == FOOTER_VIEW) {
            cardView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.activity_main_footer_view, parent, false);
            return new FooterViewHolder(cardView);
        }
        cardView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.activity_main_card_view, parent, false);
        return new NormalViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            if (holder instanceof NormalViewHolder) {
                NormalViewHolder viewHolder = (NormalViewHolder) holder;
                Picture.SinglePicture picture = singlePictureArrayList.get(position);
                viewHolder.title.setText(picture.getTitle());
                viewHolder.path = picture.getImage();
                conector.firebaseDownload(viewHolder.image, viewHolder.path, 128,96);
                viewHolder.description.setText(picture.getDescription());
            } else if (holder instanceof FooterViewHolder) {
                FooterViewHolder viewHolder = (FooterViewHolder) holder;
            }
        } catch (Exception ex) {
            Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {

        if (singlePictureArrayList == null) {
            return 0;
        }

        if (singlePictureArrayList.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        }

        // Add extra view to show the footer view
        return singlePictureArrayList.size() + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView image;
        TextView title;
        TextView description;
        String path;


        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            itemView.setOnCreateContextMenuListener(this);
            if (image != null) {
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), PreviewImage.class);
                        intent.putExtra(PATH_KEY, path);
                        v.getContext().startActivity(intent);
                    }
                });
            }
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenu.
                ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(R.string.app_name);
            menu.add(0, v.getId(), 0, R.string.pobierz).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    FireBaseStorageConector conector = new FireBaseStorageConector(v.getContext());
                    conector.firebaseDownloadFullSize(image, path);
                    if (true)
                        Toast.makeText(v.getContext(), "Pobrano zdjęcie", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(v.getContext(), "Wystąpił błąd podczas pobierania", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
//            menu.add(0, v.getId(), 0, "SMS").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    return true;
//                }
//            });
        }

    }
}
