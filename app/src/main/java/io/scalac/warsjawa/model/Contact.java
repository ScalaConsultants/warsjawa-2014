package io.scalac.warsjawa.model;

public class Contact {

    private String email;
    private String name;
    private String tagId = "";

    public Contact(String email, String name, String tagId) {
        this.email = email;
        this.name = name;
        if (tagId != null)
            this.tagId = tagId;
    }

    public Contact(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}
