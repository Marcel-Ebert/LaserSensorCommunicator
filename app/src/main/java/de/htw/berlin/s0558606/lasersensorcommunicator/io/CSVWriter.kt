package de.htw.berlin.s0558606.lasersensorcommunicator.io

import android.content.Context
import de.htw.berlin.s0558606.lasersensorcommunicator.model.AppDatabase
import java.io.File
import java.io.FileWriter
import java.util.*

/**
 * Created by Marcel Ebert S0558606 on 08.06.18.
 */
object CSVWriter {

    fun getDatabaseContentCSVFileName(context: Context): String {

        val folderPath = getFolderPath(context)
        val fileName = "$folderPath/sensordata.csv"

        val databaseContentLists = getListOfDatabaseObjects(context)

        tryWriteFileFromLists(databaseContentLists, fileName)

        return fileName
    }

    private fun getFolderPath(context: Context): String {
        val folder = File(context.filesDir.toString() + "/SensorData")

        if (!folder.exists())
            folder.mkdir()

        return folder.toString()
    }

    private fun getListOfDatabaseObjects(context: Context): List<List<String>> {
        val db = AppDatabase.getInstance(context)

        val superList = ArrayList<List<String>>()

        val locations = db.locationDao().getAllLocationsSynchronous()

        for (location in locations) {
            val measurements = db.measurementDao().findMeasurementsByLocationIDSynchronous(location.id)

            for (measurement in measurements) {
                val sensorDataList = db.sensorDataDao().findDataByMeasurementIdSynchronous(measurement.id)

                for (sensorData in sensorDataList) {
                    val miniList = ArrayList<String>()
                    miniList.add(sensorData.getDateAsString())
                    miniList.add(sensorData.pm10)
                    miniList.add(sensorData.pm25)
                    miniList.add(location.location.latitude.toString())
                    miniList.add(location.location.longitude.toString())
                    superList.add(miniList)
                }
            }
        }
        return superList

    }

    private fun tryWriteFileFromLists(superList: List<List<String>>, fileName: String) {
        try {
            val fw = FileWriter(fileName)

            fw.append("Timestamp")
            fw.append(',')

            fw.append("PM10")
            fw.append(',')

            fw.append("PM25")
            fw.append(',')

            fw.append("Latitude")
            fw.append(',')

            fw.append("Longitude")

            fw.append('\n')

            for (miniList in superList) {
                miniList.forEachIndexed { index, value ->
                    run {
                        if (index != miniList.lastIndex) {
                            fw.append(value)
                            fw.append(",")
                        } else {
                            fw.append(value)
                        }
                    }
                }

                fw.append("\n")
            }

            fw.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
