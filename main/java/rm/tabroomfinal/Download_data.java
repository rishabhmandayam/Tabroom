package rm.tabroomfinal;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Download_data implements Runnable {

    public download_complete caller;
    private final Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            String Response = msg.getData().getString("message");

            caller.get_data(Response);

        }
    };
    private String link;
    private Context con;
    private boolean refreshfromLink;

    Download_data(download_complete caller) {
        this.caller = caller;
    }


    private void writeToFile(String data) {
        try {

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.openFileOutput("tabroomdata.json", con.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String CreateJsonFromHtml(Context con) throws JSONException {
        StringBuilder sb = null;


        try {
            //InputStream is = con.getAssets().open("trimmed.txt");
            InputStream is = con.openFileInput("tabroomdata.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return download(this.link);
        }
        String fileAsString = sb.toString();


        return fileAsString;

    }

    private static String Html2Json(String inputString) throws JSONException {
        Document document = Jsoup.parse(inputString);

        Element tournListTable = document.getElementById("tournlist");

        String arrayName = tournListTable.select("tr").first().text();
        Elements tournListTableHeaderElements = tournListTable.getElementsByTag("th");

        for (Element e : tournListTableHeaderElements) {
            System.out.println(e.text());
        }
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArr = new JSONArray();
        int attributeCnt = tournListTableHeaderElements.size();

        Elements tournListTableBodyElements = tournListTable.getElementsByTag("tbody");
        for (Element e : tournListTableBodyElements) {
            Elements tournListTableBodyRowelements = e.getElementsByTag("tr");
            for (Element ex : tournListTableBodyRowelements) {
                Elements tournListTableBodyRowColumnsElements = ex.getElementsByTag("td");
                JSONObject jsonTempObj = new JSONObject();

                int tempCnt = 1;
                System.out.printf("*******************");
                String hiddenElementText = "";
                for (Element tde : tournListTableBodyRowColumnsElements) {
                    Elements linkElements = tde.getElementsByAttribute("href");
                    for (Element linkelem:linkElements) {
                        String idString = linkelem.attr("href");
                        System.out.println(idString);
                        Log.e("Link",idString);
                        if (idString != null && idString.contains("tourn_id")) {
                            System.out.println(idString);
                            jsonTempObj.put("tournamentlink","https://www.tabroom.com/index/"+idString);

                        }
//                        System.out.println(linkelem.attr("href"));
                    }
                    Elements hiddenElement = tde.getElementsByTag("span");
                    for (Element hidelement : hiddenElement) {
                        if (hidelement.hasClass("hidden")) {
                            System.out.println(hidelement.text());
                            hiddenElementText = hidelement.text();
                        }
                    }
                    String cellText = "";
                    if (hiddenElementText != null) {
                        cellText = StringUtils.replace(tde.text(), hiddenElementText, "").trim();
                    } else {
                        cellText = tde.text();
                    }
                    System.out.println(cellText);
                    jsonTempObj.put(String.valueOf(tournListTableHeaderElements.get(tempCnt - 1).text()), cellText);
                    //String keyString = tournListTableHeaderElements.get(tempCnt+1).text();


                    //to do set the rest of the properties on th etempObject
                    tempCnt++;
                }
                System.out.println(jsonTempObj);

                jsonArr.put(jsonTempObj);
                System.out.println("****************************");
            }

        }
        System.out.println(jsonArr);
        return jsonArr.toString();
    }

    public  String download(String url) throws JSONException {
        URL website;
        StringBuilder response = null;
        try {
            website = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) website.openConnection();
            connection.setRequestProperty("charset", "utf-8");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));

            response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                System.out.println(inputLine);
            }


            in.close();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        String jsonString =  Html2Json(response.toString());
        writeToFile(jsonString);
        return jsonString;
    }

    public void download_data_from_link(String link, Context context, boolean refreshfromLink) {
        this.link = "https://www.tabroom.com/index/index.mhtml";
        this.con = context;
        this.refreshfromLink = refreshfromLink;
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        try {
            if (this.refreshfromLink == true) {
                threadMsg(download(this.link));
            } else {
                threadMsg(CreateJsonFromHtml(this.con));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void threadMsg(String msg) {

        if (!msg.equals(null) && !msg.equals("")) {
            Message msgObj = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("message", msg);
            msgObj.setData(b);
            handler.sendMessage(msgObj);
        }
    }


    public interface download_complete {
        public void get_data(String data);
    }


}