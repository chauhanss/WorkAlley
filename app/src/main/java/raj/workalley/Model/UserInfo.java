package raj.workalley.Model;

import java.io.Serializable;

/**
 * Created by shruti.vig on 9/8/16.
 */
public class UserInfo implements Serializable {

    String name, _id, role, email, updatedAt,createdAt;
    boolean active;
    int __v;
    boolean isEndRequest = false;

    public String get_id() {
        return _id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public int get__v() {
        return __v;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setIsEndRequest(boolean isEndRequest) {
        this.isEndRequest = isEndRequest;
    }

    public boolean isEndRequest() {
        return isEndRequest;
    }

    @Override
    public boolean equals(Object o) {
        return _id.equalsIgnoreCase(((UserInfo) o).get_id());
    }
}
