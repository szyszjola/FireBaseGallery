package pl.szyszjola.firebasegallery;

import java.util.List;
public class Picture {

    private List<SinglePicture> singlePicture;

    public Picture(List<SinglePicture> singlePicture) {
        this.singlePicture = singlePicture;
    }

    static class SinglePicture
    {
        private String title;
        private String image;
        private String description;

        SinglePicture(String title, String image, String description) {
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

        String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
