/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.texus.shapefileviewer.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.texus.shapefileviewer.datamodel.ShapeData;
import com.texus.shapefileviewer.datamodel.ShapeField;
import com.texus.shapefileviewer.datamodel.ShapeFieldData;
import com.texus.shapefileviewer.datamodel.ShapePoint;
import com.texus.shapefileviewer.utility.LOG;

import java.util.ArrayList;

/**
 * @author Sandeep Kumar P K <br>
 *         <ahref="mailto:pksandeepkumar@gmail.com">pksandeepkumar@gmail.com</a>
 */
public class Databases extends SQLiteOpenHelper {

	Context context;
	private static final String TAG = Databases.class.getName();
	public static final String DATABASE_NAME = "DB.db";
	public static final int DATABASE_VERSION = 1;

	private static ArrayList<String> query_create_tables = null;
	private static ArrayList<String> tables = null;

	public Databases(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		loadQuery();
		try {
			for (String query : query_create_tables) {
				LOG.log("Query:", "Query:" + query);
				database.execSQL(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	/**
	 * Create list of crate table queries. 
	 */
	private void loadQuery() {
		query_create_tables = new ArrayList<String>();
		query_create_tables.add(ShapeData.CREATE_TABE_QUERY);
        query_create_tables.add(ShapeField.CREATE_TABE_QUERY);
        query_create_tables.add(ShapeFieldData.CREATE_TABE_QUERY);
        query_create_tables.add(ShapePoint.CREATE_TABE_QUERY);

	}
	
	/**
	 * Used for delete purpose, load drop table queries
	 */
	private void loadTableNames() {

//        tables.add(ItemData.TABLE_NAME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		LOG.log(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		try {
			loadTableNames();
			for (String table_name : tables) {
				database.execSQL("DROP TABLE " + table_name + "; ");
			}
		} catch (SQLException e) {
			LOG.log(TAG, "drop table error:" + e.getMessage());
		}
		onCreate(database);
	}


}
