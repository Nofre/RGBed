package nms.rgbed;

public interface NotificationsInterface {
    void reconnect();

    void showConnecting(boolean show);

    void showToast(String msg);
}
