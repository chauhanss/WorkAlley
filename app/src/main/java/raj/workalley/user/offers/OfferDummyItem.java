package raj.workalley.user.offers;

/**
 * Created by vishal.raj on 9/3/16.
 */
public class OfferDummyItem {
    private String title, amount, time, validity;

    public OfferDummyItem() {
    }

    public OfferDummyItem(String title, String amount, String time, String validity) {
        this.title = title;
        this.amount = amount;
        this.time = time;
        this.validity = validity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
}
