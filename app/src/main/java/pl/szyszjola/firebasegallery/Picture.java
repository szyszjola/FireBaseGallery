package pl.szyszjola.firebasegallery;

import java.util.ArrayList;
import java.util.List;
public class Picture {

    List<SinglePicture> singlePicture;

    public Picture(List<SinglePicture> singlePicture) {
        this.singlePicture = singlePicture;
    }

    public List<SinglePicture> getSinglePicture() {
        return singlePicture;
    }

    static class SinglePicture
    {
        private String title;
        private String image;
        private String description;

        public SinglePicture(String title, String image, String description) {
            this.title = title;
            this.image = image;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
