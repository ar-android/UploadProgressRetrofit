package com.ahmadrosid.uploadprogressretrofit;

/**
 * Created by mymacbook on 1/22/18.
 */

class ResponseUpload {

    private boolean success;
    private String message;
    private String path_file;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath_file() {
        return path_file;
    }

    public void setPath_file(String path_file) {
        this.path_file = path_file;
    }
}
