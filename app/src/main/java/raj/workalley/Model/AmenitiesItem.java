package raj.workalley.Model;

/**
 * Created by shruti.vig on 9/12/16.
 */
public class AmenitiesItem {
    String amenitiesName;
    int amenitiesIcon;
    boolean active;

    public AmenitiesItem(String amenitiesName, int amenitiesIcon, boolean active) {
        this.amenitiesName = amenitiesName;
        this.amenitiesIcon = amenitiesIcon;
        this.active = active;
    }

    public String getAmenitiesName() {
        return amenitiesName;
    }

    public int getAmenitiesIcon() {
        return amenitiesIcon;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
