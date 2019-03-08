package com.alinso.myapp.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class DeletedConversation extends BaseEntity {

    @ManyToOne
    private User eraserUser;

    @ManyToOne
    private User otherUser;

    @ManyToOne
    private Message latesMessagBeforeDelete;

    public User getEraserUser() {
        return eraserUser;
    }

    public void setEraserUser(User eraserUser) {
        this.eraserUser = eraserUser;
    }

    public User getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }

    public Message getLatesMessagBeforeDelete() {
        return latesMessagBeforeDelete;
    }

    public void setLatesMessagBeforeDelete(Message latesMessagBeforeDelete) {
        this.latesMessagBeforeDelete = latesMessagBeforeDelete;
    }
}
