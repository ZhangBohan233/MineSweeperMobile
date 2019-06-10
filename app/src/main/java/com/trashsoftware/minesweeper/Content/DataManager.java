package com.trashsoftware.minesweeper.Content;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class DataManager {

    private final static String RECORD_NAME = "record.msr";

    public static Map<String, Long> getRecords(Context context) {
        Map<String, Long> map = new HashMap<>();
        try {
            FileInputStream fis = context.openFileInput(RECORD_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                String[] sep = line.split("=");
                String key = sep[0];
                long value = Long.parseLong(sep[1], 10);
                map.put(key, value);
            }

            br.close();
        } catch (IOException e) {
            //
        }
        return map;
    }

    private static void writeRecords(Context context, Map<String, Long> records)
            throws IOException {
        FileOutputStream fos = context.openFileOutput(RECORD_NAME, Context.MODE_PRIVATE);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);
        for (Map.Entry<String, Long> entry : records.entrySet()) {
            bw.write(entry.getKey());
            bw.write('=');
            bw.write(entry.getValue().toString());
            bw.write('\n');
        }

        bw.close();
    }

    public static void updateRecord(Context context, String key, long time) throws IOException {
        Map<String, Long> records = getRecords(context);
        Long prev = records.get(key);
        if (prev == null || prev > time) {
            records.put(key, time);
        }
        writeRecords(context, records);
    }
}
