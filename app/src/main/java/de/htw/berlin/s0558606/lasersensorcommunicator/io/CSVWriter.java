package de.htw.berlin.s0558606.lasersensorcommunicator.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import de.htw.berlin.s0558606.lasersensorcommunicator.R;
import de.htw.berlin.s0558606.lasersensorcommunicator.model.AppDatabase;
import de.htw.berlin.s0558606.lasersensorcommunicator.model.Location;
import de.htw.berlin.s0558606.lasersensorcommunicator.model.Measurement;
import de.htw.berlin.s0558606.lasersensorcommunicator.model.SensorData;

/**
 * Created by Marcel Ebert S0558606 on 08.06.18.
 */
public class CSVWriter {

    public static String getDatabaseContentAsCSV(Context context) {

        File folder = new File(Environment.getExternalStorageDirectory() + "/Folder");

        boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();

        System.out.println("Folder created = " + var);

        final String fileName = folder.toString() + "/" + "sensordata.csv";

        createFileFromLists(getListOfDatabaseObjects(context), fileName);

        return fileName;
    }

    private static List<List<String>> getListOfDatabaseObjects(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);

        List<List<String>> superList = new ArrayList<>();

        List<Location> locations = db.locationDao().getAllLocationsSynchronous();

        for (Location location : locations) {
            List<Measurement> measurements = db.measurementDao().findMeasurementsByLocationIDSynchronous(location.getId());

            for (Measurement measurement : measurements) {
                List<SensorData> sensorDataList = db.sensorDataDao().findDataByMeasurementIdSynchronous(measurement.getId());

                for (SensorData sensorData : sensorDataList) {
                    List<String> miniList = new ArrayList<>();
                    miniList.add(sensorData.getDateAsString());
                    miniList.add(sensorData.getPm10());
                    miniList.add(sensorData.getPm25());
                    miniList.add(String.valueOf(location.getLocation().latitude));
                    miniList.add(String.valueOf(location.getLocation().longitude));
                    superList.add(miniList);
                }
            }
        }
        return superList;

    }

    private static void createFileFromLists(List<List<String>> superList, String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName);

            fw.append("Timestamp");
            fw.append(',');

            fw.append("PM10");
            fw.append(',');

            fw.append("PM25");
            fw.append(',');

            fw.append("Latitude");
            fw.append(',');

            fw.append("Longitude");

            fw.append('\n');

            for (List<String> miniList : superList) {
                for (String value : miniList) {
                    fw.append(value);
                    fw.append(",");
                }
            }

            // fw.flush();
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
