package com.ybook.app.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.ybook.app.util.BooksListUtil;

import java.io.Serializable;

/**
 * Created by carlos on 12/5/14.
 */
@DatabaseTable(tableName = "BookItems")
public class BookItem implements Serializable {
    @DatabaseField
    public int listID = -1;
    @DatabaseField
    public long collectTime = 0;


    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public DetailResponse detailResponse = null;

    @DatabaseField(generatedId = true)
    int id;

    public void markOrCancelMarked(BooksListUtil util) {
        int in = MarkedList.getMarkedList().index(util, detailResponse.getId());
        if (in > -1) {
            MarkedList.getMarkedList().removeBook(MarkedList.getMarkedList().getBookItems(util).get(in), util);
        } else {
            MarkedList.getMarkedList().addBook(this, util);
        }
    }

    public boolean isMarked(BooksListUtil util) {
        return MarkedList.getMarkedList().index(util, detailResponse.getId()) > -1;
    }


}
