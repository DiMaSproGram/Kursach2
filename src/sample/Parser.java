package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Parser {
    enum HardwareURL {
        PROCESSOR_URL ("https://kst.by/kompyutery-i-komplektuyushchie/processory/price/194-4035/sklad/7?sort=p.price&order=ASC&limit=100"),
        VIDEO_CARD_URL ("https://kst.by/kompyutery-i-komplektuyushchie/videokarty/price/100-6000/sklad/7?sort=p.price&order=ASC&limit=100"),
        MOTHERBOARD_URL ("https://kst.by/kompyutery-i-komplektuyushchie/materinskie-platy/price/55-650/sklad/7?sort=p.price&order=ASC&limit=100"),
        RAM_URL ("https://kst.by/kompyutery-i-komplektuyushchie/operativnaya-pamyat/price/40-800/sklad/7?sort=p.price&order=ASC&limit=100"),
        HDD_URL ("https://kst.by/kompyutery-i-komplektuyushchie/jestkie-diski/price/31-250/sklad/7?sort=p.price&order=ASC&limit=100"),
        SSD_URL ("https://kst.by/kompyutery-i-komplektuyushchie/ssd/price/21-420/sklad/7?sort=p.price&order=ASC&limit=100"),
        POWER_SUPPLY_URL ("https://kst.by/kompyutery-i-komplektuyushchie/bloki-pitaniya/price/21-160/sklad/7?sort=p.price&order=ASC&limit=100"),
        COOLERS_URL ("https://kst.by/kompyutery-i-komplektuyushchie/sistemy-ohlajdeniya/price/10-200/sklad/7/termokontrol/net?sort=p.price&order=ASC&limit=100"),
        COMPUTER_CASE_URL ("https://kst.by/kompyutery-i-komplektuyushchie/korpusa/price/33-514/sklad/7/tip/bez-bloka-pitanija/tip-korpusa/tower?sort=p.price&order=ASC&limit=100");

        private String url;

        HardwareURL(String url){
            this.url = url;
        }
        public String getUrl() {
            return url;
        }
    }
    private DataBase dataBase;
    private HardwareURL[] arrURL;
    private DataBase.Hardware[] arrHard;
    private LocalDateTime localDate;
    private static LocalDateTime lastUpdDate;
    private File file;
    private double progress = 0.01;

    Parser() throws SQLException, ClassNotFoundException, IOException {
        dataBase =  new DataBase();
        arrURL = HardwareURL.values();
        arrHard = DataBase.Hardware.values();
        localDate = LocalDateTime.now();
        file = new File("src\\sample\\file.txt");
        lastUpdDate = writeUpdDate(false);
    }
    public static boolean netIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
    public boolean isStart(){
        if(compareDates(localDate, lastUpdDate.plusDays(6)) >= 0)
            return true;
        return false;
    }
    public boolean startParsing() throws IOException, SQLException {
        lastUpdDate = writeUpdDate(true);
        dataBase.deleteAll();
        for (int i = 0; i < arrURL.length; ++i) {
            parse(arrURL[i].getUrl(), arrHard[i]);
            progress += 0.11;
            System.out.println(progress);
        }
        System.out.println(LocalDateTime.now());
        return true;
    }

    private void parse(String url, DataBase.Hardware hardware) throws IOException, SQLException {
        Document doc;
        Elements ul;
        Elements li;

        while (true) {
            if (parsePage(url, hardware) == 1)
                break;
            doc = Jsoup.connect(url).get();
            ul = doc.select("ul.pagination");
            if (ul.toString().equals(""))
                break;
            li = ul.select("li");
            if(li.last().attr("class").equals("active"))
                break;
            url = li.get(li.size() - 2).select("a").attr("href");
            System.out.println(url);
        }
    }
    private int parsePage(String url, DataBase.Hardware hardware) throws IOException, SQLException {
        System.out.println(url);
        Document doc = Jsoup.connect(url).get();
        List<Element> productList = doc.select("div.product-thumb");
        int j = 0;
        for(int i = 0; i < productList.size(); ++i) {
            if(i % hardware.getN() != 0) {
                productList.remove(i);
                continue;
            }
            if(hardware.getItemInfo()) {
                String itemHref = productList.get(i).select("div.image a").attr("href");
                System.out.println(itemHref);
                if (parseItem(itemHref, hardware) == 1)
                    return 1;
            }
            else {
                String title = productList.get(i).select("div.caption a").text();
                String price;
                Element p = productList.get(i).select("div.caption .price").first();
                price = p.attr("data-price");
                if (price.equals("Товар отсутствует"))
                    return 1;
                if(hardware.getId() == 1){
                    double temp = Double.parseDouble(price);
                    int watt;

                    if(temp > 500) {
                        watt = (int) (temp - temp % 100 - 50 * Math.round(Math.random() * 4 + 4));
                        if(watt >= 500)
                            watt = 500;
                    }
                    else if(temp > 250)
                        watt = (int) (temp - temp % 100 - 50 * Math.round(Math.random() + 1));
                    else
                        watt = (int) ((temp - temp % 100) * 100);
                    dataBase.addTitlePriceWatt(delExcess(title), temp, watt, hardware);
                }
                else
                    dataBase.addTitlePrice(delExcess(title), Double.parseDouble(price), hardware);
            }
            ++j;
        }
        System.out.println(j);
        return 0;
    }
    private int parseItem(String url, DataBase.Hardware hardware) throws IOException, SQLException {
        double price;
        String title;

        Document doc = Jsoup.connect(url).get();

        Element divPrice = doc.selectFirst("div.price");
        if(divPrice.text().equals("Товар отсутствует"))
            return 1;
        price = Double.parseDouble(divPrice.attr("data-price"));

        Element h1 = doc.selectFirst("h1.heading");
        title = h1.text();

        if(hardware.getId() == 8) {
            Element a = doc.selectFirst("a.thumbnail.image-after");
            String imageUrl = a.attr("href");
            dataBase.addTitlePriceImage(delExcess(title), price, parseImage(imageUrl), hardware);
        }
        else if(hardware.getId() == 6) {
            String watt = "";
            Element div = doc.select("div.tab-content #tab-specification").first();
            if(div == null)
                return 0;
            Elements divTitls = div.select("div.after.col-xs-7.col-md-6");

            for (int i = 0; i < divTitls.size(); ++i){
                if(divTitls.get(i).text().equals("Мощность"))
                    watt = div.select("div.col-xs-5.col-md-6").get(i).text();
            }
            dataBase.addTitlePriceWatt(delExcess(title), price, delWatt(watt), hardware);
        }
        return 0;
    }
    private BufferedImage parseImage(String url) throws IOException {
        URL imageUrl = new URL(url);
        BufferedImage image = ImageIO.read(imageUrl);
        return image;
    }

    private int delWatt(String text){
        char[] arr = text.toCharArray();
        StringBuilder strBuild = new StringBuilder();

        for(int i = 0; i < arr.length; ++i){
            if(arr[i] < 48 || arr[i] > 57)
                continue;
            strBuild.append(arr[i]);
        }
        return Integer.parseInt(strBuild.toString());
    }
    private String delExcess(String text){
        char[] arr = text.toCharArray();
        StringBuilder strBuild = new StringBuilder();

        for(int i = 0; i < arr.length; ++i){
            if(arr[i] >= 1040 && arr[i] <= 1103)
                continue;
            strBuild.append(arr[i]);
        }
        return strBuild.toString().trim();
    }
    private double delPrice(String sPrice) {
        char[] arr;
        arr = sPrice.toCharArray();
        StringBuilder strBuild = new StringBuilder();
        for(int i = 0; i < arr.length - 4; ++i) {
            strBuild.append(arr[i]);
        }
        System.out.println(strBuild);
        return Double.parseDouble(strBuild.toString());
    }

    private LocalDateTime writeUpdDate(boolean isWriting) throws IOException {
        if(file.length() == 0 || isWriting) {
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(localDate.toString());
            fileWriter.flush();
            fileWriter.close();
            return localDate;
        }
        FileReader fileReader = new FileReader(file);
        int[] arr = getDate(fileReader);
        int[] arr2 = getTime(fileReader);
        return LocalDateTime.of(arr[0], arr[1], arr[2], arr2[0], arr2[1], arr2[2]);
    }
    private int[] getDate(FileReader fileReader) throws IOException {
        return getDateTime(fileReader, 'T', "-");
    }
    private int[] getTime(FileReader fileReader) throws IOException {
        return getDateTime(fileReader, '.', ":");
    }
    private int[] getDateTime(FileReader fileReader, char board, String spliterator) throws IOException {
        StringBuilder strBuld = new StringBuilder();
        String[] arrStr;
        int letter;
        int[] arr = new int[3];

        while((letter = fileReader.read()) != (int)board)
            strBuld.append((char) letter);

        arrStr = strBuld.toString().split(spliterator);

        for(int i = 0; i < 3; ++i)
            arr[i] = Integer.parseInt(arrStr[i]);

        return arr;
    }
    public int compareDates(LocalDateTime now, LocalDateTime other) {
        int cmp = (now.getYear() - other.getYear());
        if (cmp == 0) {
            cmp = (now.getMonthValue() - other.getMonthValue());
            if (cmp == 0) {
                cmp = (now.getDayOfMonth() - other.getDayOfMonth());
            }
        }
        return cmp;
    }

    public static LocalDateTime getLastUpdDate() {
        return lastUpdDate;
    }
    public LocalDateTime getLocalDate() {
        return localDate;
    }
    public double getProgress() {
        return progress;
    }
}
