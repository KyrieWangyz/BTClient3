package xuemcu.com.btclient;


public class WeightsBean {

    private Double weight;
    private String time;

    public WeightsBean(Double weight, String time) {
        this.weight = weight;
        this.time = time;
    }


    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
