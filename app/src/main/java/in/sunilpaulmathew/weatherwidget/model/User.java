package in.sunilpaulmathew.weatherwidget.model;

public class User {
    private String id;
    private String email;
    private String formattedTime;

    private String image;


    public User(String id, String email, String formattedTime, String image) {
        this.id = id;
        this.email = email;
        this.formattedTime = formattedTime;
        this.image = image;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
