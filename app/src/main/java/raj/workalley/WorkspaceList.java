package raj.workalley;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shruti.vig on 9/12/16.
 */
public class WorkspaceList implements Serializable {

    List<Workspace> data;

    public List<Workspace> getWorkspaceData() {
        return data;
    }

    public class Workspace implements Serializable {
        String _id, name, owner;
        boolean available;
        Address address;
        ArrayList<String> amenities;

        public String getName() {
            return name;
        }

        public String get_id() {
            return _id;
        }

        public String getOwnerId() {
            return owner;
        }

        public boolean isAvailable() {
            return available;
        }

        public Address getAddress() {
            return address;
        }

        public ArrayList<String> getAmenities() {
            return amenities;
        }
    }

    public class Address implements Serializable {
        String line1, locality, state, city;
        Long pincode;
        ArrayList<Double> loc = new ArrayList<>();

        public ArrayList<Double> getLoc() {
            return loc;
        }

        public Long getPincode() {
            return pincode;
        }

        public String getCity() {
            return city;
        }

        public String getLine1() {
            return line1;
        }

        public String getLocality() {
            return locality;
        }

        public String getState() {
            return state;
        }

        public String getFullAddress() {
            StringBuilder address = new StringBuilder().append(line1).append(" ").append(locality).append(" ").append(city).append(" ").append(state).append(" ").append(pincode.toString());
            return address.toString();
        }
    }

}
