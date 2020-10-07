package highdeger.highdownloader;

public class HighDownload {

    private int id = -1;
    private String url = null;
    private String params = null;
    private String description = null;
    private String referer = null;
    private String date_added = null;
    private String date_tried = null;
    private int queue_id = -1;
    private String size_total = null;
    private String size_downloaded = null;
    private int time_elapsed = -1;
    private int time_left = -1;
    private String status = null;
    private String save_name = null;
    private String save_path = null;
    private String file_name = null;
    private HighUtil.FileType file_type = null;

    public HighDownload() {
    }

    public HighDownload(int id, String url, String params, String description, String referer, String date_added, String date_tried, int queue_id, String size_total, String size_downloaded, int time_elapsed, int time_left, String status, String save_name, String save_path, String file_name, HighUtil.FileType file_type) {
        this.id = id;
        this.url = url;
        this.params = params;
        this.description = description;
        this.referer = referer;
        this.date_added = date_added;
        this.date_tried = date_tried;
        this.queue_id = queue_id;
        this.size_total = size_total;
        this.size_downloaded = size_downloaded;
        this.time_elapsed = time_elapsed;
        this.time_left = time_left;
        this.status = status;
        this.save_name = save_name;
        this.save_path = save_path;
        this.file_name = file_name;
        this.file_type = file_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getDate_tried() {
        return date_tried;
    }

    public void setDate_tried(String date_tried) {
        this.date_tried = date_tried;
    }

    public int getQueue_id() {
        return queue_id;
    }

    public void setQueue_id(int queue_id) {
        this.queue_id = queue_id;
    }

    public String getSize_total() {
        return size_total;
    }

    public void setSize_total(String size_total) {
        this.size_total = size_total;
    }

    public String getSize_downloaded() {
        return size_downloaded;
    }

    public void setSize_downloaded(String size_downloaded) {
        this.size_downloaded = size_downloaded;
    }

    public int getTime_elapsed() {
        return time_elapsed;
    }

    public void setTime_elapsed(int time_elapsed) {
        this.time_elapsed = time_elapsed;
    }

    public int getTime_left() {
        return time_left;
    }

    public void setTime_left(int time_left) {
        this.time_left = time_left;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSave_name() {
        return save_name;
    }

    public void setSave_name(String save_name) {
        this.save_name = save_name;
    }

    public String getSave_path() {
        return save_path;
    }

    public void setSave_path(String save_path) {
        this.save_path = save_path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public HighUtil.FileType getFile_type() {
        return file_type;
    }

    public void setFile_type(HighUtil.FileType file_type) {
        this.file_type = file_type;
    }
}
