package sample;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class DataBase {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kursach";
    private static final String USER = "root";
    private static final String PASS = "1111";
    enum Hardware{
        CPU (0,"cpu",3, false),
        VIDEO_CARD (1,"videocard",4, false),
        MOTHERBOARD (2,"motherboard",7, false),
        RAM (3,"ram",7, false),
        HDD (4,"hdd",4, false),
        SSD (5,"ssd",4, false),
        POWER_SUPPLY (6,"powsupply",4, true),
        COOLERS (7,"coolers",7, false),
        COMPUTER_CASE (8,"compcase",3, true);

        private String hardware;
        private int n;
        private boolean itemInfo;
        private int id;

        Hardware(int id, String hardware, int n, boolean itemInfo){
            this.id = id;
            this.hardware = hardware;
            this.n = n;
            this.itemInfo = itemInfo;
        }
        public String getHardware() {
            return hardware;
        }
        public int getN() {
            return n;
        }
        public boolean getItemInfo() {
            return itemInfo;
        }
        public int getId() {
            return id;
        }
    }

    private Connection connection;
    private Statement stmt;

    DataBase() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(DB_URL, USER, PASS);
        stmt = connection.createStatement();
    }

    public void addTitlePrice(String title, double price, Hardware hardware) throws SQLException {
        PreparedStatement prepStat = connection.prepareStatement("INSERT INTO kursach." + hardware.getHardware() +
                "(" + hardware.getHardware() + "_title, " + hardware.getHardware() + "_price) VALUES (?, ?)");
        prepStat.setString(1, title);
        prepStat.setDouble(2, price);
        prepStat.execute();
        prepStat.close();
    }
    public void addTitlePriceWatt(String title, double price, int watt, Hardware hardware) throws SQLException {
        PreparedStatement prepStat = connection.prepareStatement("INSERT INTO kursach." + hardware.getHardware() +
                "(" + hardware.getHardware() + "_title, " + hardware.getHardware() + "_price, " + hardware.getHardware() + "_watt) VALUES (?, ?, ?)");
        prepStat.setString(1, title);
        prepStat.setDouble(2, price);
        prepStat.setInt(3, watt);
        prepStat.execute();
        prepStat.close();
    }
    public void addTitlePriceImage(String title, double price, BufferedImage img, Hardware hardware) throws SQLException, IOException {
        PreparedStatement prepStat = connection.prepareStatement("INSERT INTO kursach." + hardware.getHardware() +
                "(" + hardware.getHardware() + "_title, " + hardware.getHardware() + "_price, " + hardware.getHardware() + "_image) VALUES (?, ?, ?)");
        prepStat.setString(1, title);
        prepStat.setDouble(2, price);
        prepStat.setBinaryStream(3, toInputStream(img));
        prepStat.execute();
        prepStat.close();
    }
    public void deleteAll() throws SQLException {
        PreparedStatement prepStat = null;
        for(Hardware hard : Hardware.values()) {
            prepStat = connection.prepareStatement("DELETE FROM kursach." + hard.getHardware());
            prepStat.execute();
        }
        prepStat.close();
    }


    public String getHardwareTitle(Hardware hardware, double referencePrice) throws SQLException, IOException {
        System.out.println(referencePrice);
        ResultSet resultSet;
        PreparedStatement preparedStatement= connection.prepareStatement("SELECT * FROM kursach." + hardware.getHardware());
        String minTitle = "";
        String maxTitle = "";
        HashSet<String> titleSet = new HashSet<>();
        double lowBound = 0.96;
        double upBound = 1.03;
        double tempPrice;
        double min = 100;
        double max = 100;

        for(int i = 0; i < 10; ++i) {
            resultSet = preparedStatement.executeQuery("SELECT * FROM kursach." + hardware.getHardware());
            while (resultSet.next()) {
                tempPrice = resultSet.getDouble(hardware.getHardware() + "_price");
                if(tempPrice > max) {
                    max = tempPrice;
                    maxTitle = resultSet.getString(hardware.getHardware() + "_title");
                }
                if (tempPrice < min) {
                    min = tempPrice;
                    minTitle = resultSet.getString(hardware.getHardware() + "_title");
                }
                if (tempPrice > referencePrice * upBound)
                    continue;
                if (tempPrice > referencePrice * lowBound && tempPrice < referencePrice * upBound)
                    titleSet.add(resultSet.getString(hardware.getHardware() + "_title"));
            }
            lowBound -= 0.04;
            upBound += 0.02;
        }
        preparedStatement.execute();
        preparedStatement.close();
        if(titleSet.isEmpty()) {
            if (referencePrice < min)
                return minTitle;
            return maxTitle;
        }
        List<String> list = new ArrayList<>(titleSet);
        return list.get((int) (Math.random() * (titleSet.size() - 1)));
    }
    public String getHardwareTitleWatt(Hardware hardware, double referencePrice, double referenceWatt) throws SQLException {
        System.out.println(referencePrice);
        ResultSet resultSet;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM kursach." + hardware.getHardware());
        String minTitle = "";
        String maxTitle = "";
        double lowBound = 0.97;
        double upBound = 1.01;
        double min = 100;
        double max = 100;
        double tempPrice;
        int tempWatt;

        resultSet = preparedStatement.executeQuery("SELECT * FROM kursach." + hardware.getHardware());
        for(int i = 0; i < 5; ++i) {
            while (resultSet.next()) {
                tempPrice = resultSet.getDouble(hardware.getHardware() + "_price");
                if(tempPrice > max) {
                    max = tempPrice;
                    maxTitle = resultSet.getString(hardware.getHardware() + "_title");
                }
                if (tempPrice < min) {
                    min = tempPrice;
                    minTitle = resultSet.getString(hardware.getHardware() + "_title");
                }
                tempWatt = resultSet.getInt(hardware.getHardware() + "_watt");
                if (tempWatt < referenceWatt)
                    continue;
                if (tempPrice > referencePrice * lowBound && tempPrice < referencePrice * upBound)
                    return resultSet.getString(hardware.getHardware() + "_title");
            }
            lowBound -= 0.04;
            upBound += 0.01;
        }
        preparedStatement.execute();
        resultSet.close();
        preparedStatement.close();
        if(referencePrice < min)
            return minTitle;
        return maxTitle;
    }
    public double getPrice(Hardware hardware, String title) throws SQLException {
        ResultSet resultSet;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM kursach." + hardware.getHardware());
        double price = 0.0;
        resultSet = preparedStatement.executeQuery("SELECT * FROM kursach." + hardware.getHardware());
        while (resultSet.next())
            if(resultSet.getString(hardware.getHardware() + "_title").equals(title)) {
                price = Double.parseDouble(resultSet.getString(hardware.getHardware() + "_price"));
            }
        preparedStatement.execute();
        resultSet.close();
        preparedStatement.close();
        return price;
    }
    public int getWatt(Hardware hardware, String title) throws SQLException {
        ResultSet resultSet;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM kursach." + hardware.getHardware());
        int watt = 0;
        resultSet = preparedStatement.executeQuery("SELECT * FROM kursach." + hardware.getHardware());
        while (resultSet.next())
            if(resultSet.getString(hardware.getHardware() + "_title").equals(title)) {
                watt = Integer.parseInt(resultSet.getString(hardware.getHardware() + "_watt"));
            }
        preparedStatement.execute();
        resultSet.close();
        preparedStatement.close();
        return watt;
    }
    public BufferedImage getImage(Hardware hardware, String title) throws SQLException, IOException {
        ResultSet resultSet;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM kursach." + hardware.getHardware());
        BufferedImage bufferedImage = new BufferedImage(1000,800,1);
        resultSet = preparedStatement.executeQuery("SELECT * FROM kursach." + hardware.getHardware());
        while (resultSet.next())
            if(resultSet.getString(hardware.getHardware() + "_title").equals(title)) {
                Blob image = resultSet.getBlob(hardware.getHardware() + "_image");
                bufferedImage = ImageIO.read(image.getBinaryStream());
            }
        preparedStatement.execute();
        resultSet.close();
        preparedStatement.close();
        return bufferedImage;
    }
    private InputStream toInputStream(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrOut = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrOut);
        InputStream byteArrIn = new ByteArrayInputStream(byteArrOut.toByteArray());

        return  byteArrIn;
    }

}
