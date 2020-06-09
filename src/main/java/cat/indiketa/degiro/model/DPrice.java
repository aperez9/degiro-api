package cat.indiketa.degiro.model;

import java.time.LocalTime;

/**
 * @author indiketa
 */
public class DPrice {

    private String issueId;
    private Double bid;
    private Double ask;
    private Double last;
    private LocalTime lastTime;
    private String vwdProductName;

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public Double getAsk() {
        return ask;
    }

    public void setAsk(Double ask) {
        this.ask = ask;
    }

    public Double getLast() {
        return last;
    }

    public void setLast(Double last) {
        this.last = last;
    }

    public LocalTime getLastTime() {
        return lastTime;
    }

    public void setLastTime(LocalTime lastTime) {
        this.lastTime = lastTime;
    }

    public String getVwdProductName() {
        return vwdProductName;
    }

    public void setVwdProductName(String vwdProductName) {
        this.vwdProductName = vwdProductName;
    }

}
