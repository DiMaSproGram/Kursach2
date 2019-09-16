package sample;

import java.awt.image.BufferedImage;

public class Computer {
    private String cpuTitle;
    private String vidCardTitle;
    private String mothBoardTitle;
    private String ramTitle;
    private String hddTitle;
    private String ssdTitle;
    private String powSuppTitle;
    private String coolTitle;
    private String compCaseTitle;
    private BufferedImage compCaseImg;
    private double totalPrice;

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCpuTitle() {
        return cpuTitle;
    }

    public void setCpuTitle(String cpuTitle) {
        this.cpuTitle = cpuTitle;
    }

    public String getVidCardTitle() {
        return vidCardTitle;
    }

    public void setVidCardTitle(String vidCardTitle) {
        this.vidCardTitle = vidCardTitle;
    }

    public String getMothBoardTitle() {
        return mothBoardTitle;
    }

    public void setMothBoardTitle(String mothBoardTitle) {
        this.mothBoardTitle = mothBoardTitle;
    }

    public String getRamTitle() {
        return ramTitle;
    }

    public void setRamTitle(String ramTitle) {
        this.ramTitle = ramTitle;
    }

    public String getHddTitle() {
        return hddTitle;
    }

    public void setHddTitle(String hddTitle) {
        this.hddTitle = hddTitle;
    }

    public String getSsdTitle() {
        return ssdTitle;
    }

    public void setSsdTitle(String ssdTitle) {
        this.ssdTitle = ssdTitle;
    }

    public String getPowSuppTitle() {
        return powSuppTitle;
    }

    public void setPowSuppTitle(String powSuppTitle) {
        this.powSuppTitle = powSuppTitle;
    }

    public String getCoolTitle() {
        return coolTitle;
    }

    public void setCoolTitle(String coolTitle) {
        this.coolTitle = coolTitle;
    }

    public String getCompCaseTitle() {
        return compCaseTitle;
    }

    public void setCompCaseTitle(String compCaseTitle) {
        this.compCaseTitle = compCaseTitle;
    }

    public BufferedImage getCompCaseImg() {
        return compCaseImg;
    }

    public void setCompCaseImg(BufferedImage compCaseImg) {
        this.compCaseImg = compCaseImg;
    }
}
