package raj.workalley.user.fresh;

/**
 * Created by shruti.vig on 9/8/16.
 */
public class UserInfo {

    String name, _id, role, email;
    boolean active;

    public String get_id() {
        return _id;
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
}
