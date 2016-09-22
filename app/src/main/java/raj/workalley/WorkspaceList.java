package raj.workalley;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import raj.workalley.user.fresh.UserInfo;

/**
 * Created by shruti.vig on 9/12/16.
 */
public class WorkspaceList implements Serializable {

    List<Workspace> data;

    public List<Workspace> getWorkspaceData() {
        return data;
    }

    public class Workspace implements Serializable {
        String _id, name;
        boolean available;
        Address address;
        UserInfo owner;
        ArrayList<String> amenities;

        public String getName() {
            return name;
        }

        public String get_id() {
            return _id;
        }

        public UserInfo getOwner() {
            return owner;
        }

        public boolean isAvailable() {
            return available;
        }

        public Address getAddress() {
            return address;
        }

        public ArrayList<String> getAmenities() {
            if (amenities.size() > 0 && !amenities.get(0).equals("")) {
                String str = amenities.get(0).substring(1, amenities.get(0).length() - 1);
                ArrayList<String> amenitiesList = new ArrayList<String>(Arrays.asList(str.split(",")));
                return amenitiesList;
            }
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
