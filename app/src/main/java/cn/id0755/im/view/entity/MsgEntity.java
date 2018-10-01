package cn.id0755.im.view.entity;

public class MsgEntity {
    public boolean isLeft() {
        return left;
    }

    public MsgEntity setLeft(boolean left) {
        this.left = left;
        return this;
    }

    private boolean left;

    public String getContent() {
        return content;
    }

    public MsgEntity setContent(String content) {
        this.content = content;
        return this;
    }

    private String content;
}
