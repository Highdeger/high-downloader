package highdeger.highdownloader;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseHelper {
    Connection connection;
    String queryTableDownloads = "create table if not exists downloads (\n" // create table downloads with 16 columns
            + "id integer primary key, \n"
            + "url text not null, \n"
            + "params text, \n"
            + "description text, \n"
            + "referer text, \n"
            + "date_added text, \n" // "YYYY-MM-DD HH:MM:SS.SSS"
            + "date_tried text, \n" // "YYYY-MM-DD HH:MM:SS.SSS"
            + "queue_id integer, \n"
            + "size_total text, \n"
            + "size_downloaded text, \n"
            + "time_elapsed integer, \n" // seconds
            + "time_left integer, \n" // seconds
            + "status text, \n" // 'Completed', ' ', '0 %', '27.43 %'
            + "save_name text, \n" // save file name
            + "save_path text, \n" // save path
            + "file_name text, \n" // fetched file name from the url
            + "file_type integer);"; // 0-unknown, 1-compressed, 2-document, 3-video, 4-audio, 5-program
    String queryTableQueues = "create table if not exists queues (\n" // create table queues with 3 columns
            + "id integer primary key, \n"
            + "name text not null unique, \n" // unique, means repetitive queue names will be ignored and throw an exception
            + "speed_limit integer);"; // bytes per seconds
    Statement statement;

    public DatabaseHelper(String filenameWithExt) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + filenameWithExt, "highdl", "H1Gh@dL#iKm");
        } catch (SQLException e) {
            throw new RuntimeException("Can't connect to database.", e);
        }

        try {
            statement = connection.createStatement();
            statement.execute(queryTableDownloads);
            statement.execute(queryTableQueues);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create tables in database.");
        }
    }

    /**
     * add a download
     * @param d HighDownload, id will be ignored
     * @throws RuntimeException, if can't "run the query"
     */
    public void addDownload(HighDownload d) throws RuntimeException {
        String q = String.format("insert into downloads (url, params, description, referer, date_added, date_tried, queue_id, size_total, size_downloaded, time_elapsed, time_left, status, save_name, save_path, file_name, file_type) values (%s, %s, %s, %s, %s, %s, %d, %s, %s, %s, %s, %s, %s, %s, %s, %d);",
                d.getUrl(), d.getParams(), d.getDescription(), d.getReferer(), d.getDate_added(), d.getDate_tried(), d.getQueue_id(), d.getSize_total(), d.getSize_downloaded(), d.getTime_elapsed(), d.getTime_left(), d.getStatus(), d.getSave_name(), d.getSave_path(), d.getFile_name(), getTypeId(d.getFile_type()));
        runQuery(q, "Can't add a download to db");
    }

    /**
     * get one download by id
     * @param id int, id to search for
     * @return HighDownload, null if nothing has found
     * @throws RuntimeException, if can't "read from db" or "run the query"
     */
    public HighDownload getOneDownloadById(int id) throws RuntimeException {
        String q = String.format("select * from downloads where id = %d;", id);
        ResultSet r = runQuery(q, "Can't run query on db (download_by_id)");
        return firstHighDownload(r, "Can't read db (download_by_id)");
    }

    /**
     * get one download by url
     * @param url String, bare url or url with params(e.g. http://..?p1=3&p2=hello)
     * @return ArrayList<HighDownload>, empty if nothing has found
     * @throws RuntimeException, if can't "read from db" or "run the query"
     */
    public ArrayList<HighDownload> findAllDownloadsByUrl(String url) throws RuntimeException {
        String q = String.format("select * from downloads where url like '%s%%';", HighUtil.bareUrl(url));
        ResultSet r = runQuery(q, "Can't run query on db (all_downloads_by_url)");
        return allHighDownloads(r, "Can't read db (all_downloads_by_url)");
    }

    /**
     * get all downloads by status and type
     * @param status HighUtil.Status, filter status
     * @param file_type HighUtil.FileType, filter file type
     * @return ArrayList<HighDownload>, empty if nothing has found
     * @throws RuntimeException, if can't "read from db" or "run the query"
     */
    public ArrayList<HighDownload> getAllDownloadsByType(HighUtil.Status status, HighUtil.FileType file_type) throws RuntimeException {
        StringBuilder q = new StringBuilder();
        q.append("select * from downloads where file_type = ");
        q.append(getTypeId(file_type));
        switch (status) {
            case completed:
                q.append(" and status = 'Completed'");
            case uncompleted:
                q.append(" and status <> 'Completed'");
        }
        q.append(";");
        ResultSet r = runQuery(q.toString(), "Can't run query on db (all_downloads_by_type)");
        return allHighDownloads(r, "Can't read db (all_downloads_by_type)");
    }

    /**
     * get all downloads by status
     * @param status HighUtil.Status, filter status
     * @return ArrayList<HighDownload>, empty if nothing has found
     * @throws RuntimeException, if can't "read from db" or "run the query"
     */
    public ArrayList<HighDownload> getAllDownloads(HighUtil.Status status) throws RuntimeException {
        StringBuilder q = new StringBuilder();
        q.append("select * from downloads");
        switch (status) {
            case completed:
                q.append(" where status = 'Completed'");
            case uncompleted:
                q.append(" where status <> 'Completed'");
        }
        q.append(";");
        ResultSet r = runQuery(q.toString(), "Can't run query on db (all_downloads)");
        return allHighDownloads(r, "Can't read db (all_downloads)");
    }

    // get id by HighUtil.FileType
    private int getTypeId(HighUtil.FileType type) {
        switch (type) {
            case compressed: return 1;
            case document: return 2;
            case video: return 3;
            case audio: return 4;
            case program: return 5;
            default: return 0;
        }
    }

    // get HighUtil.FileType by id
    private HighUtil.FileType getType(int typeId) {
        switch (typeId) {
            case 1: return HighUtil.FileType.compressed;
            case 2: return HighUtil.FileType.document;
            case 3: return HighUtil.FileType.video;
            case 4: return HighUtil.FileType.audio;
            case 5: return HighUtil.FileType.program;
            default: return HighUtil.FileType.unknown;
        }
    }

    /**
     * update a download by id
     * @param fresh HighDownload, id will be used to search and other fields to edit
     * @throws RuntimeException, if can't "run the query"
     */
    public void updateDownloadById(HighDownload fresh) throws RuntimeException {
        StringBuilder q = new StringBuilder();
        q.append("update downloads set ");
        if (fresh.getUrl() != null)
            q.append(String.format("url = %s,\n", fresh.getUrl()));
        if (fresh.getDescription() != null)
            q.append(String.format("description = %s,\n", fresh.getDescription()));
        if (fresh.getReferer() != null)
            q.append(String.format("referer = %s,\n", fresh.getReferer()));
        if (fresh.getDate_added() != null)
            q.append(String.format("date_added = %s,\n", fresh.getDate_added()));
        if (fresh.getDate_tried() != null)
            q.append(String.format("date_tried = %s,\n", fresh.getDate_tried()));
        if (fresh.getQueue_id() != -1)
            q.append(String.format("queue_id = %d,\n", fresh.getQueue_id()));
        if (fresh.getSize_total() != null)
            q.append(String.format("size_total = %s,\n", fresh.getSize_total()));
        if (fresh.getSize_downloaded() != null)
            q.append(String.format("size_downloaded = %s,\n", fresh.getSize_downloaded()));
        if (fresh.getTime_elapsed() != -1)
            q.append(String.format("time_elapsed = %d,\n", fresh.getTime_elapsed()));
        if (fresh.getTime_left() != -1)
            q.append(String.format("time_left = %d,\n", fresh.getTime_left()));
        if (fresh.getStatus() != null)
            q.append(String.format("status = %s,\n", fresh.getStatus()));
        if (fresh.getSave_name() != null)
            q.append(String.format("save_name = %s,\n", fresh.getSave_name()));
        if (fresh.getSave_path() != null)
            q.append(String.format("save_path = %s,\n", fresh.getSave_path()));
        if (fresh.getFile_name() != null)
            q.append(String.format("file_name = %s,\n", fresh.getFile_name()));
        if (fresh.getFile_type() != null)
            q.append(String.format("file_type = %d\n", getTypeId(fresh.getFile_type())));
        q.append(String.format("where id = %d;", fresh.getId()));
        runQuery(q.toString(), "Can't edit the download table");
    }

    /**
     * add a queue
     * @param name String, name of the queue
     * @param speed_limit int, speed limit for the queue in Bytes/s, 0 means no limit
     * @throws RuntimeException, if can't "run the query"
     */
    public void addQueue(String name, int speed_limit) throws RuntimeException {
        String q = String.format("insert into queues (name, speed_limit) values (%s, %d);", name, speed_limit);
        runQuery(q, "Can't add a queue");
    }

    /**
     * get one queue by id
     * @param id int,
     * @return HighQueue, null if nothing has found
     * @throws RuntimeException, if can't "read from db" or "run the query"
     */
    public HighQueue getQueueById(int id) throws RuntimeException {
        String q = String.format("select * from queues where id = %d;", id);
        ResultSet r = runQuery(q, "Can't run query on db (queue_by_id)");
        try {
            if (r.first()) {
                return new HighQueue(r.getInt("id"),
                        r.getString("name"),
                        r.getInt("speed_limit"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Can't read db (queue_by_id)", e);
        }
    }

    /**
     * get all queues
     * @return ArrayList<HighQueue>, empty if noting has found
     * @throws RuntimeException, if can't "read from db" or "run the query"
     */
    public ArrayList<HighQueue> getAllQueues() throws RuntimeException {
        ArrayList<HighQueue> result = new ArrayList<>();
        String q = "select * from queues;";
        ResultSet r = runQuery(q, "Can't run query on db (all_queues)");
        try {
            while (r.next()) {
                result.add(new HighQueue(r.getInt("id"),
                        r.getString("name"),
                        r.getInt("speed_limit")));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Can't read db (all_queues)");
        }
    }

    /**
     * update a queue
     * @param fresh HighQueue, id will be used to search and other fields to edit
     * @throws RuntimeException, if can't "run the query"
     */
    public void changeQueue(HighQueue fresh) throws RuntimeException {
        StringBuilder q = new StringBuilder();
        q.append("update queues\n set ");
        if (fresh.getName() != null)
            q.append(String.format("name = %s,\n", fresh.getName()));
        if (fresh.getSpeed_limit() != -1)
            q.append(String.format("speed_limit = %d,\n", fresh.getSpeed_limit()));
        q.append(String.format("where id = %d\n;", fresh.getId()));
        runQuery(q.toString(), "Can't edit the queue table");
    }

    /**
     * run a query and throws a RuntimeException if a SQLException thrown
     * @param query: String, query to be run
     * @param error_msg: String, the message to put into the exception
     * @return ResultSet, result of query running
     * @throws RuntimeException, if can't "run the query" (wrapping SQLException)
     */
    private ResultSet runQuery(String query, String error_msg) throws RuntimeException {
        try {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(error_msg, e);
        }
    }

    /**
     * fetch first HighDownload in the ResultSet
     * @param r: ResultSet, the object from which to extract
     * @param error_msg: String, the message to put into the exception
     * @return HighDownload, if one found. null, if nothing found.
     * @throws RuntimeException, if can't "read from db" (wrapping SQLException)
     */
    private HighDownload firstHighDownload(ResultSet r, String error_msg) throws RuntimeException {
        try {
            if (r.first()) {
                return new HighDownload(r.getInt("id"),
                        r.getString("url"),
                        r.getString("params"),
                        r.getString("description"),
                        r.getString("referer"),
                        r.getString("date_added"),
                        r.getString("date_tried"),
                        r.getInt("queue_id"),
                        r.getString("size_total"),
                        r.getString("size_downloaded"),
                        r.getInt("time_elapsed"),
                        r.getInt("time_left"),
                        r.getString("status"),
                        r.getString("save_name"),
                        r.getString("save_path"),
                        r.getString("file_name"),
                        getType(r.getInt("file_type")));
            } else
                return null;

        } catch (SQLException e) {
            throw new RuntimeException(error_msg, e);
        }
    }

    /**
     * fetch all HighDownloads in the ResultSet
     * @param r: ResultSet, the object from which to extract
     * @param error_msg: String, the message to put into the exception
     * @return ArrayList<HighDownload>, size from 0 to founding count
     * @throws RuntimeException, if can't "read from db" (wrapping SQLException)
     */
    private ArrayList<HighDownload> allHighDownloads(ResultSet r, String error_msg) throws RuntimeException {
        ArrayList<HighDownload> result = new ArrayList<>();
        try {
            while (r.next()) {
                result.add(new HighDownload(r.getInt("id"),
                        r.getString("url"),
                        r.getString("params"),
                        r.getString("description"),
                        r.getString("referer"),
                        r.getString("date_added"),
                        r.getString("date_tried"),
                        r.getInt("queue_id"),
                        r.getString("size_total"),
                        r.getString("size_downloaded"),
                        r.getInt("time_elapsed"),
                        r.getInt("time_left"),
                        r.getString("status"),
                        r.getString("save_name"),
                        r.getString("save_path"),
                        r.getString("file_name"),
                        getType(r.getInt("file_type"))));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException(error_msg, e);
        }
    }
}
