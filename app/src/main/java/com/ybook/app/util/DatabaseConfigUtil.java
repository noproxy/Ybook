package com.ybook.app.util;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * DatabaseConfigUtl writes a configuration file to avoid using Annotation processing in runtime. This gains a
 * noticeable performance improvement. configuration file is written to /res/raw/ by default.
 * More info at: http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Config-Optimization
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile("/home/carlos/WorkProject/YBookWork/Ybook/app/src/main/res/raw/ormlite_config.txt");
    }
}
