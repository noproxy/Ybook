package com.ybook.app.bean;

import com.ybook.app.R;
import com.ybook.app.util.BooksListUtil;

import java.util.ArrayList;

/**
 * Created by Carlos on 2014/8/9.
 */
public class MarkedList extends BookList {
    public final static String MARKED_LIST_NAME = "collection";
    public static final String KEY_ARRAY = "array";
    public static final String KEY_TYPE = "type";
    public static final String[] QUERY_ID_ARRAY = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "N", "O", "P", "Q", "R", "S", "T", "TB", "TD", "TE", "TF", "TG", "TH", "TJ", "TK", "TL", "TM", "TN", "TP", "TQ", "TS", "TU", "TV", "U", "V", "X", "Z", "untitle",
    };
    public static final int[] QUERY_ICON_ID = {
            R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i, R.drawable.j, R.drawable.k, R.drawable.n, R.drawable.o, R.drawable.p, R.drawable.q, R.drawable.r, R.drawable.s, R.drawable.t, R.drawable.tb, R.drawable.td, R.drawable.te, R.drawable.tf, R.drawable.tg, R.drawable.th, R.drawable.tj, R.drawable.tk, R.drawable.tl, R.drawable.tm, R.drawable.tn, R.drawable.tp, R.drawable.tq, R.drawable.ts, R.drawable.tu, R.drawable.tv, R.drawable.u, R.drawable.v, R.drawable.x, R.drawable.z, R.drawable.no_type,
    };
    static final String[] TYPE_ARRAY = {
            "马列主义毛泽东思想", "哲学", "社会科学总论", "政治法律", "军事", "经济", "文化科学教育体育", "语言、文字", "文学", "艺术", "历史地理", "自然科学总论", "数理科学和化学", "天文学、地球科学", "生物科学", "医药卫生", "农业科学", "工业技术", " 一般工业技术", "矿业工程", "石油、天然气工业", "冶金工业", "金属学与金属工艺", "机械、仪表工业", "武器工业", "能源与动力工程", "原子能技术", "电工技术", "无线电电子学、电信技术", "自动化技术、计算机技术", "化学工业", "轻工业、手工业", "建筑科学", "水利工程", "交通运输", "航空、航天", "环境科学、安全科学", "综合性图书", "未上架",
    };
    private static final int id = 0;
    private static final String TAG = "MarkedList";
    private static MarkedList mInstance;
    private ArrayList<ArrayList<BookItem>> mSeparatedItems;

    private MarkedList() {
        super(MARKED_LIST_NAME);
        super.id = 0;
    }

    public static MarkedList getMarkedList() {
//        Log.i(TAG, "getMarkedList()");
        if (mInstance == null) {
            mInstance = new MarkedList();
        }
        return mInstance;
    }

    public static String getType(String queryHead) {
        for (int i = 0; i < QUERY_ID_ARRAY.length; i++) {
            if (QUERY_ID_ARRAY[i].equals(queryHead)) {
                return TYPE_ARRAY[i];
            }
        }
        return TYPE_ARRAY[0];
    }

    public static int getIconID(String queryHead) {
        for (int i = 0; i < QUERY_ID_ARRAY.length; i++) {
            if (queryHead.equals(QUERY_ID_ARRAY[i])) {
                return QUERY_ICON_ID[i];
            }
        }
        return QUERY_ICON_ID[0];
    }

    public static String getQueryHead(String queryID) {
        if (queryID == null || queryID.trim().length() == 0) {
            return "untitle";
        }
        char a = queryID.charAt(0);
        char b = queryID.charAt(2);
        if (b >= 'a' && b <= 'z') {
            return String.valueOf(a) + String.valueOf(b);
        } else {
            return String.valueOf(a);
        }
    }

    private ArrayList<ArrayList<BookItem>> separate() {
//        Log.i(TAG, "separate()");
        ArrayList<ArrayList<BookItem>> results = new ArrayList<ArrayList<BookItem>>();
        ArrayList<String> headRecord = new ArrayList<String>();
        for (BookItem bookItem : items) {
            String head = getQueryHead(bookItem.detailResponse.getQueryID());
            if (!headRecord.contains(head)) {
                headRecord.add(head);
                results.add(new ArrayList<BookItem>());
            }
            results.get(headRecord.indexOf(head)).add(bookItem);
//            Log.i(TAG, "book: " + bookItem.detailResponse.getTitle() + "; head: " + head);
        }
        return results;
    }

    public ArrayList<ArrayList<BookItem>> getSeparatedItems(BooksListUtil util) {
//        Log.i(TAG, "isUpdate:" + isUpdate);
        if (!isUpdate) {
            getBookItems(util);
        }
        mSeparatedItems = this.separate();
        return mSeparatedItems;
    }

    public int index(BooksListUtil util, SearchResponse.SearchObject object) {
        ArrayList<BookItem> bookItems = getBookItems(util);
        for (BookItem bookItem : bookItems) {
            if (object.getId().equals(bookItem.detailResponse.getId())) {
                return bookItems.indexOf(bookItem);
            }
        }
        return -1;
    }

    public int index(BooksListUtil util, String id) {
        ArrayList<BookItem> bookItems = getBookItems(util);
        for (BookItem bookItem : bookItems) {
            if (id.equals(bookItem.detailResponse.getId())) {
                return bookItems.indexOf(bookItem);
            }
        }
        return -1;
    }

}
