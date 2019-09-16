package sample;

import java.io.IOException;
import java.sql.SQLException;

public class CreationPC {
    private double pricePC;
    private double totalPrice;
    private int vidCardWatt;
    private Goals goal;
    private DataBase dataBase;
    enum Goals {
        FOR_GAMES(new double[]{ 0.28, 0.45, 0.08, 0.08, 0.03, 0.05, 0.02, 0.02, 0.03}),
        FOR_WORK(new double[]{ 0.49, 0.25, 0.08, 0.1, 0.03, 0.03, 0.02, 0.02, 0.02});

        private double arr[];
        Goals(double[] arr){
            this.arr = arr;
        }
    }

    CreationPC(double price, Goals goal) throws SQLException, ClassNotFoundException {
        pricePC = price;
        this.goal = goal;
        dataBase = new DataBase();
        totalPrice = 0;
    }
    public Computer start() throws IOException, SQLException {
        Computer comp = new Computer();

        comp.setCpuTitle(getHardware(DataBase.Hardware.CPU));
        comp.setVidCardTitle(getHardware(DataBase.Hardware.VIDEO_CARD));
        comp.setMothBoardTitle(getHardware(DataBase.Hardware.MOTHERBOARD));
        comp.setRamTitle(getHardware(DataBase.Hardware.RAM));
        comp.setHddTitle(getHardware(DataBase.Hardware.HDD));
        comp.setSsdTitle(getHardware(DataBase.Hardware.SSD));
        comp.setPowSuppTitle(getHardware(DataBase.Hardware.POWER_SUPPLY));
        comp.setCoolTitle(getHardware(DataBase.Hardware.COOLERS));
        comp.setCompCaseTitle(getHardware(DataBase.Hardware.COMPUTER_CASE));
        comp.setCompCaseImg(dataBase.getImage(DataBase.Hardware.COMPUTER_CASE, comp.getCompCaseTitle()));
        comp.setTotalPrice(totalPrice);
        return comp;
    }
    private String getHardware(DataBase.Hardware hardware) throws SQLException, IOException {
        String title;

        if(hardware == DataBase.Hardware.POWER_SUPPLY)
            title = dataBase.getHardwareTitleWatt(hardware, pricePC * goal.arr[hardware.getId()], vidCardWatt + 200);
        else
            title = dataBase.getHardwareTitle(hardware, pricePC * goal.arr[hardware.getId()]);

        if(hardware == DataBase.Hardware.VIDEO_CARD)
            vidCardWatt = dataBase.getWatt(DataBase.Hardware.VIDEO_CARD, title);

        totalPrice += dataBase.getPrice(hardware, title);
        return title;
    }
}
