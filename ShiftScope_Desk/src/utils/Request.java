package utils;

/**
 *
 * @author carlos
 */
public class Request {
    private int userId;
    private String from;
    private int type;
    private int response;
    private Metadata content;
    

    public Request() {
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the response
     */
    public int getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(int response) {
        this.response = response;
    }

    /**
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the content
     */
    public Metadata getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(Metadata content) {
        this.content = content;
    }


}
