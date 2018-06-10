package de.htw.berlin.s0558606.lasersensorcommunicator.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Marcel Ebert S0558606 on 26.05.18.
 */
@Database(version = 1, entities = {MeasuringLocation.class, SensorData.class, Measurement.class})
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "data.db";

    private static AppDatabase sInstance;

    abstract public MeasuringLocationDao locationDao();

    abstract public SensorDataDao sensorDataDao();

    abstract public MeasurementDao measurementDao();

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME).allowMainThreadQueries()
                            .build();
                }
            }
        }
        return sInstance;
    }

}
